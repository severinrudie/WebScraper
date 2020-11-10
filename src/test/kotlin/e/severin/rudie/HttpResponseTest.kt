package e.severin.rudie

import e.severin.rudie.HttpResponse.Result.Pending
import e.severin.rudie.HttpResponse.Result.Success
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.Test
import java.nio.channels.ReadableByteChannel
import kotlin.test.assertEquals

internal class HttpResponseTest {

    @Test
    fun `response checks should be non-blocking`() {
        var channel1Read = 0
        val channel1 = mockk<ReadableByteChannel> {
            every { read(any()) } answers { channel1Read }
            every { close() } just runs
        }
        var channel2Read = 0
        val channel2 = mockk<ReadableByteChannel> {
            every { read(any()) } answers { channel2Read }
            every { close() } just runs
        }

        val response1 = HttpResponse(channel1)
        val response2 = HttpResponse(channel2)

        assertEquals(Pending, response1.check())
        assertEquals(Pending, response2.check())

        channel2Read = -1

        assertEquals(Pending, response1.check())
        assertEquals(Success(""), response2.check())

        channel1Read = -1

        assertEquals(Success(""), response1.check())
        assertEquals(Success(""), response2.check())
    }
}
