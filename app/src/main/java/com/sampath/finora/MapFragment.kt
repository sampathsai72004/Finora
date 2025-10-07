package com.sampath.finora

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.card.MaterialCardView
import android.widget.TextView

class MapFragment : Fragment() {

    private lateinit var edtSearch: TextInputEditText
    private lateinit var chipSynced: Chip
    private lateinit var btnFilter: FloatingActionButton
    private lateinit var btnZoomIn: FloatingActionButton
    private lateinit var btnZoomOut: FloatingActionButton
    private lateinit var btnRecenter: FloatingActionButton
    private lateinit var txtCatches: TextView
    private lateinit var txtWeight: TextView
    private lateinit var txtSpots: TextView
    private lateinit var cardStats: MaterialCardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        // Initialize views
        edtSearch = view.findViewById(R.id.edtSearch)
        chipSynced = view.findViewById(R.id.chipSynced)
        btnFilter = view.findViewById(R.id.btnFilter)
        btnZoomIn = view.findViewById(R.id.btnZoomIn)
        btnZoomOut = view.findViewById(R.id.btnZoomOut)
        btnRecenter = view.findViewById(R.id.btnRecenter)
        txtCatches = view.findViewById(R.id.txtCatches)
        txtWeight = view.findViewById(R.id.txtWeight)
        txtSpots = view.findViewById(R.id.txtSpots)
        cardStats = view.findViewById(R.id.cardStats)

        setupListeners()

        return view
    }

    private fun setupListeners() {
        // Search box listener
        edtSearch.setOnEditorActionListener { v, _, _ ->
            val query = v.text.toString().trim()
            if (query.isNotEmpty()) {
                Toast.makeText(requireContext(), "Searching for $query", Toast.LENGTH_SHORT).show()
            }
            true
        }

        // Chip click listener
        chipSynced.setOnClickListener {
            Toast.makeText(requireContext(), "Data synced successfully ✅", Toast.LENGTH_SHORT).show()
        }

        // Filter button
        btnFilter.setOnClickListener {
            Toast.makeText(requireContext(), "Filter clicked ⚙️", Toast.LENGTH_SHORT).show()
        }

        // Zoom controls
        btnZoomIn.setOnClickListener {
            Toast.makeText(requireContext(), "Zooming in 🔍", Toast.LENGTH_SHORT).show()
        }

        btnZoomOut.setOnClickListener {
            Toast.makeText(requireContext(), "Zooming out 🔎", Toast.LENGTH_SHORT).show()
        }

        // Recenter button
        btnRecenter.setOnClickListener {
            Toast.makeText(requireContext(), "Map recentered 📍", Toast.LENGTH_SHORT).show()
        }

        // Stats card click (optional interaction)
        cardStats.setOnClickListener {
            val stats = "Catches: ${txtCatches.text}, Weight: ${txtWeight.text}, Spots: ${txtSpots.text}"
            Toast.makeText(requireContext(), stats, Toast.LENGTH_SHORT).show()
        }
    }
}
