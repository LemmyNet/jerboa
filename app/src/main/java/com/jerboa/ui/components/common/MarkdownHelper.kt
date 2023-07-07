package com.jerboa.ui.components.common

import android.content.Context
import android.os.Build
import android.text.*
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.text.util.Linkify
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.annotation.IdRes
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import coil.ImageLoader
import com.jerboa.R
import com.jerboa.convertSpToPx
import com.jerboa.openLink
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonPlugin
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.SpannableBuilder
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.core.CorePlugin.OnTextAddedListener
import io.noties.markwon.core.CoreProps
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TableAwareMovementMethod
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.AsyncDrawableScheduler
import io.noties.markwon.image.AsyncDrawableSpan
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import io.noties.markwon.movement.MovementMethodPlugin
import org.commonmark.node.Link
import java.util.regex.Pattern

/**
 * pattern that matches all valid communities; intended to be loose
 */
const val communityPatternFragment: String = """[a-zA-Z0-9_]{3,}"""

/**
 * pattern to match all valid instances
 */
const val instancePatternFragment: String =
    """([a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]\.)+[a-zA-Z]{2,}"""

/**
 * pattern to match all valid usernames
 */
const val userPatternFragment: String = """[a-zA-Z0-9_]{3,}"""

/**
 * Pattern to match lemmy's unique community pattern, e.g. !commmunity[@instance]
 */
val lemmyCommunityPattern: Pattern =
    Pattern.compile("(?<!\\S)!($communityPatternFragment)(?:@($instancePatternFragment))?\\b")

/**
 * Pattern to match lemmy's unique user pattern, e.g. @user[@instance]
 */
val lemmyUserPattern: Pattern =
    Pattern.compile("(?<!\\S)@($userPatternFragment)(?:@($instancePatternFragment))?\\b")

/**
 * Plugin to turn Lemmy-specific URIs into clickable links.
 */
class LemmyLinkPlugin : AbstractMarkwonPlugin() {
    override fun configure(registry: MarkwonPlugin.Registry) {
        registry.require(CorePlugin::class.java) { it.addOnTextAddedListener(LemmyTextAddedListener()) }
    }

    private class LemmyTextAddedListener : OnTextAddedListener {
        override fun onTextAdded(visitor: MarkwonVisitor, text: String, start: Int) {
            // we will be using the link that is used by markdown (instead of directly applying URLSpan)
            val spanFactory = visitor.configuration().spansFactory().get(
                Link::class.java,
            ) ?: return

            // don't re-use builder (thread safety achieved for
            // render calls from different threads and ... better performance)
            val builder = SpannableStringBuilder(text)
            if (addLinks(builder)) {
                // target URL span specifically
                val spans = builder.getSpans(0, builder.length, URLSpan::class.java)
                if (!spans.isNullOrEmpty()) {
                    val renderProps = visitor.renderProps()
                    val spannableBuilder = visitor.builder()
                    for (span in spans) {
                        CoreProps.LINK_DESTINATION[renderProps] = span.url
                        SpannableBuilder.setSpans(
                            spannableBuilder,
                            spanFactory.getSpans(visitor.configuration(), renderProps),
                            start + builder.getSpanStart(span),
                            start + builder.getSpanEnd(span),
                        )
                    }
                }
            }
        }

        fun addLinks(text: Spannable): Boolean {
            val communityLinkAdded = Linkify.addLinks(text, lemmyCommunityPattern, null)
            val userLinkAdded = Linkify.addLinks(text, lemmyUserPattern, null)

            return communityLinkAdded || userLinkAdded
        }
    }
}

data class SpoilerTitleSpan(val title: CharSequence)
class SpoilerCloseSpan()
class SpoilerPlugin : AbstractMarkwonPlugin() {

    override fun configure(registry: MarkwonPlugin.Registry) {
        registry.require(CorePlugin::class.java) { it.addOnTextAddedListener(SpoilerTextAddedListener()) }
    }

