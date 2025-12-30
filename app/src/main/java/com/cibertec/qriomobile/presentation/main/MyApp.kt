package com.cibertec.qriomobile

import android.app.Application
import com.cibertec.qriomobile.auth.AuthRepository
import android.util.Log


class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializa AuthRepository con el contexto de la app
        AuthRepository.init(this)

        // Solo aquí no hay token aún
        Log.d("DEBUG", "Token guardado: ${AuthRepository.getToken()}")
    }
}

