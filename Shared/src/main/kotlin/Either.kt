package e.severin.rudie

class Either<out S, out F>(private val s: S?, private val f: F?) {

    companion object {
        fun <S> success(s: S) = Either(s, null)
        fun <F> failure(f: F) = Either(null, f)

        fun <V> fromNullable(v: V?): Either<V, Unit> {
            return when (v) {
                null -> failure(Unit)
                else -> success(v)
            }
        }
    }

    fun <A> map(lambda: (S) -> A): Either<A, F> =
        if (f != null) failure(f)
        else success(lambda(s!!))

    fun <R> unwrap(ifSuccess: (S) -> R, ifFail: (F) -> R): R {
        return if (s != null) ifSuccess(s)
        else ifFail(f!!)
    }
}
