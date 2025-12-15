package com.cibertec.qriomobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.qriomobile.R
import com.cibertec.qriomobile.databinding.FragmentTradeBinding
import com.cibertec.qriomobile.data.model.RestaurantDto
import com.cibertec.qriomobile.presentation.adapter.TradeAdapter

class TradeFragment : Fragment() {

    private var _binding: FragmentTradeBinding? = null
    private val binding get() = _binding!!

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
    }

    private fun setupToolbar() {
        binding.toolbarPromos.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecycler() {
        val listaRestaurantes = listOf(
            RestaurantDto(
                id = 1,
                name = "Bembos",
                logo_url = R.drawable.ic_company
            ),
            RestaurantDto(
                id = 2,
                name = "KFC",
                logo_url = R.drawable.ic_company
            ),
            RestaurantDto(
                id = 3,
                name = "Pizza Hut",
                logo_url = R.drawable.ic_company
            )
        )

        binding.recyclerComercios.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = TradeAdapter(listaRestaurantes) { restaurant ->
                Toast.makeText(
                    requireContext(),
                    "Seleccionaste: ${restaurant.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
