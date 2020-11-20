import e.severin.rudie.Either
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.URL
import java.nio.channels.SocketChannel

internal fun openRealSocket(url: URL): Either<SocketChannel, Exception> {
    return try {
        val remote = InetSocketAddress(url.host, 80)
        val channel = SocketChannel.open(remote).apply {
            configureBlocking(false)
        }
        Either.success(channel)
    } catch (e: Exception) {
        Either.failure(e)
    }
}
