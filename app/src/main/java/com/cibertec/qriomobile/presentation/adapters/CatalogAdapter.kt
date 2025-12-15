package com.cibertec.qriomobile.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.qriomobile.data.model.ProductDto
import com.cibertec.qriomobile.databinding.ItemCatalogBinding


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

        b.imgProducto.setImageResource(item.image_url)
        b.txtNombreProducto.text = item.name
        b.txtDescripcionProducto.text = item.description
        b.txtPrecioProducto.text = "S/ ${item.price}"

        b.root.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
