// CoilImagesPlugin has package private constructor, so this is a work around.
package io.noties.markwon.image.coil;

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.URLSpan
import coil.ImageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.RenderProps
import io.noties.markwon.SpannableBuilder
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.AsyncDrawableSpan
import io.noties.markwon.image.ImageProps
import io.noties.markwon.image.ImageSpanFactory
import org.commonmark.node.Image

class ClickableCoilImagesPlugin(coil: CoilStore, imageLoader: ImageLoader) : CoilImagesPlugin(coil, imageLoader ) {


    companion object {
        fun create(
            context: Context,
            imageLoader: ImageLoader
        ): ClickableCoilImagesPlugin {
            return create(object : CoilStore {
                override fun load(drawable: AsyncDrawable): ImageRequest {
                    return ImageRequest.Builder(context)
                        .data(drawable.destination)
                        .build()
                }

                override fun cancel(disposable: Disposable) {
                    disposable.dispose()
                }
            }, imageLoader)
        }

        fun create(
            coilStore: CoilStore,
            imageLoader: ImageLoader
        ): ClickableCoilImagesPlugin {
            return ClickableCoilImagesPlugin(coilStore, imageLoader)
        }
    }

    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
        builder.setFactory(Image::class.java, ClickableImageFactory())
    }
}

class ClickableImageFactory: ImageSpanFactory() {

    override fun getSpans(configuration: MarkwonConfiguration, props: RenderProps): Any {

//val k = SpannableBuilder()
//
//        val start = k.length
//        k.append("#\uFFFC")

        val s1 = super.getSpans(configuration, props) as AsyncDrawableSpan


        val s = URLSpan(s1.drawable.destination)

//        k.setSpan(s, start, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE )
//        k.setSpan(s1, 1, k.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        return arrayOf(s, s1)
    }
}
