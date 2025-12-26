package com.cibertec.qriomobile.auth

import com.cibertec.qriomobile.data.RetrofitClient

/**
 * Administra el token de autenticación (JWT del backend tras el intercambio de Firebase).
 * Llama a `init()` una vez (por ejemplo, en MainActivity.onCreate) para enlazar el proveedor con Retrofit.
 */
object AuthManager {
    @Volatile private var token: String? = null

    fun init() {
        RetrofitClient.setAuthTokenProvider { token }
    }

    fun setToken(value: String?) { token = value }
    fun getToken(): String? = token
    fun clear() { token = null }
}
