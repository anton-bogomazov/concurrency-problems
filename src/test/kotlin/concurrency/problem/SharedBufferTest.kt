package concurrency.problem

import concurrency.createThreadAndReturnFuture
import concurrency.runTasks
import io.kotest.matchers.collections.shouldContainAnyOf
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Test
import java.util.concurrent.Callable

class SharedBufferTest {

    @Test
    fun `values pushed into buffer could be retrieved`() {
        val sut = SharedBuffer(5)

        val producer1 = producer(sut, 1, 2, 3)
        val producer2 = producer(sut, 9, 10)
        val consumer = consumer(sut, 5)

        runTasks(producer1, producer2)
        val (thread, result) = createThreadAndReturnFuture(consumer)

        thread.start()
        thread.join()

        with(result.get()) {
            shouldHaveSize(5)
            shouldContainAnyOf(1, 2, 3, 9, 10)
        }
    }

    @Test
    fun `producer should wait for consuming in case of buffer overflow`() {
        val sut = SharedBuffer(2)

        val producer = producer(sut, 1, 2, 3, 4, 5)
        val consumer1 = consumer(sut, 2)
        val consumer2 = consumer(sut, 1)

        val producerThread = Thread(producer)
        val (consumer1Thread, consumer1ResultFuture) = createThreadAndReturnFuture(consumer1)
        val (consumer2Thread, consumer2ResultFuture) = createThreadAndReturnFuture(consumer2)

        producerThread.start()
        consumer1Thread.start()
        consumer2Thread.start()
        producerThread.join()
        consumer1Thread.join()
        consumer2Thread.join()

        val valuesCouldBeConsumed = listOf(1, 2, 3)
        with(consumer1ResultFuture.get()) {
            shouldHaveSize(2)
            shouldContainAnyOf(valuesCouldBeConsumed)
        }
        with(consumer2ResultFuture.get()) {
            shouldHaveSize(1)
            shouldContainAnyOf(valuesCouldBeConsumed)
        }
    }

}

private fun producer(buffer: SharedBuffer, vararg values: Int) = Runnable {
    values.forEach { value ->
        buffer.produce(value)
    }
}

private fun consumer(buffer: SharedBuffer, numberOfValues: Int) = Callable<List<Int>> {
    val result = mutableListOf<Int>()

    for (i in 0 until numberOfValues) {
        result.add(buffer.consume())
    }

    return@Callable result
}
