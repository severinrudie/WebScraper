package e.severin.rudie

interface Contract {

    interface Fetcher {
        fun fetch(url: String): ResponseFuture

        interface ResponseFuture {
            fun setOnSuccess(action: (RawResponse) -> Unit)
        }
    }

    interface Parser {
        fun parse(
            metadata: Response.Metadata,
            raw: RawResponse,
            targetDefinition: Parsing.TargetMatchDefinition, // TODO list of definitions
            linkMatchDefinition: Parsing.LinkMatchDefinition
        ): Parsing.Result
    }
}
