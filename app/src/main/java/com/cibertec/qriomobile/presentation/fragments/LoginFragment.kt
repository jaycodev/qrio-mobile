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
import com.cibertec.qriomobile.auth.LoginRequest
import com.cibertec.qriomobile.data.RetrofitClient
import com.cibertec.qriomobile.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

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
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(context, "Ingrese correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnLogin.isEnabled = false

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val authApi = RetrofitClient.create(AuthApi::class.java)
                    val resp = authApi.customerLogin(LoginRequest(email = email, password = pass))

                    if (!resp.isSuccessful) {
                        val err = try { resp.errorBody()?.string() } catch (_: Exception) { null }
                        Log.e("LoginFragment", "Backend rechazó login: ${resp.code()} body=${err}")
                        Toast.makeText(context, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                    } else {
                        val access = resp.body()?.accessToken
                        if (access.isNullOrBlank()) {
                            Log.e("LoginFragment", "Backend no devolvió accessToken")
                            Toast.makeText(context, "Error en autenticación", Toast.LENGTH_SHORT).show()
                        } else {
                            AuthManager.setToken(access)
                            Log.d("LoginFragment", "Login backend exitoso. Token guardado.")
                            val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                            findNavController().navigate(action)
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Login fallido: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("LoginFragment", "Error login", e)
                } finally {
                    binding.btnLogin.isEnabled = true
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
