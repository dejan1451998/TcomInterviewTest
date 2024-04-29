package com.dldev.tcom.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dldev.tcom.network.VehicleApiService

class VehicleViewModelFactory(
    private val vehicleApiService: VehicleApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VehicleViewModel::class.java)) {
            return VehicleViewModel(vehicleApiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
