package com.cibertec.qriomobile.auth

import android.content.Context
import android.content.SharedPreferences
import com.cibertec.qriomobile.data.model.AuthResponse
import com.cibertec.qriomobile.data.RetrofitClient

object AuthRepository {

    private const val PREFS_NAME = "auth_prefs"
    private const val TOKEN_KEY = "jwt_token"
    private const val CUSTOMER_ID_KEY = "customer_id"

    private var jwt: String? = null
    private var customerId: Long? = null
    private var prefs: SharedPreferences? = null

    // Inicializar con contexto de la app
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        jwt = prefs?.getString(TOKEN_KEY, null)
        customerId = prefs?.getLong(CUSTOMER_ID_KEY, -1L)?.takeIf { it != -1L }
    }

    fun saveAuth(auth: AuthResponse) {
        saveToken(auth.token, auth.customerId)
    }

    // Nuevo método manual para bypass o uso genérico
    fun saveToken(token: String, cId: Long?) {
        jwt = token
        customerId = cId

        // Guardar en SharedPreferences
        prefs?.edit()?.apply {
            putString(TOKEN_KEY, jwt)
            putLong(CUSTOMER_ID_KEY, customerId ?: -1L)
            apply()
        }

        // Inyectar token a Retrofit
        RetrofitClient.setAuthTokenProvider { jwt }
    }

    fun getToken(): String? = jwt

    fun isLogged(): Boolean = jwt != null

    fun getCustomerId(): Long? = customerId

    fun logout() {
        jwt = null
        customerId = null

        prefs?.edit()?.apply {
            remove(TOKEN_KEY)
            remove(CUSTOMER_ID_KEY)
            apply()
        }

        RetrofitClient.setAuthTokenProvider { null }
    }
}
