package com.jerboa.model

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jerboa.db.entity.AppSettings
import com.jerboa.db.repository.AppSettingsRepository
import com.jerboa.jerboaApplication
import kotlinx.coroutines.launch

@Stable
class AppSettingsViewModel(
    private val repository: AppSettingsRepository,
) : ViewModel() {
    val appSettings = repository.appSettings
    var changelog by mutableStateOf("")

    fun update(appSettings: AppSettings) =
        viewModelScope.launch {
            repository.update(appSettings)
        }

    fun updateLastVersionCodeViewed(versionCode: Int) =
        viewModelScope.launch {
            repository.updateLastVersionCodeViewed(versionCode)
        }

    fun updatedPostViewMode(postViewMode: Int) =
        viewModelScope.launch {
            repository.updatePostViewMode(postViewMode)
        }

    fun loadChangelog(ctx: Context) =
        viewModelScope.launch {
            try {
                Log.d("jerboa", "Getting RELEASES.md from assets...")
                changelog = ctx.assets
                    .open("RELEASES.md")
                    .bufferedReader()
                    .use { it.readText() }
            } catch (e: Exception) {
                Log.e("jerboa", "Failed to load changelog: $e")
            }
        }
}

object AppSettingsViewModelFactory {
    val Factory =
        viewModelFactory {
            initializer {
                AppSettingsViewModel(jerboaApplication().container.appSettingsRepository)
            }
        }
}
