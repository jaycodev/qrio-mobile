package com.cibertec.qriomobile.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.qriomobile.R
import com.cibertec.qriomobile.cart.CartItem
import com.cibertec.qriomobile.databinding.ItemCartBinding

class CartAdapter(
    private var items: List<CartItem>,
    private val onRemove: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.VH>() {

    inner class VH(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val b = holder.binding
        if (!item.imageUrl.isNullOrBlank()) {
            b.imgProducto.setImageResource(R.drawable.empty)
        } else {
            b.imgProducto.setImageResource(item.imageRes ?: R.drawable.empty)
        }
        b.txtNombreProducto.text = item.name
        b.txtCantidad.text = "x${item.quantity}"
        b.txtPrecioProducto.text = "S/ ${String.format("%.2f", item.subtotal())}"
        b.btnEliminar.setOnClickListener { onRemove(item) }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
