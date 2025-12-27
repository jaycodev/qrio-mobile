package com.cibertec.qriomobile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.cibertec.qriomobile.R
import com.cibertec.qriomobile.data.model.BranchDto
import com.cibertec.qriomobile.data.model.RestaurantDto
import com.cibertec.qriomobile.data.model.OfferItemDto
import com.cibertec.qriomobile.data.remote.NetworkResult
import com.cibertec.qriomobile.data.RetrofitClient
import com.cibertec.qriomobile.data.remote.api.ApiService
import com.cibertec.qriomobile.data.repository.BranchRepository
import com.cibertec.qriomobile.data.repository.RestaurantRepository
import com.cibertec.qriomobile.data.repository.OfferRepository
import com.cibertec.qriomobile.databinding.FragmentPromotionBinding
import com.cibertec.qriomobile.presentation.adapters.OfferCarouselAdapter
import com.cibertec.qriomobile.presentation.adapters.OfferGridAdapter

class PromotionFragment : Fragment() {

    private var _binding: FragmentPromotionBinding? = null
    private val binding get() = _binding!!

    private lateinit var gridAdapter: OfferGridAdapter
    private lateinit var carouselAdapter: OfferCarouselAdapter

    private val api: ApiService by lazy { RetrofitClient.api }
    private val restaurantRepo by lazy { RestaurantRepository(api) }
    private val offerRepo by lazy { OfferRepository(api) }

    private val args: PromotionFragmentArgs by navArgs()

    private var selectedRestaurantId: Long? = null
    private var selectedBranchId: Long? = null

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

        binding.recyclerPromos.layoutManager = GridLayoutManager(requireContext(), 2)

        carouselAdapter = OfferCarouselAdapter(emptyList()) { offer ->
            onOfferClicked(offer)
        }
        binding.carruselPromos.adapter = carouselAdapter

        gridAdapter = OfferGridAdapter(emptyList()) { offer ->
            onOfferClicked(offer)
        }
        binding.recyclerPromos.adapter = gridAdapter

        // Oculta carrusel hasta seleccionar sucursal
        binding.carruselPromos.visibility = View.GONE
        binding.indicatorLayout.visibility = View.GONE

        // Cargar restaurantes y permitir selección (muestra ofertas por restaurante)
        loadRestaurants()

        // Si llega un restaurantId por navegación, cargar sus ofertas de inmediato
        val incomingRestaurantId = args.restaurantId
        incomingRestaurantId?.let { rid ->
            if (rid > 0) {
                selectedRestaurantId = rid
                binding.carruselPromos.visibility = View.VISIBLE
                binding.indicatorLayout.visibility = View.VISIBLE
                loadOffersForSelected()
            }
        }
    }

    private fun onOfferClicked(offer: OfferItemDto) {
        val action = PromotionFragmentDirections
            .actionPromotionFragmentToDetailPromotionFragment(
                promoId = offer.id,
                nombre = offer.title,
                precioFinal = 0f,
                precioOriginal = 0f,
                descuento = offer.offerDiscountPercentage?.toInt() ?: 0,
                imagen = R.drawable.empty
            )
        findNavController().navigate(action)
    }

    private fun loadRestaurants() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            when (val res = restaurantRepo.getRestaurants()) {
                is NetworkResult.Success -> renderRestaurantChips(res.data)
                else -> { /* sin restaurantes */ }
            }
        }
    }

    private fun renderRestaurantChips(restaurants: List<RestaurantDto>) {
        val container = binding.layoutCategorias
        container.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        restaurants.forEach { restaurant ->
            val chip = inflater.inflate(R.layout.item_trade_promotion, container, false)
            val img = chip.findViewById<ImageView>(R.id.imgLogoComercio)
            val txt = chip.findViewById<TextView>(R.id.txtNombreComercio)
            img.setImageResource(R.drawable.ic_company)
            txt.text = restaurant.name
            chip.setOnClickListener {
                selectedRestaurantId = restaurant.id
                binding.carruselPromos.visibility = View.VISIBLE
                binding.indicatorLayout.visibility = View.VISIBLE
                loadOffersForSelected()
            }
            container.addView(chip)
        }
    }

    private fun loadOffersForSelected() {
        val restaurantId = selectedRestaurantId
        Log.d("PromotionFragment", "loadOffersForSelected called. SelectedRestaurantId: $restaurantId")

        if (restaurantId == null) return

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            Log.d("PromotionFragment", "Fetching offers by restaurant from repo...")
            when (val res = offerRepo.getOffersByRestaurant(restaurantId)) {
                is NetworkResult.Success -> {
                    Log.d("PromotionFragment", "Offers fetched successfully for restaurant $restaurantId. Count: ${res.data.size}")
                    carouselAdapter.submitList(res.data)
                    gridAdapter.submitList(res.data)
                }
                else -> {
                    Log.e("PromotionFragment", "Failed to fetch offers or empty result")
                    carouselAdapter.submitList(emptyList())
                    gridAdapter.submitList(emptyList())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
