package com.example.mysimpleapp.data.api

import com.example.mysimpleapp.data.api.model.LoginRequest
import com.example.mysimpleapp.data.api.model.LoginResponse
import com.example.mysimpleapp.data.api.model.RegisterRequest
import com.example.mysimpleapp.data.api.model.RegisterResponse
import com.example.mysimpleapp.data.api.model.SaveWordRequest
import com.example.mysimpleapp.data.api.model.WordResponse
import com.example.mysimpleapp.data.api.model.EmptyRequest
import com.example.mysimpleapp.data.api.model.GetWordsRequest
import com.example.mysimpleapp.data.api.model.GetWordsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("/registration")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/save-word")
    suspend fun saveWord(
        @Header("Authorization") token: String,
        @Body request: SaveWordRequest
    ): Response<Unit>

    @POST("/get-word")
    suspend fun getWord(
        @Header("Authorization") token: String,
        @Body request: EmptyRequest = EmptyRequest()
    ): Response<WordResponse>

    @POST("get-words-by-user")
    suspend fun getWordsByUser(
        @Header("Authorization") token: String,
        @Body request: GetWordsRequest
    ): Response<GetWordsResponse>
} 