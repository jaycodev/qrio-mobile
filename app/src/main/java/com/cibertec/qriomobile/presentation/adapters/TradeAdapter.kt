package com.cibertec.qriomobile.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.qriomobile.R
import com.cibertec.qriomobile.data.model.RestaurantDto

class TradeAdapter(
    private val restaurantes: List<RestaurantDto>,
    private val onItemClick: (RestaurantDto) -> Unit
) : RecyclerView.Adapter<TradeAdapter.RestaurantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trade_promotion, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(restaurantes[position])
    }

    override fun getItemCount(): Int = restaurantes.size

    inner class RestaurantViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val imgLogo: ImageView =
            itemView.findViewById(R.id.imgLogoComercio)
        private val txtNombre: TextView =
            itemView.findViewById(R.id.txtNombreComercio)

        fun bind(restaurant: RestaurantDto) {
            txtNombre.text = restaurant.name
            val resId = if (restaurant.logo_url != 0) restaurant.logo_url else R.drawable.ic_company
            imgLogo.setImageResource(resId)

            itemView.setOnClickListener {
                onItemClick(restaurant)
            }
        }
    }
}
