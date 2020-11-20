import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

internal class MockExecutorService : ExecutorService {
    enum class Finish {
        IMMEDIATELY, NEVER
    }

    internal var finish: Finish = Finish.IMMEDIATELY

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> submit(task: Callable<T>): Future<T> = when (finish) {
        Finish.IMMEDIATELY -> ImmediatelyCompleteFuture(task.call())
        Finish.NEVER -> NeverCompleteFuture()
    }

    override fun execute(command: Runnable) {
        TODO("Not yet implemented")
    }

    override fun shutdown() {
        TODO("Not yet implemented")
    }

    override fun shutdownNow(): MutableList<Runnable> {
        TODO("Not yet implemented")
    }

    override fun isShutdown(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isTerminated(): Boolean {
        TODO("Not yet implemented")
    }

    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> submit(task: Runnable, result: T): Future<T> {
        TODO("Not yet implemented")
    }

    override fun submit(task: Runnable): Future<*> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> invokeAll(tasks: MutableCollection<out Callable<T>>): MutableList<Future<T>> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> invokeAll(
        tasks: MutableCollection<out Callable<T>>,
        timeout: Long,
        unit: TimeUnit
    ): MutableList<Future<T>> {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>, timeout: Long, unit: TimeUnit): T {
        TODO("Not yet implemented")
    }
}
