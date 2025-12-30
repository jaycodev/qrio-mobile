package com.cibertec.qriomobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.cibertec.qriomobile.data.model.ProductDto
import com.cibertec.qriomobile.databinding.FragmentCatalogBinding
import com.cibertec.qriomobile.presentation.adapters.CatalogAdapter
import com.cibertec.qriomobile.data.remote.NetworkResult
import com.cibertec.qriomobile.data.RetrofitClient
import com.cibertec.qriomobile.data.remote.api.ApiService
import com.cibertec.qriomobile.data.repository.ProductRepository
import com.cibertec.qriomobile.R
import com.cibertec.qriomobile.cart.CartManager
import kotlinx.coroutines.launch


class CatalogFragment : Fragment() {

    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CatalogAdapter
    private val args: CatalogFragmentArgs by navArgs()
    private val api: ApiService by lazy { RetrofitClient.api }
    private val productRepo by lazy { ProductRepository(api) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarPromos.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.recyclerComercios.layoutManager =
            GridLayoutManager(requireContext(), 2)
        adapter = CatalogAdapter(emptyList()) { product ->
            onProductClicked(product)
        }

        binding.recyclerComercios.adapter = adapter

        val branchId = args.branchId
        val tableNumber = args.tableNumber

        // Persistimos contexto del QR para el carrito / pedidos
        CartManager.branchId = branchId
        CartManager.tableNumber = tableNumber

        if (branchId > 0) {
            loadProducts(branchId)
        }

        // Navegar al chat de IA
        binding.fabAiChat.setOnClickListener {
            val action = CatalogFragmentDirections.actionCatalogToChat(branchId)
            findNavController().navigate(action)
        }

        // Navegar al Carrito (Nuevo)
        binding.btnCartContainer.setOnClickListener {
            findNavController().navigate(R.id.fragment_car)
        }

        // Actualizar Badge inicialmente
        updateCartBadge()
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge()
    }

    private fun updateCartBadge() {
        val count = CartManager.count()
        if (count > 0) {
            binding.txtCartBadge.text = count.toString()
            binding.txtCartBadge.visibility = View.VISIBLE
        } else {
            binding.txtCartBadge.visibility = View.GONE
        }
    }

    private fun onProductClicked(product: ProductDto) {
        val action =
            CatalogFragmentDirections
                .actionCatalogFragmentToDetailCatalogFragment(
                    id = product.id,
                    nombre = product.name,
                    descripcion = product.description ?: "",
                    precio = product.price.toFloat(),
                    imagen = product.imageRes ?: 0,
                    imageUrl = product.imageUrl
                )

        findNavController().navigate(action)
    }

    private fun loadProducts(branchId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                when (val res = productRepo.getProductsByBranch(branchId)) {
                    is NetworkResult.Success -> adapter = CatalogAdapter(res.data) { product -> onProductClicked(product) }.also {
                        binding.recyclerComercios.adapter = it
                    }
                    else -> {
                        adapter = CatalogAdapter(emptyList()) { product -> onProductClicked(product) }.also {
                            binding.recyclerComercios.adapter = it
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
