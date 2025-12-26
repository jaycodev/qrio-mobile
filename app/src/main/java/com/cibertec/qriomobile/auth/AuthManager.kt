package com.cibertec.qriomobile.auth

import com.cibertec.qriomobile.data.RetrofitClient

/**
 * Administra el idToken del usuario autenticado y lo expone al RetrofitClient.
 * Tu compañera, al completar el login con Firebase, solo debe llamar a setToken(idToken).
 */
object AuthManager {
    @Volatile
    private var idToken: String? = null

    /** Llamar una vez al inicio de la app (por ejemplo, en MainActivity.onCreate). */
    fun init() {
        RetrofitClient.setAuthTokenProvider { idToken }
    }

    /** Asignar/actualizar el token obtenido de Firebase. */
    fun setToken(token: String?) {
        idToken = token
    }

    /** Limpiar el token al cerrar sesión. */
    fun clearToken() {
        idToken = null
    }
}
