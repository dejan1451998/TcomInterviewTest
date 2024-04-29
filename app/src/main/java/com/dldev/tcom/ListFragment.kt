package com.dldev.tcom

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.dldev.tcom.network.ApiClient
import com.dldev.tcom.view_model.VehicleViewModel
import com.dldev.tcom.view_model.VehicleViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ListFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var searchEditText: EditText
    private lateinit var sortButton: Button
    private val vehicleViewModel: VehicleViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = view.findViewById(R.id.tabs)
        viewPager = view.findViewById(R.id.view_pager)
        searchEditText = view.findViewById(R.id.search_bar)
        sortButton = view.findViewById(R.id.sort_btn)

        val categoryAdapter = CategoryAdapter(this)
        viewPager.adapter = categoryAdapter

        viewPager.setCurrentItem(0, false)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = categoryAdapter.getTitle(position)
        }.attach()

        tabLayout.getTabAt(0)?.select()

        vehicleViewModel.setSelectedCategory(categoryAdapter.getTitle(viewPager.currentItem))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                vehicleViewModel.setSelectedCategory(tab.text.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                vehicleViewModel.setSearchQuery(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        sortButton.setOnClickListener {
            showSortPopup(view)
        }
    }

    private fun showSortPopup(anchor: View) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_sort_menu, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupView.findViewById<TextView>(R.id.tvAscending).setOnClickListener {
            vehicleViewModel.setSortOrder(VehicleViewModel.SortingOrder.ASCENDING)
            popupWindow.dismiss()
        }

        popupView.findViewById<TextView>(R.id.tvDescending).setOnClickListener {
            vehicleViewModel.setSortOrder(VehicleViewModel.SortingOrder.DESCENDING)
            popupWindow.dismiss()
        }


        val xOffset = anchor.width - 200
        val yOffset = -anchor.height + 200


        popupWindow.showAsDropDown(anchor, xOffset, yOffset)
    }



    inner class CategoryAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        private val categories = arrayOf("Auto", "Motor", "Kamion")

        fun getTitle(position: Int): String = categories[position]

        override fun getItemCount(): Int = categories.size

        override fun createFragment(position: Int): Fragment {
            val category = categories[position]
            return VehicleListFragment.newInstance(category)
        }
    }
}
