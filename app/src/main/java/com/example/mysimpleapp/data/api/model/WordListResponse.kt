package com.example.mysimpleapp.data.api.model

data class WordListResponse(
    val wordList: List<WordResponseRemote>,
    val totalCount: Int
) 