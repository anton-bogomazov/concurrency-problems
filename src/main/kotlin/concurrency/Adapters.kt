@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package concurrency

import java.util.concurrent.Semaphore

fun Semaphore.acquireOrThrow() =
    try {
        this.acquire()
    } catch (e: InterruptedException) {
        throw RuntimeException(e)
    }

inline fun <T : Object, R> synchronized(lock: T, block: T.() -> R): R {
    kotlin.synchronized(lock) {
        return lock.block()
    }
}

fun Any.toJavaObj() = (this as Object)
