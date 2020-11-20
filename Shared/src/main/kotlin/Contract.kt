package e.severin.rudie

import java.util.Queue

interface Contract {

    interface Fetcher {
        fun fetch(url: String)

        val responses: Queue<Response>
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
