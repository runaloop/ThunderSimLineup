package com.catp.thundersimlineup.ui.lineuplist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.annotation.ApplicationScope
import com.catp.thundersimlineup.annotation.ViewModelScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.lineup_list.*
import toothpick.ktp.KTP
import toothpick.ktp.binding.module
import toothpick.ktp.delegate.inject
import toothpick.smoothie.viewmodel.closeOnViewModelCleared
import toothpick.smoothie.viewmodel.installViewModelBinding

class LineupListFragment : Fragment() {

    private val lineupListViewModel by inject<LineupListViewModel>()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lineupListViewModel.selectedDay.observe(this, Observer {
            tvSelectedDate.text = it.day.toString()
        })
        lineupListViewModel.text.observe(this, Observer {
            if(it.isNotEmpty()){
                Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
            }
        })

        rvLineupList.layoutManager = LinearLayoutManager(context)
        rvLineupList.adapter = lineupAdapter

        lineupListViewModel.currentLineup.observe(this, Observer { lineup ->
            lineupAdapter.setNewLineup(requireContext(), lineup)
        })

        super.onViewCreated(view, savedInstanceState)
    }

    @VisibleForTesting
    private fun injectDependencies() {
        KTP.openScopes(ApplicationScope::class.java)
            .openSubScope(ViewModelScope::class.java)
            .inject(this)
    }
}