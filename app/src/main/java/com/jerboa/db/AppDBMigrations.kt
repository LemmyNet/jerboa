package com.jerboa.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

const val UPDATE_APP_CHANGELOG_UNVIEWED = "UPDATE AppSettings SET viewed_changelog = 0"

val MIGRATION_1_2 =
    object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "alter table account add column default_listing_type INTEGER NOT " +
                    "NULL default 0",
            )
            db.execSQL(
                "alter table account add column default_sort_type INTEGER NOT " +
                    "NULL default 0",
            )
        }
    }

val MIGRATION_2_3 =
    object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
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

val MIGRATION_3_4 =
    object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                alter table AppSettings add column viewed_changelog INTEGER NOT NULL 
                default 0
            """,
            )
        }
    }

val MIGRATION_4_5 =
    object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Material v3 migration
            // Remove dark_theme and light_theme
            // Add theme_color
            // SQLITE for android can't drop columns, you have to redo the table
            db.execSQL(
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
            db.execSQL(
                """
            INSERT INTO AppSettingsBackup (id, font_size, theme, viewed_changelog)
            select id, font_size, theme, viewed_changelog from AppSettings
            """,
            )
            db.execSQL("DROP TABLE AppSettings")
            db.execSQL("ALTER TABLE AppSettingsBackup RENAME to AppSettings")
        }
    }

val MIGRATION_5_6 =
    object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            //  Update changelog viewed
            db.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
        }
    }
val MIGRATION_6_7 =
    object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
        }
    }
val MIGRATION_7_8 =
    object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
            db.execSQL(
                "alter table AppSettings add column post_view_mode INTEGER NOT NULL default 0",
            )
        }
    }

val MIGRATION_8_9 =
    object : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Add new default_font_size of 16
            // SQLITE for android can't drop columns or redo defaults, you have to redo the table
            db.execSQL(
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
            db.execSQL(
                """
            INSERT INTO AppSettingsBackup (id, font_size, theme, theme_color, viewed_changelog, 
            post_view_mode)
            select id, font_size, theme, theme_color, viewed_changelog, post_view_mode from 
            AppSettings
            """,
            )
            db.execSQL("DROP TABLE AppSettings")
            db.execSQL("ALTER TABLE AppSettingsBackup RENAME to AppSettings")
        }
    }

val MIGRATION_9_10 =
    object : Migration(9, 10) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Add show_bottom_nav column
            db.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
            db.execSQL(
                "ALTER TABLE AppSettings add column show_bottom_nav INTEGER NOT NULL default 1",
            )
        }
    }

val MIGRATION_10_11 =
    object : Migration(10, 11) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Add show_bottom_nav column
            db.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
            db.execSQL(
                "ALTER TABLE AppSettings add column show_collapsed_comment_content INTEGER NOT NULL default 0",
            )
            db.execSQL(
                "ALTER TABLE AppSettings add column show_comment_action_bar_by_default INTEGER NOT NULL default 1",
            )
        }
    }

val MIGRATION_11_12 =
    object : Migration(11, 12) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
            db.execSQL(
                "ALTER TABLE AppSettings add column show_voting_arrows_in_list_view INTEGER NOT NULL default 1",
            )
        }
    }

val MIGRATION_12_13 =
    object : Migration(12, 13) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
            db.execSQL(
                "ALTER TABLE AppSettings add column use_custom_tabs INTEGER NOT NULL default 1",
            )
        }
    }

val MIGRATION_13_14 =
    object : Migration(13, 14) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
            db.execSQL(
                "ALTER TABLE AppSettings add column use_private_tabs INTEGER NOT NULL default 0",
            )
        }
    }

val MIGRATION_14_15 =
    object : Migration(14, 15) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
            db.execSQL(
                "ALTER TABLE AppSettings add column secure_window INTEGER NOT NULL default 0",
            )
        }
    }

val MIGRATION_15_16 =
    object : Migration(15, 16) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
            db.execSQL(
                "ALTER TABLE AppSettings add column show_parent_comment_navigation_buttons INTEGER NOT NULL default 1",
            )
            db.execSQL(
                "ALTER TABLE AppSettings add column navigate_parent_comments_with_volume_buttons INTEGER NOT NULL default 0",
            )
        }
    }

val MIGRATION_16_17 =
    object : Migration(16, 17) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
            db.execSQL(
                "ALTER TABLE AppSettings add column blur_nsfw INTEGER NOT NULL default 1",
            )
        }
    }

val MIGRATION_17_18 =
    object : Migration(17, 18) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN show_text_descriptions_in_navbar INTEGER NOT NULL DEFAULT 1",
            )
        }
    }

val MIGRATION_18_19 =
    object : Migration(18, 19) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN backConfirmationMode INTEGER NOT NULL DEFAULT 1",
            )
        }
    }

val MIGRATION_19_20 =
    object : Migration(19, 20) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
            // Add new default show_parent_comment_navigation_buttons to 0

            db.execSQL(
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

            db.execSQL(
                """
            INSERT INTO AppSettingsBackup SELECT * FROM AppSettings
            """,
            )
            db.execSQL("DROP TABLE AppSettings")
            db.execSQL("ALTER TABLE AppSettingsBackup RENAME to AppSettings")
        }
    }

val MIGRATION_20_21 =
    object : Migration(20, 21) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN show_post_link_previews INTEGER NOT NULL DEFAULT 1",
            )
        }
    }

val MIGRATION_21_22 =
    object : Migration(21, 22) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
            db.execSQL(
                "ALTER TABLE Account ADD COLUMN verification_state INTEGER NOT NULL DEFAULT 0",
            )
        }
    }

val MIGRATION_22_23 =
    object : Migration(22, 23) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN markAsReadOnScroll INTEGER NOT NULL DEFAULT 0",
            )
        }
    }

val MIGRATION_23_24 =
    object : Migration(23, 24) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN post_actionbar_mode INTEGER NOT NULL DEFAULT 0",
            )
        }
    }

val MIGRATION_24_25 =
    object : Migration(24, 25) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN auto_play_gifs INTEGER NOT NULL DEFAULT 0",
            )
        }
    }

val MIGRATION_25_26 =
    object : Migration(25, 26) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN post_navigation_gesture_mode INTEGER NOT NULL DEFAULT 0",
            )
        }
    }

val MIGRATION_26_27 =
    object : Migration(26, 27) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(UPDATE_APP_CHANGELOG_UNVIEWED)
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN swipe_to_action_preset INTEGER NOT NULL DEFAULT 1",
            )
        }
    }

val MIGRATION_27_28 =
    object : Migration(27, 28) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN last_version_code_viewed INTEGER NOT NULL default 0",
            )
        }
    }

val MIGRATION_28_29 =
    object : Migration(28, 29) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Add is_admin and is_mod columns
            db.execSQL(
                "ALTER TABLE Account add column is_admin INTEGER NOT NULL default 0",
            )
            db.execSQL(
                "ALTER TABLE Account add column is_mod INTEGER NOT NULL default 0",
            )
        }
    }

val MIGRATION_29_30 =
    object : Migration(29, 30) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Add new default swipe_to_action_preset to 0

            db.execSQL(
                """
                   CREATE TABLE IF NOT EXISTS AppSettingsBackup (
                      `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                      `font_size` INTEGER NOT NULL DEFAULT 16, 
                      `theme` INTEGER NOT NULL DEFAULT 0, 
                      `theme_color` INTEGER NOT NULL DEFAULT 0, 
                      `viewed_changelog` INTEGER NOT NULL DEFAULT 0, 
                      `post_view_mode` INTEGER NOT NULL DEFAULT 0, 
                      `show_bottom_nav` INTEGER NOT NULL DEFAULT 1, 
                      `post_navigation_gesture_mode` INTEGER NOT NULL DEFAULT 0, 
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
                      `markAsReadOnScroll` INTEGER NOT NULL DEFAULT 0, 
                      `backConfirmationMode` INTEGER NOT NULL DEFAULT 1, 
                      `show_post_link_previews` INTEGER NOT NULL DEFAULT 1, 
                      `post_actionbar_mode` INTEGER NOT NULL DEFAULT 0, 
                      `auto_play_gifs` INTEGER NOT NULL DEFAULT 0, 
                      `swipe_to_action_preset` INTEGER NOT NULL DEFAULT 0, 
                      `last_version_code_viewed` INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent(),
            )

            db.execSQL(
                """
            INSERT INTO AppSettingsBackup SELECT * FROM AppSettings
            """,
            )
            db.execSQL("DROP TABLE AppSettings")
            db.execSQL("ALTER TABLE AppSettingsBackup RENAME to AppSettings")
        }
    }

val MIGRATION_30_31 =
    object : Migration(30, 31) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Fix wrong ordering in previous migration

            db.execSQL(
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
                      `backConfirmationMode` INTEGER NOT NULL DEFAULT 1, 
                      `show_post_link_previews` INTEGER NOT NULL DEFAULT 1,
                      `markAsReadOnScroll` INTEGER NOT NULL DEFAULT 0,
                      `post_actionbar_mode` INTEGER NOT NULL DEFAULT 0, 
                      `auto_play_gifs` INTEGER NOT NULL DEFAULT 0,
                      `post_navigation_gesture_mode` INTEGER NOT NULL DEFAULT 0,
                      `swipe_to_action_preset` INTEGER NOT NULL DEFAULT 0, 
                      `last_version_code_viewed` INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent(),
            )

            db.execSQL(
                """
            INSERT INTO AppSettingsBackup SELECT * FROM AppSettings
            """,
            )
            db.execSQL("DROP TABLE AppSettings")
            db.execSQL("ALTER TABLE AppSettingsBackup RENAME to AppSettings")

            // Reset to default, many may have this probably disabled in the mean time
            // But would be reset again due the ordering change
            db.execSQL("UPDATE AppSettings SET secure_window = 0")
        }
    }

val MIGRATION_31_32 =
    object : Migration(31, 32) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Fix wrong ordering and defaults in previous migration

            db.execSQL(
                """
                   CREATE TABLE IF NOT EXISTS AppSettingsBackup (
                      `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                      `font_size` INTEGER NOT NULL DEFAULT 16,
                      `theme` INTEGER NOT NULL DEFAULT 0,
                      `theme_color` INTEGER NOT NULL DEFAULT 0,
                      `post_view_mode` INTEGER NOT NULL DEFAULT 0,
                      `show_bottom_nav` INTEGER NOT NULL DEFAULT 1,
                      `post_navigation_gesture_mode` INTEGER NOT NULL DEFAULT 0,
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
                      `markAsReadOnScroll` INTEGER NOT NULL DEFAULT 0,
                      `backConfirmationMode` INTEGER NOT NULL DEFAULT 1,
                      `show_post_link_previews` INTEGER NOT NULL DEFAULT 1,
                      `post_actionbar_mode` INTEGER NOT NULL DEFAULT 0,
                      `auto_play_gifs` INTEGER NOT NULL DEFAULT 0,
                      `swipe_to_action_preset` INTEGER NOT NULL DEFAULT 0,
                      `last_version_code_viewed` INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent(),
            )

            // Need to select explicitly, because mark_changelog_viewed was dropped
            db.execSQL(
                """
            INSERT INTO AppSettingsBackup SELECT
                      `id`,
                      `font_size`,
                      `theme`,
                      `theme_color`,
                      `post_view_mode`,
                      `show_bottom_nav`,
                      `post_navigation_gesture_mode`,
                      `show_collapsed_comment_content`,
                      `show_comment_action_bar_by_default`,
                      `show_voting_arrows_in_list_view`,
                      `show_parent_comment_navigation_buttons`,
                      `navigate_parent_comments_with_volume_buttons`,
                      `use_custom_tabs`,
                      `use_private_tabs`,
                      `secure_window`,
                      `blur_nsfw`,
                      `show_text_descriptions_in_navbar`,
                      `markAsReadOnScroll`,
                      `backConfirmationMode`,
                      `show_post_link_previews`,
                      `post_actionbar_mode`,
                      `auto_play_gifs`,
                      `swipe_to_action_preset`,
                      `last_version_code_viewed`
             FROM AppSettings
            """,
            )
            db.execSQL("DROP TABLE AppSettings")
            db.execSQL("ALTER TABLE AppSettingsBackup RENAME to AppSettings")

            // Reset a few messups to default
            db.execSQL("UPDATE AppSettings SET post_actionbar_mode = 0")
        }
    }

