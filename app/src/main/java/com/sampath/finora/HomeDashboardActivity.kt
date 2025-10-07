package com.sampath.finora

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeDashboardActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_dashboard)

        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_scan -> loadFragment(ScanFragment())
                R.id.nav_history -> loadFragment(HistoryFragment())
                R.id.nav_map -> loadFragment(MapFragment())
                R.id.nav_settings -> loadFragment(SettingsFragment())
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right, // enter
                R.anim.slide_out_left, // exit
                R.anim.slide_in_left,  // pop enter
                R.anim.slide_out_right // pop exit
            )
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        return true
    }

}
