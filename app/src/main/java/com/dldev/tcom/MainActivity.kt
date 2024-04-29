package com.dldev.tcom

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dldev.tcom.network.ApiClient
import com.dldev.tcom.view_model.VehicleViewModel
import com.dldev.tcom.view_model.VehicleViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val factory = VehicleViewModelFactory(ApiClient.vehicleApiService)
        val vehicleViewModel = ViewModelProvider(this, factory).get(VehicleViewModel::class.java)

        bottomNavigation = findViewById(R.id.bottom_navigation)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.map_tab -> {
                    setFragment(MapFragment())
                    true
                }
                R.id.list_tab -> {
                    setFragment(ListFragment())
                    true
                }
                R.id.favorite_tab -> {
                    setFragment(FavoritesFragment())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            setFragment(MapFragment())
            bottomNavigation.selectedItemId = R.id.map_tab
        }
    }

    private fun setFragment(fragment: Fragment) {
        val tag = fragment.javaClass.simpleName
        val existingFragment = supportFragmentManager.findFragmentByTag(tag)
        supportFragmentManager.beginTransaction().apply {
            supportFragmentManager.fragments.forEach { fragment ->
                hide(fragment)
            }

            if (existingFragment != null) {
                show(existingFragment)
            } else {
                add(R.id.fragment_container, fragment, tag)
            }
        }.commit()
    }
}