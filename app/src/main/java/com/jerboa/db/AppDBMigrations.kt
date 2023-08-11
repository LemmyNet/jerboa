package com.jerboa.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

const val UPDATE_APP_CHANGELOG_UNVIEWED = "UPDATE AppSettings SET viewed_changelog = 0"

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "alter table account add column default_listing_type INTEGER NOT " +
                "NULL default 0",
        )
        database.execSQL(
            "alter table account add column default_sort_type INTEGER NOT " +
                "NULL default 0",
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS AppSettings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                    font_size INTEGER NOT NULL DEFAULT 14,  
                    theme INTEGER NOT NULL DEFAULT 0,
                    light_theme INTEGER NOT NULL DEFAULT 0,
                    dark_theme INTEGER NOT NULL DEFAULT 0
                )
            """,
        )
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
                alter table AppSettings add column viewed_changelog INTEGER NOT NULL 
                default 0
            """,
        )
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Material v3 migration
        // Remove dark_theme and light_theme
        // Add theme_color
        // SQLITE for android cant drop columns, you have to redo the table
        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS AppSettingsBackup(
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                    font_size INTEGER NOT NULL DEFAULT 14,  
                    theme INTEGER NOT NULL DEFAULT 0,
                    theme_color INTEGER NOT NULL DEFAULT 0,
                    viewed_changelog INTEGER NOT NULL DEFAULT 0
                )
            """,
        )
        database.execSQL(
            """
            INSERT INTO AppSettingsBackup (id, font_size, theme, viewed_changelog)
            select id, font_size, theme, viewed_changelog from AppSettings
            """,
        )
        database.execSQL("DROP TABLE AppSettings")
        database.execSQL("ALTER TABLE AppSettingsBackup RENAME to AppSettings")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        //  Update changelog viewed
        database.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
    }
}
val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
    }
}
val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
        database.execSQL(
            "alter table AppSettings add column post_view_mode INTEGER NOT NULL default 0",
        )
    }
}

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add new default_font_size of 16
        // SQLITE for android cant drop columns or redo defaults, you have to redo the table
        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS AppSettingsBackup(
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                    font_size INTEGER NOT NULL DEFAULT 16,  
                    theme INTEGER NOT NULL DEFAULT 0,
                    theme_color INTEGER NOT NULL DEFAULT 0,
                    viewed_changelog INTEGER NOT NULL DEFAULT 0,
                    post_view_mode INTEGER NOT NULL default 0
                )
            """,
        )
        database.execSQL(
            """
            INSERT INTO AppSettingsBackup (id, font_size, theme, theme_color, viewed_changelog, 
            post_view_mode)
            select id, font_size, theme, theme_color, viewed_changelog, post_view_mode from 
            AppSettings
            """,
        )
        database.execSQL("DROP TABLE AppSettings")
        database.execSQL("ALTER TABLE AppSettingsBackup RENAME to AppSettings")
    }
}

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add show_bottom_nav column
        database.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
        database.execSQL(
            "ALTER TABLE AppSettings add column show_bottom_nav INTEGER NOT NULL default 1",
        )
    }
}

val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add show_bottom_nav column
        database.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
        database.execSQL(
            "ALTER TABLE AppSettings add column show_collapsed_comment_content INTEGER NOT NULL default 0",
        )
        database.execSQL(
            "ALTER TABLE AppSettings add column show_comment_action_bar_by_default INTEGER NOT NULL default 1",
        )
    }
}

val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
        database.execSQL(
            "ALTER TABLE AppSettings add column show_voting_arrows_in_list_view INTEGER NOT NULL default 1",
        )
    }
}

val MIGRATION_12_13 = object : Migration(12, 13) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
        database.execSQL(
            "ALTER TABLE AppSettings add column use_custom_tabs INTEGER NOT NULL default 1",
        )
    }
}

val MIGRATION_13_14 = object : Migration(13, 14) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
        database.execSQL(
            "ALTER TABLE AppSettings add column use_private_tabs INTEGER NOT NULL default 0",
        )
    }
}

val MIGRATION_14_15 = object : Migration(14, 15) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
        database.execSQL(
            "ALTER TABLE AppSettings add column secure_window INTEGER NOT NULL default 0",
        )
    }
}

val MIGRATION_15_16 = object : Migration(15, 16) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
        database.execSQL(
            "ALTER TABLE AppSettings add column show_parent_comment_navigation_buttons INTEGER NOT NULL default 1",
        )
        database.execSQL(
            "ALTER TABLE AppSettings add column navigate_parent_comments_with_volume_buttons INTEGER NOT NULL default 0",
        )
    }
}

val MIGRATION_16_17 = object : Migration(16, 17) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
        database.execSQL(
            "ALTER TABLE AppSettings add column blur_nsfw INTEGER NOT NULL default 1",
        )
    }
}

