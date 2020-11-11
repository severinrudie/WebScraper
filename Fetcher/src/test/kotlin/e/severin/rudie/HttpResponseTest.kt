package e.severin.rudie

import HttpResponse
import HttpResponse.Result.Complete.Success
import HttpResponse.Result.Pending
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.Before
import org.junit.Test
import java.nio.channels.ReadableByteChannel
import kotlin.test.assertEquals

internal class HttpResponseTest {

    var channelRead = 0
    lateinit var channel: ReadableByteChannel
    lateinit var response: HttpResponse

    @Before
    fun setup() {
        channelRead = 0
        channel = mockk {
            every { read(any()) } answers { channelRead }
            every { close() } just runs
        }
        response = HttpResponse(channel)
    }

    @Test
    fun `response checks should be non-blocking`() {
        var channel2Read = 0
        val channel2 = mockk<ReadableByteChannel> {
            every { read(any()) } answers { channel2Read }
            every { close() } just runs
        }
        val response2 = HttpResponse(channel2)

        assertEquals(Pending, response.check())
        assertEquals(Pending, response2.check())

        channel2Read = -1

        assertEquals(Pending, response.check())
        assertEquals(Success(""), response2.check())

        channelRead = -1

        assertEquals(Success(""), response.check())
        assertEquals(Success(""), response2.check())
    }

    @Test
    fun `GIVEN listener was attached before success WHEN success THEN listener should be called`() {
        var callCount = 0
        val listener: (RawResponse) -> Unit = { callCount++ }

        response.setOnSuccess(listener)

        assertEquals(0, callCount)

        response.check()
        assertEquals(0, callCount)

        channelRead = -1
        response.check()
        assertEquals(1, callCount)
    }

    @Test
    fun `GIVEN response succeeded WHEN listener attached THEN listener should be called`() {
        var callCount = 0
        val listener: (RawResponse) -> Unit = { callCount++ }
        channelRead = -1
        response.check()

        assertEquals(0, callCount)

        response.setOnSuccess(listener)

        assertEquals(1, callCount)
    }

    @Test
    fun `GIVEN listener was already called WHEN check is repeatedly called THEN listener should not be called`() {
        var callCount = 0
        val listener: (RawResponse) -> Unit = { callCount++ }
        channelRead = -1

        assertEquals(0, callCount)

        response.setOnSuccess(listener)

        assertEquals(1, callCount)

        response.check()
        response.check()
        response.check()

        assertEquals(1, callCount)
    }

    @Test
    fun `only one onSuccess callback will be active at any given time`() {
        var cc1 = 0
        val l1: (RawResponse) -> Unit = { cc1++ }
        var cc2 = 0
        val l2: (RawResponse) -> Unit = { cc2++ }

        response.setOnSuccess(l1)

        assertEquals(0, cc1)
        assertEquals(0, cc2)

        channelRead = -1

        response.setOnSuccess(l2)

        assertEquals(0, cc1)
        assertEquals(1, cc2)
    }
}
