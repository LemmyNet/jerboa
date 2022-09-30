package com.jerboa.db

import android.content.Context
import androidx.annotation.NonNull
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.launch

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
    ) @NonNull
    val defaultListingType: Int,
    @ColumnInfo(
        name = "default_sort_type",
        defaultValue = "0"
    ) @NonNull
    val defaultSortType: Int
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

@Database(
    version = 2,
    entities = [Account::class],
    exportSchema = true
)
abstract class AppDB : RoomDatabase() {
    abstract fun accountDao(): AccountDao

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
                    .addMigrations(MIGRATION_1_2)
                    .build()
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
