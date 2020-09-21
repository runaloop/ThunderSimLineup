package com.catp.thundersimlineup.ui.whatsnew

import android.os.Bundle
import android.view.*
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.StatUtil
import com.catp.thundersimlineup.annotation.ApplicationScope
import com.catp.thundersimlineup.annotation.NewViewModelScope
import com.catp.thundersimlineup.annotation.ViewModelScope
import com.catp.thundersimlineup.ui.BaseFragment
import com.catp.thundersimlineup.ui.BaseViewModel
import com.catp.thundersimlineup.ui.list.configureRecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.android.synthetic.main.fragment_changes_list.*
import toothpick.ktp.KTP
import toothpick.smoothie.viewmodel.closeOnViewModelCleared
import toothpick.smoothie.viewmodel.installViewModelBinding
import javax.inject.Inject


class WhatsNewFragment : BaseFragment() {

    @Inject
    lateinit var whatsNewViewModel: WhatsNewViewModel
    override val viewModel: BaseViewModel
        get() = whatsNewViewModel

    @Inject
    lateinit var statUtil: StatUtil


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
            changesAdapter.setData(list)
        })

        configureRecyclerView(changesAdapter, rvChangesList, this, null)
        //Without this headerview is not collapsing
        changesAdapter.addListener(FlexibleAdapter.OnItemClickListener { _, position ->
            changesAdapter.notifyItemChanged(position)
            true
        })

        statUtil.sendViewStat(this, "WhatsNew")
    }

    @VisibleForTesting
    private fun injectDependencies() {
        KTP.openScopes(ApplicationScope::class.java)
            .openSubScope(ViewModelScope::class.java)
            .openSubScope(NewViewModelScope::class.java)
            .installViewModelBinding<WhatsNewViewModel>(this)
            .closeOnViewModelCleared(this)
            .inject(this)
    }


}
