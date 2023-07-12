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

@Entity
data class Account(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "current") val current: Boolean,
    @ColumnInfo(name = "instance") val instance: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "jwt") val jwt: String,
    @ColumnInfo(
        name = "default_listing_type",
        defaultValue = "0",
    )
    val defaultListingType: Int,
    @ColumnInfo(
        name = "default_sort_type",
        defaultValue = "0",
    )
    val defaultSortType: Int,
)

@Entity
data class AppSettings(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(
        name = "font_size",
        defaultValue = DEFAULT_FONT_SIZE.toString(), // This is changed to 16
    )
    val fontSize: Int,
    @ColumnInfo(
        name = "theme",
        defaultValue = "0",
    )
    val theme: Int,
    @ColumnInfo(
        name = "theme_color",
        defaultValue = "0",
    )
    val themeColor: Int,
    @ColumnInfo(
        name = "viewed_changelog",
        defaultValue = "0",
    )
    val viewedChangelog: Int,
    @ColumnInfo(
        name = "post_view_mode",
        defaultValue = "0",
    )
    val postViewMode: Int,
    @ColumnInfo(
        name = "show_bottom_nav",
        defaultValue = "1",
    )
    val showBottomNav: Boolean,
    @ColumnInfo(
        name = "show_collapsed_comment_content",
        defaultValue = "0",
    )
    val showCollapsedCommentContent: Boolean,
    @ColumnInfo(
        name = "show_comment_action_bar_by_default",
        defaultValue = "1",
    )
    val showCommentActionBarByDefault: Boolean,
    @ColumnInfo(
        name = "show_voting_arrows_in_list_view",
        defaultValue = "1",
    )
    val showVotingArrowsInListView: Boolean,
    @ColumnInfo(
        name = "show_parent_comment_navigation_buttons",
        defaultValue = "1",
    )
    val showParentCommentNavigationButtons: Boolean,
    @ColumnInfo(
        name = "navigate_parent_comments_with_volume_buttons",
        defaultValue = "0",
    )
    val navigateParentCommentsWithVolumeButtons: Boolean,
    @ColumnInfo(
        name = "use_custom_tabs",
        defaultValue = "1",
    )
    val useCustomTabs: Boolean,
    @ColumnInfo(
        name = "use_private_tabs",
        defaultValue = "0",
    )
    val usePrivateTabs: Boolean,
    @ColumnInfo(
        name = "secure_window",
        defaultValue = "0",
    )
    val secureWindow: Boolean,
    @ColumnInfo(
        name = "blur_nsfw",
        defaultValue = "1",
    )
    val blurNSFW: Boolean,
    @ColumnInfo(
        name = "show_text_descriptions_in_navbar",
        defaultValue = "1",
    )
    val showTextDescriptionsInNavbar: Boolean,
    @ColumnInfo(
        name = "backConfirmationMode",
        defaultValue = "1",
    )
    val backConfirmationMode: Int,
)

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
)

@Database(
    version = 19,
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
