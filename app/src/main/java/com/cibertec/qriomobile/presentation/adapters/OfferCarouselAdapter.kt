package com.cibertec.qriomobile.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.qriomobile.R
import com.cibertec.qriomobile.data.model.OfferItemDto
import com.cibertec.qriomobile.databinding.ItemHomeBinding

class OfferCarouselAdapter(
    private var items: List<OfferItemDto>,
    private val onClick: (OfferItemDto) -> Unit
) : RecyclerView.Adapter<OfferCarouselAdapter.VH>() {

    inner class VH(val binding: ItemHomeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val b = holder.binding
        b.imgProducto.setImageResource(R.drawable.empty)
        b.txtNombreProducto.text = item.title
        // Este layout no tiene campo de descuento; se omite
        b.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<OfferItemDto>) {
        items = newItems
        notifyDataSetChanged()
    }
}
