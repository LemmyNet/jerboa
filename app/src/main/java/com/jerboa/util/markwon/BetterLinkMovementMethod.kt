package com.jerboa.util.markwon

import android.graphics.RectF
import android.text.Selection
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.TextView
import com.jerboa.R

/**
 * Kotlin adaption of Saket their [BetterLinkMovementMethod](https://github.com/saket/Better-Link-Movement-Method)
 *
 * Also includes a crude fix for long click not being fully consumed
 * check for the following in your other longclick handler `(v.selectionStart == -1 && v.selectionEnd == -1)`
 *
 * @author Maarten Vercruysse
 */
open class BetterLinkMovementMethod internal constructor() : LinkMovementMethod() {
    private var onLinkClickListener: OnLinkClickListener? = null
    private var onLinkLongClickListener: OnLinkLongClickListener? = null
    private val touchedLineBounds = RectF()
    private var isUrlHighlighted = false
    private var clickableSpanUnderTouchOnActionDown: ClickableSpan? = null
    private var activeTextViewHashcode = 0
    private var ongoingLongPressTimer: LongPressTimer? = null
    private var wasLongPressRegistered = false

    companion object {
        private var singleInstance: BetterLinkMovementMethod? = null

        open class LongPressTimer : Runnable {
            private lateinit var onTimerReachedListener: OnTimerReachedListener

            interface OnTimerReachedListener {
                fun onTimerReached()
            }

            @Override
            override fun run() {
                onTimerReachedListener.onTimerReached()
            }

            fun setOnTimerReachedListener(listener: OnTimerReachedListener) {
                onTimerReachedListener = listener
            }
        }

        /**
         * A wrapper to support all [ClickableSpan]s that may or may not provide URLs.
         */
        protected open class ClickableSpanWithText protected constructor(
            private val span: ClickableSpan,
            private val text: String,
        ) {
            fun span(): ClickableSpan = span

            fun text(): String = text

            companion object {
                fun ofSpan(
                    textView: TextView,
                    span: ClickableSpan,
                ): ClickableSpanWithText {
                    val s = textView.text as Spanned
                    val text: String =
                        if (span is URLSpan) {
                            span.url
                        } else {
                            val start = s.getSpanStart(span)
                            val end = s.getSpanEnd(span)
                            s.subSequence(start, end).toString()
                        }
                    return ClickableSpanWithText(span, text)
                }
            }
        }

        /**
         * Return a new instance of BetterLinkMovementMethod.
         */
        fun newInstance(): BetterLinkMovementMethod = BetterLinkMovementMethod()
    }

    interface OnLinkClickListener {
        /**
         * @param textView The TextView on which a click was registered.
         * @param url      The clicked URL.
         * @return True if this click was handled. False to let Android handle the URL.
         */
        fun onClick(
            textView: TextView,
            url: String,
        ): Boolean
    }

    interface OnLinkLongClickListener {
        /**
         * @param textView The TextView on which a long-click was registered.
         * @param url      The long-clicked URL.
         * @return True if this long-click was handled. False to let Android handle the URL (as a short-click).
         */
        fun onLongClick(
            textView: TextView,
            url: String,
        ): Boolean
    }

    /**
     * Get a static instance of BetterLinkMovementMethod. Do note that registering a click listener on the returned
     * instance is not supported because it will potentially be shared on multiple TextViews.
     */
    @Suppress("unused")
    fun getInstance(): BetterLinkMovementMethod? {
        if (singleInstance == null) {
            singleInstance = BetterLinkMovementMethod()
        }
        return singleInstance
    }

    /**
     * Set a listener that will get called whenever any link is clicked on the TextView.
     */
    fun setOnLinkClickListener(clickListener: OnLinkClickListener): BetterLinkMovementMethod {
        if (this === singleInstance) {
            throw UnsupportedOperationException(
                "Setting a click listener on the instance returned by getInstance() is not supported to avoid memory " +
                    "leaks. Please use newInstance() or any of the linkify() methods instead.",
            )
        }
        onLinkClickListener = clickListener
        return this
    }

