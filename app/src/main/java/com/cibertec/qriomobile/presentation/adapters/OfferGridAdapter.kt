package com.cibertec.qriomobile.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.qriomobile.R
import com.cibertec.qriomobile.data.model.OfferItemDto
import com.cibertec.qriomobile.databinding.ItemPromoBinding

class OfferGridAdapter(
    private var items: List<OfferItemDto>,
    private val onClick: (OfferItemDto) -> Unit
) : RecyclerView.Adapter<OfferGridAdapter.VH>() {

    inner class VH(val binding: ItemPromoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemPromoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val b = holder.binding
        b.imgPromo.setImageResource(R.drawable.empty)
        b.tvNombre.text = item.title

        val discount = item.offerDiscountPercentage
        if (discount != null) {
            b.tvDescuento.visibility = View.VISIBLE
            b.tvDescuento.text = "-${discount.toInt()}%"
        } else {
            b.tvDescuento.visibility = View.GONE
        }
        // No tenemos precios aquí; oculta precio original/final
        b.tvPrecio.visibility = View.GONE
        b.tvPrecioTachado.visibility = View.GONE

        b.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<OfferItemDto>) {
        items = newItems
        notifyDataSetChanged()
    }
}
