import e.severin.rudie.Contract
import e.severin.rudie.Response
import java.net.URL
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

// TODO read up on concurrentlinkeddeque. supposed to be a good non-blocking
//  implementation. could be interesting

class FetcherImpl(private val config: Config = Config(6_000)) : Contract.Fetcher {
    data class Config(val timeout: Long)

    override val responses: Queue<Response> = ConcurrentLinkedDeque() // TODO queue<response>

    private val requests: Queue<HttpRequest> = ConcurrentLinkedDeque()
    private val threadPool = ThreadPoolExecutor(
        5,
        100,
        30,
        TimeUnit.SECONDS,
        LinkedBlockingQueue() // TODO figure out what this is for
    )

    override fun fetch(url: String) {
        // TODO url could fail conversion
        requests += HttpRequest(URL(url), threadPool, HttpRequest.Config(config.timeout))
    }

    fun start() = driver.start() // TODO who calls this? add to interface?

    fun stop() = driver.join(1_000)

    private val driver = Thread {
        while (true) {
            drive()
            Thread.sleep(100)
        }
    }

    private fun drive() {
        val stillRequesting = LinkedList<HttpRequest>()

        while (requests.isNotEmpty()) {
            val request = requests.poll() ?: break

            when (val response = request.check().getIfComplete()) {
                null -> stillRequesting += request
                else -> responses += response
            }
        }
        requests += stillRequesting
    }
}
