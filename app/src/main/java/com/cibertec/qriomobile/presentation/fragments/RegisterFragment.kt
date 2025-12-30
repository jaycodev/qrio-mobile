package com.cibertec.qriomobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cibertec.qriomobile.auth.AuthApi
import com.cibertec.qriomobile.auth.CustomerApi
import com.cibertec.qriomobile.auth.CreateCustomerRequest
import com.cibertec.qriomobile.auth.LoginRequest
import com.cibertec.qriomobile.auth.AuthManager
import com.cibertec.qriomobile.data.RetrofitClient
import com.cibertec.qriomobile.databinding.FragmentRegisterBinding
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(context, "Completa nombre, correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnRegister.isEnabled = false
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val customerApi = RetrofitClient.create(CustomerApi::class.java)
                    val resp = customerApi.create(CreateCustomerRequest(name, email, phone.ifEmpty { null }, pass))
                    if (!resp.isSuccessful) {
                        Toast.makeText(context, "Registro inválido", Toast.LENGTH_SHORT).show()
                    } else {
                        // Login inmediato tras registro
                        val authApi = RetrofitClient.create(AuthApi::class.java)
                        val loginResp = authApi.customerLogin(LoginRequest(email, pass))
                        if (loginResp.isSuccessful) {
                            val token = loginResp.body()?.accessToken
                            if (!token.isNullOrBlank()) {
                                AuthManager.setToken(token)
                                findNavController().navigate(R.id.homeFragment)
                            } else {
                                Toast.makeText(context, "Error al obtener token", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    binding.btnRegister.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
