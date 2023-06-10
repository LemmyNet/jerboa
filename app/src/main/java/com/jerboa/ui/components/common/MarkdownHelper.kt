package com.jerboa.ui.components.common

import android.content.Context
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
import coil.ImageLoader
import com.jerboa.R
import com.jerboa.openLink
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin

object MarkdownHelper {
    private var markwon: Markwon? = null

    fun init(context: Context, useCustomTabs: Boolean) {
        val loader = ImageLoader.Builder(context)
            .crossfade(true)
            .placeholder(R.drawable.ic_launcher_foreground)
            .build()

        markwon = Markwon.builder(context)
            .usePlugin(CoilImagesPlugin.create(context, loader))
            .usePlugin(LinkifyPlugin.create())
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                    builder.linkResolver { view, link ->
                        link?.let { openLink(link, view.context, useCustomTabs) }
                    }
                }
            })
            .build()
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
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, mergedStyle.fontSize.value)
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
