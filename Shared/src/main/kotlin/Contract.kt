package e.severin.rudie

interface Contract {

    interface Fetcher {
        fun fetch(url: String): ResponseFuture

        interface ResponseFuture {
            fun setOnSuccess(action: (RawResponse) -> Unit)
        }
    }

    interface Parser {
        fun parse(raw: RawResponse): ParsingResult
    }
}
