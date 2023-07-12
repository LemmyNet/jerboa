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
import androidx.annotation.IdRes
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.jerboa.util.MarkwonLemmyLinkPlugin
import com.jerboa.util.MarkwonSpoilerPlugin
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TableAwareMovementMethod
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.html.TagHandlerNoOp
import io.noties.markwon.image.AsyncDrawableSpan
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import io.noties.markwon.movement.MovementMethodPlugin
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

object MarkdownHelper {
    private var markwon: Markwon? = null
    private var previewMarkwon: Markwon? = null

    fun init(navController: NavController, useCustomTabs: Boolean, usePrivateTabs: Boolean) {
        val context = navController.context
        val loader = ImageLoader.Builder(context)
            .crossfade(true)
            .placeholder(R.drawable.ic_launcher_foreground)
            .build()

        // main markdown parser has coil + html on
        markwon = Markwon.builder(context)
            // email urls interfere with lemmy links
            .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS))
            .usePlugin(MarkwonLemmyLinkPlugin())
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TablePlugin.create(context))
            .usePlugin(CoilImagesPlugin.create(context, loader))
            .usePlugin(HtmlPlugin.create())
            // use TableAwareLinkMovementMethod to handle clicks inside tables,
            // wraps LinkMovementMethod internally
            .usePlugin(MovementMethodPlugin.create(TableAwareMovementMethod.create()))
            .usePlugin(MarkwonSpoilerPlugin(true))
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                    builder.linkResolver { _, link ->
                        openLink(link, navController, useCustomTabs, usePrivateTabs)
                    }
                }
            })
            .build()

        // no image parser has html off
        previewMarkwon = Markwon.builder(context)
            // email urls interfere with lemmy links
            .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS))
            .usePlugin(MarkwonLemmyLinkPlugin())
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TablePlugin.create(context))
            .usePlugin(HtmlPlugin.create { plugin -> plugin.addHandler(TagHandlerNoOp.create("img")) })
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                    builder.linkResolver { _, _ -> }
                }
            })
            .usePlugin(MarkwonSpoilerPlugin(false))
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
        onLongClick: (() -> Unit)? = null,
        style: TextStyle = MaterialTheme.typography.bodyLarge,
    ) {
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
                },
                onReset = {},
                modifier = modifier,
            )
        }
    }

    private fun createTextView(
        context: Context,
        color: Color = Color.Unspecified,
        defaultColor: Color,
        fontSize: TextUnit = TextUnit.Unspecified,
        textAlign: TextAlign? = null,
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
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mergedStyle.fontSize.value)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                lineHeight = convertSpToPx(mergedStyle.lineHeight, context)
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

    @Composable
    fun CreateMarkdownPreview(
        markdown: String,
        modifier: Modifier = Modifier,
        color: Color = MaterialTheme.colorScheme.onSurface,
        onClick: (() -> Unit)? = null,
        style: TextStyle,
        defaultColor: Color,
    ) {
        AndroidView(
            factory = { ctx ->
                createTextViewPreview(
                    context = ctx,
                    color = color,
                    defaultColor = defaultColor,
                    fontSize = TextUnit.Unspecified,
                    style = style,
                    onClick = onClick,
                )
            },
            update = { textView ->
                previewMarkwon!!.setMarkdown(textView, markdown)
            },
            onReset = {},
            modifier = modifier,
        )
    }

    private fun createTextViewPreview(
        context: Context,
        color: Color = Color.Unspecified,
        defaultColor: Color,
        fontSize: TextUnit = TextUnit.Unspecified,
        maxLines: Int = 5,
        style: TextStyle,
        onClick: (() -> Unit)? = null,
    ): TextView {
        val textColor = color.takeOrElse { style.color.takeOrElse { defaultColor } }
        val mergedStyle = style.merge(
            TextStyle(
                color = textColor,
                fontSize = if (fontSize != TextUnit.Unspecified) fontSize else style.fontSize,
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
