package com.jerboa

import android.app.Application
import android.os.Build
import android.os.StrictMode
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
    lateinit var imageViewerLoader: ImageLoader
    private lateinit var imageLoader: ImageLoader
    lateinit var imageGifLoader: ImageLoader

    override fun onCreate() {
        super.onCreate()

        try {
            Class.forName("dalvik.system.CloseGuard")
                .getMethod("setEnabled", Boolean::class.javaPrimitiveType)
                .invoke(null, true)
        } catch (e: ReflectiveOperationException) {
            throw RuntimeException(e)
        }
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .detectLeakedClosableObjects()
                .detectContentUriWithoutPermission()
                .detectCleartextNetwork()
                .detectFileUriExposure()
                .detectLeakedRegistrationObjects()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .build())
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .permitDiskReads()
                .penaltyLog()
                .build())

        container = AppDBContainer(this)
        imageLoader =
            ImageLoader.Builder(this)
                .okHttpClient(API.httpClient)
                .crossfade(true)
                .error(R.drawable.error_placeholder)
                .placeholder(R.drawable.ic_launcher_foreground)
                .components {
                    add(SvgDecoder.Factory())
                }
                .build()

        imageGifLoader =
            imageLoader.newBuilder()
                .components {
                    if (Build.VERSION.SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .build()

        imageViewerLoader =
            imageGifLoader.newBuilder()
                .okHttpClient(DownloadProgress.downloadProgressHttpClient)
                .build()
    }

    override fun newImageLoader(): ImageLoader = imageLoader
}
