package com.sampath.finora

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.android.material.materialswitch.MaterialSwitch
import com.sampath.finora.R
import com.sampath.finora.DBHelper
import java.util.Calendar
import java.util.Locale

object Session {
    var currentUserId: Long = 1L
    var currentUserEmail: String = "captain@finora.ai"
}

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var db: DBHelper
    private val prefs by lazy {
        requireContext().getSharedPreferences("finora_prefs", Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DBHelper(requireContext())

        val greeting = view.findViewById<TextView>(R.id.greetingText)
        val chipSync = view.findViewById<Chip>(R.id.syncStatus)
        val txtName = view.findViewById<TextView>(R.id.userName)
        val txtEmail = view.findViewById<TextView>(R.id.userEmail)
        val txtMemberSince = view.findViewById<TextView>(R.id.memberSince)
        val txtTotalCatches = view.findViewById<TextView>(R.id.txtTotalCatches)
        val txtTotalWeight = view.findViewById<TextView>(R.id.txtTotalWeight)
        val swDark = view.findViewById<MaterialSwitch>(R.id.switchDarkMode)
        val swNotif = view.findViewById<MaterialSwitch>(R.id.switchNotifications)
        val swLoc = view.findViewById<MaterialSwitch>(R.id.switchLocation)

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val prefix = when (hour) { in 0..11 -> "Good morning"; in 12..16 -> "Good afternoon"; else -> "Good evening" }
        greeting.text = "$prefix,\nCaptain"

        val uid = Session.currentUserId
        txtName.text = db.getUserName(uid)
        txtEmail.text = Session.currentUserEmail
        txtMemberSince.text = "★ 4.8   Member since January 2024"

        val (totalScans, totalWeight, _) = db.getDashboardStats(uid)
        txtTotalCatches.text = totalScans.toString()
        txtTotalWeight.text = String.format(Locale.US, "%.1fkg", totalWeight)

        chipSync.setOnClickListener {
            val isSynced = chipSync.text.toString().equals("Synced", true)
            chipSync.text = if (isSynced) "Syncing..." else "Synced"
        }

        swDark.isChecked = prefs.getBoolean("dark_mode", true)
        swNotif.isChecked = prefs.getBoolean("notifications", true)
        swLoc.isChecked = prefs.getBoolean("location", true)

        swDark.setOnCheckedChangeListener { _, enabled ->
            prefs.edit().putBoolean("dark_mode", enabled).apply()
            requireActivity().recreate()
        }
        swNotif.setOnCheckedChangeListener { _, enabled ->
            prefs.edit().putBoolean("notifications", enabled).apply()
            Toast.makeText(requireContext(), if (enabled) "Notifications on" else "Notifications off", Toast.LENGTH_SHORT).show()
        }
        swLoc.setOnCheckedChangeListener { _, enabled ->
            prefs.edit().putBoolean("location", enabled).apply()
            if (enabled) ensureLocationPermission()
        }
    }

    private fun ensureLocationPermission() {
        val granted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
        }
    }
}
