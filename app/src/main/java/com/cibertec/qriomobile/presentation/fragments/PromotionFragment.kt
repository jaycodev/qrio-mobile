package com.cibertec.qriomobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cibertec.qriomobile.R
import com.cibertec.qriomobile.data.model.PromoUi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.cibertec.qriomobile.databinding.FragmentPromotionBinding
import com.cibertec.qriomobile.data.model.ProductDto
import com.cibertec.qriomobile.data.model.OfferDto
import com.cibertec.qriomobile.presentation.adapters.PromoAdapter

class PromotionFragment : Fragment() {

    private var _binding: FragmentPromotionBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PromoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPromotionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerPromos.layoutManager =
            GridLayoutManager(requireContext(), 2)

        // --- Datos fake (como tú dijiste) ---
        val product = ProductDto(
            id = 1,
            category_id = 10,
            name = "1 Parrilla Regular + 1 Papa Regular",
            description = "Combo especial",
            price = 28.90,
            image_url = R.drawable.empty
        )

        val offer = OfferDto(
            id = 1,
            title = "Promo 30%",
            offer_discount_percentage = 30.0
        )

        val promoUi = PromoUi(
            id = product.id,
            name = product.name,
            imageUrl = product.image_url,
            priceOriginal = product.price,
            discountPercent = offer.offer_discount_percentage,
            priceFinal = calcularPrecioFinal(
                product.price,
                offer.offer_discount_percentage
            )
        )

        adapter = PromoAdapter(listOf(promoUi)) { promo ->
            onPromoClicked(promo)
        }

        binding.recyclerPromos.adapter = adapter
    }

    private fun calcularPrecioFinal(precio: Double, descuento: Double?): Double {
        return if (descuento != null)
            precio - (precio * descuento / 100)
        else
            precio
    }

    private fun onPromoClicked(promo: PromoUi) {
        val action =
            PromotionFragmentDirections
                .actionPromotionFragmentToDetailPromotionFragment(
                    promoId = promo.id,
                    nombre = promo.name,
                    precioFinal = promo.priceFinal.toFloat(),
                    precioOriginal = promo.priceOriginal.toFloat(),
                    descuento = promo.discountPercent?.toInt() ?: 0,
                    imagen = promo.imageUrl
                )

        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

