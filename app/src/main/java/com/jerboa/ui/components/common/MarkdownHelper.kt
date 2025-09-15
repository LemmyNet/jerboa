package com.jerboa.ui.components.common

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.text.util.Linkify
import android.util.TypedValue
import android.view.View
import android.view.View.NOT_FOCUSABLE
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import coil.imageLoader
import com.jerboa.JerboaAppState
import com.jerboa.convertSpToPx
import com.jerboa.util.markwon.BetterLinkMovementMethod
import com.jerboa.util.markwon.ForceHttpsPlugin
import com.jerboa.util.markwon.MarkwonLemmyLinkPlugin
import com.jerboa.util.markwon.MarkwonSpoilerPlugin
import com.jerboa.util.markwon.ScriptRewriteSupportPlugin
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TableAwareMovementMethod
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.html.TagHandlerNoOp
import io.noties.markwon.image.AsyncDrawableSpan
import io.noties.markwon.image.coil.ClickableCoilImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import io.noties.markwon.movement.MovementMethodPlugin
import java.util.regex.Pattern

/**
 * pattern that matches all valid communities; intended to be loose
 */
const val COMMUNITY_PATTERN_FRAGMENT: String = """[a-zA-Z0-9_]{3,}"""

/**
 * pattern to match all valid instances
 */
const val INSTANCE_PATTERN_FRAGMENT: String =
    """([a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]\.)+[a-zA-Z]{2,}"""

/**
 * pattern to match all valid usernames
 */
const val USER_PATTERN_FRAGMENT: String = """[a-zA-Z0-9_]{3,}"""

/**
 * Pattern to match lemmy's unique community pattern, e.g. !commmunity[@instance]
 */
val lemmyCommunityPattern: Pattern =
    Pattern.compile("(?<!\\S)!($COMMUNITY_PATTERN_FRAGMENT)(?:@($INSTANCE_PATTERN_FRAGMENT))?\\b")

/**
 * Pattern to match lemmy's unique user pattern, e.g. @user[@instance]
 */
val lemmyUserPattern: Pattern =
    Pattern.compile("(?<!\\S)@($USER_PATTERN_FRAGMENT)(?:@($INSTANCE_PATTERN_FRAGMENT))?\\b")

object MarkdownHelper {
    private var markwon: Markwon? = null
    private var previewMarkwon: Markwon? = null

