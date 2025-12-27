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
import com.cibertec.qriomobile.auth.AuthRepository
import com.cibertec.qriomobile.data.RetrofitClient
import com.cibertec.qriomobile.data.model.CustomerDto
import com.cibertec.qriomobile.data.model.FirebaseAuthRequest
import com.cibertec.qriomobile.data.repository.CustomerRepository
import com.cibertec.qriomobile.databinding.FragmentLoginBinding
import com.cibertec.qriomobile.data.remote.NetworkResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

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
        auth = FirebaseAuth.getInstance()

        // Si ya hay token guardado, saltamos al Home
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

            loginOrRegister(email, password)
        }
    }

    private fun loginOrRegister(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { handleFirebaseUser() }
            .addOnFailureListener { registerUser(email, password) }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { handleFirebaseUser() }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error de autenticación", Toast.LENGTH_SHORT).show()
            }
    }

    private fun handleFirebaseUser() {
        val user = auth.currentUser ?: run {
            Toast.makeText(requireContext(), "Usuario Firebase inválido", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtenemos token Firebase
        user.getIdToken(true).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Toast.makeText(requireContext(), "Error obteniendo token Firebase", Toast.LENGTH_SHORT).show()
                return@addOnCompleteListener
            }

            val firebaseToken = task.result?.token
            if (firebaseToken.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Token Firebase vacío", Toast.LENGTH_SHORT).show()
                return@addOnCompleteListener
            }

            // Llamada al backend
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.api.firebaseAuth(FirebaseAuthRequest(firebaseToken))
                    if (response.isSuccessful) {
                        response.body()?.let { authResponse ->
                            // Guardamos token
                            AuthRepository.saveAuth(authResponse)
                            RetrofitClient.setAuthTokenProvider { AuthRepository.getToken() }

                            // Redirigimos según si es nuevo usuario
                            if (authResponse.isNew) {
                                findNavController().navigate(R.id.completeProfileFragment)
                            } else {
                                findNavController().navigate(R.id.homeFragment)
                            }
                        } ?: showToast("Error backend")
                    } else {
                        showToast("Error backend: ${response.code()}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showToast("Error de red")
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



