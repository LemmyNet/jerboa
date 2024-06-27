package com.jerboa.db.repository

import androidx.annotation.WorkerThread
import com.jerboa.db.dao.AccountDao
import com.jerboa.db.entity.Account

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class AccountRepository(
    private val accountDao: AccountDao,
) {
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val currentAccount = accountDao.getCurrent()
    val allAccounts = accountDao.getAll()

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
    suspend fun setVerificationState(
        accountId: Long,
        state: Int,
    ) {
        accountDao.setVerificationState(accountId, state)
    }

    @WorkerThread
    suspend fun delete(account: Account) {
        accountDao.delete(account)
    }

    @WorkerThread
    suspend fun updateCurrent(accountId: Long) {
        accountDao.updateCurrent(accountId)
    }

    @WorkerThread
    suspend fun getCurrentAsync(): Account? = accountDao.getCurrentAsync()
}
