package com.cibertec.qriomobile.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cibertec.qriomobile.R
import com.cibertec.qriomobile.auth.AuthRepository
import com.cibertec.qriomobile.cart.CartManager
import com.cibertec.qriomobile.data.RetrofitClient
import com.cibertec.qriomobile.data.model.CreateOrderItemDto
import com.cibertec.qriomobile.data.model.CreateOrderRequestDto
import com.cibertec.qriomobile.databinding.FragmentSummaryStripeBinding
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SummaryStripeFragment : Fragment() {

    private var _binding: FragmentSummaryStripeBinding? = null
    private val binding get() = _binding!!

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
        val discount = 0.0 
        val total = subtotal - discount

        binding.txtSubtotal.text = "S/ %.2f".format(subtotal)
        binding.txtDescuento.text = "- S/ %.2f".format(discount)
        binding.txtTotal.text = "S/ %.2f".format(total)
    }

    private fun createOrder() {
        val branchId = CartManager.branchId
        val tableNum = CartManager.tableNumber
        val selectedTableId = CartManager.tableId
        val totalAmount = CartManager.total()

        // OBTENER CLIENTE REAL DESDE AUTH REPOSITORY
        val customerId = AuthRepository.getCustomerId()
        
        if (customerId == null || customerId == 0L) {
             Toast.makeText(context, "Error: Sesión no válida. Inicia sesión de nuevo.", Toast.LENGTH_LONG).show()
             AuthRepository.logout()
             findNavController().navigate(R.id.loginFragment)
             return
        }

        // Validación de sucursal
        if (branchId == 0L) {
            Toast.makeText(context, "Error: No se ha seleccionado una sucursal.", Toast.LENGTH_SHORT).show()
            return
        }

        val orderItems = CartManager.getItems().map { item ->
            CreateOrderItemDto(
                productId = item.productId,
                quantity = item.quantity,
                unitPrice = BigDecimal.valueOf(item.unitPrice)
            )
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val currentDate = sdf.format(Date())

        binding.btnPagar.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            var tableIdToSend = when {
                selectedTableId > 0L -> selectedTableId
                tableNum > 0 -> tableNum.toLong()
                else -> 1L
            }
            if (selectedTableId <= 0L && tableNum > 0) {
                // Intentar resolver el ID real consultando opciones de filtro
                try {
                    val optResp = RetrofitClient.api.getOrderFilterOptions(branchId)
                    if (optResp.isSuccessful) {
                        val options = optResp.body()?.data?.tables ?: emptyList()
                        val match = options.firstOrNull { it.label.contains("Mesa $tableNum") }
                        if (match != null) {
                            tableIdToSend = match.value
                            Log.d("SummaryStripe", "Resuelto tableId real=${match.value} para label='${match.label}'")
                        } else {
                            Log.w("SummaryStripe", "No se encontró opción para Mesa $tableNum, usando $tableIdToSend")
                        }
                    } else {
                        Log.w("SummaryStripe", "filter-options HTTP ${optResp.code()} → no se pudo resolver tableId")
                    }
                } catch (e: Exception) {
                    Log.e("SummaryStripe", "Error resolviendo tableId", e)
                }
            } else if (selectedTableId <= 0L && tableNum <= 0) {
                Log.w("SummaryStripe", "Usando tableId por defecto=1L (no hay selección válida)")
            }

            val request = CreateOrderRequestDto(
                branchId = branchId,
                tableId = tableIdToSend,
                customerId = customerId,
                total = BigDecimal.valueOf(totalAmount),
                people = 1,
                orderDate = currentDate,
                items = orderItems
            )
            try {
                val jsonRequest = Gson().toJson(request)
                Log.d("SummaryStripe", "JSON enviado: $jsonRequest")

                val response = RetrofitClient.api.createOrder(request)
                
                if (response.isSuccessful) {
                    CartManager.clear()
                    // Aquí es donde navegarías a Stripe si ya tuvieras el clientSecret
                    findNavController().navigate(R.id.action_summaryStripeFragment_to_confirmationStripeFragment)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("SummaryStripe", "Error ${response.code()}: $errorBody")
                    Toast.makeText(context, "Error al crear pedido (${response.code()})", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("SummaryStripe", "Excepción", e)
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
