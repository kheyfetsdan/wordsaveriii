package com.example.mysimpleapp.viewmodels.dictionary

import com.example.mysimpleapp.data.TextEntity
import com.example.mysimpleapp.data.api.model.WordResponseRemote

data class DictionaryState(
    val words: List<WordResponseRemote> = emptyList(),
    val currentPage: Int = 1,
    val totalPages: Int = 0,
    val searchQuery: String = "",
    val sortBy: String = "text_asc",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val showErrorMessage: Boolean = false
) 