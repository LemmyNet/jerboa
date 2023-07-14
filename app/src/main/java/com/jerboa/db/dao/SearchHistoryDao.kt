package com.jerboa.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jerboa.db.entity.SearchHistory

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM SearchHistory")
    fun history(): LiveData<List<SearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = SearchHistory::class)
    suspend fun insert(item: SearchHistory)

    @Query("DELETE FROM SearchHistory WHERE account_id IS :accountId AND search_term = :searchTerm")
    suspend fun delete(accountId: Int?, searchTerm: String)

    @Query("DELETE FROM SearchHistory")
    suspend fun clear()
}
