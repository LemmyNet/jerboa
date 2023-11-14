package com.jerboa

import androidx.room.Room.databaseBuilder
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.jerboa.db.AppDB
import com.jerboa.db.MIGRATIONS_LIST
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationsTest {
    private val testDB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper =
        MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            AppDB::class.java,
        )

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        // Create earliest version of the database.
        helper.createDatabase(testDB, 1).apply {
            close()
        }

        // Open latest version of the database. Room validates the schema
        // once all migrations execute.
        databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDB::class.java,
            testDB,
        ).addMigrations(*MIGRATIONS_LIST).build().apply {
            openHelper.writableDatabase.close()
        }
    }
}
