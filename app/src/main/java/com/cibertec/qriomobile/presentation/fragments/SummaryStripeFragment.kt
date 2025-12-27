package com.cibertec.qriomobile.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cibertec.qriomobile.R
import com.cibertec.qriomobile.cart.CartManager
import com.cibertec.qriomobile.data.RetrofitClient
import com.cibertec.qriomobile.data.model.CreateOrderItemDto
import com.cibertec.qriomobile.data.model.CreateOrderRequestDto
import com.cibertec.qriomobile.data.remote.NetworkResult
import com.cibertec.qriomobile.data.remote.api.ApiService
import com.cibertec.qriomobile.databinding.FragmentSummaryStripeBinding
import java.math.BigDecimal

class SummaryStripeFragment : Fragment() {

    private var _binding: FragmentSummaryStripeBinding? = null
    private val binding get() = _binding!!

    // Simular ID de cliente (en una app real vendría del Auth/User)
    private val fakeCustomerId = 2L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryStripeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Llenar datos
        val subtotal = CartManager.total()
        val discount = 0.0 // Lógica de descuentos
        val total = subtotal - discount

        binding.txtSubtotal.text = "S/ %.2f".format(subtotal)
        binding.txtDescuento.text = "- S/ %.2f".format(discount)
        binding.txtTotal.text = "S/ %.2f".format(total)

        binding.btnPagar.setOnClickListener {
            createOrder(total)
        }
    }

    private fun createOrder(totalAmount: Double) {
        val branchId = CartManager.branchId
        val tableNum = CartManager.tableNumber

        // Validaciones básicas
        if (branchId <= 0L) {
            Toast.makeText(context, "Error: No se ha detectado sucursal (QR)", Toast.LENGTH_SHORT).show()
            // Permitir continuar para pruebas si lo deseas, o bloquear
            // return 
        }

        // Preparar items
        val orderItems = CartManager.getItems().map { item ->
            CreateOrderItemDto(
                productId = item.productId,
                quantity = item.quantity,
                unitPrice = BigDecimal.valueOf(item.unitPrice)
            )
        }

        // Preparar request
        val request = CreateOrderRequestDto(
            tableId = tableNum.toLong(),
            customerId = fakeCustomerId, // Ajustar con usuario real logueado
            total = BigDecimal.valueOf(totalAmount),
            people = 1,
            items = orderItems
        )

        binding.btnPagar.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            try {
                val api = RetrofitClient.api
                val response = api.createOrder(request)
                
                // Corrección: ApiSuccess no tiene campo 'success', solo 'message' y 'data'.
                // Retrofit considera isSuccessful si el código es 2xx.
                if (response.isSuccessful) {
                    val apiResult = response.body()
                    if (apiResult != null) {
                        // Se asume éxito si llega body. Puedes chequear apiResult.data != null si quieres
                        CartManager.clear()
                        findNavController().navigate(R.id.action_summaryStripeFragment_to_confirmationStripeFragment)
                    } else {
                        Toast.makeText(context, "Respuesta vacía del servidor", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Error creando pedido: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Fallo de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnPagar.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