val MIGRATION_17_18 = object : Migration(17, 18) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
        database.execSQL(
            "ALTER TABLE AppSettings ADD COLUMN show_text_descriptions_in_navbar INTEGER NOT NULL DEFAULT 1",
        )
    }
}

val MIGRATION_18_19 = object : Migration(18, 19) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
        database.execSQL(
            "ALTER TABLE AppSettings ADD COLUMN backConfirmationMode INTEGER NOT NULL DEFAULT 1",
        )
    }
}

val MIGRATION_19_20 = object : Migration(19, 20) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
        // Add new default show_parent_comment_navigation_buttons to 0

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS AppSettingsBackup (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `font_size` INTEGER NOT NULL DEFAULT 16, 
                `theme` INTEGER NOT NULL DEFAULT 0, 
                `theme_color` INTEGER NOT NULL DEFAULT 0, 
                `viewed_changelog` INTEGER NOT NULL DEFAULT 0, 
                `post_view_mode` INTEGER NOT NULL DEFAULT 0, 
                `show_bottom_nav` INTEGER NOT NULL DEFAULT 1, 
                `show_collapsed_comment_content` INTEGER NOT NULL DEFAULT 0, 
                `show_comment_action_bar_by_default` INTEGER NOT NULL DEFAULT 1, 
                `show_voting_arrows_in_list_view` INTEGER NOT NULL DEFAULT 1, 
                `show_parent_comment_navigation_buttons` INTEGER NOT NULL DEFAULT 0, 
                `navigate_parent_comments_with_volume_buttons` INTEGER NOT NULL DEFAULT 0, 
                `use_custom_tabs` INTEGER NOT NULL DEFAULT 1, 
                `use_private_tabs` INTEGER NOT NULL DEFAULT 0, 
                `secure_window` INTEGER NOT NULL DEFAULT 0, 
                `blur_nsfw` INTEGER NOT NULL DEFAULT 1, 
                `show_text_descriptions_in_navbar` INTEGER NOT NULL DEFAULT 1, 
                `backConfirmationMode` INTEGER NOT NULL DEFAULT 1
            )

            """.trimIndent(),
        )

        database.execSQL(
            """
            INSERT INTO AppSettingsBackup SELECT * FROM AppSettings
            """,
        )
        database.execSQL("DROP TABLE AppSettings")
        database.execSQL("ALTER TABLE AppSettingsBackup RENAME to AppSettings")
    }
}

val MIGRATION_20_21 = object : Migration(20, 21) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
        database.execSQL(
            "ALTER TABLE AppSettings ADD COLUMN show_post_link_previews INTEGER NOT NULL DEFAULT 1",
        )
    }
}

val MIGRATION_21_22 = object : Migration(21, 22) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
        database.execSQL(
            "ALTER TABLE Account ADD COLUMN verification_state INTEGER NOT NULL DEFAULT 0",
        )
    }
}

val MIGRATION_22_21 = object : Migration(22, 21) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Account DROP COLUMN verification_state",
        )
    }
}

val MIGRATION_22_23 = object : Migration(22, 23) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
        database.execSQL(
            "ALTER TABLE AppSettings ADD COLUMN markAsReadOnScroll INTEGER NOT NULL DEFAULT 0",
        )
    }
}

val MIGRATION_23_22 = object : Migration(23, 22) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE AppSettings DROP COLUMN markAsReadOnScroll",
        )
    }
}

val MIGRATION_23_24 = object : Migration(23, 24) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
        database.execSQL(
            "ALTER TABLE AppSettings ADD COLUMN post_actionbar_mode INTEGER NOT NULL DEFAULT 0",
        )
    }
}

val MIGRATION_24_23 = object : Migration(24, 23) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE AppSettings DROP COLUMN post_actionbar_mode",
        )
    }
}

val MIGRATION_24_25 = object : Migration(24, 25) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
        database.execSQL(
            "ALTER TABLE AppSettings ADD COLUMN auto_play_gifs INTEGER NOT NULL DEFAULT 0",
        )
    }
}

val MIGRATION_25_24 = object : Migration(25, 24) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE AppSettings DROP COLUMN auto_play_gifs",
        )
    }
}

// Don't forget to test your migration with `./gradlew app:connectAndroidTest`
val MIGRATIONS_LIST = arrayOf(
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
    MIGRATION_17_18,
    MIGRATION_18_19,
    MIGRATION_19_20,
    MIGRATION_20_21,
    MIGRATION_21_22,
    MIGRATION_22_21,
    MIGRATION_22_23,
    MIGRATION_23_22,
    MIGRATION_23_24,
    MIGRATION_24_23,
    MIGRATION_24_25,
    MIGRATION_25_24,
)
