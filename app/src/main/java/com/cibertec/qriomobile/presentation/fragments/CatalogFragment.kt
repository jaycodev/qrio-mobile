package com.cibertec.qriomobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.cibertec.qriomobile.data.model.ProductDto
import com.cibertec.qriomobile.databinding.FragmentCatalogBinding
import com.cibertec.qriomobile.presentation.adapters.CatalogAdapter


class CatalogFragment : Fragment() {

    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CatalogAdapter

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

        binding.recyclerComercios.layoutManager =
            GridLayoutManager(requireContext(), 2)

        // --- Datos fake ---
        val products = listOf(
            ProductDto(
                id = 1,
                category_id = 10,
                name = "Arroz Chaufa",
                description = "Delicioso arroz chaufa con pollo",
                price = 15.90,
                image_url = R.drawable.ic_company
            ),
            ProductDto(
                id = 2,
                category_id = 10,
                name = "Lomo Saltado",
                description = "Clásico lomo saltado peruano",
                price = 18.50,
                image_url = R.drawable.ic_company
            )
        )

        adapter = CatalogAdapter(products) { product ->
            onProductClicked(product)
        }

        binding.recyclerComercios.adapter = adapter
    }

    private fun onProductClicked(product: ProductDto) {
        val action =
            CatalogFragmentDirections
                .actionCatalogFragmentToDetailCatalogFragment(
                    id = product.id,
                    nombre = product.name,
                    descripcion = product.description ?: "",
                    precio = product.price.toFloat(),
                    imagen = product.image_url
                )

        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
