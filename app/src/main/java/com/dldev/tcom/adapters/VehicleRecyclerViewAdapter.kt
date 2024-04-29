package com.dldev.tcom.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dldev.tcom.R
import com.dldev.tcom.VehicleScreen
import com.dldev.tcom.network.models.Vehicle
import com.dldev.tcom.view_model.VehicleViewModel

class VehicleRecyclerViewAdapter(private val viewModel: VehicleViewModel) :
    RecyclerView.Adapter<VehicleRecyclerViewAdapter.VehicleViewHolder>() {

    var vehicles: List<Vehicle> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.info_window, parent, false)
        return VehicleViewHolder(view, viewModel)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val vehicle = vehicles[position]
        holder.bind(vehicle)
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, VehicleScreen::class.java).apply {
                putExtra("vehicleID", vehicle.vehicleID)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = vehicles.size

    class VehicleViewHolder(itemView: View, private val viewModel: VehicleViewModel) :
        RecyclerView.ViewHolder(itemView) {

        private val imageView: ImageView = itemView.findViewById(R.id.car_image)
        private val nameTextView: TextView = itemView.findViewById(R.id.car_name)
        private val ratingTextView: TextView = itemView.findViewById(R.id.car_rating)
        private val priceTextView: TextView = itemView.findViewById(R.id.car_price)
        private val favoriteIconView: ImageView = itemView.findViewById(R.id.favorite_icon)

        fun bind(vehicle: Vehicle) {
            Glide.with(itemView.context)
                .load(vehicle.imageURL)
                .placeholder(R.drawable.placeholder_ic)
                .error(R.drawable.placeholder_ic)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)

            nameTextView.text = vehicle.name
            ratingTextView.text = "${vehicle.rating} ★"
            priceTextView.text = "${vehicle.price}€"

            updateFavoriteIcon(vehicle.isFavorite)

            favoriteIconView.setOnClickListener {
                viewModel.toggleFavorite(vehicle) { isFavorite ->
                    updateFavoriteIcon(isFavorite)
                }
            }
        }

        private fun updateFavoriteIcon(isFavorite: Boolean) {
            favoriteIconView.setImageResource(if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border)
        }
    }



    fun updateVehicles(newVehicles: List<Vehicle>) {
        vehicles = newVehicles
    }
}
