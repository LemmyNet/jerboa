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
import com.jerboa.toBool
import com.jerboa.ui.theme.DEFAULT_FONT_SIZE
import java.util.concurrent.Executors

// Warning: Be careful about changing any of these defaults, as it will mean you'll need to
// regenerate the database
// Unfortunately can't use enum ordinals here, because Room's compile time annotations
// don't allow it
//
// Use Int for Bools
const val DEFAULT_THEME = 0
const val DEFAULT_THEME_COLOR = 0
const val DEFAULT_LAST_VERSION_CODE_VIEWED = 0
const val DEFAULT_POST_VIEW_MODE = 0
const val DEFAULT_POST_NAVIGATION_GESTURE_MODE = 0
const val DEFAULT_SHOW_BOTTOM_NAV = 1
const val DEFAULT_SHOW_COLLAPSED_COMMENT_CONTENT = 0
const val DEFAULT_SHOW_COMMENT_ACTION_BAR_BY_DEFAULT = 1
const val DEFAULT_SHOW_VOTING_ARROWS_IN_LIST_VIEW = 1
const val DEFAULT_SHOW_PARENT_COMMENT_NAVIGATION_BUTTONS = 0
const val DEFAULT_NAVIGATE_PARENT_COMMENTS_WITH_VOLUME_BUTTONS = 0
const val DEFAULT_USE_CUSTOM_TABS = 1
const val DEFAULT_USE_PRIVATE_TABS = 0
const val DEFAULT_SECURE_WINDOW = 0
const val DEFAULT_BLUR_NSFW = 1
const val DEFAULT_SHOW_TEXT_DESCRIPTIONS_IN_NAVBAR = 1
const val DEFAULT_BACK_CONFIRMATION_MODE = 1
const val DEFAULT_MARK_AS_READ_ON_SCROLL = 0
const val DEFAULT_SHOW_POST_LINK_PREVIEWS = 1
const val DEFAULT_POST_ACTION_BAR_MODE = 0
const val DEFAULT_AUTO_PLAY_GIFS = 0
const val DEFAULT_SWIPE_TO_ACTION_PRESET = 0
const val DEFAULT_DISABLE_AUTO_PLAY = 0

val APP_SETTINGS_DEFAULT =
    AppSettings(
        id = 1,
        fontSize = DEFAULT_FONT_SIZE,
        theme = DEFAULT_THEME,
        themeColor = DEFAULT_THEME_COLOR,
        lastVersionCodeViewed = DEFAULT_LAST_VERSION_CODE_VIEWED,
        postViewMode = DEFAULT_POST_VIEW_MODE,
        postNavigationGestureMode = DEFAULT_POST_NAVIGATION_GESTURE_MODE,
        showBottomNav = DEFAULT_SHOW_BOTTOM_NAV.toBool(),
        showCollapsedCommentContent = DEFAULT_SHOW_COLLAPSED_COMMENT_CONTENT.toBool(),
        showCommentActionBarByDefault = DEFAULT_SHOW_COMMENT_ACTION_BAR_BY_DEFAULT.toBool(),
        showVotingArrowsInListView = DEFAULT_SHOW_VOTING_ARROWS_IN_LIST_VIEW.toBool(),
        showParentCommentNavigationButtons = DEFAULT_SHOW_PARENT_COMMENT_NAVIGATION_BUTTONS.toBool(),
        navigateParentCommentsWithVolumeButtons = DEFAULT_NAVIGATE_PARENT_COMMENTS_WITH_VOLUME_BUTTONS.toBool(),
        useCustomTabs = DEFAULT_USE_CUSTOM_TABS.toBool(),
        usePrivateTabs = DEFAULT_USE_PRIVATE_TABS.toBool(),
        secureWindow = DEFAULT_SECURE_WINDOW.toBool(),
        blurNSFW = DEFAULT_BLUR_NSFW,
        showTextDescriptionsInNavbar = DEFAULT_SHOW_TEXT_DESCRIPTIONS_IN_NAVBAR.toBool(),
        backConfirmationMode = DEFAULT_BACK_CONFIRMATION_MODE,
        markAsReadOnScroll = DEFAULT_MARK_AS_READ_ON_SCROLL.toBool(),
        showPostLinkPreviews = DEFAULT_SHOW_POST_LINK_PREVIEWS.toBool(),
        postActionBarMode = DEFAULT_POST_ACTION_BAR_MODE,
        autoPlayGifs = DEFAULT_AUTO_PLAY_GIFS.toBool(),
        swipeToActionPreset = DEFAULT_SWIPE_TO_ACTION_PRESET,
        disableVideoAutoplay = DEFAULT_DISABLE_AUTO_PLAY,
    )

@Database(
    version = 34,
    entities = [Account::class, AppSettings::class],
    exportSchema = true,
)
abstract class AppDB : RoomDatabase() {
    abstract fun accountDao(): AccountDao

    abstract fun appSettingsDao(): AppSettingsDao

    companion object {
        @Volatile
        private var instance: AppDB? = null

        fun getDatabase(context: Context): AppDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return instance ?: synchronized(this) {
                val i =
                    Room
                        .databaseBuilder(
                            context.applicationContext,
                            AppDB::class.java,
                            "jerboa",
                        ).addMigrations(
                            *MIGRATIONS_LIST,
                        )
                        // Necessary because it can't insert data on creation
                        .addCallback(
                            object : Callback() {
                                override fun onOpen(db: SupportSQLiteDatabase) {
                                    super.onCreate(db)
                                    Executors.newSingleThreadExecutor().execute {
                                        db.insert(
                                            "AppSettings",
                                            // Ensures it won't overwrite the existing data
                                            CONFLICT_IGNORE,
                                            ContentValues(2).apply {
                                                put("id", 1)
                                            },
                                        )
                                    }
                                }
                            },
                        ).build()
                instance = i
                // return instance
                i
            }
        }
    }
}
