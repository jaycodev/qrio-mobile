package com.cibertec.qriomobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cibertec.qriomobile.auth.AuthRepository
import com.cibertec.qriomobile.data.RetrofitClient

import com.cibertec.qriomobile.data.model.CustomerDto
import com.cibertec.qriomobile.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.cibertec.qriomobile.auth.AuthApi

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!AuthRepository.isLogged()) {
            // Token no existe → redirigir a login
            findNavController().navigate(R.id.loginFragment)
            return
        }

        loadProfile()
        setupActions()
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            try {
                // Estrategia correcta: usar /auth/token-info para obtener customerId y luego /customers/{id}
                val token = AuthRepository.getToken()
                Log.d("Profile", "Solicitando token-info con token presente=${!token.isNullOrBlank()}")
                val authApi = RetrofitClient.create(AuthApi::class.java)
                val infoResp = authApi.tokenInfo()
                Log.d("Profile", "token-info HTTP ${infoResp.code()} - headers=${infoResp.headers()} body=${Gson().toJson(infoResp.body())}")
                if (!infoResp.isSuccessful) {
                    if (infoResp.code() == 401) {
                        Toast.makeText(requireContext(), "Sesión expirada", Toast.LENGTH_SHORT).show()
                        logoutAndRedirect()
                        return@launch
                    } else {
                        Toast.makeText(requireContext(), "No se pudo leer token-info", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                }

                val customerId = infoResp.body()?.customerId
                if (customerId == null) {
                    Toast.makeText(requireContext(), "Token sin customerId", Toast.LENGTH_SHORT).show()
                    Log.w("Profile", "token-info sin customerId: ${Gson().toJson(infoResp.body())}")
                    return@launch
                }

                val custResp = RetrofitClient.api.getCustomerById(customerId)
                Log.d("Profile", "GET /customers/{id} HTTP ${custResp.code()} headers=${custResp.headers()} body=${Gson().toJson(custResp.body())}")
                if (custResp.isSuccessful) {
                    val customer = custResp.body()?.data
                    if (customer != null) {
                        renderProfile(customer)
                    } else {
                        Toast.makeText(requireContext(), "Perfil vacío", Toast.LENGTH_SHORT).show()
                    }
                } else if (custResp.code() == 401) {
                    Toast.makeText(requireContext(), "Sesión expirada", Toast.LENGTH_SHORT).show()
                    logoutAndRedirect()
                } else {
                    Toast.makeText(requireContext(), "No se pudo cargar perfil", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Profile", "Error cargando perfil", e)
                Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun tryFallbackLoad() {
        try {
            val authApi = RetrofitClient.create(AuthApi::class.java)
            val infoResp = authApi.tokenInfo()
            if (infoResp.isSuccessful) {
                val customerId = infoResp.body()?.customerId
                Log.d("Profile", "Fallback token-info: customerId=$customerId")
                if (customerId != null) {
                    val custResp = RetrofitClient.api.getCustomerById(customerId)
                    Log.d("Profile", "Fallback /customers/{id} HTTP ${custResp.code()}")
                    val cust = custResp.body()?.data
                    if (cust != null) renderProfile(cust)
                }
            } else {
                Log.w("Profile", "token-info falló: HTTP ${infoResp.code()}")
            }
        } catch (e: Exception) {
            Log.e("Profile", "Error en fallback", e)
        }
    }

    private fun renderProfile(me: CustomerDto) {
        Log.d("Profile", "Render customer: id=${me.id} email=${me.email} phone=${me.phone} name=${me.name} status=${me.status}")
        binding.txtUserName.text = me.name ?: "Cliente"
        binding.txtUserEmail.text = me.email ?: "—"
        binding.txtEmail.text = "📧 Email: ${me.email ?: "—"}"
        val infoExtra = buildString {
            if (!me.status.isNullOrBlank()) append("📌 Estado: ${me.status}\n")
            if (!me.phone.isNullOrBlank()) append("📞 Teléfono: ${me.phone}")
        }
        binding.txtPhone.text = if (infoExtra.isNotBlank()) infoExtra else "📞 Teléfono: No registrado"
        if (me.email.isNullOrBlank() || me.phone.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Faltan datos: email/phone vacíos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupActions() {
        binding.btnLogout.setOnClickListener { logoutAndRedirect() }
        binding.btnChangePassword.setOnClickListener {
            Toast.makeText(requireContext(), "Función no implementada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logoutAndRedirect() {
        AuthRepository.logout()
        findNavController().navigate(R.id.loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

