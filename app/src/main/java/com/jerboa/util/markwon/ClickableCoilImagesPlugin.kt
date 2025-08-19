// CoilImagesPlugin has package private constructor, so this is a work around.
package io.noties.markwon.image.coil

import android.content.Context
import android.text.style.URLSpan
import android.view.View
import coil.ImageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import com.jerboa.JerboaAppState
import com.jerboa.toHttps
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.RenderProps
import io.noties.markwon.SpanFactory
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.AsyncDrawableSpan
import io.noties.markwon.image.ImageProps
import io.noties.markwon.image.ImageSpanFactory
import org.commonmark.node.Image

class ClickableCoilImagesPlugin(
    coil: CoilStore,
    imageLoader: ImageLoader,
    private val appState: JerboaAppState,
) : CoilImagesPlugin(
        coil,
        imageLoader,
    ) {
    companion object {
        fun create(
            context: Context,
            imageLoader: ImageLoader,
            appState: JerboaAppState,
        ): ClickableCoilImagesPlugin =
            create(
                object : CoilStore {
                    override fun load(drawable: AsyncDrawable): ImageRequest =
                        ImageRequest
                            .Builder(context)
                            .data(drawable.destination)
                            .build()

                    override fun cancel(disposable: Disposable) {
                        disposable.dispose()
                    }
                },
                imageLoader,
                appState,
            )

        fun create(
            coilStore: CoilStore,
            imageLoader: ImageLoader,
            appState: JerboaAppState,
        ): ClickableCoilImagesPlugin = ClickableCoilImagesPlugin(coilStore, imageLoader, appState)
    }

    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
        builder.setFactory(Image::class.java, ClickableImageFactory(appState))
    }
}

internal class ClickableImageFactory(
    val appState: JerboaAppState,
) : HttpsImageSpanFactory() {
    override fun getSpans(
        configuration: MarkwonConfiguration,
        props: RenderProps,
    ): Any {
        val image = super.getSpans(configuration, props) as AsyncDrawableSpan
        val clickSpan =
            object : URLSpan(image.drawable.destination) {
                override fun onClick(view: View) {
                    view.cancelPendingInputEvents()
                    appState.openMediaViewer(image.drawable.destination)
                }
            }

        return arrayOf<Any>(image, clickSpan)
    }
}

/**
 * Custom implementation of [ImageSpanFactory] that rewrites all http:// links to https://
 */
internal open class HttpsImageSpanFactory : SpanFactory {
    override fun getSpans(
        configuration: MarkwonConfiguration,
        props: RenderProps,
    ): Any? =
        AsyncDrawableSpan(
            configuration.theme(),
            AsyncDrawable(
                ImageProps.DESTINATION.require(props).toHttps(),
                configuration.asyncDrawableLoader(),
                configuration.imageSizeResolver(),
                ImageProps.IMAGE_SIZE[props],
            ),
            AsyncDrawableSpan.ALIGN_BOTTOM,
            ImageProps.REPLACEMENT_TEXT_IS_LINK[props, false],
        )
}
