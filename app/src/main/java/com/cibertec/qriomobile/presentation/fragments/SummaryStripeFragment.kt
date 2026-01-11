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
import com.cibertec.qriomobile.BuildConfig
import com.cibertec.qriomobile.R
import com.cibertec.qriomobile.auth.AuthRepository
import com.cibertec.qriomobile.cart.CartManager
import com.cibertec.qriomobile.data.RetrofitClient
import com.cibertec.qriomobile.data.model.CreateOrderItemDto
import com.cibertec.qriomobile.data.model.CreateOrderRequestDto
import com.cibertec.qriomobile.databinding.FragmentSummaryStripeBinding
import com.google.gson.Gson
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SummaryStripeFragment : Fragment() {

    private var _binding: FragmentSummaryStripeBinding? = null
    private val binding get() = _binding!!

    // Usar delegado lazy para evitar problemas de inicialización de lateinit
    private val paymentSheet: PaymentSheet by lazy {
        PaymentSheet(this, ::onPaymentSheetResult)
    }
    
    private var paymentIntentClientSecret: String? = null
    private var paymentIntentId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryStripeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar Stripe con seguridad
        try {
            val stripeKey = BuildConfig.STRIPE_PUBLISHABLE_KEY
            if (stripeKey.isBlank() || stripeKey.contains("test_placeholder")) {
                Log.e("SummaryStripe", "Stripe Key no configurada correctamente")
            } else {
                PaymentConfiguration.init(requireContext(), stripeKey)
                // Forzar acceso a la propiedad lazy para que se inicialice
                Log.d("SummaryStripe", "Stripe inicializado: ${paymentSheet.hashCode()}")
            }
        } catch (e: Exception) {
            Log.e("SummaryStripe", "Error inicializando Stripe", e)
        }

        // Configurar Toolbar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Mostrar totales
        updateUi()

        // Botón de pago: primero crear PaymentIntent, luego crear orden tras pago
        binding.btnPagar.setOnClickListener {
            startPaymentFlow()
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

    private fun startPaymentFlow() {
        val branchId = CartManager.branchId
        val tableNum = CartManager.tableNumber
        val totalAmount = CartManager.total()

        val customerId = AuthRepository.getCustomerId()
        
        if (customerId == null || customerId == 0L) {
             Toast.makeText(context, "Error: Sesión no válida. Inicia sesión de nuevo.", Toast.LENGTH_LONG).show()
             AuthRepository.logout()
             findNavController().navigate(R.id.loginFragment)
             return
        }

        if (branchId == 0L) {
            Toast.makeText(context, "Error: No se ha seleccionado una sucursal.", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnPagar.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 1) Solicitar PaymentIntent al backend
                val amount = BigDecimal.valueOf(totalAmount)
                val paymentReq = com.cibertec.qriomobile.data.model.PaymentIntentRequestDto(
                    amount = amount,
                    currency = "pen",
                    description = "Pago de orden",
                    orderId = null,
                    receiptEmail = null
                )
                val payResp = RetrofitClient.api.createPaymentIntent(paymentReq)
                if (payResp.isSuccessful) {
                    val intent = payResp.body()
                    Log.d("SummaryStripe", "Intent Stripe: ${Gson().toJson(intent)}")
                    paymentIntentClientSecret = intent?.clientSecret
                    paymentIntentId = intent?.intentId
                    if (!paymentIntentClientSecret.isNullOrBlank()) {
                        presentPaymentSheet()
                    } else {
                        Toast.makeText(context, "Error: no se recibió clientSecret", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = payResp.errorBody()?.string()
                    Log.e("SummaryStripe", "Error creando PaymentIntent ${payResp.code()}: $errorBody")
                    Toast.makeText(context, "Error creando pago (${payResp.code()})", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("SummaryStripe", "Excepción", e)
                Toast.makeText(context, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.btnPagar.isEnabled = true
            }
        }
    }

    private fun createOrder() {
        val branchId = CartManager.branchId
        val tableNum = CartManager.tableNumber
        val totalAmount = CartManager.total()
        val customerId = AuthRepository.getCustomerId() ?: 0L

        val orderItems = CartManager.getItems().map { item ->
            CreateOrderItemDto(
                productId = item.productId,
                quantity = item.quantity,
                unitPrice = BigDecimal.valueOf(item.unitPrice)
            )
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val currentDate = sdf.format(Date())

        val request = CreateOrderRequestDto(
            branchId = branchId,
            tableId = if (tableNum > 0) tableNum.toLong() else 1L,
            customerId = customerId,
            total = BigDecimal.valueOf(totalAmount),
            people = 1,
            orderDate = currentDate,
            items = orderItems,
            paymentIntentId = paymentIntentId
        )

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.createOrder(request)
                if (response.isSuccessful) {
                    CartManager.clear()
                    findNavController().navigate(R.id.action_summaryStripeFragment_to_confirmationStripeFragment)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("SummaryStripe", "Error creando orden ${response.code()}: $errorBody")
                    Toast.makeText(context, "Error creando orden (${response.code()})", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("SummaryStripe", "Excepción creando orden", e)
                Toast.makeText(context, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun presentPaymentSheet() {
        val clientSecret = paymentIntentClientSecret ?: return
        try {
            // El método present() de PaymentSheet recibe el clientSecret y la configuración
            paymentSheet.presentWithPaymentIntent(
                clientSecret,
                PaymentSheet.Configuration(
                    merchantDisplayName = "Qrio Mobile",
                    allowsDelayedPaymentMethods = false
                )
            )
        } catch (e: Exception) {
            Log.e("SummaryStripe", "Error al presentar PaymentSheet", e)
            Toast.makeText(context, "Error al abrir pasarela de pago", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                Log.d("Stripe", "Pago cancelado")
            }
            is PaymentSheetResult.Failed -> {
                Log.e("Stripe", "Error en el pago: ${paymentSheetResult.error}")
                Toast.makeText(context, "Fallo en el pago: ${paymentSheetResult.error.message}", Toast.LENGTH_LONG).show()
            }
            is PaymentSheetResult.Completed -> {
                Log.d("Stripe", "Pago completado con éxito")
                createOrder()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
