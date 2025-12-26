package com.cibertec.qriomobile.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.cibertec.qriomobile.R
import com.cibertec.qriomobile.auth.AuthManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa el proveedor de token para el interceptor HTTP.
        // Tu compañera luego llamará a AuthManager.setToken(idToken) al terminar el login.
        AuthManager.init()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.navHostFragment).navigateUp()
    }
}