    /**
     * Set a listener that will get called whenever any link is clicked on the TextView.
     */
    fun setOnLinkLongClickListener(longClickListener: OnLinkLongClickListener): BetterLinkMovementMethod {
        if (this === singleInstance) {
            throw UnsupportedOperationException(
                "Setting a long-click listener on the instance returned by getInstance() is not supported to avoid " +
                    "memory leaks. Please use newInstance() or any of the linkify() methods instead.",
            )
        }
        onLinkLongClickListener = longClickListener
        return this
    }

    override fun onTouchEvent(
        textView: TextView,
        text: Spannable,
        event: MotionEvent,
    ): Boolean {
        if (activeTextViewHashcode != textView.hashCode()) {
            // Bug workaround: TextView stops calling onTouchEvent() once any URL is highlighted.
            // A hacky solution is to reset any "autoLink" property set in XML. But we also want
            // to do this once per TextView.
            activeTextViewHashcode = textView.hashCode()
            textView.autoLinkMask = 0
        }
        val clickableSpanUnderTouch: ClickableSpan? = findClickableSpanUnderTouch(textView, text, event)
        if (event.action == MotionEvent.ACTION_DOWN) {
            clickableSpanUnderTouchOnActionDown = clickableSpanUnderTouch
        }
        val touchStartedOverAClickableSpan = clickableSpanUnderTouchOnActionDown != null

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (clickableSpanUnderTouch != null) {
                    highlightUrl(textView, clickableSpanUnderTouch, text)

                    if (touchStartedOverAClickableSpan && onLinkLongClickListener != null) {
                        val longClickListener: LongPressTimer.OnTimerReachedListener =
                            object :
                                LongPressTimer.OnTimerReachedListener {
                                override fun onTimerReached() {
                                    wasLongPressRegistered = true
                                    textView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    removeUrlHighlightColor(textView)
                                    dispatchUrlLongClick(textView, clickableSpanUnderTouch)
                                }
                            }
                        startTimerForRegisteringLongClick(textView, longClickListener)
                    }
                }

                touchStartedOverAClickableSpan
            }

            MotionEvent.ACTION_UP -> {
                // Register a click only if the touch started and ended on the same URL.
                if (clickableSpanUnderTouch != null &&
                    !wasLongPressRegistered &&
                    touchStartedOverAClickableSpan &&
                    clickableSpanUnderTouch === clickableSpanUnderTouchOnActionDown
                ) {
                    dispatchUrlClick(textView, clickableSpanUnderTouch)
                }
                cleanupOnTouchUp(textView)

                // Consume this event even if we could not find any spans to avoid letting Android handle this event.
                // Android's TextView implementation has a bug where links get clicked even when there is no more text
                // next to the link and the touch lies outside its bounds in the same direction.
                touchStartedOverAClickableSpan
            }

            MotionEvent.ACTION_CANCEL -> {
                cleanupOnTouchUp(textView)
                false
            }

            MotionEvent.ACTION_MOVE -> {
                // Stop listening for a long-press as soon as the user wanders off to unknown lands.
                if (clickableSpanUnderTouch !== clickableSpanUnderTouchOnActionDown) {
                    removeLongPressCallback(textView)
                }
                if (!wasLongPressRegistered) {
                    // Toggle highlight.
                    clickableSpanUnderTouch?.let { highlightUrl(textView, it, text) } ?: removeUrlHighlightColor(
                        textView,
                    )
                }
                touchStartedOverAClickableSpan
            }

