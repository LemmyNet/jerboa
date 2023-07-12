package com.jerboa.db.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.jerboa.db.dao.AppSettingsDao
import com.jerboa.db.entity.AppSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class AppSettingsRepository(
    private val appSettingsDao: AppSettingsDao,
    private val httpClient: OkHttpClient = OkHttpClient(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    private val _changelog = MutableStateFlow("")
    val changelog = _changelog.asStateFlow()

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val appSettings = appSettingsDao.getSettings()

    @WorkerThread
    suspend fun update(appSettings: AppSettings) {
        appSettingsDao.updateAppSettings(appSettings)
    }

    @WorkerThread
    suspend fun markChangelogViewed() {
        appSettingsDao.markChangelogViewed()
    }

    @WorkerThread
    suspend fun updatePostViewMode(postViewMode: Int) {
        appSettingsDao.updatePostViewMode(postViewMode)
    }

    @WorkerThread
    suspend fun updateChangelog() {
        withContext(ioDispatcher) {
            try {
                Log.d("jerboa", "Fetching RELEASES.md ...")
                // Fetch the markdown text
                val releasesUrl =
                    "https://raw.githubusercontent.com/dessalines/jerboa/main/RELEASES.md".toHttpUrl()
                val req = Request.Builder().url(releasesUrl).build()
                val res = httpClient.newCall(req).execute()
                _changelog.value = res.body.string()
            } catch (e: Exception) {
                Log.e("jerboa", "Failed to load changelog: $e")
            }
        }
    }
}
