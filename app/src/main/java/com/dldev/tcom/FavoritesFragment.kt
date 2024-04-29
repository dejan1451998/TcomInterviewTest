package com.dldev.tcom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dldev.tcom.adapters.VehicleRecyclerViewAdapter
import com.dldev.tcom.view_model.VehicleViewModel


class FavoritesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private val vehicleViewModel: VehicleViewModel by activityViewModels()
    private lateinit var adapter: VehicleRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_view_favorites)
        adapter = VehicleRecyclerViewAdapter(vehicleViewModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        vehicleViewModel.favoriteVehicles.observe(viewLifecycleOwner) { favoriteVehicles ->
            if (favoriteVehicles.isNullOrEmpty()) {

            } else {
                adapter.vehicles = favoriteVehicles
            }
        }
    }
}
