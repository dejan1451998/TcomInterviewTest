package com.dldev.tcom

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.fragment.app.activityViewModels
import com.dldev.tcom.adapters.InfoWindowAdapter
import com.dldev.tcom.network.models.Vehicle
import com.dldev.tcom.view_model.VehicleViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private val vehicleViewModel: VehicleViewModel by activityViewModels()
    private var selectedMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        mapView = view.findViewById(R.id.map)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        vehicleViewModel.selectedCategory.observe(viewLifecycleOwner) { selectedCategory ->
            vehicleViewModel.vehicles.observe(viewLifecycleOwner) { vehicles ->
                vehicles?.let {
                    googleMap?.clear()
                    it.filter { vehicle ->
                        vehicle.vehicleTypeID.toString() == selectedCategory
                    }.forEach { vehicle ->
                        addMarkerForVehicle(vehicle)
                    }
                }
            }
        }

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map.apply {
            setInfoWindowAdapter(InfoWindowAdapter(LayoutInflater.from(context), vehicleViewModel))

            setOnMarkerClickListener { marker ->
                selectedMarker?.let {
                    it.setIcon(getMarkerBitmapFromView(it.snippet ?: "", Color.BLACK))
                }

                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(marker.position, 15f)
                googleMap?.animateCamera(cameraUpdate)

                marker.setIcon(getMarkerBitmapFromView(marker.snippet ?: "", resources.getColor(R.color.active_icon)))
                selectedMarker = marker

                marker.showInfoWindow()

                true
            }

            setOnInfoWindowClickListener { marker ->
                val vehicle = marker.tag as Vehicle

                val intent = Intent(context, VehicleScreen::class.java).apply {
                    putExtra("vehicleID", vehicle.vehicleID)
                }

                context?.startActivity(intent)
            }

            setOnInfoWindowCloseListener { marker ->
                marker.setIcon(getMarkerBitmapFromView(marker.snippet ?: "", Color.BLACK))
            }
        }

        val beograd = LatLng(44.7866, 20.4489)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(beograd, 10f))

        vehicleViewModel.vehicles.observe(viewLifecycleOwner) { vehicles ->
            googleMap?.clear()
            vehicles.forEach { vehicle ->
                addMarkerForVehicle(vehicle)
            }
        }

        vehicleViewModel.filteredVehicles.observe(viewLifecycleOwner) { vehicles ->
            googleMap?.clear()
            vehicles.forEach { vehicle ->
                if (vehicle.name.contains(vehicleViewModel._searchQuery.value ?: "", ignoreCase = true)) {
                    addMarkerForVehicle(vehicle)
                }
            }
        }
    }


    private fun addMarkerForVehicle(vehicle: Vehicle) {
        val location = LatLng(vehicle.location.latitude, vehicle.location.longitude)
        val markerOptions = MarkerOptions().position(location)
            .icon(getMarkerBitmapFromView("${vehicle.price}€", Color.BLACK))
            .title(vehicle.name)
            .snippet("${vehicle.price}€")
        val marker = googleMap?.addMarker(markerOptions)
        marker?.tag = vehicle
    }


    private fun getMarkerBitmapFromView(price: String, @ColorInt color: Int): BitmapDescriptor {
        val customMarkerView = LayoutInflater.from(context).inflate(R.layout.marker_layout, null)
        val markerTextView = customMarkerView.findViewById<TextView>(R.id.marker_text)
        markerTextView.text = price
        val background = customMarkerView.background.mutate() as GradientDrawable
        background.setColor(color)

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.layout(0, 0, customMarkerView.measuredWidth, customMarkerView.measuredHeight)
        customMarkerView.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(customMarkerView.measuredWidth, customMarkerView.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        customMarkerView.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }



    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        mapView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}