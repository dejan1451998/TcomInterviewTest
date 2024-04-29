package com.dldev.tcom

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dldev.tcom.network.ApiClient
import com.dldev.tcom.network.models.Vehicle
import com.dldev.tcom.view_model.VehicleViewModel
import com.dldev.tcom.view_model.VehicleViewModelFactory
import kotlinx.coroutines.launch

class VehicleScreen : AppCompatActivity() {
    private lateinit var vehicleViewModel: VehicleViewModel
    private var vehicleID: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vehicle_screen)

        val factory = VehicleViewModelFactory(ApiClient.vehicleApiService)
        vehicleViewModel = ViewModelProvider(this, factory).get(VehicleViewModel::class.java)

        vehicleID = intent.getIntExtra("vehicleID", -1)

        if (vehicleID != -1) {
            getVehicleDetails()
        }

        val prevActivity = findViewById<LinearLayout>(R.id.prev_activity)
        prevActivity.setOnClickListener {
            finish()
        }
    }

    private fun getVehicleDetails() {
        lifecycleScope.launch {
            try {
                val response = vehicleViewModel.getVehicle(vehicleID)
                if (response.isSuccessful && response.body() != null) {
                    val vehicle = response.body()!!
                    displayVehicleDetails(vehicle)
                } else {
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun displayVehicleDetails(vehicle: Vehicle) {
        val carImage = findViewById<ImageView>(R.id.car_image)
        val favoriteIcon = findViewById<ImageView>(R.id.favorite_icon)
        val vehicleModel = findViewById<TextView>(R.id.vehicle_model)
        val vehicleRating = findViewById<TextView>(R.id.vehicle_rating)
        val vehiclePrice = findViewById<TextView>(R.id.vehicle_price)
        val vehicleLatitude = findViewById<TextView>(R.id.vehicle_latitude)
        val vehicleLongitude = findViewById<TextView>(R.id.vehicle_longitude)

        Glide.with(this)
            .load(vehicle.imageURL)
            .placeholder(R.drawable.placeholder_ic)
            .error(R.drawable.placeholder_ic)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(carImage)

        vehicleModel.text = vehicle.name
        vehicleRating.text = "${vehicle.rating} ★"
        vehiclePrice.text = "${vehicle.price}€"
        vehicleLatitude.text = vehicle.location.latitude.toString()
        vehicleLongitude.text = vehicle.location.longitude.toString()
        updateFavoriteIcon(favoriteIcon, vehicle.isFavorite)

        favoriteIcon.setOnClickListener {
            vehicleViewModel.toggleFavorite(vehicle) { isFavorite ->
                updateFavoriteIcon(favoriteIcon, isFavorite)
            }
        }
    }

    private fun updateFavoriteIcon(favoriteIcon: ImageView ,isFavorite: Boolean) {
        favoriteIcon.setImageResource(if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border)
    }
}
