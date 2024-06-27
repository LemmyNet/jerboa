package com.jerboa.db

import android.content.Context
import com.jerboa.db.repository.AccountRepository
import com.jerboa.db.repository.AppSettingsRepository

class AppDBContainer(
    private val context: Context,
) {
    private val database by lazy { AppDB.getDatabase(context) }
    val accountRepository by lazy { AccountRepository(database.accountDao()) }
    val appSettingsRepository by lazy { AppSettingsRepository(database.appSettingsDao()) }
}
