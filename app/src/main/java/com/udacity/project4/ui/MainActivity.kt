package com.udacity.project4.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var navController: NavController
    private val appBarConfig = AppBarConfiguration(
        setOf(R.id.loginFragment, R.id.locationListFragment)
    )

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportFragmentManager.findFragmentById(R.id.nav_host_fragment).let {
            (it as NavHostFragment).findNavController()
        }.apply {
            navController = this
            addOnDestinationChangedListener { _, des, _ ->
                binding.toolbar.isVisible = des.id !in listOf(R.id.loginFragment)
            }
        }

        setupActionBarWithNavController(navController, appBarConfig)

        viewModel.isLogin.observe(this) {
            if (!it) {
                navController.navigate(R.id.loginFragment)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}