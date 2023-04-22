package concurrency.problem

import concurrency.toJavaObj
import concurrency.synchronized

class SharedBuffer(private val size: Int) {
    private val buffer: IntArray = IntArray(size)
    private var count = 0
    private var head = 0
    private var tail = 0

    fun produce(item: Int) = synchronized(this.toJavaObj()) {
        while (count == size) {
            wait()
        }

        buffer[tail] = item
        tail = (tail + 1) % size
        count++

        notifyAll()
    }

    fun consume(): Int = synchronized(this.toJavaObj()) {
        while (count == 0) {
            wait()
        }

        val item = buffer[head]
        head = (head + 1) % size
        count--

        notifyAll()

        return item
    }
}
