package com.jerboa.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.jerboa.db.entity.Account

@Dao
interface AccountDao {
    @Query("SELECT * FROM account")
    fun getAll(): LiveData<List<Account>>

    @Query("SELECT * FROM account where current = 1 limit 1")
    fun getCurrent(): LiveData<Account?>

    @Query("SELECT * FROM account where current = 1 limit 1")
    suspend fun getCurrentAsync(): Account?

    @Insert(onConflict = OnConflictStrategy.IGNORE, entity = Account::class)
    suspend fun insert(account: Account)

    @Update(entity = Account::class)
    suspend fun update(account: Account)

    @Query("UPDATE account set current = 0 where current = 1")
    suspend fun removeCurrent()

    @Query("UPDATE account set current = 1 where id = :accountId")
    suspend fun setCurrent(accountId: Long)

    // Important to use this instead of calling removeCurrent and setCurrent manually
    // Because the previous would cause livedata to emit null then newAccount
    @Transaction
    suspend fun updateCurrent(accountId: Long) {
        removeCurrent()
        setCurrent(accountId)
    }

    @Query("Update account set verification_state = :state where id = :accountId")
    suspend fun setVerificationState(
        accountId: Long,
        state: Int,
    )

    @Delete(entity = Account::class)
    suspend fun delete(account: Account)
}
