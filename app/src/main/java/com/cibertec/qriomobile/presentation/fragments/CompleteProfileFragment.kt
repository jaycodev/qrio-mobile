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
import com.cibertec.qriomobile.data.repository.CustomerRepository
import com.cibertec.qriomobile.data.remote.NetworkResult
import com.cibertec.qriomobile.databinding.FragmentCompleteProfileBinding
import kotlinx.coroutines.launch

class CompleteProfileFragment : Fragment() {

    private var _binding: FragmentCompleteProfileBinding? = null
    private val binding get() = _binding!!

    // private lateinit var auth: FirebaseAuth // Eliminado porque ya no usas Firebase
    private lateinit var customerRepository: CustomerRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompleteProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // auth = FirebaseAuth.getInstance() // Eliminado

        // Inicializa tu repositorio con RetrofitClient
        customerRepository = CustomerRepository(RetrofitClient.api)

        binding.btnSaveProfile.setOnClickListener {
            saveProfile()
        }
    }

    private fun saveProfile() {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(requireContext(), "Completa los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val customerId = AuthRepository.getCustomerId()
            ?: return Toast.makeText(requireContext(), "Cliente no encontrado", Toast.LENGTH_SHORT).show()

        val customer = CustomerDto(
            name = name,
            phone = phone
        )

        lifecycleScope.launch {
            when (val result = customerRepository.update(customerId, customer)) {
                is NetworkResult.Success -> {
                    Toast.makeText(requireContext(), "Perfil completado", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.homeFragment)
                }
                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
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
