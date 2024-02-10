package com.jerboa.util.markwon

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.URLSpan
import android.text.util.Linkify
import com.jerboa.ui.components.common.lemmyCommunityPattern
import com.jerboa.ui.components.common.lemmyUserPattern
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonPlugin
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.SpannableBuilder
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.core.CoreProps
import org.commonmark.node.Link

/**
 * Plugin to turn Lemmy-specific URIs into clickable links.
 */
class MarkwonLemmyLinkPlugin : AbstractMarkwonPlugin() {
    override fun configure(registry: MarkwonPlugin.Registry) {
        registry.require(CorePlugin::class.java) { it.addOnTextAddedListener(LemmyTextAddedListener()) }
    }

    private class LemmyTextAddedListener : CorePlugin.OnTextAddedListener {
        override fun onTextAdded(
            visitor: MarkwonVisitor,
            text: String,
            start: Int,
        ) {
            // we will be using the link that is used by markdown (instead of directly applying URLSpan)
            val spanFactory =
                visitor.configuration().spansFactory().get(
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
