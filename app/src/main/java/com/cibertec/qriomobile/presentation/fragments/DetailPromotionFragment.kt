package com.cibertec.qriomobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cibertec.qriomobile.databinding.FragmentDetailPromotionBinding

class DetailPromotionFragment : Fragment() {

    private var _binding: FragmentDetailPromotionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPromotionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = DetailPromotionFragmentArgs.fromBundle(requireArguments())

        // Imagen
        binding.imgBannerPromo.setImageResource(args.imagen)

        // Textos
        binding.tvNombrePromo.text = args.nombre
        binding.tvPrecio.text = "S/ ${args.precioFinal}"
        binding.tvPrecioOriginal.text = "S/ ${args.precioOriginal}"
        binding.tvDescuento.text = "-${args.descuento}%"

        // Toolbar back
        binding.toolbarPromos.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
