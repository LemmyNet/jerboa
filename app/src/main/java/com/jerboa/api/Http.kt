package com.jerboa.api

import android.content.Context
import android.net.TrafficStats
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import com.jerboa.DEFAULT_LEMMY_INSTANCES
import com.jerboa.toastException
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.logging.*
import io.ktor.http.HttpHeaders
import it.vercruysse.lemmyapi.LemmyApi
import it.vercruysse.lemmyapi.LemmyApiBaseController
import it.vercruysse.lemmyapi.pictrs.datatypes.UploadImage
import it.vercruysse.lemmyapi.setDefaultClientConfig
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.TimeUnit

const val DEFAULT_INSTANCE = "lemmy.ml"
const val DEFAULT_VERSION = "0.19.0"

object API {
    private val TEMP_RECOGNISED_AS_LEMMY_INSTANCES = mutableSetOf<String>()
    private val TEMP_NOT_RECOGNISED_AS_LEMMY_INSTANCES = mutableSetOf<String>()
    private val initialized = CompletableDeferred<Unit>()
    private lateinit var newApi: LemmyApiBaseController

    // Not super reliable if used during startup
    // But simplifies a lot of things
    var version: String = DEFAULT_VERSION

    // This allows verificationState to respond on failure of the api creation
    private val _apiFailState = MutableStateFlow(false)

    // TODO add check for this
    val apiFailState: StateFlow<Boolean> = _apiFailState

    val httpClient: OkHttpClient =
        OkHttpClient
            .Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addNetworkInterceptor { chain ->
                if (SDK_INT >= 36) {
                    TrafficStats.setThreadStatsTag(Thread.currentThread().threadId().toInt())
                } else {
                    TrafficStats.setThreadStatsTag(Thread.currentThread().id.toInt())
                }

                chain
                    .request()
                    .newBuilder()
                    .header("User-Agent", "Jerboa")
                    .build()
                    .let(chain::proceed)
            }.build()

    init {
        LemmyApi.setDefaultClientConfig {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("LemmyAPI", message)
                    }
                }
                level = LogLevel.ALL
                sanitizeHeader { header -> header == HttpHeaders.Authorization }
            }

            install(UserAgent) {
                agent = "Jerboa"
            }

            engine {
                preconfigured = httpClient
            }
        }
    }

    suspend fun getInstance(): LemmyApiBaseController {
        initialized.await()
        return newApi
    }

    // This use is discouraged, use getInstance() as much as possible
    fun getInstanceOrNull(): LemmyApiBaseController? =
        if (::newApi.isInitialized) {
            newApi
        } else {
            null
        }

    /**
     * This is a safe way to set the lemmy instance,
     * Use this when we know the instance is valid
     *
     * It fallbacks to a default if the instantiation fails
     */
    suspend fun setLemmyInstanceSafe(
        instance: String,
        auth: String? = null,
        overrideVersion: String = DEFAULT_VERSION,
    ) {
        try {
            setLemmyInstance(instance, auth)
        } catch (e: Throwable) {
            Log.i("setLemmyInstanceSafe", "Failed to set lemmy instance", e)
            _apiFailState.value = true
            setLemmyInstance(createTempInstanceVersion(instance, overrideVersion, auth))
        }
    }

    suspend fun setLemmyInstance(
        instance: String,
        auth: String? = null,
    ): LemmyApiBaseController {
        setLemmyInstance(LemmyApi.getLemmyApi(instance, auth))
        return newApi
    }

    fun setLemmyInstance(api: LemmyApiBaseController) {
        Log.d("setLemmyInstance", "Setting lemmy instance to ${api.baseUrl}")
        version = api.version.toString()
        newApi = api
        initialized.complete(Unit)
    }

    suspend fun createTempInstanceSafe(
        host: String,
        auth: String? = null,
        overrideVersion: String = DEFAULT_VERSION,
    ): LemmyApiBaseController =
        try {
            createTempInstance(host, auth)
        } catch (e: Throwable) {
            Log.i("createTempInstanceSafe", "Failed to set lemmy instance", e)
            createTempInstanceVersion(host, overrideVersion, auth)
        }

    suspend fun createTempInstance(
        host: String,
        auth: String? = null,
    ): LemmyApiBaseController = LemmyApi.getLemmyApi(host, auth)

    fun createTempInstanceVersion(
        host: String,
        version: String,
        auth: String? = null,
    ): LemmyApiBaseController = LemmyApi.getLemmyApi(host, version, auth)

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

        return resp.fold(
            onSuccess = {
                if (it.files.isEmpty()) {
                    toastException(ctx, Exception("Upload image failed"))
                    return ""
                } else {
                    it.files[0].url.orEmpty()
                }
            },
            onFailure = {
                toastException(ctx, it as? Exception ?: Exception("Unknown error"))
                ""
            },
        )
    }

    fun apiFailureHandled() {
        _apiFailState.value = false
    }
}

fun <T> Result<T>.toApiState(): ApiState<T> =
    this.fold(
        onSuccess = { ApiState.Success(it) },
        onFailure = { ApiState.Failure(it) },
    )
