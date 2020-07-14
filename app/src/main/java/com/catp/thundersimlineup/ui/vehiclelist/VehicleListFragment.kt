package com.catp.thundersimlineup.ui.vehiclelist

import android.os.Bundle
import android.view.*
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.annotation.ApplicationScope
import com.catp.thundersimlineup.annotation.ViewModelScope
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.select.getSelectExtension
import kotlinx.android.synthetic.main.fragment_vehicle_list.*
import toothpick.ktp.KTP
import toothpick.smoothie.viewmodel.closeOnViewModelCleared
import java.util.*
import javax.inject.Inject

class VehicleListFragment : Fragment() {

    @Inject
    lateinit var vehicleListViewModel: VehicleListViewModel

    val itemAdapter = ItemAdapter<VehicleItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_vehicle_list, container, false)
        injectDependencies()
        setHasOptionsMenu(true)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                itemAdapter.filter(s)
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                itemAdapter.filter(s)
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        rvVehicleList.layoutManager = LinearLayoutManager(context)

        vehicleListViewModel.vehicles.observe(this, Observer { list ->
            itemAdapter.set(list.map { VehicleItem(it) })
        })

        val fastAdapter = FastAdapter.with(itemAdapter)
        val selectExtension = fastAdapter.getSelectExtension()
        selectExtension.isSelectable = true
        selectExtension.selectOnLongClick = false
        itemAdapter.itemFilter.filterPredicate = { item: VehicleItem, constraint: CharSequence? ->
            item.vehicle.title.toLowerCase(Locale.getDefault())
                .contains(
                    constraint.toString().toLowerCase(Locale.getDefault())
                )
        }


        rvVehicleList.adapter = fastAdapter

        vehicleListViewModel.viewCreated()



        super.onViewCreated(view, savedInstanceState)
    }

    @VisibleForTesting
    private fun injectDependencies() {
        KTP
            .openScopes(ApplicationScope::class.java)
            .openSubScope(ViewModelScope::class.java)
            .closeOnViewModelCleared(this).apply {
                inject(this@VehicleListFragment)
            }
    }
}