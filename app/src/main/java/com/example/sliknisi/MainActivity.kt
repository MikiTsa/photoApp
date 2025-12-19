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

        binding.bottomNavigation.setupWithNavController(navController)
    }

    private fun setupHomeButton() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.fabHome.setOnClickListener {
            navController.navigate(R.id.homeFragment)

            binding.bottomNavigation.menu.setGroupCheckable(0, true, false)
            for (i in 0 until binding.bottomNavigation.menu.size()) {
                binding.bottomNavigation.menu.getItem(i).isChecked = false
            }
            binding.bottomNavigation.menu.setGroupCheckable(0, true, true)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    // Clear bottom nav selection when on home
                    binding.bottomNavigation.menu.setGroupCheckable(0, true, false)
                    for (i in 0 until binding.bottomNavigation.menu.size()) {
                        binding.bottomNavigation.menu.getItem(i).isChecked = false
                    }
                    binding.bottomNavigation.menu.setGroupCheckable(0, true, true)
                }
            }
        }
    }

    private fun removeNavigationBadges() {
        binding.bottomNavigation.removeBadge(R.id.achievementsFragment)
        binding.bottomNavigation.removeBadge(R.id.mapFragment)
        binding.bottomNavigation.removeBadge(R.id.addFragment)
        binding.bottomNavigation.removeBadge(R.id.profileFragment)
    }
}