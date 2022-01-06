package com.jerboa

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
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

@Composable
fun getCurrentAccount(accountViewModel: AccountViewModel): Account? {
    val accounts by accountViewModel.allAccounts.observeAsState()
    return getCurrentAccount(accounts)
}

fun getCurrentAccount(accounts: List<Account>?): Account? {
    return accounts?.firstOrNull { it.default_ }
}

fun toastException(ctx: Context, error: Exception) {
    Log.e("jerboa", error.toString())
    Toast.makeText(ctx, error.toString(), Toast.LENGTH_SHORT).show()
}

@Composable
fun voteColor(myVote: Int?): Color {
    return when (myVote) {
        1 -> MaterialTheme.colors.secondary
        -1 -> MaterialTheme.colors.error
        else -> LocalContentColor.current
    }
}
