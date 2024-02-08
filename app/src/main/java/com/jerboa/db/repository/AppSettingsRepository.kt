package com.jerboa.db.repository

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import com.jerboa.db.dao.AppSettingsDao
import com.jerboa.db.entity.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class AppSettingsRepository(
    private val appSettingsDao: AppSettingsDao,
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
    suspend fun updateLastVersionCodeViewed(versionCode: Int) {
        appSettingsDao.updateLastVersionCode(versionCode)
    }

    @WorkerThread
    suspend fun updatePostViewMode(postViewMode: Int) {
        appSettingsDao.updatePostViewMode(postViewMode)
    }

    @WorkerThread
    suspend fun updateChangelog(ctx: Context) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("jerboa", "Getting RELEASES.md from assets...")
                val releasesStr = ctx.assets.open("RELEASES.md").bufferedReader().use { it.readText() }
                _changelog.value = releasesStr
            } catch (e: Exception) {
                Log.e("jerboa", "Failed to load changelog: $e")
            }
        }
    }
}
