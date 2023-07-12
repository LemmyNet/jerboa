package com.jerboa.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.jerboa.db.entity.AppSettings

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM AppSettings limit 1")
    fun getSettings(): LiveData<AppSettings>

    @Update
    suspend fun updateAppSettings(appSettings: AppSettings)

    @Query("UPDATE AppSettings set viewed_changelog = 1")
    suspend fun markChangelogViewed()

    @Query("UPDATE AppSettings set post_view_mode = :postViewMode")
    suspend fun updatePostViewMode(postViewMode: Int)
}
