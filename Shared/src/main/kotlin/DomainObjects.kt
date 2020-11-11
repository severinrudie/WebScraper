package e.severin.rudie

import java.net.URL

typealias RawResponse = String

data class ParsingResult(
    val metaData: ParsingMetaData,
    val found: List<String>,
    val followUpLinks: List<URL>
)

data class ParsingMetaData(val url: URL)
