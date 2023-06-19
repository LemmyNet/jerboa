package com.jerboa.ui.components.common

import android.content.Context
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.URLSpan
import android.text.util.Linkify
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.annotation.IdRes
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.toArgb
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
    Pattern.compile("(?:^|\\s)!($communityPatternFragment)(?:@($instancePatternFragment))?\\b")

/**
 * Pattern to match lemmy's unique user pattern, e.g. @user[@instance]
 */
val lemmyUserPattern: Pattern =
    Pattern.compile("(?:^|\\s)@($userPatternFragment)(?:@($instancePatternFragment))?\\b")

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

    @Composable
    fun CreateMarkdownView(
        markdown: String,
        color: Color = Color.Unspecified,
        onClick: (() -> Unit)? = null,
        onLongClick: (() -> Unit)? = null,
    ) {
        val style = MaterialTheme.typography.bodyLarge
        val defaultColor: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)

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
                markwon!!.setMarkdown(textView, markdown)
//            if (disableLinkMovementMethod) {
//                textView.movementMethod = null
//            }
            },
        )
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
