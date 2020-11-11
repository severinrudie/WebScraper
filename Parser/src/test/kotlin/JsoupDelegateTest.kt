import e.severin.rudie.Parsing
import e.severin.rudie.Response
import org.junit.Test
import java.io.File
import java.net.URL
import kotlin.test.assertEquals

internal class JsoupDelegateTest {

    private val rawHttp2Demo = File("src/test/kotlin/sampleresponses/http2demo-dot-com").readText()

    @Test
    fun `test http2demo follow up links`() {
        val parsingResult = JsoupDelegate().parse(
            Response.Metadata(URL("http://www.whatever.com")),
            rawHttp2Demo,
            Parsing.TargetMatchDefinition(null, null),
            Parsing.LinkMatchDefinition.All
        )

        val links = parsingResult.followUpLinks.map { it.toString() }
        val expected = listOf(
            "https://www.cdn77.com/",
            "http://1153288396.rsc.cdn77.org/img/cdn77-test-563kb.jpg",
            "http://1153288396.rsc.cdn77.org//img/cdn77-test-3mb.jpg",
            "http://1153288396.rsc.cdn77.org//img/cdn77-test-14mb.jpg",
            "https://1906714720.rsc.cdn77.org/img/cdn77-test-563kb.jpg",
            "https://1906714720.rsc.cdn77.org/img/cdn77-test-3mb.jpg",
            "https://1906714720.rsc.cdn77.org/img/cdn77-test-14mb.jpg",
            "https://www.cdn77.com/http2",
            "https://datapacket.com/",
        )

        assertEquals(expected, links)
    }

    @Test
    fun `test http2demo get all elements`() {
        val parsingResult = JsoupDelegate().parse(
            Response.Metadata(URL("http://www.whatever.com")),
            rawHttp2Demo,
            Parsing.TargetMatchDefinition(null, null),
            Parsing.LinkMatchDefinition.All
        )

        assertEquals(87, parsingResult.foundText.size)
    }

    @Test
    fun `test http2demo all 'a' tags`() {
        val parsingResult = JsoupDelegate().parse(
            Response.Metadata(URL("http://www.whatever.com")),
            rawHttp2Demo,
            Parsing.TargetMatchDefinition("a", null),
            Parsing.LinkMatchDefinition.None
        )

        val expected = listOf(
            "REFRESH",
            "Run HTTP/2 test",
            "",
            "Check CDN77.com site",
            "[0.5MB]",
            "[3MB]",
            "[14MB]",
            "[0.5MB]",
            "[3MB]",
            "[14MB]",
            "What others talk about, we deliver",
            "",
        )

        assertEquals(expected, parsingResult.foundText)
    }

    @Test
    fun `test http2demo 'a' tags with text`() {
        val parsingResult = JsoupDelegate().parse(
            Response.Metadata(URL("http://www.whatever.com")),
            rawHttp2Demo,
            Parsing.TargetMatchDefinition("a", "MB"),
            Parsing.LinkMatchDefinition.None
        )

        val expected = listOf(
            "[0.5MB]",
            "[3MB]",
            "[14MB]",
            "[0.5MB]",
            "[3MB]",
            "[14MB]",
        )

        assertEquals(expected, parsingResult.foundText)
    }

    @Test
    fun `test http2demo get all by text`() {
        val parsingResult = JsoupDelegate().parse(
            Response.Metadata(URL("http://www.whatever.com")),
            rawHttp2Demo,
            Parsing.TargetMatchDefinition(null, "HTTP"),
            Parsing.LinkMatchDefinition.None
        )

        val expected = listOf(
            "Success(text=HTTP/1.1 200 OK Date: Wed, 11 Nov 2020 22:27:21 GMT Content-Type: text/html Content-Length: 105216 Connection: close ETag: \"5aa91b6d-19b00\" Cache-Control: no-cache Access-Control-Allow-Origin: * Server: CDN77-Turbo X-77-NZT: AVm7uxIoVhjv3BNnAQ== X-Cache: HIT X-Age: 23532508 Accept-Ranges: bytes )",
            "HTTP/2 technology demo",
            "HTTP/2",
            "Run HTTP/2 test",
            "Hello! Unfortunately, your browser does not support HTTP/2, we are sorry. Try to upgrade to the newest version and test again.",
            "We can accelerate all your online content, whether you run a site, e-commerce or videos. Get the lowest latency with a&nbspCDN & HTTP/2 combo.",
            "HTTP/1 Sample files: * *",
            "HTTP/2 Sample files: * *",
            "HTTP/2",
        )

        assertEquals(expected, parsingResult.foundText)
    }

    @Test
    fun `assert return metadata url matches input metadata url`() {
        val url = URL("http://www.whatever.com")

        val parsingResult = JsoupDelegate().parse(
            Response.Metadata(url),
            rawHttp2Demo,
            Parsing.TargetMatchDefinition(null, "HTTP"),
            Parsing.LinkMatchDefinition.None
        )

        // toString used because URL#equals makes a network request
        assertEquals(url.toString(), parsingResult.metadata.url.toString())
    }
}
