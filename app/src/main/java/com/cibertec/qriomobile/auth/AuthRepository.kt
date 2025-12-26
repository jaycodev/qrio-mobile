package com.cibertec.qriomobile.auth

/**
 * Repositorio sencillo para integrar el token del login con el interceptor HTTP.
 * Tu flujo de Firebase debe llamar a onLoginSuccess(token) y onLogout() cuando corresponda.
 */
object AuthRepository {
    @Volatile
    private var tokenCache: String? = null

    fun onLoginSuccess(token: String?) {
        tokenCache = token
        AuthManager.setToken(token)
    }

    /** Llamar cuando Firebase renueve el idToken automáticamente. */
    fun onTokenRefreshed(newToken: String?) {
        tokenCache = newToken
        AuthManager.setToken(newToken)
    }

    fun onLogout() {
        tokenCache = null
        AuthManager.clearToken()
    }

    fun currentToken(): String? = tokenCache
}
