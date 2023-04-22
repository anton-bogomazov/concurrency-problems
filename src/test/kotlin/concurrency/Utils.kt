package concurrency

import java.util.concurrent.Callable
import java.util.concurrent.FutureTask

fun runTasks(vararg tasks: Runnable) =
        tasks.map(::Thread).apply {
            forEach(Thread::start)
            forEach(Thread::join)
        }

fun <T> createThreadAndReturnFuture(task: Callable<T>): Pair<Thread, FutureTask<T>> {
    val result = FutureTask(task)
    val thread = Thread(result)

    return thread to result
}
