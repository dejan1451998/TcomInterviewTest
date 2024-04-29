package com.dldev.tcom.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dldev.tcom.network.VehicleApiService
import com.dldev.tcom.network.models.Vehicle
import kotlinx.coroutines.launch
import retrofit2.Response

class VehicleViewModel(private val vehicleApiService: VehicleApiService) : ViewModel() {
    private val _vehicles = MutableLiveData<List<Vehicle>>()
    val vehicles: LiveData<List<Vehicle>> = _vehicles
    val favoriteVehicles = MutableLiveData<List<Vehicle>>()
    private val _selectedCategory = MutableLiveData<String?>()
    val selectedCategory: LiveData<String?> = _selectedCategory
    val categoryToTypeId = mapOf(
        "Auto" to 1,
        "Motor" to 2,
        "Kamion" to 3
    )
    private val lastLoadedCategory = MutableLiveData<Int?>()
    val _searchQuery = MutableLiveData<String>("")
    val _sortOrder = MutableLiveData(SortingOrder.ASCENDING)


    private val _filteredVehicles = MediatorLiveData<List<Vehicle>>()
    val filteredVehicles: LiveData<List<Vehicle>> = _filteredVehicles

    init {
        _filteredVehicles.addSource(_vehicles) { filterAndSortVehicles() }
        _filteredVehicles.addSource(_searchQuery) { filterAndSortVehicles() }
        _filteredVehicles.addSource(_sortOrder) { filterAndSortVehicles() }
        _filteredVehicles.addSource(_selectedCategory) { filterAndSortVehicles() }
        loadVehicles()
    }

    enum class SortingOrder { ASCENDING, DESCENDING }

    fun setSelectedCategory(categoryName: String?) {
        categoryName?.let {
            val typeId = categoryToTypeId[it]
            _selectedCategory.value = typeId.toString()
            if (typeId != lastLoadedCategory.value) {
                lastLoadedCategory.value = typeId
                loadVehicles(typeId)
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        filterAndSortVehicles()
    }

    fun setSortOrder(order: SortingOrder) {
        _sortOrder.value = order
    }

    private fun filterAndSortVehicles() {
        val currentCategory = _selectedCategory.value.orEmpty()
        val searchQuery = _searchQuery.value.orEmpty().lowercase()
        val sortOrder = _sortOrder.value ?: SortingOrder.ASCENDING

        val filtered = _vehicles.value.orEmpty()
            .filter { it.name.lowercase().contains(searchQuery) && it.vehicleTypeID.toString() == currentCategory }

        val sorted = when (sortOrder) {
            SortingOrder.ASCENDING -> filtered.sortedBy { it.price }
            SortingOrder.DESCENDING -> filtered.sortedByDescending { it.price }
        }

        _filteredVehicles.value = sorted
    }

    fun loadVehicles(categoryId: Int? = null) {
        viewModelScope.launch {
            try {
                val response = vehicleApiService.getAllVehicles()
                if (response.isSuccessful && response.body() != null) {
                    val vehicles = response.body()!!
                    val filteredVehicles = categoryId?.let { id ->
                        vehicles.filter { vehicle -> vehicle.vehicleTypeID == id }
                    } ?: vehicles
                    _vehicles.postValue(filteredVehicles)
                    _vehicles.value = vehicles
                    updateFavoriteVehicles()
                } else {
                    Log.e("VehicleViewModel", "Error loading vehicles: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("VehicleViewModel", "Error loading vehicles", e)
            }
        }
    }

    suspend fun getVehicle(vehicleID: Int): Response<Vehicle> {
        return vehicleApiService.getVehicle(vehicleID)
    }


    fun toggleFavorite(vehicle: Vehicle, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isCurrentlyFavorite = vehicle.isFavorite
            Log.d("Favorites", "Attempting to toggle favorite. Current state: ${vehicle.isFavorite}, Vehicle ID: ${vehicle.vehicleID}")
            val response = if (isCurrentlyFavorite) {
                vehicleApiService.removeFromFavorites(vehicle.vehicleID)
            } else {
                vehicleApiService.addToFavorites(vehicle.vehicleID)
            }

            if (response.isSuccessful) {
                val updatedList = _vehicles.value?.map { v ->
                    if (v.vehicleID == vehicle.vehicleID) v.copy(isFavorite = !isCurrentlyFavorite) else v
                }
                _vehicles.postValue(updatedList!!)
                updateFavoriteVehicles()
                callback(!isCurrentlyFavorite)
                Log.d("Favorites", "Favorite toggled successfully. New state: ${!isCurrentlyFavorite}")
            } else {
                Log.e("Favorites", "Error toggling favorite status: ${response.errorBody()?.string()}")
            }
        }
    }




    fun updateFavoriteVehicles() {
        favoriteVehicles.value = _vehicles.value?.filter { it.isFavorite }
    }
}