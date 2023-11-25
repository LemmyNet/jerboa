package com.jerboa.api

import android.content.Context
import android.util.Log
import com.jerboa.DEFAULT_LEMMY_INSTANCES
import com.jerboa.toastException
import it.vercruysse.lemmyapi.LemmyApi
import it.vercruysse.lemmyapi.pictrs.datatypes.UploadImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.TimeUnit
import it.vercruysse.lemmyapi.v0x19.LemmyApi as LemmyApiV19

// TODO, regressed functionality: -> logging redactions
// Remove global error handler? do we need this anymore?
// httpAgent is now LemmyApi, not sure how to make this configurable
// Timeout is 20s, not 30s not configurable, need to figure this out
// Reuse httpClient

const val DEFAULT_INSTANCE = "lemmy.ml"

object API {
    // Kinda crucial that newApi is initialized before we do anything with it
    // Example even before those parseUrl util calls
    private lateinit var newApi: LemmyApiV19

    private val TEMP_RECOGNISED_AS_LEMMY_INSTANCES = mutableSetOf<String>()
    private val TEMP_NOT_RECOGNISED_AS_LEMMY_INSTANCES = mutableSetOf<String>()

    // TODO make this to be passed to LemmyApi
    val httpClient: OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addNetworkInterceptor { chain ->
                chain.request().newBuilder()
                    .header("User-Agent", "Jerboa")
                    .build()
                    .let(chain::proceed)
            }
            .build()

    suspend fun getInstance(): LemmyApiV19 {
        if (!::newApi.isInitialized) {
            // SHOULD NOT HAPPEN
            Log.e("Jerboa", "API.getInstance() called before API.setLemmyInstance()")
            newApi = LemmyApi.getLemmyApi(DEFAULT_INSTANCE)
        }
        return newApi
    }

    // This use is discouraged, use getInstance() as much as possible
    fun getInstanceOrNull(): LemmyApiV19? {
        return if (::newApi.isInitialized) {
            newApi
        } else {
            null
        }
    }

    suspend fun setLemmyInstance(
        instance: String,
        auth: String? = null,
    ): LemmyApiV19 {
        newApi = LemmyApi.getLemmyApi(instance, auth)
        return newApi
    }

    fun setLemmyInstance(api: LemmyApiV19) {
        newApi = api
    }

    suspend fun createTempInstance(
        host: String,
        auth: String? = null,
    ): LemmyApiV19 {
        return LemmyApi.getLemmyApi(host, auth)
    }

    fun createTempInstanceVersion(
        host: String,
        version: String,
    ): LemmyApiV19 {
        return LemmyApi.getLemmyApi(host, version)
    }

    suspend fun checkIfLemmyInstance(url: String): Boolean {
        try {
            val host = URL(url).host

            if (DEFAULT_LEMMY_INSTANCES.contains(host) || TEMP_RECOGNISED_AS_LEMMY_INSTANCES.contains(host)) {
                return true
            } else if (TEMP_NOT_RECOGNISED_AS_LEMMY_INSTANCES.contains(host)) {
                return false
            } else {
                return withContext(Dispatchers.IO) {
                    return@withContext if (LemmyApi.isLemmyInstance(url)) {
                        TEMP_RECOGNISED_AS_LEMMY_INSTANCES.add(host)
                        true
                    } else {
                        TEMP_NOT_RECOGNISED_AS_LEMMY_INSTANCES.add(host)
                        false
                    }
                }
            }
        } catch (_: MalformedURLException) {
            return false
        }
    }

    suspend fun uploadPictrsImage(
        imageIs: InputStream,
        ctx: Context,
    ): String {
        Log.d("jerboa", "Uploading image....")
        val resp = getInstance().uploadImage(UploadImage(listOf(imageIs.readBytes())))
        Log.d("jerboa", "Uploading done.")

        // TODO could be empty
        return resp.fold(
            onSuccess = { it.files[0].url.orEmpty() },
            onFailure = {
                toastException(ctx, it as? Exception ?: Exception("Unknown error"))
                ""
            },
        )
    }
}

sealed class ApiState<out T> {
    abstract class Holder<T>(val data: T) : ApiState<T>()

    class Success<T>(data: T) : Holder<T>(data)

    class Appending<T>(data: T) : Holder<T>(data)

    class AppendingFailure<T>(data: T) : Holder<T>(data)

    class Failure(val msg: Throwable) : ApiState<Nothing>()

    data object Loading : ApiState<Nothing>()

    data object Refreshing : ApiState<Nothing>()

    data object Empty : ApiState<Nothing>()
}

fun <T> Result<T>.toApiState(): ApiState<T> {
    return this.fold(
        onSuccess = { ApiState.Success(it) },
        onFailure = { ApiState.Failure(it) },
    )
}
