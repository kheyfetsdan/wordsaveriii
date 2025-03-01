package com.example.mysimpleapp.data.api

import com.example.mysimpleapp.data.api.model.RegisterRequest
import com.example.mysimpleapp.data.api.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/registration")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
} 