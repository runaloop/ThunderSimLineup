package com.catp.thundersimlineup.ui.vehiclelist

import android.os.Bundle
import android.view.*
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.catp.thundersimlineup.MainActivityViewModel
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.StatUtil
import com.catp.thundersimlineup.annotation.ApplicationScope
import com.catp.thundersimlineup.annotation.VehicleListViewModelScope
import com.catp.thundersimlineup.annotation.ActivityViewModelScope
import com.catp.thundersimlineup.ui.BaseFragment
import com.catp.thundersimlineup.ui.BaseViewModel
import com.catp.thundersimlineup.ui.list.configureRecyclerView
import kotlinx.android.synthetic.main.fragment_vehicle_list.*
import toothpick.ktp.KTP
import toothpick.smoothie.viewmodel.closeOnViewModelCleared
import toothpick.smoothie.viewmodel.installViewModelBinding
import javax.inject.Inject

class VehicleListFragment : BaseFragment() {

    @Inject
    lateinit var activityViewModel: MainActivityViewModel

    @Inject
    lateinit var vehicleListViewModel: VehicleListViewModel

    override val viewModel: BaseViewModel
        get() = vehicleListViewModel

    @Inject
    lateinit var statUtil: StatUtil

    lateinit var itemAdapter: VehicleAdapter

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
                    itemAdapter.setFilter(s)
                    itemAdapter.filterItems(1000L)
                }
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, findNavController())
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemAdapter = VehicleAdapter()

        vehicleListViewModel.vehicles.observe(viewLifecycleOwner, Observer { list ->
            itemAdapter.setData(requireContext(), list)
        })
        configureRecyclerView(itemAdapter, rvVehicleList, this, activityViewModel)

        statUtil.sendViewStat(this, "VehicleList")
    }


    override fun onPause() {
        activityViewModel.pushFavorites()
        super.onPause()
    }

    @VisibleForTesting
    private fun injectDependencies() {
        KTP
            .openScopes(ApplicationScope::class.java)
            .openSubScope(ActivityViewModelScope::class.java)
            .openSubScope(VehicleListViewModelScope::class.java)
            .installViewModelBinding<VehicleListViewModel>(this)
            .closeOnViewModelCleared(this)
            .inject(this)
    }
}