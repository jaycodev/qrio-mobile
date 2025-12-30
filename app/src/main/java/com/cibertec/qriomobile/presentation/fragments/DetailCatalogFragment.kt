package com.cibertec.qriomobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cibertec.qriomobile.databinding.FragmentDetailCatalogBinding
import com.cibertec.qriomobile.cart.CartManager
import com.bumptech.glide.Glide


class DetailCatalogFragment : Fragment() {

    private var _binding: FragmentDetailCatalogBinding? = null
    private val binding get() = _binding!!

    private var cantidad = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = DetailCatalogFragmentArgs.fromBundle(requireArguments())

        // Datos
        binding.txtNombreDetalle.text = args.nombre
        binding.txtDescripcionDetalle.text = args.descripcion
        binding.txtPrecioDetalle.text = "S/ ${args.precio}"

        // Cargar imagen: Prioridad URL, luego recurso local
        if (!args.imageUrl.isNullOrBlank()) {
            Glide.with(this)
                .load(args.imageUrl)
                .placeholder(R.drawable.empty)
                .error(R.drawable.empty)
                .into(binding.imgDetalleProducto)
        } else {
            binding.imgDetalleProducto.setImageResource(args.imagen)
        }

        // Toolbar back
        binding.toolbarPromos.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Contador
        binding.txtCantidad.text = cantidad.toString()

        binding.btnSumar.setOnClickListener {
            cantidad++
            binding.txtCantidad.text = cantidad.toString()
        }

        binding.btnRestar.setOnClickListener {
            if (cantidad > 1) {
                cantidad--
                binding.txtCantidad.text = cantidad.toString()
            }
        }

        // Comprar
        binding.btnComprarDetalle.setOnClickListener {
            val args = DetailCatalogFragmentArgs.fromBundle(requireArguments())
            // Agregar al carrito con URL de imagen si existe
            CartManager.add(
                productId = args.id,
                name = args.nombre,
                price = args.precio.toDouble(),
                quantity = cantidad,
                imageRes = args.imagen,
                imageUrl = args.imageUrl // <--- Pasamos la URL al carrito
            )
            // Ir al carrito
            findNavController().navigate(R.id.fragment_car)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