    fun init(
        appState: JerboaAppState,
        useCustomTabs: Boolean,
        usePrivateTabs: Boolean,
        onLongClick: BetterLinkMovementMethod.OnLinkLongClickListener,
    ) {
        val context = appState.navController.context
        val loader = context.imageLoader
        // main markdown parser has coil + html on
        markwon =
            Markwon
                .builder(context)
                .usePlugin(ForceHttpsPlugin())
                // email urls interfere with lemmy links
                .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS))
                .usePlugin(ScriptRewriteSupportPlugin())
                .usePlugin(MarkwonLemmyLinkPlugin())
                .usePlugin(MarkwonSpoilerPlugin(true))
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(TablePlugin.create(context))
                .usePlugin(ClickableCoilImagesPlugin.create(context, loader, appState))
                .usePlugin(HtmlPlugin.create())
                // use TableAwareLinkMovementMethod to handle clicks inside tables,
                // wraps LinkMovementMethod internally
                .usePlugin(
                    MovementMethodPlugin.create(
                        TableAwareMovementMethod(
                            BetterLinkMovementMethod.newInstance().setOnLinkLongClickListener(onLongClick),
                        ),
                    ),
                ).usePlugin(
                    object : AbstractMarkwonPlugin() {
                        override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                            builder.linkResolver { view, link ->
                                // Previously when openLink wasn't suspending it was somehow preventing the click from propagating
                                // Now it doesn't anymore and we have to do it manually
                                view.cancelPendingInputEvents()
                                appState.openLink(link, useCustomTabs, usePrivateTabs)
                            }
                        }
                    },
                ).build()

        // no image parser has html off
        previewMarkwon =
            Markwon
                .builder(context)
                // email urls interfere with lemmy links
                .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS))
                .usePlugin(MarkwonLemmyLinkPlugin())
                .usePlugin(ScriptRewriteSupportPlugin())
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(TablePlugin.create(context))
                .usePlugin(HtmlPlugin.create { plugin -> plugin.addHandler(TagHandlerNoOp.create("img")) })
                .usePlugin(
                    object : AbstractMarkwonPlugin() {
                        override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                            builder.linkResolver { _, _ -> }
                        }
                    },
                ).usePlugin(MarkwonSpoilerPlugin(false))
                .build()
    }

    /*
     * This is a workaround for previews.
     */
    fun init(context: Context) {
        markwon = Markwon.builder(context).build()
        previewMarkwon = Markwon.builder(context).build()
    }

    @Composable
    fun CreateMarkdownView(
        markdown: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        onClick: (() -> Unit)? = null,
        onLongClick: ((View) -> Boolean)? = null,
        style: TextStyle = MaterialTheme.typography.bodyLarge,
    ) {
        AndroidView(
            factory = { ctx ->
                createTextView(
                    context = ctx,
                    color = color,
                    style = style,
                    onClick = onClick,
                    onLongClick = onLongClick,
                )
            },
            update = { textView ->
                val md = markwon!!.toMarkdown(markdown)
                for (img in md.getSpans(0, md.length, AsyncDrawableSpan::class.java)) {
                    img.drawable.initWithKnownDimensions(textView.width, textView.textSize)
                }
                markwon!!.setParsedMarkdown(textView, md)
            },
            modifier = modifier,
        )
    }

    private fun createTextView(
        context: Context,
        color: Color = Color.Unspecified,
        textAlign: TextAlign? = null,
        @FontRes fontResource: Int? = null,
        style: TextStyle,
        onClick: (() -> Unit)? = null,
        onLongClick: ((View) -> Boolean)? = null,
    ): TextView {
        val textColor = color.takeOrElse { style.color }
        val mergedStyle =
            style.merge(
                TextStyle(
                    color = textColor,
                    fontSize = style.fontSize,
                    textAlign = textAlign ?: TextAlign.Unspecified,
                ),
            )
        return TextView(context).apply {
            onClick?.let { setOnClickListener { onClick() } }
            onLongClick?.let { setOnLongClickListener(it) }
            setTextColor(textColor.toArgb())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mergedStyle.fontSize.value)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                lineHeight = convertSpToPx(mergedStyle.lineHeight, context)
            }
            width = maxWidth

            textAlign?.let { align ->
                textAlignment =
                    when (align) {
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

    @Composable
    fun CreateMarkdownPreview(
        markdown: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        onClick: (() -> Unit)? = null,
        style: TextStyle,
    ) {
        AndroidView(
            factory = { ctx ->
                createTextViewPreview(
                    context = ctx,
                    color = color,
                    style = style,
                    onClick = onClick,
                )
            },
            update = { textView ->
                previewMarkwon?.setMarkdown(textView, markdown)
            },
            modifier = modifier,
        )
    }

    private fun createTextViewPreview(
        context: Context,
        color: Color = Color.Unspecified,
        maxLines: Int = 5,
        style: TextStyle,
        onClick: (() -> Unit)? = null,
    ): TextView {
        val textColor = color.takeOrElse { style.color }
        val mergedStyle =
            style.merge(
                TextStyle(
                    color = textColor,
                    fontSize = style.fontSize,
                ),
            )
        return TextView(context).apply {
            onClick?.let { setOnClickListener { onClick() } }
            setTextColor(textColor.toArgb())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mergedStyle.fontSize.value)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                lineHeight = convertSpToPx(mergedStyle.lineHeight, context)
            }
            width = maxWidth
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            this.movementMethod = null
            this.linksClickable = false
            ellipsize = TextUtils.TruncateAt.END
            setMaxLines(maxLines)
            focusable = NOT_FOCUSABLE
        }
    }
}
