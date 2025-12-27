package com.cibertec.qriomobile.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.qriomobile.R
import com.cibertec.qriomobile.cart.CartManager
import com.cibertec.qriomobile.databinding.FragmentCarBinding
import com.cibertec.qriomobile.presentation.adapters.CartAdapter

class CarFragment : Fragment() {

    private var _binding: FragmentCarBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar back
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Mesa
        binding.txtMesa.text = CartManager.tableNumber.toString()

        // Lista carrito
        adapter = CartAdapter(CartManager.getItems()) { item ->
            CartManager.remove(item.productId)
            refreshCart()
        }
        binding.rvCarrito.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCarrito.adapter = adapter

        // Vaciar carrito
        binding.btnVaciar.setOnClickListener {
            CartManager.clear()
            refreshCart()
        }

        // Continuar
        binding.btnContinuar.setOnClickListener {
            if (CartManager.count() == 0) {
                Toast.makeText(context, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Navegar al resumen / pago
            findNavController().navigate(R.id.action_carFragment_to_summaryStripeFragment)
        }

        refreshCart()
    }

    private fun refreshCart() {
        adapter.submitList(CartManager.getItems())
        binding.textView.text = CartManager.count().toString()
        binding.txtTotal.text = "Total: S/ %s".format(String.format("%.2f", CartManager.total()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
