package com.jerboa.util.markwon

import com.jerboa.toHttps
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.core.CoreProps
import io.noties.markwon.core.spans.LinkSpan
import org.commonmark.node.Link

/**
 * A markwon plugin that rewrites all http:// links to https://
 */

class ForceHttpsPlugin : AbstractMarkwonPlugin() {
    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
        builder.setFactory(
            Link::class.java,
        ) { configuration, props ->
            LinkSpan(
                configuration.theme(),
                CoreProps.LINK_DESTINATION.require(props).toHttps(),
                configuration.linkResolver(),
            )
        }
    }
}
