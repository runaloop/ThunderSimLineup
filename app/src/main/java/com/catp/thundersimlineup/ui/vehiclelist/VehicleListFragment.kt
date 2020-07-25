package com.catp.thundersimlineup.ui.vehiclelist

import android.os.Bundle
import android.view.*
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.annotation.ApplicationScope
import com.catp.thundersimlineup.annotation.ViewModelScope
import com.catp.thundersimlineup.setToolbarText
import com.catp.thundersimlineup.ui.lineuplist.LineupListViewModel
import com.catp.thundersimlineup.ui.list.configureRecyclerView
import kotlinx.android.synthetic.main.fragment_vehicle_list.*
import toothpick.ktp.KTP
import toothpick.smoothie.viewmodel.closeOnViewModelCleared
import javax.inject.Inject

class VehicleListFragment : Fragment() {

    @Inject
    lateinit var vehicleListViewModel: VehicleListViewModel

    @Inject
    lateinit var lineupListViewModel: LineupListViewModel

    val itemAdapter = VehicleAdapter()

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
        inflater.inflate(R.menu.vehicle_list_menu, menu)
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return onQueryTextChange(s)
            }

            override fun onQueryTextChange(s: String): Boolean {
                if (itemAdapter.hasNewFilter(s)) {
                    itemAdapter.setFilter(s);
                    itemAdapter.filterItems(1000L)
                }
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        vehicleListViewModel.vehicles.observe(this, Observer { list ->
            itemAdapter.setData(requireContext(), list)
        })

        configureRecyclerView(itemAdapter, rvVehicleList, this, lineupListViewModel)


        vehicleListViewModel.viewCreated()

        setToolbarText(R.string.title_vehicles)
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onDetach() {
        lineupListViewModel.pushFavorites()
        super.onDetach()
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