val MIGRATION_32_33 =
    object : Migration(32, 33) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Force update a few wrong defaults
            db.execSQL("UPDATE AppSettings SET show_comment_action_bar_by_default = 1")
            db.execSQL("UPDATE AppSettings SET markAsReadOnScroll = 0")
            db.execSQL("UPDATE AppSettings SET show_post_link_previews = 1")
            db.execSQL("UPDATE AppSettings SET backConfirmationMode = 1")
            db.execSQL("UPDATE AppSettings SET use_private_tabs = 0")
            db.execSQL("UPDATE AppSettings SET use_custom_tabs = 1")
        }
    }

val MIGRATION_33_34 =
    object : Migration(33, 34) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE AppSettings ADD COLUMN disable_video_autoplay INTEGER NOT NULL DEFAULT 0",
            )
        }
    }

// Don't forget to test your migration with `./gradlew app:connectAndroidTest`
val MIGRATIONS_LIST =
    arrayOf(
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
        MIGRATION_22_23,
        MIGRATION_23_24,
        MIGRATION_24_25,
        MIGRATION_25_26,
        MIGRATION_26_27,
        MIGRATION_27_28,
        MIGRATION_28_29,
        MIGRATION_29_30,
        MIGRATION_30_31,
        MIGRATION_31_32,
        MIGRATION_32_33,
        MIGRATION_33_34,
    )
