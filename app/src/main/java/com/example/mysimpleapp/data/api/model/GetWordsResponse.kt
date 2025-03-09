package com.example.mysimpleapp.data.api.model

data class GetWordsResponse(
    val wordList: List<WordResponseRemote>,
    val total: Int,
    val page: Int
) 