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
import com.example.mysimpleapp.data.api.model.SaveWordIdRequest
import com.example.mysimpleapp.data.api.model.WordResponseRemote
import com.example.mysimpleapp.data.api.model.WordStatRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.PUT
import retrofit2.http.DELETE

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

    @GET("word/{id}")
    suspend fun getWordById(
        @Header("Authorization") token: String,
        @Path("id") wordId: Int
    ): Response<WordResponseRemote>

    @PUT("word")
    suspend fun updateWord(
        @Header("Authorization") token: String,
        @Body request: SaveWordIdRequest
    ): Response<Unit>

    @DELETE("delete-word/{id}")
    suspend fun deleteWord(
        @Header("Authorization") token: String,
        @Path("id") wordId: Int
    ): Response<Unit>

    @PUT("word-stat/{id}")
    suspend fun updateWordStat(
        @Header("Authorization") token: String,
        @Path("id") wordId: Int,
        @Body request: WordStatRequest
    ): Response<Unit>
} 