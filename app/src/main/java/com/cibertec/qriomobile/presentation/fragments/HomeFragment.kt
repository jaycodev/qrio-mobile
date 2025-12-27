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
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import android.net.Uri
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

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
            launchQrScanner()
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

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        val contents = result.contents ?: return@registerForActivityResult
        parseBranchAndTable(contents)?.let { (branchId, tableNumber) ->
            val action = HomeFragmentDirections.actionHomeFragmentToCatalogFragment(branchId, tableNumber)
            findNavController().navigate(action)
        }
    }

    private fun launchQrScanner() {
        val options = ScanOptions()
            .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            .setPrompt("Escanea el QR de la sucursal")
            .setBeepEnabled(true)
            .setOrientationLocked(false)
        barcodeLauncher.launch(options)
    }

    private fun parseBranchAndTable(text: String): Pair<Long, Int>? {
        // 1) Intentar parsear JSON con keys esperadas: { branchId, tableNumber, v }
        runCatching {
            val json = org.json.JSONObject(text)
            val b = json.optLong("branchId", -1L)
            val t = json.optInt("tableNumber", -1)
            if (b > 0 && t > 0) return Pair(b, t)
        }

        // 2) Soportar formatos simples: dos números en cualquier formato
        // Ejemplos válidos: "123,7", "branch=123;table=7", "123|7", "Suc:123 Mesa:7"
        val nums = Regex("\\d+").findAll(text).map { it.value.toLong() }.toList()
        if (nums.size >= 2) {
            val branchId = nums[0]
            val tableNumber = nums[1].toInt()
            return Pair(branchId, tableNumber)
        }

        // 3) Fallback: intentar parsear URL con query params
        runCatching { Uri.parse(text) }.getOrNull()?.let { uri ->
            val b = (uri.getQueryParameter("branch") ?: uri.getQueryParameter("branchId"))?.toLongOrNull()
            val t = (uri.getQueryParameter("table") ?: uri.getQueryParameter("tableNumber"))?.toIntOrNull()
            if (b != null && t != null) return Pair(b, t)
        }
        return null
    }

    private fun performLogout() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val api = com.cibertec.qriomobile.data.RetrofitClient.create(com.cibertec.qriomobile.auth.AuthApi::class.java)
                runCatching { api.logout() }
                com.cibertec.qriomobile.auth.AuthManager.clear()
                val nav = findNavController()
                nav.popBackStack(nav.graph.startDestinationId, false)
            } catch (_: Exception) {
                com.cibertec.qriomobile.auth.AuthManager.clear()
                val nav = findNavController()
                nav.popBackStack(nav.graph.startDestinationId, false)
            }
        }
    }
}
