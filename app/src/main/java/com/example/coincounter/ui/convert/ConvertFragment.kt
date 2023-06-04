package com.example.coincounter.ui.convert

import android.R
import android.R.attr.defaultValue
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.coincounter.changerates
import com.example.coincounter.databinding.FragmentConverterBinding


class ConvertFragment : Fragment() {

    private var _binding: FragmentConverterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var rates: changerates? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val convertViewModel =
            ViewModelProvider(this).get(ConvertViewModel::class.java)

        _binding = FragmentConverterBinding.inflate(inflater, container, false)

        val bundle = this.arguments
        if (bundle != null) {
            rates = bundle.getSerializable("rates")!! as changerates
        }

        val spinner: Spinner = binding.spMoney
        val spinnerRate: Spinner = binding.spRate
        val etRate: EditText = binding.etRate
        val etMoney: EditText = binding.etMoney

        val root: View = binding.root

        /*val textView: TextView = binding.textNotifications
        convertViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/

        if (rates != null) {
            var ratesList = rates!!.rateKeysToList()

            val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, ratesList)
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            val adapterRate = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, ratesList)
            adapterRate.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinnerRate.adapter = adapter
            //val textView2: TextView = binding.textNotifications2
            //textView2.text = rates!!.rates["USD"].toString()
        }

        etMoney.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isEmpty()){
                    etRate.setText("")
                }else{
                    val money = spinner.selectedItem.toString()
                    val rate = spinnerRate.selectedItem.toString()
                    val moneyValue = etMoney.text.toString().toDouble()
                    val result:Double = rates!!.changeRate(money, rate, moneyValue)
                    etRate.setText(result.toString())
                }
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}