package com.jerboa.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jerboa.db.dao.AccountDao
import com.jerboa.db.dao.AppSettingsDao
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AppSettings
import java.util.concurrent.Executors

const val DEFAULT_FONT_SIZE = 16

val APP_SETTINGS_DEFAULT = AppSettings(
    id = 1,
    fontSize = DEFAULT_FONT_SIZE,
    theme = 0,
    themeColor = 0,
    viewedChangelog = 0,
    postViewMode = 0,
    showBottomNav = true,
    showCollapsedCommentContent = false,
    showCommentActionBarByDefault = true,
    showVotingArrowsInListView = true,
    showParentCommentNavigationButtons = false,
    navigateParentCommentsWithVolumeButtons = false,
    useCustomTabs = true,
    usePrivateTabs = false,
    secureWindow = false,
    blurNSFW = true,
    showTextDescriptionsInNavbar = true,
    backConfirmationMode = 1,
    markAsReadOnScroll = false,
    showPostLinkPreviews = true,
    postActionbarMode = 0,
    autoPlayGifs = false,
)

@Database(
    version = 25,
    entities = [Account::class, AppSettings::class],
    exportSchema = true,
)
abstract class AppDB : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun appSettingsDao(): AppSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDB? = null

        fun getDatabase(
            context: Context,
        ): AppDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDB::class.java,
                    "jerboa",
                )
                    .addMigrations(
                        *MIGRATIONS_LIST,
                    )
                    // Necessary because it can't insert data on creation
                    .addCallback(object : Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Executors.newSingleThreadExecutor().execute {
                                db.insert(
                                    "AppSettings",
                                    CONFLICT_IGNORE, // Ensures it won't overwrite the existing data
                                    ContentValues(2).apply {
                                        put("id", 1)
                                    },
                                )
                            }
                        }
                    }).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
