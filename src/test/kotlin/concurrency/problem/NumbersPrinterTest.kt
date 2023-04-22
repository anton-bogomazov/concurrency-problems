package concurrency.problem

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class NumbersPrinterTest {

    private val outputStream = ByteArrayOutputStream()

    @BeforeEach
    fun mockStdOut() {
        System.setOut(PrintStream(outputStream))
    }
    
    @AfterEach
    fun resetStdOutMock() {
        outputStream.reset()
        System.setOut(System.out)
    }

    @Test
    fun `printer prints numbers in right order in stdout`() {
        NumbersPrinter(10).printNumbersSequentially()
        outputStream.toString() shouldBe "1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n"
    }

}
