package com.universidad.mindsparkai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.universidad.mindsparkai.databinding.ActivityMainBinding
// import dagger.hilt.android.AndroidEntryPoint  // ← COMENTADO

// @AndroidEntryPoint  // ← COMENTADO TEMPORALMENTE
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupFabButton()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Setup bottom navigation if it exists
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.setupWithNavController(navController)
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
}