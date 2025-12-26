package com.cibertec.qriomobile.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.qriomobile.data.model.ProductDto
import com.cibertec.qriomobile.databinding.ItemCatalogBinding
import com.bumptech.glide.Glide
import com.cibertec.qriomobile.R


class CatalogAdapter(
    private val items: List<ProductDto>,
    private val onClick: (ProductDto) -> Unit
) : RecyclerView.Adapter<CatalogAdapter.CatalogViewHolder>() {

    inner class CatalogViewHolder(
        val binding: ItemCatalogBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogViewHolder {
        val binding = ItemCatalogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CatalogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CatalogViewHolder, position: Int) {
        val item = items[position]
        val b = holder.binding

            // Cargar imagen: URL si existe, si no recurso local
            when {
                !item.imageUrl.isNullOrBlank() -> {
                    Glide.with(b.imgProducto)
                        .load(item.imageUrl)
                        .placeholder(R.drawable.empty)
                        .error(R.drawable.empty)
                        .into(b.imgProducto)
                }
                item.imageRes != null -> b.imgProducto.setImageResource(item.imageRes)
                else -> b.imgProducto.setImageResource(R.drawable.empty)
            }
        b.txtNombreProducto.text = item.name
        b.txtDescripcionProducto.text = item.description
        b.txtPrecioProducto.text = "S/ ${item.price}"

        b.root.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
