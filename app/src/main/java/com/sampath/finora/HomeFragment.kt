package com.sampath.finora

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import androidx.fragment.app.Fragment
import java.util.Calendar

class HomeFragment : Fragment() {

    private lateinit var dbHelper: DBHelper

    private lateinit var greetingText: TextView
    private lateinit var todayScansValue: TextView
    private lateinit var totalWeightValue: TextView
    private lateinit var topSpeciesValue: TextView
    private lateinit var startScanBtn: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dbHelper = DBHelper(requireContext())
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        greetingText = view.findViewById(R.id.greetingText)
        todayScansValue = view.findViewById(R.id.todayScansValue)
        totalWeightValue = view.findViewById(R.id.totalWeightValue)
        topSpeciesValue = view.findViewById(R.id.topSpeciesValue)
        startScanBtn = view.findViewById(R.id.startScanBtn)

        // Option tiles clickable
        view.findViewById<RelativeLayout>(R.id.historyOption).setOnClickListener {
            Toast.makeText(requireContext(), "Open History", Toast.LENGTH_SHORT).show()
            // navigate to HistoryFragment (via parent activity)
            (activity as? HomeDashboardActivity)?.let {
                it.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, HistoryFragment())
                    .addToBackStack(null)
                    .commit()
                it.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)
                    .selectedItemId = R.id.nav_history
            }
        }

        view.findViewById<RelativeLayout>(R.id.mapOption).setOnClickListener {
            (activity as? HomeDashboardActivity)?.let {
                it.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, MapFragment())
                    .addToBackStack(null)
                    .commit()
                it.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)
                    .selectedItemId = R.id.nav_map
            }
        }

        view.findViewById<RelativeLayout>(R.id.exportOption).setOnClickListener {
            Toast.makeText(requireContext(), "Export clicked", Toast.LENGTH_SHORT).show()
        }

        startScanBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Start Scanning (placeholder)", Toast.LENGTH_SHORT).show()
            // replace with real Scan flow
            (activity as? HomeDashboardActivity)?.let {
                it.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, ScanFragment())
                    .addToBackStack(null)
                    .commit()
                it.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)
                    .selectedItemId = R.id.nav_scan
            }
        }

        // Ensure test user exists and sample data
        if (dbHelper.getUserId("test@example.com") == -1L) {
            dbHelper.insertUser("test@example.com", "testpass", "Alex Captain")
            val newId = dbHelper.getUserId("test@example.com")
            if (newId > 0L) insertDummyDataForTesting(newId)
        }

        updateDashboardUI()

        return view
    }

    override fun onResume() {
        super.onResume()
        updateDashboardUI()
    }

    private fun updateDashboardUI() {
        val currentUserId = dbHelper.getUserId("test@example.com").takeIf { it != -1L } ?: -1L

        if (currentUserId <= 0) {
            greetingText.text = "Hello, Guest"
            todayScansValue.text = "0"
            totalWeightValue.text = "0.0kg"
            topSpeciesValue.text = "N/A"
            return
        }

        val userName = dbHelper.getUserName(currentUserId)
        val greetingPrefix = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
        greetingText.text = "$greetingPrefix,\n${userName}"

        val (totalScans, totalWeight, topSpecies) = dbHelper.getDashboardStats(currentUserId)
        todayScansValue.text = totalScans.toString()
        totalWeightValue.text = String.format("%.1fkg", totalWeight)
        topSpeciesValue.text = topSpecies.replaceFirstChar { it.uppercase() }
    }

    private fun insertDummyDataForTesting(userId: Long) {
        val (currentScans, _, _) = dbHelper.getDashboardStats(userId)
        if (currentScans < 1) {
            val catchId1 = dbHelper.startNewCatchRecord(userId, 34.05, -118.25)
            if (catchId1 != -1L) {
                dbHelper.insertFishDetail(catchId1, "Cod", 0.95, 55.0, 15.5, 0.99)
                dbHelper.insertFishDetail(catchId1, "Cod", 0.88, 48.0, 10.0, 0.98)
                dbHelper.insertFishDetail(catchId1, "Mackerel", 0.99, 30.0, 0.5, 1.0)
                dbHelper.insertFishDetail(catchId1, "Mackerel", 0.99, 30.5, 0.7, 1.0)
                dbHelper.insertFishDetail(catchId1, "Tuna", 0.75, 100.0, 50.0, 0.85)
            }
        }
    }
}
