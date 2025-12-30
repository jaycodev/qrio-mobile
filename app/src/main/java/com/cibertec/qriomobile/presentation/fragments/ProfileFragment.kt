package com.cibertec.qriomobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cibertec.qriomobile.auth.AuthRepository
import com.cibertec.qriomobile.data.RetrofitClient

import com.cibertec.qriomobile.data.model.CustomerDto
import com.cibertec.qriomobile.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

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
                val response = RetrofitClient.api.getMe()
                if (response.isSuccessful) {
                    val me = response.body()
                    if (me != null) renderProfile(me)
                    else Toast.makeText(requireContext(), "Perfil vacío", Toast.LENGTH_SHORT).show()
                } else if (response.code() == 401) {
                    Toast.makeText(requireContext(), "Sesión expirada", Toast.LENGTH_SHORT).show()
                    logoutAndRedirect()
                } else {
                    Toast.makeText(requireContext(), "No se pudo cargar perfil", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun renderProfile(me: com.cibertec.qriomobile.data.model.MeResponse) {
        binding.txtUserName.text = me.name ?: "Usuario"
        binding.txtUserEmail.text = me.email ?: "—"
        binding.txtEmail.text = "📧 Email: ${me.email ?: "—"}"
        val infoExtra = buildString {
            if (!me.role.isNullOrBlank()) append("👤 Rol: ${me.role}\n")
            if (me.restaurantId != null) append("🍽️ Restaurante ID: ${me.restaurantId}\n")
            if (me.branchId != null) append("🏬 Sucursal ID: ${me.branchId}")
        }
        binding.txtPhone.text = if (infoExtra.isNotBlank()) infoExtra else "📞 Teléfono: No registrado"
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

