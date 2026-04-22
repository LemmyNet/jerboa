package com.jerboa.util.markwon

import android.text.style.URLSpan
import com.jerboa.toHttps
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.image.ImageProps
import org.commonmark.node.Image

/**
 * Renders markdown `![alt](url)` images as plain clickable links using the
 * alt text as the visible label. Use this instead of a real image-loading
 * plugin to avoid network traffic while keeping the link reachable.
 */
class LinkOnlyImagesPlugin : AbstractMarkwonPlugin() {
    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
        builder.setFactory(Image::class.java) { _, props ->
            URLSpan(ImageProps.DESTINATION.require(props).toHttps())
        }
    }
}
