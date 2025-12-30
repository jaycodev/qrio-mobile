package com.cibertec.qriomobile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.navigation.fragment.findNavController
import com.cibertec.qriomobile.R
import com.cibertec.qriomobile.databinding.FragmentTradeBinding
import com.cibertec.qriomobile.data.model.RestaurantDto
import com.cibertec.qriomobile.presentation.adapter.TradeAdapter
import com.cibertec.qriomobile.data.RetrofitClient
import com.cibertec.qriomobile.data.remote.NetworkResult
import com.cibertec.qriomobile.data.remote.api.ApiService
import com.cibertec.qriomobile.data.repository.RestaurantRepository

class TradeFragment : Fragment() {

    private var _binding: FragmentTradeBinding? = null
    private val binding get() = _binding!!

    private val api: ApiService by lazy { RetrofitClient.api }
    private val restaurantRepo by lazy { RestaurantRepository(api) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTradeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecycler()
        loadRestaurants()
    }

    private fun setupToolbar() {
        binding.toolbarPromos.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecycler() {
        binding.recyclerComercios.layoutManager = GridLayoutManager(requireContext(), 3)
        // Adaptador inicial vacío
        binding.recyclerComercios.adapter = TradeAdapter(emptyList()) { }
    }

    private fun loadRestaurants() {
        Log.d("TradeFragment", "loadRestaurants: Iniciando petición...")
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            val result = restaurantRepo.getRestaurants()
            Log.d("TradeFragment", "loadRestaurants: Resultado obtenido: $result")
            
            when (result) {
                is NetworkResult.Success -> {
                    Log.d("TradeFragment", "loadRestaurants: Éxito. Cantidad: ${result.data.size}")
                    val items = result.data.map { r ->
                        RestaurantDto(
                            id = r.id,
                            name = r.name,
                            description = r.description,
                            logo_url = if (r.logo_url != 0) r.logo_url else R.drawable.ic_company,
                            is_active = r.is_active
                        )
                    }
                    binding.recyclerComercios.adapter = TradeAdapter(items) { restaurant ->
                        Log.d("TradeFragment", "Click en restaurante: ${restaurant.name} (ID: ${restaurant.id})")
                        val rid = restaurant.id ?: 0L
                        // Navegar a la lista de sucursales (BranchListFragment)
                        val action = TradeFragmentDirections.actionTradeFragmentToBranchListFragment(rid)
                        findNavController().navigate(action)
                    }
                }
                else -> {
                    Log.e("TradeFragment", "loadRestaurants: Error o respuesta desconocida")
                    Toast.makeText(requireContext(), "Sin restaurantes", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
