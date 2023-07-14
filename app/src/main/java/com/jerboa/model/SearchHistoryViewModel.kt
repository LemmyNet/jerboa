package com.jerboa.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jerboa.db.entity.SearchHistory
import com.jerboa.db.repository.SearchHistoryRepository
import com.jerboa.jerboaApplication

class SearchHistoryViewModel(private val repository: SearchHistoryRepository) : ViewModel() {
    val searchHistory = repository.history()

    suspend fun insert(item: SearchHistory) {
        repository.insert(item)
    }

    suspend fun delete(item: SearchHistory) {
        repository.delete(item)
    }
}

object SearchHistoryViewModelFactory {
    val Factory = viewModelFactory {
        initializer {
            SearchHistoryViewModel(jerboaApplication().container.searchHistoryRepository)
        }
    }
}
