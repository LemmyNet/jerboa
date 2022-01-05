package com.jerboa

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
val prettyTime = PrettyTime(Locale.getDefault())

val gson = Gson()

// convert a data class to a map
fun <T> T.serializeToMap(): Map<String, String> {
    return convert()
}

// convert an object of type I to type O
inline fun <I, reified O> I.convert(): O {
    val json = gson.toJson(this)
    return gson.fromJson(
        json,
        object : TypeToken<O>() {}.type
    )
}

fun previewLines(text: String): String {
    val min = minOf(300, text.length)
    return text.substring(0, min)
}
