package com.catp.thundersimlineup.ui.lineuplist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.catp.thundersimlineup.*
import com.catp.thundersimlineup.annotation.ApplicationScope
import com.catp.thundersimlineup.annotation.LineupListViewModelScope
import com.catp.thundersimlineup.annotation.ViewModelScope
import com.catp.thundersimlineup.data.FilterState
import com.catp.thundersimlineup.ui.list.configureRecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.lineup_list.*
import toothpick.ktp.KTP
import toothpick.smoothie.viewmodel.closeOnViewModelCleared
import toothpick.smoothie.viewmodel.installViewModelBinding
import javax.inject.Inject


class LineupListFragment : Fragment() {

    @Inject
    lateinit var lineupListViewModel: LineupListViewModel

    @Inject
    lateinit var mainActivityViewModel: MainActivityViewModel

    @Inject
    lateinit var statUtil: StatUtil


    private val lineupAdapter = LineupAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.lineup_list, container, false)
        injectDependencies()

        return root
    }

    private var chips: List<Chip> = emptyList()
    override fun onDestroyView() {
        super.onDestroyView()
        chips = emptyList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //LoggingLifecycleObserver.registerLogging(viewLifecycleOwner.lifecycle,"😻LineupListFragmentView:")
        lineupListViewModel.text.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty() && getView() != null) {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
            }
        })
        configureFilter()

        fab.setOnClickListener {
            fab.isEnabled = false
            mainActivityViewModel.pushFavorites()
            findNavController(this).navigate(R.id.action_lineup_list_fragment_to_vehicle_list)
        }
        fab.isEnabled = true

        lineupListViewModel.lineupLoadStatus.observe(viewLifecycleOwner, Observer { value ->
            progressBarStatus(value, pbLineup)
        })

        lineupListViewModel.refreshData(false)

        configureRecyclerView(lineupAdapter, rvLineupList, this, mainActivityViewModel)

        lineupListViewModel.currentLineup.observe(viewLifecycleOwner, Observer { lineup ->
            lineupAdapter.setNewLineup(this.requireContext(), lineup)
            updateLineupText(lineup)
            rvLineupList.scrollToPosition(0)
        })

        mainActivityViewModel.calendarDate.observe(viewLifecycleOwner, Observer { date ->
            lineupListViewModel.onDateChanged(date)
        })

        statUtil.sendViewStat(this, "LineupList")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //LoggingLifecycleObserver.registerLogging(lifecycle, "😻LineupListFragment:")
        super.onCreate(savedInstanceState)
    }


    @SuppressLint("FragmentLiveDataObserve")
    private fun configureFilter() {
        lineupListViewModel.filterStatus.observe(this, Observer { filter ->
            lineupAdapter.setFilterState(requireContext(), filter)
        })

        lineupListViewModel.filterAvailable.observe(this, Observer { filter ->
            updateFilterVisibility(filter)
        })

        chips = listOf(
            teamAChip,
            teamBChip,
            tanksChip,
            planesChip,
            helisChip,
            lowLineupChip,
            highLineupChip,
            nowLineupChip,
            laterLineupChip
        )

        chips.forEach {
            it.setOnCheckedChangeListener { _, _ ->
                lineupListViewModel.filterChange(
                    "",
                    teamAChip.isChecked,
                    teamBChip.isChecked,
                    tanksChip.isChecked,
                    planesChip.isChecked,
                    helisChip.isChecked,
                    lowLineupChip.isChecked,
                    highLineupChip.isChecked,
                    nowLineupChip.isChecked,
                    laterLineupChip.isChecked
                )
            }
        }
    }

    private fun updateFilterVisibility(filter: FilterState) {
        chips.forEachIndexed { index, chip ->
            chip.visibility = if (filter[index]) View.VISIBLE else View.GONE
            chip.isChecked = filter[index]
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    override fun onPause() {
        mainActivityViewModel.pushFavorites()
        super.onPause()
    }

    private fun updateLineupText(lineup: LineupRequestInteractor.LineupForToday) {
        val currentLineupLow = lineup.lineupNow.first!!.lineupEntity.name
        val currentLineupTop = lineup.lineupNow.second!!.lineupEntity.name
        val hoursToChange = lineup.timeToChange.toHours().toString()
        val minutesToChange = (lineup.timeToChange.toMinutes() % 60).toString()
        if (lineup.timeToChange.isZero) {
            //hide
            tvCurrentLineup.text = getString(
                R.string.selected_day_lineup_text,
                currentLineupLow,
                currentLineupTop
            )
        } else {
            val nextLineupLow = lineup.lineupThen.first!!.lineupEntity.name
            val nextLineupTop = lineup.lineupThen.second!!.lineupEntity.name
            tvCurrentLineup.text = getString(
                R.string.today_active_lineups,
                currentLineupLow,
                currentLineupTop,
                hoursToChange, minutesToChange,
                nextLineupLow, nextLineupTop
            )
        }
    }


    @VisibleForTesting
    private fun injectDependencies() {
        KTP
            .openScopes(ApplicationScope::class.java)
            .openSubScope(ViewModelScope::class.java)
            .openSubScope(LineupListViewModelScope::class.java)
            .installViewModelBinding<LineupListViewModel>(this)
            .closeOnViewModelCleared(this)
            .inject(this)
    }
}