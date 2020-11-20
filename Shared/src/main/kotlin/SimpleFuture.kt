package e.severin.rudie

import java.util.concurrent.Future

interface SimpleFuture<T> {
    fun getIfComplete(): T?
}

// Java Future
fun <T> Future<T>.getIfComplete(): T? {
    return if (!isDone) null
    else get()
}
