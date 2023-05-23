package com.example.coincounter.ui.historic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HistoricViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Historic Fragment"
    }
    val text: LiveData<String> = _text
}