package com.jerboa.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
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
)

@Dao
interface AccountDao {
    @Query("SELECT * FROM account")
    fun getAll(): LiveData<List<Account>>

    @Query("SELECT * FROM account")
    fun getAllSync(): List<Account>

    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = Account::class)
    suspend fun insert(account: Account)

    @Update(entity = Account::class)
    suspend fun update(account: Account)

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

    @Query("UPDATE AppSettings set post_view_mode = :postViewMode")
    suspend fun updatePostViewMode(postViewMode: Int)
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
    suspend fun update(account: Account) {
        accountDao.update(account)
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
class AppSettingsRepository(
    private val appSettingsDao: AppSettingsDao,
    private val httpClient: OkHttpClient = OkHttpClient(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    private val _changelog = MutableStateFlow("")
    val changelog = _changelog.asStateFlow()

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

    @WorkerThread
    suspend fun updatePostViewMode(postViewMode: Int) {
        appSettingsDao.updatePostViewMode(postViewMode)
    }

    @WorkerThread
    suspend fun updateChangelog() {
        withContext(ioDispatcher) {
            try {
                Log.d("jerboa", "Fetching RELEASES.md ...")
                // Fetch the markdown text
                val releasesUrl =
                    "https://raw.githubusercontent.com/dessalines/jerboa/main/RELEASES.md".toHttpUrl()
                val req = Request.Builder().url(releasesUrl).build()
                val res = httpClient.newCall(req).execute()
                _changelog.value = res.body.string()
            } catch (e: Exception) {
                Log.e("jerboa", "Failed to load changelog: $e")
            }
        }
    }
}

@Database(
    version = 18,
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
                    .allowMainThreadQueries() // TODO: Why are we allowing this?
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
    val changelog = repository.changelog

    fun update(appSettings: AppSettings) = viewModelScope.launch {
        repository.update(appSettings)
    }

    fun markChangelogViewed() = viewModelScope.launch {
        repository.markChangelogViewed()
    }

    fun updatedPostViewMode(postViewMode: Int) = viewModelScope.launch {
        repository.updatePostViewMode(postViewMode)
    }

    fun updateChangelog() = viewModelScope.launch {
        repository.updateChangelog()
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
