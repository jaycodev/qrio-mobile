package com.cibertec.qriomobile.auth

import com.cibertec.qriomobile.data.RetrofitClient


// AuthManager.kt
object AuthManager {
    private var token: String? = null

    fun init() {
        // Inicialización si necesitas algo al arrancar la app
    }

    fun getToken(): String? = token

    fun setToken(newToken: String?) {
        token = newToken
        RetrofitClient.setAuthTokenProvider { token } // Inyecta en el interceptor
    }

    fun clearToken() {
        token = null
        RetrofitClient.setAuthTokenProvider { null }
    }

}
