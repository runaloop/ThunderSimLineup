package com.catp.thundersimlineup

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.catp.thundersimlineup.annotation.ApplicationScope
import com.catp.thundersimlineup.annotation.ViewModelScope
import com.catp.thundersimlineup.ui.lineuplist.LineupListViewModel
import kotlinx.android.synthetic.main.activity_main.*
import toothpick.ktp.KTP
import toothpick.ktp.binding.module
import toothpick.smoothie.viewmodel.closeOnViewModelCleared
import toothpick.smoothie.viewmodel.installViewModelBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        injectDependencies()


    }

    @VisibleForTesting
    private fun injectDependencies() {
        KTP.openScopes(ApplicationScope::class.java)
            .openSubScope(ViewModelScope::class.java) { scope ->
                scope.installViewModelBinding<LineupListViewModel>(this)
                    .closeOnViewModelCleared(this)
                    .installModules(module {
                    })
            }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        return super.onCreateOptionsMenu(menu)
    }
}
