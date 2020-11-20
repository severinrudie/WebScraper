import e.severin.rudie.ExtractCode
import java.lang.NumberFormatException

private val regex = "HTTP/1.1 (\\d+)".toRegex()

val naiveExtractCode: ExtractCode = {
    try {
        regex.find(this)?.groups?.get(1)?.value?.toInt() ?: -1
    } catch (e: NumberFormatException) {
        -1
    }
}
