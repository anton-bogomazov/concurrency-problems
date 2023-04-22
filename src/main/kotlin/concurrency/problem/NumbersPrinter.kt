package concurrency.problem

import concurrency.acquireOrThrow
import java.lang.Runnable
import java.lang.Thread
import java.util.concurrent.Semaphore

class NumbersPrinter(maxNumber: Int = 100) {
    private val evenLock = Semaphore(0)
    private val oddLock = Semaphore(1)

    private val printOddsTask = Runnable {
        val oddNumbers = 1..maxNumber step 2
        oddNumbers.forEach {
            oddLock.acquireOrThrow()
            println(it)
            evenLock.release()
        }
    }

    private val printEvensTask = Runnable {
        val evenNumbers = 2..maxNumber step 2
        evenNumbers.forEach {
            evenLock.acquireOrThrow()
            println(it)
            oddLock.release()
        }
    }

    fun printNumbersSequentially() =
            listOf(printEvensTask, printOddsTask)
                    .map(::Thread)
                    .apply {
                        forEach(Thread::start)
                        forEach(Thread::join)
                    }
}
