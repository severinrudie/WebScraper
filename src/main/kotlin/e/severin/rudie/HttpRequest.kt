package e.severin.rudie

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

class HttpResponse(private val channel: ReadableByteChannel) {
    sealed class Result {
        object Pending : Result()
        data class Failure(val code: Int) : Result()
        data class Success(val text: String) : Result()
    }

    private var cached: Result? = null

    fun check(): Result {
        cached?.let { return it }

        return check(ByteArrayOutputStream(), ByteBuffer.allocate(twoMb)).also {
            if (it != Result.Pending) {
                cached = it
                channel.close()
            }
        }
    }

    private tailrec fun check(output: ByteArrayOutputStream, buffer: ByteBuffer): Result {
        return when (val bytesRead = channel.read(buffer)) {
            0 -> Result.Pending
            -1 -> Result.Success(output.toString(Charsets.US_ASCII)) // TODO handle failure
            else -> {
                buffer.flip()
                output.write(buffer.array(), 0, bytesRead)
                buffer.clear()
                check(output, buffer)
            }
        }
    }
}
