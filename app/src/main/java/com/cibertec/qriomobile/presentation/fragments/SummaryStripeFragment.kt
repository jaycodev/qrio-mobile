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
import com.cibertec.qriomobile.databinding.FragmentSummaryStripeBinding
import kotlinx.coroutines.launch
import java.math.BigDecimal

class SummaryStripeFragment : Fragment() {

    private var _binding: FragmentSummaryStripeBinding? = null
    private val binding get() = _binding!!

    // Usamos un ID de cliente fijo ya que indicaste no usar Firebase
    private val defaultCustomerId = 1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryStripeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar Toolbar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Mostrar totales
        updateUi()

        // Botón de pago
        binding.btnPagar.setOnClickListener {
            createOrder()
        }
    }

    private fun updateUi() {
        val subtotal = CartManager.total()
        val discount = 0.0 // Aquí podrías aplicar lógica de cupones si tuvieras
        val total = subtotal - discount

        binding.txtSubtotal.text = "S/ %.2f".format(subtotal)
        binding.txtDescuento.text = "- S/ %.2f".format(discount)
        binding.txtTotal.text = "S/ %.2f".format(total)
    }

    private fun createOrder() {
        val branchId = CartManager.branchId
        val tableNum = CartManager.tableNumber
        val totalAmount = CartManager.total()

        // Validación opcional: verificar si hay sucursal/mesa detectada
        /*
        if (branchId <= 0L) {
             Toast.makeText(context, "No se detectó sucursal (QR)", Toast.LENGTH_SHORT).show()
             return
        }
        */

        val orderItems = CartManager.getItems().map { item ->
            CreateOrderItemDto(
                productId = item.productId,
                quantity = item.quantity,
                unitPrice = BigDecimal.valueOf(item.unitPrice)
            )
        }

        val request = CreateOrderRequestDto(
            tableId = if (tableNum > 0) tableNum.toLong() else 1L, // Default a 1 si no hay mesa
            customerId = defaultCustomerId,
            total = BigDecimal.valueOf(totalAmount),
            people = 1,
            items = orderItems
        )

        binding.btnPagar.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.createOrder(request)
                if (response.isSuccessful) {
                    // Limpiar carrito y navegar a confirmación
                    CartManager.clear()
                    findNavController().navigate(R.id.action_summaryStripeFragment_to_confirmationStripeFragment)
                } else {
                    Toast.makeText(context, "Error al crear pedido: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
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
