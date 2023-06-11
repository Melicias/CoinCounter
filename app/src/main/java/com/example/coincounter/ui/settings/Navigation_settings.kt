package com.example.coincounter.ui.settings
import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.Toast
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
    private val CB1CENT = "CB1CENT"
    private val CB2CENT = "CB2CENT"
    private val CB5CENT = "CB5CENT"
    private val CB10CENT = "CB10CENT"
    private val CB20CENT = "CB20CENT"
    private val CB50CENT = "CB50CENT"
    private val CB1EURO = "CB1EURO"
    private val CB2EURO = "CB2EURO"

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

        val cb1cent: CheckBox = binding.checkBox1cents
        val cb2cent: CheckBox = binding.checkBox2cents
        val cb5cent: CheckBox = binding.checkBox5cents
        val cb10cent: CheckBox = binding.checkBox10cents
        val cb20cent: CheckBox = binding.checkBox20cents
        val cb50cent: CheckBox = binding.checkBox50cents
        val cb1euro: CheckBox = binding.checkBox1euro
        val cb2euro: CheckBox = binding.checkBox2euro

        if(mSettings.contains(CB1CENT)){
            cb1cent.isChecked = mSettings.getBoolean(CB1CENT,true)
            cb2cent.isChecked = mSettings.getBoolean(CB2CENT,true)
            cb5cent.isChecked = mSettings.getBoolean(CB5CENT,true)
            cb10cent.isChecked = mSettings.getBoolean(CB10CENT,true)
            cb20cent.isChecked = mSettings.getBoolean(CB20CENT,true)
            cb50cent.isChecked = mSettings.getBoolean(CB50CENT,true)
            cb1euro.isChecked = mSettings.getBoolean(CB1EURO,true)
            cb2euro.isChecked = mSettings.getBoolean(CB2EURO,true)
        }

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

            //COINS I WANT TO COUNT CHECKBOEXES
            editor.putBoolean(CB1CENT, cb1cent.isChecked())
            editor.putBoolean(CB2CENT, cb2cent.isChecked())
            editor.putBoolean(CB5CENT, cb5cent.isChecked())
            editor.putBoolean(CB10CENT, cb10cent.isChecked())
            editor.putBoolean(CB20CENT, cb20cent.isChecked())
            editor.putBoolean(CB50CENT, cb50cent.isChecked())
            editor.putBoolean(CB1EURO, cb1euro.isChecked())
            editor.putBoolean(CB2EURO, cb2euro.isChecked())
            editor.commit()

            Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}