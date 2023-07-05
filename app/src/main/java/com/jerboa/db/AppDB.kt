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
)

@Database(
    version = 17,
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
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6,
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                        MIGRATION_8_9,
                        MIGRATION_9_10,
                        MIGRATION_10_11,
                        MIGRATION_11_12,
                        MIGRATION_12_13,
                        MIGRATION_13_14,
                        MIGRATION_14_15,
                        MIGRATION_15_16,
                        MIGRATION_16_17,
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