    private class SpoilerTextAddedListener : OnTextAddedListener {
        override fun onTextAdded(visitor: MarkwonVisitor, text: String, start: Int) {
            val spoilerTitleRegex = Regex("(:::\\s*spoiler\\s*)(.*)")
            // Find all spoiler "start" lines
            val spoilerTitles = spoilerTitleRegex.findAll(text)

            for (match in spoilerTitles) {
                val spoilerTitle = match.groups[2]!!.value
                visitor.builder().setSpan(SpoilerTitleSpan(spoilerTitle), start, start + match.groups[2]!!.range.last, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            val spoilerCloseRegex = Regex("^(?!.*spoiler).*:::")
            // Find all spoiler "end" lines
            val spoilerCloses = spoilerCloseRegex.findAll(text)
            for (match in spoilerCloses) {
                visitor.builder().setSpan(SpoilerCloseSpan(), start, start + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    override fun afterSetText(textView: TextView) {
        try {
            val spanned = SpannableStringBuilder(textView.text)
            val spoilerTitleSpans = spanned.getSpans(0, spanned.length, SpoilerTitleSpan::class.java)
            val spoilerCloseSpans = spanned.getSpans(0, spanned.length, SpoilerCloseSpan::class.java)

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
                val getSpoilerTitle = { openParam: Boolean ->
                    if (openParam) "▼ ${spoilerTitleSpan.title}" else "▶ ${spoilerTitleSpan.title}"
                }

                val spoilerTitle = getSpoilerTitle(false)

                val spoilerContent = spanned.subSequence(spanned.getSpanEnd(spoilerTitleSpan) + 1, spoilerEnd - 3) as SpannableStringBuilder

                // Remove spoiler content from span
                spanned.replace(spoilerStart, spoilerEnd, spoilerTitle)
                // Set span block title
                spanned.setSpan(spoilerTitle, spoilerStart, spoilerStart + spoilerTitle.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                val wrapper = object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        open = !open

                        spanned.replace(spoilerStart, spoilerStart + spoilerTitle.length, getSpoilerTitle(open))
                        if (open) {
                            spanned.insert(spoilerStart + spoilerTitle.length, spoilerContent)
                        } else {
                            spanned.replace(spoilerStart + spoilerTitle.length, spoilerStart + spoilerTitle.length + spoilerContent.length, "")
                        }

                        textView.text = spanned
                        AsyncDrawableScheduler.schedule(textView)
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

object MarkdownHelper {
    private var markwon: Markwon? = null

    fun init(navController: NavController, useCustomTabs: Boolean, usePrivateTabs: Boolean) {
        val context = navController.context
        val loader = ImageLoader.Builder(context)
            .crossfade(true)
            .placeholder(R.drawable.ic_launcher_foreground)
            .build()

        markwon = Markwon.builder(context)
            .usePlugin(CoilImagesPlugin.create(context, loader))
            // email urls interfere with lemmy links
            .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS))
            .usePlugin(LemmyLinkPlugin())
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TablePlugin.create(context))
            // use TableAwareLinkMovementMethod to handle clicks inside tables,
            // wraps LinkMovementMethod internally
            .usePlugin(MovementMethodPlugin.create(TableAwareMovementMethod.create()))
            .usePlugin(HtmlPlugin.create())
            .usePlugin(SpoilerPlugin())
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                    builder.linkResolver { _, link ->
                        openLink(link, navController, useCustomTabs, usePrivateTabs)
                    }
                }
            })
            .build()
    }

    /*
     * This is a workaround for previews.
     */
    fun init(context: Context) {
        markwon = Markwon.builder(context).build()
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun CreateMarkdownView(
        markdown: String,
        color: Color = Color.Unspecified,
        onClick: (() -> Unit)? = null,
        onLongClick: (() -> Unit)? = null,
    ) {
        val style = MaterialTheme.typography.bodyLarge
        val defaultColor: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)

        BoxWithConstraints {
            val canvasWidthMaybe = with(LocalDensity.current) { maxWidth.toPx() }.toInt()
            val textSizeMaybe = with(LocalDensity.current) { style.fontSize.toPx() }

            AndroidView(
                factory = { ctx ->
                    createTextView(
                        context = ctx,
                        color = color,
                        defaultColor = defaultColor,
                        fontSize = TextUnit.Unspecified,
                        style = style,
                        viewId = null,
                        onClick = onClick,
                        onLongClick = onLongClick,
                    )
                },
                update = { textView ->
                    val md = markwon!!.toMarkdown(markdown)
                    for (img in md.getSpans(0, md.length, AsyncDrawableSpan::class.java)) {
                        img.drawable.initWithKnownDimensions(canvasWidthMaybe, textSizeMaybe)
                    }
                    markwon!!.setParsedMarkdown(textView, md)
                    //            if (disableLinkMovementMethod) {
                    //                textView.movementMethod = null
                    //            }
                },
                onReset = {},
            )
        }
    }

    private fun createTextView(
        context: Context,
        color: Color = Color.Unspecified,
        defaultColor: Color,
        fontSize: TextUnit = TextUnit.Unspecified,
        textAlign: TextAlign? = null,
        maxLines: Int = Int.MAX_VALUE,
        @FontRes fontResource: Int? = null,
        style: TextStyle,
        @IdRes viewId: Int? = null,
        onClick: (() -> Unit)? = null,
        onLongClick: (() -> Unit)? = null,
    ): TextView {
        val textColor = color.takeOrElse { style.color.takeOrElse { defaultColor } }
        val mergedStyle = style.merge(
            TextStyle(
                color = textColor,
                fontSize = if (fontSize != TextUnit.Unspecified) fontSize else style.fontSize,
                textAlign = textAlign,
            ),
        )
        return TextView(context).apply {
            onClick?.let { setOnClickListener { onClick() } }
            onLongClick?.let { setOnLongClickListener { onLongClick(); true } }
            setTextColor(textColor.toArgb())
            setMaxLines(maxLines)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mergedStyle.fontSize.value)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                setLineHeight(convertSpToPx(mergedStyle.lineHeight, context))
            }
            width = maxWidth

            viewId?.let { id = viewId }
            textAlign?.let { align ->
                textAlignment = when (align) {
                    TextAlign.Left, TextAlign.Start -> View.TEXT_ALIGNMENT_TEXT_START
                    TextAlign.Right, TextAlign.End -> View.TEXT_ALIGNMENT_TEXT_END
                    TextAlign.Center -> View.TEXT_ALIGNMENT_CENTER
                    else -> View.TEXT_ALIGNMENT_TEXT_START
                }
            }

            fontResource?.let { font ->
                typeface = ResourcesCompat.getFont(context, font)
            }
        }
    }
}
