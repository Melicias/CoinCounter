package com.example.coincounter.ui.settings
import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.coincounter.apiCall
import com.example.coincounter.changerates
import com.example.coincounter.databinding.FragmentNavigationSettingsBinding


class navigation_settings : Fragment() {

    private var _binding: FragmentNavigationSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var rates: changerates? = changerates("","",0,"",mapOf<String, Double>())
    private val PREFS_NAME = "YOUR_TAG"
    private val DATA_TAG = "RATE_PREFERENCE"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNavigationSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //val textView: TextView = binding.textDashboard

        var ap: apiCall = apiCall()
        rates = ap.run(this.requireContext())

        var mSettings = requireContext()!!.getSharedPreferences(PREFS_NAME, 0)
        val spinner: Spinner = binding.spSettings
        val btSave: Button = binding.btSettings

        if (rates != null && rates?.base != "") {
            var ratesList = rates!!.rateKeysToList()
            val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, ratesList)
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            if (mSettings.contains(DATA_TAG)) {
                val savedRate = mSettings.getString(DATA_TAG, "")
                val spinnerPosition = adapter.getPosition(savedRate)
                spinner.setSelection(spinnerPosition)
            }
        }

        btSave.setOnClickListener {
            val selectedRate = spinner.selectedItem.toString()
            val editor = mSettings.edit()
            editor.putString(DATA_TAG, selectedRate)
            editor.commit()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}