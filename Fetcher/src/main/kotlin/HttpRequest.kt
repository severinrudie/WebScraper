import e.severin.rudie.Contract
import e.severin.rudie.RawResponse
import java.io.ByteArrayOutputStream
import java.net.InetSocketAddress
import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.nio.channels.SocketChannel
import java.nio.charset.StandardCharsets

private const val twoMb = 2 * 1024 * 1024

/**
 * Simple, extremely naive HTTP request.
 *
 * Single-threaded but non-blocking.
 */
class HttpRequest(private val url: URL) {

    fun send(): HttpResponse {
        val remote = InetSocketAddress(url.host, 80)
        val channel = SocketChannel.open(remote).apply {
            configureBlocking(false)
        }

        val request = """
            GET / HTTP/1.1
            User-Agent: WebScraper
            Accept: text/*
            Connection: close
            Host: ${url.host}
            
            
        """.trimIndent()
            .let { ByteBuffer.wrap(it.toByteArray(StandardCharsets.US_ASCII)) }
        channel.write(request)

        return HttpResponse(channel)
    }
}

class HttpResponse(private val channel: ReadableByteChannel) : Contract.Fetcher.ResponseFuture {
    sealed class Result {
        object Pending : Result()
        sealed class Complete : Result() {
            data class Failure(val code: Int) : Complete()
            data class Success(val text: String) : Complete()
        }
    }

    private var cached: Result.Complete? = null
    private var onSuccess: ((RawResponse) -> Unit)? = null

    /**
     * If the request has already completed, this will immediately be called.
     *
     * Otherwise, it will not be called until [check] is called after a successful
     * response.
     */
    override fun setOnSuccess(action: (RawResponse) -> Unit) {
        onSuccess = action

        when (val cached = cached) {
            null -> check()
            is Result.Complete.Success -> action(cached.text)
            is Result.Complete.Failure -> { /* TODO */ }
        }
    }

    fun check(): Result {
        cached?.let { return it }

        return check(ByteArrayOutputStream(), ByteBuffer.allocate(twoMb)).also { result ->
            if (result is Result.Complete) {
                cached = result
                channel.close()
            }
            val onSuccess = onSuccess
            if (result is Result.Complete.Success && onSuccess != null) {
                onSuccess(result.text)
            }
        }
    }

    private tailrec fun check(output: ByteArrayOutputStream, buffer: ByteBuffer): Result {
        return when (val bytesRead = channel.read(buffer)) {
            0 -> Result.Pending
            -1 -> Result.Complete.Success(output.toString(Charsets.US_ASCII)) // TODO handle failure
            else -> {
                buffer.flip()
                output.write(buffer.array(), 0, bytesRead)
                buffer.clear()
                check(output, buffer)
            }
        }
    }
}
