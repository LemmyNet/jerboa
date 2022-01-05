package com.jerboa.db

import android.content.Context
import androidx.room.*

@Entity
data class Account(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "selected") val selected:  
        Boolean,
    @ColumnInfo(name = "instance") val instance: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "avatar") val avatar:  
        String?,
    @ColumnInfo(name = "jwt") val jwt:  
        String,
)

@Dao
interface AccountDao {
    @Query("SELECT * FROM account")
    fun getAll(): List<Account>

    @Query(
        "SELECT * FROM account WHERE selected = 1 " +
            "LIMIT 1"
    )
    fun getSelected(): Account?

    @Insert
    fun insert(account: Account)

    @Delete
    fun delete(account: Account)
}

@Database(entities = [Account::class], version = 1)
abstract class AppDB : RoomDatabase() {
    abstract fun accountDao(): AccountDao

    companion object {
        private var db: AppDB? = null
        fun getInstance(ctx: Context): AppDB {
            if (db == null) {
                db = Room.databaseBuilder(
                    ctx.applicationContext,
                    AppDB::class.java,
                    "jerboa"
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return db!!
        }
    }
}
