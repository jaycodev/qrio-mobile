package com.cibertec.qriomobile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cibertec.qriomobile.auth.AuthApi
import com.cibertec.qriomobile.auth.AuthManager
import com.cibertec.qriomobile.auth.FirebaseLoginRequest
import com.cibertec.qriomobile.data.RetrofitClient
import com.cibertec.qriomobile.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            // Corrección: IDs coinciden con fragment_login.xml (etEmail, etPassword)
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(context, "Ingrese correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnLogin.isEnabled = false

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    // 1. Login con Firebase
                    val authResult = auth.signInWithEmailAndPassword(email, pass).await()
                    val user = authResult.user

                    if (user != null) {
                        // 2. Intercambio de token con tu Backend (Autenticación Real)
                        if (onFirebaseLoginSuccess(user)) {
                            Toast.makeText(context, "Bienvenido ${user.email}", Toast.LENGTH_SHORT).show()
                            val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                            findNavController().navigate(action)
                        } else {
                             Toast.makeText(context, "Error en autenticación con el servidor", Toast.LENGTH_SHORT).show()
                             auth.signOut() // Logout si falla en backend
                        }
                    } else {
                        Toast.makeText(context, "Error obteniendo usuario", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Login fallido: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("LoginFragment", "Error login", e)
                } finally {
                    binding.btnLogin.isEnabled = true
                }
            }
        }
    }

    private suspend fun onFirebaseLoginSuccess(user: FirebaseUser): Boolean {
        return try {
            val tokenResult = user.getIdToken(true).await()
            val idToken = tokenResult.token ?: return false

            Log.d("LoginFragment", "Firebase ID Token obtenido. len=${idToken.length}, pref=${idToken.take(12)}...")

            val authApi = RetrofitClient.create(AuthApi::class.java)
            val resp = authApi.loginWithFirebase(FirebaseLoginRequest(idToken = idToken, firebaseToken = idToken), idToken)

            if (!resp.isSuccessful) {
                val err = try { resp.errorBody()?.string() } catch (e: Exception) { null }
                Log.e("LoginFragment", "Backend rechazó login: ${resp.code()} body=${err}")
                return false
            }

            val access = resp.body()?.accessToken
            if (access.isNullOrBlank()) {
                Log.e("LoginFragment", "Backend no devolvió accessToken")
                return false
            }

            // Guardar el JWT de tu backend para futuras peticiones
            AuthManager.setToken(access)
            Log.d("LoginFragment", "Login backend exitoso. Token guardado.")

            // Opcional: leer claims
            try {
                val info = authApi.tokenInfo()
                if (info.isSuccessful) {
                    val claims = info.body()
                    Log.d("LoginFragment", "User info: $claims")
                }
            } catch (e: Exception) {
                Log.w("LoginFragment", "No se pudo obtener info del token", e)
            }
            true
        } catch (e: Exception) {
            Log.e("LoginFragment", "Excepción en onFirebaseLoginSuccess", e)
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
