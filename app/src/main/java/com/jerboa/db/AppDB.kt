package com.jerboa.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

const val DEFAULT_FONT_SIZE = 14

@Entity
data class Account(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "current") val current: Boolean,
    @ColumnInfo(name = "instance") val instance: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "jwt") val jwt: String,
    @ColumnInfo(
        name = "default_listing_type",
        defaultValue = "0"
    )
    val defaultListingType: Int,
    @ColumnInfo(
        name = "default_sort_type",
        defaultValue = "0"
    )
    val defaultSortType: Int
)

@Entity
data class AppSettings(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(
        name = "font_size",
        defaultValue = DEFAULT_FONT_SIZE.toString()
    )
    val fontSize: Int,
    @ColumnInfo(
        name = "theme",
        defaultValue = "0"
    )
    val theme: Int,
    @ColumnInfo(
        name = "light_theme",
        defaultValue = "0"
    )
    val lightTheme: Int,
    @ColumnInfo(
        name = "dark_theme",
        defaultValue = "0"
    )
    val darkTheme: Int,
    @ColumnInfo(
        name = "viewed_changelog",
        defaultValue = "0"
    )
    val viewedChangelog: Int
)

@Dao
interface AccountDao {
    @Query("SELECT * FROM account")
    fun getAll(): LiveData<List<Account>>

    @Query("SELECT * FROM account")
    fun getAllSync(): List<Account>

    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = Account::class)
    suspend fun insert(account: Account)

    @Query("UPDATE account set current = 0 where current = 1")
    suspend fun removeCurrent()

    @Query("UPDATE account set current = 1 where id = :accountId")
    suspend fun setCurrent(accountId: Int)

    @Delete(entity = Account::class)
    suspend fun delete(account: Account)
}

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM AppSettings limit 1")
    fun getSettings(): LiveData<AppSettings>

    @Update
    suspend fun updateAppSettings(appSettings: AppSettings)

    @Query("UPDATE AppSettings set viewed_changelog = 1")
    suspend fun markChangelogViewed()
}

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class AccountRepository(private val accountDao: AccountDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allAccounts = accountDao.getAll()

    fun getAllSync(): List<Account> {
        return accountDao.getAllSync()
    }

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    suspend fun insert(account: Account) {
        accountDao.insert(account)
    }

    @WorkerThread
    suspend fun removeCurrent() {
        accountDao.removeCurrent()
    }

    @WorkerThread
    suspend fun setCurrent(accountId: Int) {
        accountDao.setCurrent(accountId)
    }

    @WorkerThread
    suspend fun delete(account: Account) {
        accountDao.delete(account)
    }
}

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class AppSettingsRepository(private val appSettingsDao: AppSettingsDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val appSettings = appSettingsDao.getSettings()

    @WorkerThread
    suspend fun update(appSettings: AppSettings) {
        appSettingsDao.updateAppSettings(appSettings)
    }

    @WorkerThread
    suspend fun markChangelogViewed() {
        appSettingsDao.markChangelogViewed()
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "alter table account add column default_listing_type INTEGER NOT " +
                "NULL default 0"
        )
        database.execSQL(
            "alter table account add column default_sort_type INTEGER NOT " +
                "NULL default 0"
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS AppSettings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                    font_size INTEGER NOT NULL DEFAULT $DEFAULT_FONT_SIZE,  
                    theme INTEGER NOT NULL DEFAULT 0,
                    light_theme INTEGER NOT NULL DEFAULT 0,
                    dark_theme INTEGER NOT NULL DEFAULT 0
                )
            """
        )
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
                alter table AppSettings add column viewed_changelog INTEGER NOT NULL 
                default 0
            """
        )
    }
}

@Database(
    version = 4,
    entities = [Account::class, AppSettings::class],
    exportSchema = true
)
abstract class AppDB : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun appSettingsDao(): AppSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDB? = null

        fun getDatabase(
            context: Context
        ): AppDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDB::class.java,
                    "jerboa"
                )
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
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
//                                        put("font_size", DEFAULT_FONT_SIZE)
//                                        put("theme", 0)
//                                        put("light_theme", 0)
//                                        put("dark_theme", 0)
//                                        put("")
                                    }
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

class AccountViewModel(private val repository: AccountRepository) : ViewModel() {

    val allAccounts = repository.allAccounts

    val allAccountSync = repository.getAllSync()

    fun insert(account: Account) = viewModelScope.launch {
        repository.insert(account)
    }

    fun removeCurrent() = viewModelScope.launch {
        repository.removeCurrent()
    }

    fun setCurrent(accountId: Int) = viewModelScope.launch {
        repository.setCurrent(accountId)
    }

    fun delete(account: Account) = viewModelScope.launch {
        repository.delete(account)
    }
}

class AccountViewModelFactory(private val repository: AccountRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccountViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class AppSettingsViewModel(private val repository: AppSettingsRepository) : ViewModel() {

    val appSettings = repository.appSettings

    fun update(appSettings: AppSettings) = viewModelScope.launch {
        repository.update(appSettings)
    }

    fun markChangelogViewed() = viewModelScope.launch {
        repository.markChangelogViewed()
    }
}

class AppSettingsViewModelFactory(private val repository: AppSettingsRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppSettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppSettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
