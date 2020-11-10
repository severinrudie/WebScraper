package e.severin.rudie

import org.junit.Ignore
import org.junit.Test
import java.net.URL

internal class HttpRequestTest {

    /**
     * Slow and flaky, not suitable to be a unit test.  But running it manually can
     * show you at a glance whether or not requests are being resolved concurrently.
     */
    @Test
    @Ignore
    fun `manually test requests are concurrent`() {
        // https://whynohttps.com/
        val responses = listOf(
            "http://www.http2demo.io/",
            "http://www.baidu.com/",
            "http://xinhuanet.com/",
            "http://apache.org/"
        ).map { HttpRequest(URL(it)).send() }

        var finished = responses.map { false }
        while (finished.any { !it }) {
            val newFinished = responses.map { it.check() !is HttpResponse.Result.Pending }
            if (finished != newFinished) {
                println("Finished: $newFinished")
                finished = newFinished
            }
        }
        println("All responses completed")
    }
}
