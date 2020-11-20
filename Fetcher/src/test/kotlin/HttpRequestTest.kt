import HttpRequest.State.Establishing
import HttpRequest.State.Failure
import HttpRequest.State.Pending
import HttpRequest.State.Success
import HttpRequest.State.Unestablished
import MockExecutorService.Finish.IMMEDIATELY
import MockExecutorService.Finish.NEVER
import e.severin.rudie.Either
import e.severin.rudie.Response
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val TIMEOUT = 1_000L
private const val PAST_TIMEOUT = 2_000L
private const val WITHIN_TIMEOUT = 500L

internal class HttpRequestTest {

    private lateinit var request: HttpRequest
    private var nextSocketRead: Int = -100
    private var nextTime: Long = -1
    private var nextSocketEither: Either<SocketChannel, Exception>? = null
    private lateinit var mockSocket: SocketChannel

    private val url = URL("https://www.wikipedia.org/")
    private val config = HttpRequest.Config(TIMEOUT)
    private val mockExecutorService = MockExecutorService()

    @Before
    fun setup() {
        nextSocketRead = -100 // -1 is used as "read complete"
        nextTime = -1
        nextSocketEither = null
        mockSocket = mockk {
            every { write(any<ByteBuffer>()) } returns 0
            every { read(any<ByteBuffer>()) } answers { nextSocketRead }
        }
        request = HttpRequest(url, mockExecutorService, config, ::nextTime, { nextSocketEither!! })
    }

    @Test
    fun `GIVEN state is unestablished WHEN socket never finishes opening THEN state should be establishing`() {
        mockExecutorService.finish = NEVER

        assertEquals(Unestablished, request.state)

        assertNull(request.check().getIfComplete())
        assertTrue { request.state is Establishing }
    }

    @Test
    fun `GIVEN state is unestablished WHEN socket never finishes opening AND time is elapsed THEN state should be failure`() {
        mockExecutorService.finish = NEVER

        assertEquals(Unestablished, request.state)

        assertNull(request.check().getIfComplete())
        assertTrue { request.state is Establishing }

        nextTime = PAST_TIMEOUT

        assertThat(request.check().getIfComplete()).isInstanceOf(Response.Failure::class.java)
        assertTrue { request.state is Failure }
    }

    @Test
    fun `GIVEN timeout has occured AND connection has not been established THEN state should be failure`() {
        mockExecutorService.finish = IMMEDIATELY
        nextTime = PAST_TIMEOUT

        assertEquals(Unestablished, request.state)

        assertThat(request.check().getIfComplete()).isInstanceOf(Response.Failure::class.java)
        assertTrue { request.state is Failure }
    }

    @Test
    fun `GIVEN state is pending AND timeout has occured WHEN successful response is received THEN state should be success`() {
        mockExecutorService.finish = IMMEDIATELY
        nextTime = PAST_TIMEOUT
        request.state = (Pending(mockSocket))
        nextSocketRead = -1

        assertTrue { request.state is Pending }

        assertThat(request.check().getIfComplete()).isInstanceOf(Response.Success::class.java)

        assertTrue { request.state is Success }
    }

    @Test
    fun `WHEN establishing socket fails THEN state should be failure`() {
        mockExecutorService.finish = IMMEDIATELY
        nextSocketEither = Either.failure(RuntimeException())
        request.check()

        assertThat(request.state).isInstanceOf(Establishing::class.java)

        request.check()
        assertThat(request.check().getIfComplete()).isInstanceOf(Response.Failure::class.java)
        assertThat(request.state).isInstanceOf(Failure::class.java)
    }

    @Test
    fun `GIVEN state is failure WHEN state is checked THEN state should remain failure`() {
        request.state = Failure(RuntimeException())
        nextSocketEither = Either.success(mockSocket)

        assertThat(request.check().getIfComplete()).isInstanceOf(Response.Failure::class.java)
        assertTrue { request.state is Failure }

        nextSocketRead = 500

        assertThat(request.check().getIfComplete()).isInstanceOf(Response.Failure::class.java)
        assertTrue { request.state is Failure }

        nextSocketRead = -1

        assertThat(request.check().getIfComplete()).isInstanceOf(Response.Failure::class.java)
        assertTrue { request.state is Failure }
    }

    @Test
    fun `GIVEN state is success WHEN state is checked THEN state should remain success`() {
        request.state = Success(mockk<Response.Success>())
        nextSocketEither = Either.failure(RuntimeException())

        assertThat(request.check().getIfComplete()).isInstanceOf(Response.Success::class.java)
        assertTrue { request.state is Success }

        nextSocketRead = 500

        assertThat(request.check().getIfComplete()).isInstanceOf(Response.Success::class.java)
        assertTrue { request.state is Success }

        nextSocketRead = -1

        assertThat(request.check().getIfComplete()).isInstanceOf(Response.Success::class.java)
        assertTrue { request.state is Success }
    }
}
