import e.severin.rudie.Either
import e.severin.rudie.Response
import e.severin.rudie.SimpleFuture
import e.severin.rudie.getIfComplete
import java.io.ByteArrayOutputStream
import java.net.SocketException
import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.nio.charset.StandardCharsets
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeoutException

private const val TWO_MB = 2 * 1024 * 1024

// TODO:
//  - follow redirects
//  - handle HTTPS (probably a wrapper around this)
//  - so, so much more...
// Internal constructor exists for testing
class HttpRequest internal constructor(
    private val url: URL,
    private val executor: ExecutorService,
    private val config: Config,
    private val getTimeMs: () -> Long,
    private val openSocket: (URL) -> Either<SocketChannel, Exception>
) {

    constructor(
        url: URL,
        executor: ExecutorService,
        config: Config
    ) : this(url, executor, config, System::currentTimeMillis, ::openRealSocket)

    private val startTime = getTimeMs()
    internal var state: State = State.Unestablished

    private val future = object : SimpleFuture<Response> {
        var response: Response? = null

        override fun getIfComplete(): Response? = response
    }

    data class Config(val timeoutMs: Long)

    /**
     * Unestablished >-|--> Pending >-|--> Success >-|   Failure >-|
     *         /\--<---|      /\--<---|      /\--<---|      /\--<--|
     *                 |              |                     |
     *                 |------>-------|---------->----------|
     */
    internal sealed class State {
        object Unestablished : State()
        data class Establishing(val futureChannel: Future<Either<SocketChannel, Exception>>) : State()
        data class Pending(val channel: SocketChannel) : State()
        data class Success(val response: Response) : State()
        data class Failure(val reason: Exception) : State()
    }

    // TODO maybe this should check until it stops changing
    fun check(): SimpleFuture<Response> {
        state = state.toNext()

        future.response = when (val stateCopy = state) {
            is State.Success -> stateCopy.response
            is State.Failure -> Response.Failure(Response.Metadata(url), stateCopy.reason)
            else -> future.response
        }

        return future
    }

    private fun State.toNext(): State = when (this) {
        is State.Unestablished -> maybeToEstablishing()
        is State.Establishing -> maybeToPending()
        is State.Pending -> maybeToSuccess()
        is State.Success -> this
        is State.Failure -> this
    }

    private fun State.Unestablished.maybeToEstablishing(): State {
        return if (hasTimedOut()) State.Failure(TimeoutException())
        else State.Establishing(
            executor.submit(
                Callable {
                    openSocket(url)
                }
            )
        )
    }

    private fun State.Establishing.maybeToPending(): State {
        if (hasTimedOut()) return State.Failure(TimeoutException())
        val maybeChannel = futureChannel.getIfComplete() ?: return this

        return maybeChannel.unwrap(
            ifFail = { e ->
                State.Failure(e)
            },
            ifSuccess = { channel ->
                val request = """
                    GET / HTTP/1.1
                    User-Agent: WebScraper
                    Accept: text/*
                    Connection: close
                    Host: ${url.host}
                    
                    
                """.trimIndent()
                    .let { ByteBuffer.wrap(it.toByteArray(StandardCharsets.US_ASCII)) }
                channel.write(request)

                State.Pending(channel)
            }
        )
    }

    private fun State.Pending.maybeToSuccess(
        output: ByteArrayOutputStream = ByteArrayOutputStream(),
        buffer: ByteBuffer = ByteBuffer.allocate(TWO_MB)
    ): State {
        val bytesRead = try {
            channel.read(buffer)
        } catch (e: SocketException) {
            return State.Failure(e)
        }

        return when (bytesRead) {
            0 ->
                if (hasTimedOut()) State.Failure(TimeoutException())
                else this
            -1 -> State.Success(
                Response.Success(
                    Response.Metadata(url),
                    output.toString(Charsets.US_ASCII),
                )
            )
            else -> {
                buffer.flip()
                output.write(buffer.array(), 0, bytesRead)
                buffer.clear()
                maybeToSuccess(output, buffer)
            }
        }
    }

    private fun hasTimedOut() = getTimeMs() - startTime > config.timeoutMs
}
