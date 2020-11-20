import e.severin.rudie.Response
import org.junit.Ignore
import org.junit.Test

internal class ManualRequestManyTest {
    @Test
    @Ignore // Manual test only.  Hits the network
    fun `manually test concurrent http requests`() {
        val startTime = System.currentTimeMillis()
        val fetcher = FetcherImpl()
        fetcher.start()

        inputs.forEach { fetcher.fetch(it) }

        val seen = mutableSetOf<Response>()

        while (seen.size < inputs.size) {
            val next = fetcher.responses.poll()
            next?.let {
                seen += it
                val str = when (it) {
                    is Response.Success -> "New value for ${it.metadata.url}.  Elapsed: ${System.currentTimeMillis() - startTime}"
                    is Response.Failure -> "Request to ${it.metadata.url} failed. Reason: ${it.reason::class.java.simpleName}"
                }
                println(str)
            }
            Thread.sleep(50)
        }

        val successes = seen.filterIsInstance<Response.Success>()
        val failures = seen.filterIsInstance<Response.Failure>()

        println("\n\n===== Totals =====\n\n")
        println("Total elapsed time: ${System.currentTimeMillis() - startTime}")
        println("Successful: ${successes.size}")
        println("Failed: ${failures.size}")

        println("\n\nBegin dumping successful responses\n\n")
        println(seen.filterIsInstance<Response.Success>().joinToString(separator = "\n\n\n\n\n"))

//        println("\n\nBegin dumping success response codes\n\n")
//        println(successes.map { it.rawResponse.naiveExtractCode() })

        fetcher.stop()
    }

    private val inputs = listOf(
        "http://baidu.com",
        "http://sohu.com",
        "http://xinhuanet.com",
        "http://apache.org",
        "http://w3.org",
        "http://babytree.com",
        "http://myshopify.com",
        "http://whitepages.com",
        "http://tianya.cn",
        "http://go.com",
        "http://mit.edu",
        "http://gnu.org",
        "http://panda.tv",
        "http://soso.com",
        "http://china.com.cn",
        "http://hugedomains.com",
        "http://rednet.cn",
        "http://nature.com",
        "http://drudgereport.com",
        "http://nginx.org",
        "http://techcrunch.com",
        "http://moatads.com",
        "http://miit.gov.cn",
        "http://beian.gov.cn",
        "http://17ok.com",
        "http://washington.edu",
        "http://thestartmagazine.com",
        "http://jrj.com.cn",
        "http://rlcdn.com",
        "http://definition.org",
        "http://brainly.com",
        "http://ntp.org",
        "http://chinadaily.com.cn",
        "http://tripod.com",
        "http://tapad.com",
        "http://axs.com",
        "http://yimg.com",
        "http://startribune.com",
        "http://cbc.ca",
        "http://geocities.com",
        "http://gmw.cn",
        "http://eastday.com",
        "http://eepurl.com",
        "http://cafemom.com",
        "http://ucla.edu",
        "http://hdfcbank.com",
        "http://example.com",
        "http://gohoi.com",
        "http://techofires.com",
        "http://www.gov.cn",
        "http://ox.ac.uk",
        "http://sigonews.com",
        "http://lenovo.com",
        "http://itfactly.com",
        "http://aboutads.info",
        "http://reverso.net",
        "http://ideapuls.com",
        "http://sendgrid.net",
        "http://genius.com",
        "http://easybib.com",
        "http://nyu.edu",
        "http://shareasale-analytics.com",
        "http://bizrate.com",
        "http://redfin.com",
        "http://ufl.edu",
        "http://icio.us",
        "http://cowner.net",
        "http://oecd.org",
        "http://youth.cn",
        "http://cctv.com",
        "http://theconversation.com",
        "http://trend-chaser.com",
        "http://imageshack.us",
        "http://youdao.com",
        "http://fao.org",
        "http://angelfire.com",
        "http://chinanews.com",
        "http://hexun.com",
        "http://youronlinechoices.com",
        "http://tencent.com",
        "http://lijit.com",
        "http://dedecms.com",
        "http://senate.gov",
        "http://zol.com.cn",
        "http://51sole.com",
        "http://bu.edu",
        "http://livedoor.jp",
        "http://adoptapet.com",
        "http://rutgers.edu",
        "http://ihg.com",
        "http://dmm.co.jp",
        "http://medicinenet.com",
        "http://zemanta.com",
        "http://wikidot.com",
        "http://tremorhub.com",
        "http://dmoz.org",
        "http://cntv.cn",
        "http://zara.com",
        "http://alternativenation.net",
        "http://namnak.com",
        "http://www.httpvshttps.com/",
    )
}
