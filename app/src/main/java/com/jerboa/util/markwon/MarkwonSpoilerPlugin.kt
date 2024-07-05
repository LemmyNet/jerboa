package com.jerboa.util.markwon

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonPlugin
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.image.AsyncDrawableScheduler

data class SpoilerTitleSpan(
    val title: CharSequence,
)

class SpoilerCloseSpan

class MarkwonSpoilerPlugin(
    val enableInteraction: Boolean,
) : AbstractMarkwonPlugin() {
    override fun configure(registry: MarkwonPlugin.Registry) {
        registry.require(CorePlugin::class.java) {
            it.addOnTextAddedListener(
                SpoilerTextAddedListener(),
            )
        }
    }

    private class SpoilerTextAddedListener : CorePlugin.OnTextAddedListener {
        override fun onTextAdded(
            visitor: MarkwonVisitor,
            text: String,
            start: Int,
        ) {
            val spoilerTitleRegex = Regex("(:::\\s+spoiler\\s+)(.*)")
            // Find all spoiler "start" lines
            val spoilerTitles = spoilerTitleRegex.findAll(text)

            for (match in spoilerTitles) {
                val spoilerTitle = match.groups[2]!!.value
                visitor.builder().setSpan(
                    SpoilerTitleSpan(spoilerTitle),
                    start,
                    start + match.groups[2]!!.range.last,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
            }

            val spoilerCloseRegex = Regex("^(?!.*spoiler).*:::")
            // Find all spoiler "end" lines
            val spoilerCloses = spoilerCloseRegex.findAll(text)
            for (match in spoilerCloses) {
                visitor
                    .builder()
                    .setSpan(SpoilerCloseSpan(), start, start + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    override fun afterSetText(textView: TextView) {
        try {
            val spanned = SpannableStringBuilder(textView.text)
            val spoilerTitleSpans =
                spanned.getSpans(0, spanned.length, SpoilerTitleSpan::class.java)
            val spoilerCloseSpans =
                spanned.getSpans(0, spanned.length, SpoilerCloseSpan::class.java)

            spoilerTitleSpans.sortBy { spanned.getSpanStart(it) }
            spoilerCloseSpans.sortBy { spanned.getSpanStart(it) }

            spoilerTitleSpans.forEachIndexed { index, spoilerTitleSpan ->
                val spoilerStart = spanned.getSpanStart(spoilerTitleSpan)

                var spoilerEnd = spanned.length
                if (index < spoilerCloseSpans.size) {
                    val spoilerCloseSpan = spoilerCloseSpans[index]
                    spoilerEnd = spanned.getSpanEnd(spoilerCloseSpan)
                }

                var open = false
                // The space at the end is necessary for the lengths to be the same
                // This reduces complexity as else it would need complex logic to determine the replacement length
                val getSpoilerTitle = { openParam: Boolean ->
                    if (openParam) "▼ ${spoilerTitleSpan.title}\n" else "▶ ${spoilerTitleSpan.title}\u200B"
                }

                val spoilerTitle = getSpoilerTitle(false)

                val spoilerContent =
                    spanned.subSequence(
                        spanned.getSpanEnd(spoilerTitleSpan) + 1,
                        spoilerEnd - 3,
                    ) as SpannableStringBuilder

                // Remove spoiler content from span
                spanned.replace(spoilerStart, spoilerEnd, spoilerTitle)
                // Set span block title
                spanned.setSpan(
                    spoilerTitle,
                    spoilerStart,
                    spoilerStart + spoilerTitle.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                )

                val wrapper =
                    object : ClickableSpan() {
                        override fun onClick(p0: View) {
                            if (enableInteraction) {
                                textView.cancelPendingInputEvents()
                                open = !open

                                val spoilerStartCurrent = spanned.getSpanStart(spoilerTitle)

                                spanned.replace(
                                    spoilerStartCurrent,
                                    spoilerStartCurrent + spoilerTitle.length,
                                    getSpoilerTitle(open),
                                )
                                if (open) {
                                    spanned.insert(spoilerStartCurrent + spoilerTitle.length, spoilerContent)
                                } else {
                                    spanned.replace(
                                        spoilerStartCurrent + spoilerTitle.length,
                                        spoilerStartCurrent + spoilerTitle.length + spoilerContent.length,
                                        "",
                                    )
                                }

                                textView.text = spanned
                                AsyncDrawableScheduler.schedule(textView)
                            }
                        }

                        override fun updateDrawState(ds: TextPaint) {
                        }
                    }

                // Set spoiler block type as ClickableSpan
                spanned.setSpan(
                    wrapper,
                    spoilerStart,
                    spoilerStart + spoilerTitle.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                )

                textView.text = spanned
            }
        } catch (e: Exception) {
            Log.w("jerboa", "Failed to parse spoiler tag. Format incorrect")
        }
    }
}
