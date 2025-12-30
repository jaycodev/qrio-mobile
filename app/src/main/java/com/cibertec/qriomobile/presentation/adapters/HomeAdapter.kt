package com.cibertec.qriomobile.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cibertec.qriomobile.R

import com.cibertec.qriomobile.data.model.ProductDto
import com.cibertec.qriomobile.data.model.PromoUi
import com.cibertec.qriomobile.databinding.ItemHomeBinding

class HomeAdapter(
    private val items: List<PromoUi>,
    private val onPromoClick: (PromoUi) -> Unit
) : RecyclerView.Adapter<HomeAdapter.PromoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home, parent, false)
        return PromoViewHolder(view, onPromoClick)
    }

    override fun onBindViewHolder(holder: PromoViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class PromoViewHolder(itemView: View, val onClick: (PromoUi) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val txtNombre = itemView.findViewById<TextView>(R.id.txtNombreProducto)
        private val txtPrecio = itemView.findViewById<TextView>(R.id.txtPrecioProducto)
        private val img = itemView.findViewById<ImageView>(R.id.imgProducto)

        private var currentPromo: PromoUi? = null

        init {
            itemView.setOnClickListener {
                currentPromo?.let { onClick(it) }
            }
        }

        fun bind(promo: PromoUi) {
            currentPromo = promo
            txtNombre.text = promo.name
            txtPrecio.text = "S/ ${promo.priceFinal}"
            
            // Usamos Glide para cargar imagen si es posible, o placeholder
            if (promo.imageUrl != 0) {
                 img.setImageResource(promo.imageUrl)
            } else {
                 img.setImageResource(R.drawable.descarga) // Imagen por defecto
            }
        }
    }
}
