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
            // Se cambió la lógica del QR a selección manual en Trade o CatalogFragment
            // Navegamos al TradeFragment para seleccionar Comercio -> Sucursal
            findNavController().navigate(R.id.action_homeFragment_to_tradeFragment)
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
}
