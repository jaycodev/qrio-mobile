package com.cibertec.qriomobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.cibertec.qriomobile.databinding.FragmentHomeBinding
import com.cibertec.qriomobile.data.model.PromoUi
import com.cibertec.qriomobile.presentation.adapters.HomeAdapter
import com.cibertec.qriomobile.R
import android.net.Uri
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import android.widget.Toast

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lista de promociones (PromoUi)
        val promoList = listOf(
            PromoUi(1, "Pizza 1x1", R.drawable.empty, 28.9, 30.0, 19.0),
            PromoUi(2, "Pizza 2x1", R.drawable.empty, 28.9, 30.0, 19.0),
            PromoUi(3, "Pizza 3x1", R.drawable.empty, 28.9, 30.0, 19.0)
        )

        // Adapter para RecyclerView
        val adapter = HomeAdapter(promoList) { promo ->
            val action = HomeFragmentDirections
                .actionHomeFragmentToDetailPromotionFragment(
                    promoId = promo.id,
                    nombre = promo.name,
                    precioFinal = promo.priceFinal.toFloat(),
                    precioOriginal = promo.priceOriginal.toFloat(),
                    descuento = promo.discountPercent?.toInt() ?: 0,
                    imagen = promo.imageUrl
                )
            findNavController().navigate(action)
        }

        // Configurar RecyclerView
        binding.rvHome.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvHome.adapter = adapter

        // Configurar botones de navegación
        binding.btnComercio.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_tradeFragment)
        }

        binding.btnCatalogo.setOnClickListener {
            // Nueva lógica: Escanear QR y navegar al catálogo con branchId y mesa
            startQrScan()
        }

        binding.btnPromos.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_promotionFragment)
        }

        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        // Mantener navegación con click normal; usar long click para cerrar sesión
        binding.btnProfile.setOnLongClickListener {
            performLogout()
            true
        }
    }

    private fun performLogout() {
        // TODO: Limpiar datos de sesión aquí (ej. SharedPreferences, AuthManager, etc.)
        
        // Navegar al login y limpiar el backstack para que no se pueda volver atrás
        findNavController().navigate(
            R.id.loginFragment,
            null,
            androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true) // Limpia todo el historial
                .build()
        )
    }

    // Launcher para escanear QR usando ZXing Embedded
    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val text = result.contents
            val parsed = parseQrPayload(text)
            if (parsed != null) {
                val (branchId, tableNumber) = parsed
                val action = HomeFragmentDirections
                    .actionHomeFragmentToCatalogFragment(branchId = branchId, tableNumber = tableNumber)
                findNavController().navigate(action)
            } else {
                Toast.makeText(requireContext(), "QR inválido: $text", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Cancelado sin contenido
        }
    }

    private fun startQrScan() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt("Escanea el QR de la mesa")
            setBeepEnabled(true)
            setOrientationLocked(true)
        }
        barcodeLauncher.launch(options)
    }

    // Intenta parsear formatos soportados:
    // 1) Texto estilo "QR_BR{branch}_T{table}_{...}"
    // 2) URI estilo "qrio://table?branchId=1&table=2"
    private fun parseQrPayload(payload: String): Pair<Long, Int>? {
        // Caso URI
        try {
            val uri = Uri.parse(payload)
            if (uri.scheme == "qrio" && uri.host == "table") {
                val b = uri.getQueryParameter("branchId")?.toLongOrNull()
                val t = uri.getQueryParameter("table")?.toIntOrNull()
                if (b != null && b > 0 && t != null && t > 0) return b to t
            }
        } catch (_: Exception) { }

        // Caso patrón QR_BR{branch}_T{table}_SUF
        val regex = Regex("^QR_BR(\\d+)_T(\\d+)_.*$")
        val m = regex.matchEntire(payload)
        if (m != null) {
            val branchId = m.groupValues[1].toLongOrNull()
            val tableNum = m.groupValues[2].toIntOrNull()
            if (branchId != null && branchId > 0 && tableNum != null && tableNum > 0) {
                return branchId to tableNum
            }
        }
        return null
    }
}
