package com.catp.thundersimlineup.ui.lineuplist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.catp.model.VehicleType
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.annotation.ApplicationScope
import com.catp.thundersimlineup.annotation.ViewModelScope
import com.catp.thundersimlineup.data.db.entity.Vehicle
import com.catp.thundersimlineup.ui.vehiclelist.VehicleItem
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.fastadapter.FastAdapter
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import kotlinx.android.synthetic.main.lineup_list.*
import toothpick.ktp.KTP
import toothpick.smoothie.viewmodel.closeOnViewModelCleared
import javax.inject.Inject


class LineupListFragment : Fragment() {

    @Inject
    lateinit var lineupListViewModel: LineupListViewModel

    val lineupAdapter = LineupAdapter()
    val stickyHeaderAdapter = StickyHeaderAdapter<VehicleItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.lineup_list, container, false)
        injectDependencies()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lineupListViewModel.text.observe(this, Observer {
            if (it.isNotEmpty() && getView() != null) {
                Snackbar.make(getView()!!, it, Snackbar.LENGTH_LONG).show()
            }
        })

        val fastAdapter = FastAdapter.with(lineupAdapter)

        rvLineupList.layoutManager = LinearLayoutManager(context)
        //TODO: Check the diff
        //rvLineupList.itemAnimator = DefaultItemAnimator()
        rvLineupList.adapter = stickyHeaderAdapter.wrap(fastAdapter)

        val decoration = StickyRecyclerHeadersDecoration(stickyHeaderAdapter)
        rvLineupList.addItemDecoration(decoration)


        lineupListViewModel.filterStatus.observe(this, Observer { filter ->
            lineupAdapter.setFilterState(requireContext(), filter)
        })

        lineupListViewModel.currentLineup.observe(this, Observer { lineup ->
            lineupAdapter.setNewLineup(requireContext(), lineup)
            updateLineupText(lineup)
        })

        listOf(
            teamAChip,
            teamBChip,
            tanksChip,
            planesChip,
            helisChip,
            lowLineupChip,
            highLineupChip
        ).forEach {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                lineupListViewModel.filterChange(
                    "",
                    teamAChip.isChecked,
                    teamBChip.isChecked,
                    tanksChip.isChecked,
                    planesChip.isChecked,
                    helisChip.isChecked,
                    lowLineupChip.isChecked,
                    highLineupChip.isChecked
                )
            }
        }

        lineupListViewModel.refreshData(false)

        fab.setOnClickListener {
            findNavController(this).navigate(R.id.action_lineup_list_fragment_to_vehicle_list)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun updateLineupText(lineup: LineupRequestInteractor.LineupForToday) {
        val currentLineupLow = lineup.lineupNow.first!!.lineupEntity.name
        val currentLineupTop = lineup.lineupNow.second!!.lineupEntity.name
        val nextLineupLow = lineup.lineupThen.first!!.lineupEntity.name
        val nextLineupTop = lineup.lineupThen.second!!.lineupEntity.name
        val hoursToChange = lineup.timeToChange.toHours().toString()
        val minutesToChange = (lineup.timeToChange.toMinutes() % 60).toString()
        if (lineup.timeToChange.isZero) {
            //hide
            tvCurrentLineup.setText(
                getString(
                    R.string.selected_day_lineup_text,
                    currentLineupLow,
                    currentLineupTop
                )
            )
        } else {
            tvCurrentLineup.setText(
                getString(
                    R.string.today_active_lineups,
                    currentLineupLow,
                    currentLineupTop,
                    hoursToChange, minutesToChange,
                    nextLineupLow, nextLineupTop
                )
            )
        }
    }


    @VisibleForTesting
    private fun injectDependencies() {
        KTP
            .openScopes(ApplicationScope::class.java)
            .openSubScope(ViewModelScope::class.java)
            .closeOnViewModelCleared(this).apply {
                inject(this@LineupListFragment)
            }
    }
}