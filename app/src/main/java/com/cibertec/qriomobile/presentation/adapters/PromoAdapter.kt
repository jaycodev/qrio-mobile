package com.cibertec.qriomobile.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cibertec.qriomobile.data.model.PromoUi
import com.cibertec.qriomobile.databinding.ItemPromoBinding


class PromoAdapter(
    private val items: List<PromoUi>,
    private val onClick: (PromoUi) -> Unit
) : RecyclerView.Adapter<PromoAdapter.PromoViewHolder>() {

    inner class PromoViewHolder(
        val binding: ItemPromoBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoViewHolder {
        val binding = ItemPromoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PromoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PromoViewHolder, position: Int) {
        val item = items[position]
        val b = holder.binding

        b.imgPromo.setImageResource(item.imageUrl)
        b.tvNombre.text = item.name
        b.tvPrecio.text = "S/ ${item.priceFinal}"
        b.tvPrecioTachado.text = "S/ ${item.priceOriginal}"

        if (item.discountPercent != null) {
            b.tvDescuento.text = "-${item.discountPercent.toInt()}%"
        } else {
            b.tvDescuento.visibility = View.GONE
        }

        b.root.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
