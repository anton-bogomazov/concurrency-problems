package concurrency.problem

import concurrency.toJavaObj

class ReaderWriterLock {
    private var numberOfReaders = 0
    private var isBeingWritten = false

    fun acquireReadLock() = concurrency.synchronized(this.toJavaObj()) {
        while (isBeingWritten) {
            wait()
        }
        numberOfReaders++
    }

    fun releaseReadLock() = concurrency.synchronized(this.toJavaObj()) {
        numberOfReaders--
        notifyAll()
    }

    fun acquireWriteLock() = concurrency.synchronized(this.toJavaObj()) {
        while (numberOfReaders > 0 || isBeingWritten) {
            wait()
        }
        isBeingWritten = true
    }

    fun releaseWriteLock() = concurrency.synchronized(this.toJavaObj()) {
        isBeingWritten = false
        notifyAll()
    }
}
