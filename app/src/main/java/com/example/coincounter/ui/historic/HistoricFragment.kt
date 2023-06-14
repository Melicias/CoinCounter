package com.example.coincounter.ui.historic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coincounter.classes.Conversion
import com.example.coincounter.databinding.FragmentDashboardBinding
import com.example.coincounter.adaptors.hist_adapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.LinkedList


class HistoricFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val PREFS_NAME = "YOUR_TAG"
    private val CONVERSION_TAG = "conversion"

    var conversions: LinkedList<Conversion> = LinkedList<Conversion>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val historicViewModel =
            ViewModelProvider(this).get(HistoricViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val rvHistoric: RecyclerView = binding.rvHistorico
        val historicPlaceholder: TextView = binding.historicPlaceholder
        var mSettings = requireContext().getSharedPreferences(PREFS_NAME, 0)
        if(mSettings.contains(CONVERSION_TAG)) {
            val gson = Gson()
            val json: String = mSettings.getString(CONVERSION_TAG,"")!!
            val type: Type = object : TypeToken<LinkedList<Conversion?>?>() {}.type
            this.conversions = gson.fromJson<LinkedList<Conversion>>(json, type)
        }
        if (conversions.isEmpty()) {
            historicPlaceholder.setVisibility(View.VISIBLE) ;
            historicPlaceholder.text="No conversions history available"
        }else{
            historicPlaceholder.setVisibility(View.GONE) ;

        val AdaptadorConversions = hist_adapter(this.conversions,historicPlaceholder)
        rvHistoric.setAdapter(AdaptadorConversions)
        rvHistoric.setLayoutManager(LinearLayoutManager(context))
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}