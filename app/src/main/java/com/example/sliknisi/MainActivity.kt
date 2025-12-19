package com.example.sliknisi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.sliknisi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupHomeButton()
        removeNavigationBadges()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Connect bottom navigation with nav controller
        binding.bottomNavigation.setupWithNavController(navController)
    }

    private fun setupHomeButton() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.fabHome.setOnClickListener {
            // Navigate to home fragment
            navController.navigate(R.id.homeFragment)
        }
    }

    private fun removeNavigationBadges() {
        // Remove any notification badges/dots from nav items
        binding.bottomNavigation.removeBadge(R.id.achievementsFragment)
        binding.bottomNavigation.removeBadge(R.id.mapFragment)
        binding.bottomNavigation.removeBadge(R.id.addFragment)
        binding.bottomNavigation.removeBadge(R.id.profileFragment)
    }
}