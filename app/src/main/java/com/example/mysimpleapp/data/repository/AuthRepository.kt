package com.example.mysimpleapp.data.repository

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class AuthRepository @Inject constructor(private val context: Context) {
    
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }
    
    companion object {
        private const val KEY_TOKEN = "token"
    }
    
    fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }
    
    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }
    
    fun clearToken() {
        sharedPreferences.edit().remove(KEY_TOKEN).apply()
    }
    
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
} 