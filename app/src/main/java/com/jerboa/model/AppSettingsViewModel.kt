package com.jerboa.model

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jerboa.db.entity.AppSettings
import com.jerboa.db.repository.AppSettingsRepository
import com.jerboa.jerboaApplication
import kotlinx.coroutines.launch

@Stable
class AppSettingsViewModel(private val repository: AppSettingsRepository) : ViewModel() {
    val appSettings = repository.appSettings
    val changelog = repository.changelog

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

    fun updateChangelog(ctx: Context) =
        viewModelScope.launch {
            repository.updateChangelog(ctx)
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
