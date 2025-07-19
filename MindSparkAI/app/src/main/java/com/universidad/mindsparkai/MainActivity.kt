package com.universidad.mindsparkai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.universidad.mindsparkai.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupFabButton()
        setupUI()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Setup bottom navigation if it exists
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.setupWithNavController(navController)

        // Manejar navegación y ocultar/mostrar bottom nav según el fragmento
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.welcomeFragment,
                R.id.loginFragment,
                R.id.registerFragment,
                R.id.chatFragment -> {
                    bottomNav?.visibility = android.view.View.GONE
                    binding.fabChat.visibility = android.view.View.GONE
                }
                else -> {
                    bottomNav?.visibility = android.view.View.VISIBLE
                    binding.fabChat.visibility = android.view.View.VISIBLE
                }
            }
        }
    }

    private fun setupFabButton() {
        binding.fabChat.setOnClickListener {
            // Navigate to chat fragment
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController

            // Navigate to chat if not already there
            if (navController.currentDestination?.id != R.id.chatFragment) {
                navController.navigate(R.id.chatFragment)
            }
        }
    }

    private fun setupUI() {
        // Configurar status bar y tema
        window.statusBarColor = getColor(R.color.status_bar)

        // Configurar cualquier otro aspecto de la UI
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}