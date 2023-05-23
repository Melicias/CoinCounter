package com.example.coincounter.ui.convert

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConvertViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Convert Fragment"
    }
    val text: LiveData<String> = _text
}