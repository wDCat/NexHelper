package cat.dcat.util

import android.util.Log

/**
 * Created by DCat on 2019/2/11.
 */
class LogHelper {
}

private val debugMode = true
fun println(msg: Any = "\n") {
    if (!debugMode) return
    Log.d("Kotlin", "" + msg)
}

fun dlog(msg: Any = "\n") = println(msg)


fun Any.d(msg: Any = "", e: Throwable? = null) {
    if (!debugMode) return
    Log.d("" + this, "" + msg, e)
}

fun Any.e(msg: Any = "", e: Throwable? = null) {
    Log.e("" + this, "" + msg, e)
}