            else -> false
        }
    }

    private fun cleanupOnTouchUp(textView: TextView) {
        wasLongPressRegistered = false
        clickableSpanUnderTouchOnActionDown = null
        removeUrlHighlightColor(textView)
        removeLongPressCallback(textView)
    }

    /**
     * Determines the touched location inside the TextView's text and returns the ClickableSpan found under it (if any).
     *
     * @return The touched ClickableSpan or null.
     */
    private fun findClickableSpanUnderTouch(
        textView: TextView,
        text: Spannable,
        event: MotionEvent,
    ): ClickableSpan? {
        // So we need to find the location in text where touch was made, regardless of whether the TextView
        // has scrollable text. That is, not the entire text is currently visible.
        var touchX = event.x.toInt()
        var touchY = event.y.toInt()

        // Ignore padding.
        touchX -= textView.totalPaddingLeft
        touchY -= textView.totalPaddingTop

        // Account for scrollable text.
        touchX += textView.scrollX
        touchY += textView.scrollY
        val layout = textView.layout
        val touchedLine = layout.getLineForVertical(touchY)
        val touchOffset = layout.getOffsetForHorizontal(touchedLine, touchX.toFloat())
        touchedLineBounds.left = layout.getLineLeft(touchedLine)
        touchedLineBounds.top = layout.getLineTop(touchedLine).toFloat()
        touchedLineBounds.right = layout.getLineWidth(touchedLine) + touchedLineBounds.left
        touchedLineBounds.bottom = layout.getLineBottom(touchedLine).toFloat()
        return if (touchedLineBounds.contains(touchX.toFloat(), touchY.toFloat())) {
            // Find a ClickableSpan that lies under the touched area.
            val spans: Array<ClickableSpan?> = text.getSpans(touchOffset, touchOffset, ClickableSpan::class.java)
            for (span in spans) {
                if (span is ClickableSpan) {
                    return span
                }
            }
            // No ClickableSpan found under the touched location.
            null
        } else {
            // Touch lies outside the line's horizontal bounds where no spans should exist.
            null
        }
    }

    /**
     * Adds a background color span at <var>clickableSpan</var>'s location.
     */
    private fun highlightUrl(
        textView: TextView,
        clickableSpan: ClickableSpan?,
        text: Spannable,
    ) {
        if (isUrlHighlighted) {
            return
        }
        isUrlHighlighted = true
        val spanStart = text.getSpanStart(clickableSpan)
        val spanEnd = text.getSpanEnd(clickableSpan)
        val highlightSpan = BackgroundColorSpan(textView.highlightColor)
        text.setSpan(highlightSpan, spanStart, spanEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        textView.setTag(R.id.bettermovementmethod_highlight_background_span, highlightSpan)
        Selection.setSelection(text, spanStart, spanEnd)
    }

    /**
     * Removes the highlight color under the Url.
     */
    protected fun removeUrlHighlightColor(textView: TextView) {
        if (!isUrlHighlighted) {
            return
        }
        isUrlHighlighted = false
        val text = textView.text as Spannable
        val highlightSpan = textView.getTag(R.id.bettermovementmethod_highlight_background_span) as BackgroundColorSpan
        text.removeSpan(highlightSpan)
        Selection.removeSelection(text)
    }

    // This is the crude fix, increase timeout
    private fun startTimerForRegisteringLongClick(
        textView: TextView,
        longClickListener: LongPressTimer.OnTimerReachedListener,
    ) {
        ongoingLongPressTimer = LongPressTimer()
        ongoingLongPressTimer!!.setOnTimerReachedListener(longClickListener)
        textView.postDelayed(ongoingLongPressTimer, ViewConfiguration.getLongPressTimeout().toLong() + 100L)
    }

    /**
     * Remove the long-press detection timer.
     */
    private fun removeLongPressCallback(textView: TextView) {
        if (ongoingLongPressTimer != null) {
            textView.removeCallbacks(ongoingLongPressTimer)
            ongoingLongPressTimer = null
        }
    }

    private fun dispatchUrlClick(
        textView: TextView,
        clickableSpan: ClickableSpan,
    ) {
        val clickableSpanWithText: ClickableSpanWithText = ClickableSpanWithText.ofSpan(textView, clickableSpan)
        val handled =
            onLinkClickListener != null && onLinkClickListener!!.onClick(textView, clickableSpanWithText.text())
        if (!handled) {
            // Let Android handle this click.
            clickableSpanWithText.span().onClick(textView)
        }
    }

    protected open fun dispatchUrlLongClick(
        textView: TextView,
        clickableSpan: ClickableSpan,
    ) {
        val clickableSpanWithText: ClickableSpanWithText = ClickableSpanWithText.ofSpan(textView, clickableSpan)
        val handled =
            onLinkLongClickListener != null &&
                onLinkLongClickListener!!.onLongClick(
                    textView,
                    clickableSpanWithText.text(),
                )
        if (!handled) {
            // Let Android handle this long click as a short-click.
            clickableSpanWithText.span().onClick(textView)
        }
    }
}
