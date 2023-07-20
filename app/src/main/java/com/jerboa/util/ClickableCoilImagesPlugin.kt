// CoilImagesPlugin has package private constructor, so this is a work around.
package io.noties.markwon.image.coil

import android.content.Context
import android.text.style.ClickableSpan
import android.view.View
import coil.ImageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import com.jerboa.JerboaAppState
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.RenderProps
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.AsyncDrawableSpan
import io.noties.markwon.image.ImageSpanFactory
import org.commonmark.node.Image

class ClickableCoilImagesPlugin(coil: CoilStore, imageLoader: ImageLoader, private val appState: JerboaAppState) : CoilImagesPlugin(coil, imageLoader) {

    companion object {
        fun create(
            context: Context,
            imageLoader: ImageLoader,
            appState: JerboaAppState,
        ): ClickableCoilImagesPlugin {
            return create(
                object : CoilStore {
                    override fun load(drawable: AsyncDrawable): ImageRequest {
                        return ImageRequest.Builder(context)
                            .data(drawable.destination)
                            .build()
                    }

                    override fun cancel(disposable: Disposable) {
                        disposable.dispose()
                    }
                },
                imageLoader,
                appState,
            )
        }

        fun create(
            coilStore: CoilStore,
            imageLoader: ImageLoader,
            appState: JerboaAppState,
        ): ClickableCoilImagesPlugin {
            return ClickableCoilImagesPlugin(coilStore, imageLoader, appState)
        }
    }

    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
        builder.setFactory(Image::class.java, ClickableImageFactory(appState))
    }
}

class ClickableImageFactory(val appState: JerboaAppState) : ImageSpanFactory() {

    override fun getSpans(configuration: MarkwonConfiguration, props: RenderProps): Any {
        val image = super.getSpans(configuration, props) as AsyncDrawableSpan
        val clickSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                view.cancelPendingInputEvents()
                appState.toView(image.drawable.destination)
            }
        }

        return arrayOf(image, clickSpan)
    }
}
