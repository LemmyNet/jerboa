package com.jerboa.util

import android.content.Context
import android.content.SharedPreferences
import com.jerboa.getAvailableLanguages

class JerboaPreferences(context: Context) {
    private val PREFERENCE_NAME = "JerboaPreferences"

    // If the device's language is available, use it
    // use english otherwise
    val DEFAULT_LANGUAGE =
        if (getAvailableLanguages(context).contains(androidx.compose.ui.text.intl.Locale.current.language)) {
            androidx.compose.ui.text.intl.Locale.current.language
        } else {
            "en"
        }

    private val preference: SharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getLanguageLocal(): String {
        return preference.getString("Language", DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    fun setLanguageLocal(local: String) {
        val editor = preference.edit()
        editor.putString("Language", local)
        editor.apply()
    }
}
