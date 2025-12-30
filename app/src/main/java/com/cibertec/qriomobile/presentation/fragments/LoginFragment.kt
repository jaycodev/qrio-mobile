package com.cibertec.qriomobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cibertec.qriomobile.auth.AuthRepository
import com.cibertec.qriomobile.auth.AuthApi
import com.cibertec.qriomobile.auth.LoginRequest
import com.cibertec.qriomobile.data.RetrofitClient
import com.cibertec.qriomobile.databinding.FragmentLoginBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Si ya hay sesión (simulada o real), ir al Home
        if (AuthRepository.isLogged()) {
            findNavController().navigate(R.id.homeFragment)
            return
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Completa los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val authApi = RetrofitClient.create(AuthApi::class.java)
                    // Login de cliente contra backend
                    val resp = authApi.customerLogin(LoginRequest(email, password))
                    if (resp.isSuccessful) {
                        val token = resp.body()?.accessToken
                        if (!token.isNullOrBlank()) {
                            // Persistir token
                            AuthRepository.saveToken(token, null)
                            // Opcional: obtener info del token para cachear customerId
                            val info = authApi.tokenInfo()
                            val cId = if (info.isSuccessful) info.body()?.customerId else null
                            if (cId != null) {
                                AuthRepository.saveToken(token, cId)
                            }
                            Toast.makeText(requireContext(), "Sesión iniciada", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.homeFragment)
                        } else {
                            Toast.makeText(requireContext(), "Respuesta inválida del servidor", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val code = resp.code()
                        val msg = when (code) {
                            401 -> "Credenciales inválidas"
                            403 -> "Usuario inactivo"
                            else -> "Error de autenticación ($code)"
                        }
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
