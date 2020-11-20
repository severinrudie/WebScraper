
import e.severin.rudie.Contract
import e.severin.rudie.Parsing
import e.severin.rudie.RawResponse
import e.severin.rudie.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.MalformedURLException
import java.net.URL

class JsoupDelegate : Contract.Parser {
    override fun parse(
        metadata: Response.Metadata,
        raw: RawResponse,
        targetDefinition: Parsing.TargetMatchDefinition,
        linkMatchDefinition: Parsing.LinkMatchDefinition
    ): Parsing.Result {
        val document = Jsoup.parse(raw)

        return Parsing.Result(
            metadata = Parsing.Metadata(metadata.url),
            responseCode = raw.naiveExtractCode(),
            foundText = getTargetedText(document, targetDefinition),
            followUpLinks = getLinks(document, linkMatchDefinition)
        )
    }

    private fun getTargetedText(doc: Document, targetDefinition: Parsing.TargetMatchDefinition): List<String> {
        val elementsByTag = when (targetDefinition.withTag) {
            null -> doc.allElements
            else -> doc.select(targetDefinition.withTag)
        }
        val filterForText = when (val withText = targetDefinition.withText) {
            null -> { e: Element -> true }
            else -> { e: Element -> e.ownText().contains(withText) }
        }

        return elementsByTag.filter { filterForText(it) }
            .map { it.ownText() }
    }

    private fun getLinks(doc: Document, linkDefinition: Parsing.LinkMatchDefinition): Set<String> =
        when (linkDefinition) {
            is Parsing.LinkMatchDefinition.None -> emptySet()
            is Parsing.LinkMatchDefinition.All ->
                doc.select("a[href]")
                    .eachAttr("href")
                    .mapNotNull {
                        try {
                            URL(it)
                        } catch (e: MalformedURLException) {
                            null
                        }
                    }.map { it.toString() }
                    // This _needs_ to be converted to strings before being put into a set, to
                    // avoid the URL#equals network call
                    .toSet()
        }
}
