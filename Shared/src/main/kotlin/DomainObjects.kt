package e.severin.rudie

import java.net.URL

typealias RawResponse = String

sealed class Response {
    data class Metadata(val url: URL)

    // TODO Rename. This just means we got *something* back
    data class Success(val metadata: Metadata, val rawResponse: String) : Response()
    data class Failure(val metadata: Metadata, val reason: Exception) : Response()
}

object Parsing {

    // TODO revisit this.  Need to think about what I'm trying to parse out, and also how I need to target it
    data class TargetMatchDefinition(val withTag: String?, val withText: String?)

    sealed class LinkMatchDefinition {
        object All : LinkMatchDefinition()
        object None : LinkMatchDefinition()
        // TODO Note that attribute 'rel="nofollow"' on <a> is used by Google to stop spiders from crawling. See
        // https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/rel
        // TODO update to respect robots.txt
    }

    data class Result(
        val metadata: Metadata,
        val responseCode: Int,
        val foundText: List<String>,
        // Follow up is a set to avoid duplication, but cannot be Set<URL> because URL#equals makes a network call
        val followUpLinks: Set<String>
    )

    data class Metadata(val url: URL)
}
