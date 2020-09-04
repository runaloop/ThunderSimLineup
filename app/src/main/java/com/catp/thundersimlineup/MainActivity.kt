package com.catp.thundersimlineup

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import com.catp.thundersimlineup.annotation.ApplicationScope
import com.catp.thundersimlineup.annotation.ViewModelScope
import com.catp.thundersimlineup.ui.lineuplist.LineupListViewModel
import kotlinx.android.synthetic.main.activity_main.*
import toothpick.ktp.KTP
import toothpick.smoothie.viewmodel.closeOnViewModelCleared
import toothpick.smoothie.viewmodel.installViewModelBinding
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        injectDependencies()
        NavigationUI.setupActionBarWithNavController(
            this,
            findNavController(R.id.nav_host_fragment)
        )
        if (viewModel.notificationTaskCreateDelegate != "OK") {
            throw RuntimeException("Notifcation task should be initialized")
        }
    }

    @VisibleForTesting
    private fun injectDependencies() {
        val activityScope = KTP.openScopes(ApplicationScope::class.java)
            .openSubScope(ViewModelScope::class.java) { scope ->
                scope
                    .installViewModelBinding<LineupListViewModel>(this)
                    .installViewModelBinding<MainActivityViewModel>(this)
                    .closeOnViewModelCleared(this)
            }
        activityScope.inject(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //handle the click on the back arrow click
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        val navController = findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }


}
