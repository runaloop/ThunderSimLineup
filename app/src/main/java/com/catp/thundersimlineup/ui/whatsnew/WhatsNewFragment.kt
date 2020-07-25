package com.catp.thundersimlineup.ui.calendar

import android.os.Bundle
import android.view.*
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.annotation.ApplicationScope
import com.catp.thundersimlineup.annotation.ViewModelScope
import com.catp.thundersimlineup.ui.list.configureRecyclerView
import com.catp.thundersimlineup.ui.whatsnew.ChangesAdapter
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.android.synthetic.main.fragment_changes_list.*
import toothpick.ktp.KTP
import javax.inject.Inject


class WhatsNewFragment : Fragment() {

    @Inject
    lateinit var whatsNewViewModel: WhatsNewViewModel

    val changesAdapter = ChangesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_changes_list, container, false)
        injectDependencies()
        setHasOptionsMenu(true)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return onQueryTextChange(s)
            }

            override fun onQueryTextChange(s: String): Boolean {
                if (changesAdapter.hasNewFilter(s)) {
                    changesAdapter.setFilter(s)
                    changesAdapter.filterItems(1000L)
                }
                return true
            }
        }
        )

        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        whatsNewViewModel.changes.observe(viewLifecycleOwner, Observer { list ->
            changesAdapter.setData(requireContext(), list)
        })

        configureRecyclerView(changesAdapter, rvChangesList, this, null)
        //Without this headerview is not collapsing
        changesAdapter.addListener(FlexibleAdapter.OnItemClickListener { view, position ->
            true
        })

        whatsNewViewModel.viewCreated()
    }

    @VisibleForTesting
    private fun injectDependencies() {
        KTP.openScopes(ApplicationScope::class.java)
            .openSubScope(ViewModelScope::class.java)
            .inject(this)
    }


}
