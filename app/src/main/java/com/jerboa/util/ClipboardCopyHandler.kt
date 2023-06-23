package com.jerboa.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.jerboa.findActivity

object ClipboardCopyHandler {
    fun copyToClipboard(context: Context, textToCopy: CharSequence, clipLabel: CharSequence): Boolean {
        val activity = context.findActivity()
        activity?.let {
            val clipboard: ClipboardManager = it.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(clipLabel, textToCopy)
            clipboard.setPrimaryClip(clip)
            return true
        } ?: run {
            return false
        }
    }
}
