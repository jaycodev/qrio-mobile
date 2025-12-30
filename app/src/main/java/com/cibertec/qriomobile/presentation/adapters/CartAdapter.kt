package com.cibertec.qriomobile.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

        // Lógica corregida para cargar imagen
        if (!item.imageUrl.isNullOrBlank()) {
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.empty) // Imagen mientras carga
                .error(R.drawable.empty)       // Imagen si falla
                .into(b.imgProducto)
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
