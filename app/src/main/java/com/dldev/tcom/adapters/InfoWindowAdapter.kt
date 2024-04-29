package com.dldev.tcom.adapters

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dldev.tcom.R
import com.dldev.tcom.network.models.Vehicle
import com.dldev.tcom.view_model.VehicleViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker


class InfoWindowAdapter(inflater: LayoutInflater, private val viewModel: VehicleViewModel) : GoogleMap.InfoWindowAdapter {
    private val view = inflater.inflate(R.layout.info_window, null)

    override fun getInfoWindow(marker: Marker): View {
        val vehicle = marker.tag as Vehicle

        val imageView: ImageView = view.findViewById(R.id.car_image)

        Glide.with(view.context)
            .load(vehicle.imageURL)
            .placeholder(R.drawable.placeholder_ic)
            .error(R.drawable.placeholder_ic)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)

        val nameTextView: TextView = view.findViewById(R.id.car_name)
        nameTextView.text = vehicle.name

        val ratingTextView: TextView = view.findViewById(R.id.car_rating)
        ratingTextView.text = "${vehicle.rating} ★"

        val priceTextView: TextView = view.findViewById(R.id.car_price)
        priceTextView.text = "${vehicle.price}€"

        val favoriteIconView: ImageView = view.findViewById(R.id.favorite_icon)
        updateFavoriteIcon(favoriteIconView ,vehicle.isFavorite)

        favoriteIconView.setOnClickListener {
            viewModel.toggleFavorite(vehicle) { isFavorite ->
                updateFavoriteIcon(favoriteIconView, isFavorite)
            }
        }

        return view
    }

    private fun updateFavoriteIcon(favoriteIconView: ImageView,isFavorite: Boolean) {
        favoriteIconView.setImageResource(if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border)
    }

    override fun getInfoContents(marker: Marker): View? {
        return null
    }
}