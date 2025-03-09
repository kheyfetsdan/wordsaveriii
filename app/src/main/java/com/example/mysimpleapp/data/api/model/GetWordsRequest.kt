package com.example.mysimpleapp.data.api.model

data class GetWordsRequest(
    val sortingParam: String,
    val sortingDirection: String,
    val page: Int,
    val pageSize: Int
) 