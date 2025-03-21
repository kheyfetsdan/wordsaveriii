package com.example.mysimpleapp.viewmodels.dictionary

import com.example.mysimpleapp.data.TextEntity

data class DictionaryState(
    val words: List<TextEntity> = emptyList(),
    val currentPage: Int = 1,
    val totalPages: Int = 0,
    val searchQuery: String = "",
    val sortBy: String = "text_asc",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val showErrorMessage: Boolean = false
) 