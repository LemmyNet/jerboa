package com.jerboa

import android.app.Application
import android.os.Build
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import com.jerboa.api.API
import com.jerboa.db.AppDBContainer
import com.jerboa.util.downloadprogress.DownloadProgress

class JerboaApplication : Application(), ImageLoaderFactory {
    lateinit var container: AppDBContainer
    lateinit var imageViewLoader: ImageLoader
    lateinit var imageLoader: ImageLoader
    override fun onCreate() {
        super.onCreate()
        container = AppDBContainer(this)
        imageLoader = ImageLoader.Builder(this)
            .okHttpClient(API.httpClient)
            .crossfade(true)
            .placeholder(R.drawable.ic_launcher_foreground)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()

        imageViewLoader = imageLoader.newBuilder()
            //.okHttpClient(DownloadProgress.downloadProgressHttpClient)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    override fun newImageLoader(): ImageLoader = imageLoader
}
