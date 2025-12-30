package com.cibertec.qriomobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cibertec.qriomobile.auth.AuthRepository
import com.cibertec.qriomobile.data.RetrofitClient
import com.cibertec.qriomobile.databinding.FragmentLoginBinding

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

            // LOGIN MANUAL / BYPASS (Sin Firebase)
            // Aquí simulamos un token y un ID de cliente
            // Si tu backend requiere un token válido, deberás generarlo o implementar auth real contra tu backend aquí.
            
            // Simulamos token
            val dummyToken = "dummy_token_bypass_no_firebase"
            val dummyCustomerId = 1L 

            // Guardamos en AuthRepository (SharedPreferences)
            AuthRepository.saveToken(dummyToken, dummyCustomerId)

            Toast.makeText(requireContext(), "Sesión iniciada (Local)", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
