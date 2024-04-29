package com.dldev.tcom

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dldev.tcom.adapters.VehicleRecyclerViewAdapter
import com.dldev.tcom.view_model.VehicleViewModel


class VehicleListFragment : Fragment() {

    companion object {
        private const val ARG_CATEGORY = "category"

        fun newInstance(category: String): VehicleListFragment =
            VehicleListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY, category)
                }
            }
    }

    private var category: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val vehicleViewModel: VehicleViewModel by activityViewModels()
    private val adapter: VehicleRecyclerViewAdapter by lazy {
        VehicleRecyclerViewAdapter(vehicleViewModel)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = arguments?.getString(ARG_CATEGORY)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vehicle_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.vehicles_recycler_view)
        progressBar = view.findViewById(R.id.progress_bar)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerView.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE

        vehicleViewModel.selectedCategory.observe(viewLifecycleOwner) { categoryId ->
            categoryId?.let { updateVehicleList(it) } ?: clearVehicles()
        }

        vehicleViewModel.filteredVehicles.observe(viewLifecycleOwner) { vehicles ->
            progressBar.visibility = View.GONE
            if (vehicles.isNullOrEmpty()) {
                recyclerView.visibility = View.INVISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                adapter.updateVehicles(vehicles)
            }
        }
    }

    private fun updateVehicleList(selectedCategory: String?) {
        vehicleViewModel.vehicles.observe(viewLifecycleOwner) { vehicles ->
            if (vehicles.isNullOrEmpty()) {
                recyclerView.visibility = View.INVISIBLE
                Log.d("VehicleListFragment", "No vehicles loaded")
            } else {
                val filteredVehicles = vehicles.filter { vehicle ->
                    vehicle.vehicleTypeID.toString() == selectedCategory
                }
                if (filteredVehicles.isNotEmpty()) {
                    adapter.vehicles = filteredVehicles
                    recyclerView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.INVISIBLE
                    Log.d("VehicleListFragment", "No vehicles found for category: $selectedCategory")
                }
            }
        }
    }


    fun clearVehicles() {
        adapter.vehicles = emptyList()
        recyclerView.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
    }
}