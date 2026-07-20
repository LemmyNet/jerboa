package com.jerboa.ui.components.common

import android.content.Context
import android.os.Build
import android.text.Spanned
import android.text.TextUtils
import android.text.util.Linkify
import android.util.LruCache
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
import com.jerboa.db.entity.AppSettings
import com.jerboa.db.entity.lowBandwidthMode
import com.jerboa.util.markwon.BetterLinkMovementMethod
import com.jerboa.util.markwon.ForceHttpsPlugin
import com.jerboa.util.markwon.LinkOnlyImagesPlugin
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

    /**
     * Caches the parsed [Spanned] result of a markdown string, keyed by [CachedMarkdown].
     *
     * This exists because items inside a LazyColumn are fully disposed and recreated (including
     * their underlying AndroidView) whenever they scroll out of and back into the viewport. Without
     * this cache, every re-entry into the viewport would re-parse the markdown from scratch and
     * recreate the image spans, causing embedded images to visibly pop out and back in on every
     * scroll pass. Reusing the same Spanned (and thus the same already-resolved image drawables)
     * across recreations avoids that.
     */
    private val parsedMarkdownCache = LruCache<String, CachedMarkdown>(500)

    private data class CachedMarkdown(
        val sourceText: String,
        val spanned: Spanned,
    )

    fun init(
        appState: JerboaAppState,
        appSettings: AppSettings,
        ctx: Context,
        onLongClick: BetterLinkMovementMethod.OnLinkLongClickListener,
    ) {
        val context = appState.navController.context
        val lowBandwidthMode = appSettings.lowBandwidthMode(ctx)
        val loader = context.imageLoader
        val imagesPlugin = if (lowBandwidthMode) {
            // Render `![alt](url)` as a plain clickable link — no network load.
            LinkOnlyImagesPlugin()
        } else {
            ClickableCoilImagesPlugin.create(context, loader, appState)
        }
        val htmlPlugin = if (lowBandwidthMode) {
            // HtmlPlugin's default `<img>` handler would still load images;
            // swap it for a no-op so inline HTML images are dropped.
            HtmlPlugin.create { plugin -> plugin.addHandler(TagHandlerNoOp.create("img")) }
        } else {
            HtmlPlugin.create()
        }

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
                .usePlugin(imagesPlugin)
                .usePlugin(htmlPlugin)
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
                                appState.openLink(
                                    url = link,
                                    useCustomTabs = appSettings.useCustomTabs,
                                    usePrivateTabs = appSettings.usePrivateTabs
                                )
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
        // Stable identifier (e.g. "${postId}_${commentId}") used to cache the parsed markdown
        // across recompositions/view recreations. Prefer a unique per-item id over falling back
        // to the markdown text: two different items can have identical text, and a shared Spanned
        // means sharing its AsyncDrawableSpan/AsyncDrawable image spans too. Android's
        // Drawable.setCallback() only tracks one callback at a time, so if two such items are on
        // screen simultaneously, only the most recently scheduled one reliably repaints when its
        // image loads - the other can show a stale/blank image. Falls back to the markdown text
        // itself when no id is available, which is still correct as long as that scenario can't occur.
        cacheKey: String? = null,
    ) {
        AndroidView(
            factory = { ctx -> createTextView(context = ctx) },
            update = { textView ->
                applyTextStyle(
                    textView = textView,
                    color = color,
                    style = style,
                )
                // Set on every recomposition (not just in `factory`), since with onReset
                // the underlying View may be pooled/reused for a different item. If we only
                // set these in `factory`, a reused View would keep firing the click handlers
                // of the item it was originally created for.
                textView.setOnClickListener(onClick?.let { click -> View.OnClickListener { click() } })
                textView.setOnLongClickListener(onLongClick)

                val parser = markwon!!
                val effectiveKey = cacheKey ?: markdown
                val cached = parsedMarkdownCache.get(effectiveKey)
                val md = if (cached != null && cached.sourceText == markdown) {
                    cached.spanned
                } else {
                    parser.toMarkdown(markdown).also {
                        parsedMarkdownCache.put(effectiveKey, CachedMarkdown(markdown, it))
                    }
                }
                for (img in md.getSpans(0, md.length, AsyncDrawableSpan::class.java)) {
                    img.drawable.initWithKnownDimensions(textView.width, textView.textSize)
                }
                parser.setParsedMarkdown(textView, md)
            },
            onReset = { textView ->
                textView.setOnClickListener(null)
                textView.setOnLongClickListener(null)
                textView.text = null
            },
            modifier = modifier,
        )
    }

    private fun createTextView(context: Context): TextView =
        TextView(context).apply {
            width = maxWidth
        }

    private fun applyTextStyle(
        textView: TextView,
        color: Color,
        textAlign: TextAlign? = null,
        @FontRes fontResource: Int? = null,
        style: TextStyle,
    ) {
        val context = textView.context
        val textColor = color.takeOrElse { style.color }
        val mergedStyle =
            style.merge(
                TextStyle(
                    color = textColor,
                    fontSize = style.fontSize,
                    textAlign = textAlign ?: TextAlign.Unspecified,
                ),
            )
        textView.apply {
            setTextColor(textColor.toArgb())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mergedStyle.fontSize.value)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                lineHeight = convertSpToPx(mergedStyle.lineHeight, context)
            }

            textAlign?.let { align ->
                textAlignment =
                    when (align) {
                        TextAlign.Left, TextAlign.Start -> View.TEXT_ALIGNMENT_TEXT_START
                        TextAlign.Right, TextAlign.End -> View.TEXT_ALIGNMENT_TEXT_END
                        TextAlign.Center -> View.TEXT_ALIGNMENT_CENTER
                        else -> View.TEXT_ALIGNMENT_TEXT_START
                    }
            }

            typeface = fontResource?.let { font -> ResourcesCompat.getFont(context, font) }
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
            factory = { ctx -> createTextViewPreview(context = ctx) },
            update = { textView ->
                applyTextStylePreview(
                    textView = textView,
                    color = color,
                    style = style,
                )
                // Set on every recomposition (not just in `factory`), since with onReset
                // the underlying View may be pooled/reused for a different item. If we only
                // set this in `factory`, a reused View would keep firing the click handler
                // of the item it was originally created for (e.g. opening the wrong post).
                textView.setOnClickListener(onClick?.let { click -> View.OnClickListener { click() } })
                previewMarkwon?.setMarkdown(textView, markdown)
            },
            onReset = { textView ->
                textView.setOnClickListener(null)
                textView.text = null
            },
            modifier = modifier,
        )
    }

    private fun createTextViewPreview(
        context: Context,
        maxLines: Int = 5,
    ): TextView =
        TextView(context).apply {
            width = maxWidth
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            this.movementMethod = null
            this.linksClickable = false
            ellipsize = TextUtils.TruncateAt.END
            setMaxLines(maxLines)
            focusable = NOT_FOCUSABLE
        }

    private fun applyTextStylePreview(
        textView: TextView,
        color: Color,
        style: TextStyle,
    ) {
        val context = textView.context
        val textColor = color.takeOrElse { style.color }
        val mergedStyle =
            style.merge(
                TextStyle(
                    color = textColor,
                    fontSize = style.fontSize,
                ),
            )
        textView.apply {
            setTextColor(textColor.toArgb())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mergedStyle.fontSize.value)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                lineHeight = convertSpToPx(mergedStyle.lineHeight, context)
            }
        }
    }
}
