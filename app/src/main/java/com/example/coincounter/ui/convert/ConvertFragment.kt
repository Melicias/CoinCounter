package com.example.coincounter.ui.convert

import android.R
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ahmedkgamil.searchablespinner.SearchableSpinner
import com.example.coincounter.apiCall
import com.example.coincounter.changerates
import com.example.coincounter.databinding.FragmentConverterBinding


class ConvertFragment : Fragment() {

    private var _binding: FragmentConverterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var rates: changerates? = changerates("","",0,"",mapOf<String, Double>())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val convertViewModel =
            ViewModelProvider(this).get(ConvertViewModel::class.java)

        _binding = FragmentConverterBinding.inflate(inflater, container, false)

        var ap: apiCall = apiCall()
        rates = ap.run(this.requireContext())
        //val spinnerer2 =binding.spinnerTest
        val spinner: SearchableSpinner = binding.spMoney as SearchableSpinner
        val spinnerRate:SearchableSpinner = binding.spRate as SearchableSpinner
        val etRate: EditText = binding.etRate
        val etMoney: EditText = binding.etMoney
        val root: View = binding.root
        val swapCurrency=binding.swapCurrency
        etRate.setInputType(InputType.TYPE_NULL); //make the text disabled

        spinner.setTitle("Search item");
        if (rates != null && rates?.base != ""){
            var ratesList = rates!!.rateKeysToList()
            val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, ratesList)
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            val adapterRate = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, ratesList)
            adapterRate.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            spinnerRate.adapter = adapter
        }
        spinner.setOnItemChangedListener {
            updateConvertions(it as String,spinnerRate.selectedItem.toString(),etMoney,etRate)
        }
        spinnerRate.setOnItemChangedListener {
            updateConvertions(spinner.selectedItem.toString(),it as String,etMoney,etRate)
        }
        etMoney.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isEmpty()){
                    etRate.setText("")
                }else{
                    updateConvertions(spinner.selectedItem.toString(),spinnerRate.selectedItem.toString(),etMoney,etRate)
                }
            }
        })
        swapCurrency.setOnClickListener{
            val auxPosition=spinner.selectedItemPosition
            spinner.setSelection(spinnerRate.selectedItemPosition)
            spinnerRate.setSelection(auxPosition)
            updateConvertions(spinner.selectedItem.toString(),spinnerRate.selectedItem.toString(),etMoney,etRate)
        }

        return root
    }

    public fun updateConvertions(money:String,rate:String,etMoney:EditText,etRate:EditText){
            if(etMoney.text.isEmpty()){
                return
            }
            val money = money
            val rate = rate
            val moneyValue = etMoney.text.toString().toDouble()
            val result:Double = rates!!.changeRate(money, rate, moneyValue)
            etRate.setText(result.toString())

    }

    public fun setRates(rates: changerates) {
        this.rates = rates
        if (rates != null) {
            val spinner: Spinner = binding.spMoney
            val spinnerRate: Spinner = binding.spRate

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}