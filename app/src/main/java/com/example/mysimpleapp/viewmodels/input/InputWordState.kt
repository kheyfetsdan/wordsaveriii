package com.example.mysimpleapp.viewmodels.input

data class InputWordState(
    val text: String = "",
    val translation: String = "",
    val isValid: Boolean = false,
    val showErrorMessage: Boolean = false,
    val showSuccessMessage: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false
) 