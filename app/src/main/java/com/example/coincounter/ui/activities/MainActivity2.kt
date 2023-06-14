package com.example.coincounter.ui.activities

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.coincounter.R
import com.example.coincounter.classes.apiCall
import com.example.coincounter.classes.changerates
import com.example.coincounter.databinding.ActivityMain2Binding
import com.google.gson.Gson
import okhttp3.*
import java.io.FileWriter
import java.io.IOException


private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

private const val PERMISSION_REQUEST_CODE = 200

private lateinit var layout: View

private lateinit var binding: ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {

    var rates: changerates? = null
    var ap: apiCall = apiCall()
    val client = OkHttpClient()

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            //if (isGranted) {
                //Toast.makeText(applicationContext, "Permission request granted", Toast.LENGTH_LONG).show()
                //getRates()
            //} else {
                //Toast.makeText(applicationContext, "Permission request denied", Toast.LENGTH_LONG).show()
                //getRates()
            //}
            getRates()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }



    fun getRates(){
        rates = ap.run(applicationContext)

        if (rates == null){
            call(ap.localpath, ap.url_final)
        }else{
            val intent = Intent(this, MainActivity::class.java).apply {
                val bundle = Bundle()
                bundle.putSerializable("rates", rates)
            }
            startActivity(intent)
        }
    }

    fun call(localpath:String, url:String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response){
                val t = response.body()?.string()
                try {
                    rates = Gson().fromJson(t, changerates::class.java)
                    ap.rates = rates
                    try {
                        FileWriter(localpath).use { it.write(t) }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }
}