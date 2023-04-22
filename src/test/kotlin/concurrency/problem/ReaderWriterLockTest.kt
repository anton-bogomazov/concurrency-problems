package concurrency.problem

import concurrency.runTasks
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThan
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import kotlin.math.abs

class ReaderWriterLockTest {

    @Test
    fun `readers waiting when writers are accessing resource`() {
        val testData = ReadWriteLockTestData()
        val sut = ReaderWriterLock()

        val writer = writer(sut, testData)
        val reader = reader(sut, testData)

        runTasks(writer, reader)

        testData.readLockAquired!! shouldBeGreaterThanOrEqualTo testData.writeLockReleased!!
    }

    @Test
    fun `any amount of readers could access shared resource`() {
        val testData1 = ReadWriteLockTestData()
        val testData2 = ReadWriteLockTestData()
        val sut = ReaderWriterLock()

        val reader1 = reader(sut, testData1)
        val reader2 = reader(sut, testData2)

        runTasks(reader1, reader2)

        // second reader is accessing the resource in the same time as first
        abs(testData2.readLockAquired!!.toEpochSecond() - testData1.readLockReleased!!.toEpochSecond()) shouldBeLessThan READING_TIME
        testData2.readLockAquired!! shouldBeLessThan testData1.readLockReleased!!
    }

    @Test
    fun `writers waiting when readers are accessing resource`() {
        val testData = ReadWriteLockTestData()
        val sut = ReaderWriterLock()

        val writer = writer(sut, testData)
        val reader = reader(sut, testData)

        runTasks(reader, writer)

        testData.writeLockReleased!! shouldBeGreaterThanOrEqualTo testData.readLockAquired!!
    }

}

private fun writer(lock: ReaderWriterLock, testData: ReadWriteLockTestData) = Runnable {
    lock.acquireWriteLock()
    testData.writeLockAquired = OffsetDateTime.now()
    Thread.sleep(50)
    lock.releaseWriteLock()
    testData.writeLockReleased = OffsetDateTime.now()
}

private const val READING_TIME = 30L

private fun reader(lock: ReaderWriterLock, testData: ReadWriteLockTestData) = Runnable {
    lock.acquireReadLock()
    testData.readLockAquired = OffsetDateTime.now()
    Thread.sleep(READING_TIME)
    lock.releaseReadLock()
    testData.readLockReleased = OffsetDateTime.now()
}

private data class ReadWriteLockTestData(
        var writeLockAquired: OffsetDateTime? = null,
        var readLockAquired: OffsetDateTime? = null,
        var writeLockReleased: OffsetDateTime? = null,
        var readLockReleased: OffsetDateTime? = null,
)
