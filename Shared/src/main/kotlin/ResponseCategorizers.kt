package e.severin.rudie

// TODO In retrospect, these will both probably wind up being replaced by teh parser.  If it's a redirect, it'll just add the redirected link to the queue

interface IsSuccessful {
    fun check(input: RawResponse): Boolean
}

typealias ExtractCode = RawResponse.() -> Int
