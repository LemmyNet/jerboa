package com.jerboa.db.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.jerboa.db.entity.SearchHistory
import com.jerboa.db.dao.SearchHistoryDao

class SearchHistoryRepository(
    private val searchHistoryDao: SearchHistoryDao,
) {
    fun history(): LiveData<List<SearchHistory>> = searchHistoryDao.history()
        .map { history ->
            history
                .sortedByDescending { it.id }
                .distinctBy { it.searchTerm }
        }

    suspend fun insert(item: SearchHistory) =
        searchHistoryDao.insert(item)

    suspend fun delete(item: SearchHistory) = searchHistoryDao.delete(item.accountId, item.searchTerm)
}
