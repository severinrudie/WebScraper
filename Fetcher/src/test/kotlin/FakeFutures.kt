import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class NeverCompleteFuture<T> : Future<T> {
    override fun isDone(): Boolean = false

    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun isCancelled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun get(): T {
        TODO("Not yet implemented")
    }

    override fun get(timeout: Long, unit: TimeUnit): T {
        TODO("Not yet implemented")
    }
}

class ImmediatelyCompleteFuture<T>(private val value: T) : Future<T> {
    override fun isDone(): Boolean = true

    override fun get(): T = value

    override fun get(timeout: Long, unit: TimeUnit): T = value

    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun isCancelled(): Boolean {
        TODO("Not yet implemented")
    }
}
