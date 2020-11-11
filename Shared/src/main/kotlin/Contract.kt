package e.severin.rudie

typealias RawResponse = String

interface Contract {

    interface Fetcher {

        fun fetch(url: String): ResponseFuture

        interface ResponseFuture {
            fun setOnSuccess(action: (RawResponse) -> Unit)
        }
    }
}
