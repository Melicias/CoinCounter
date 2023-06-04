package com.example.coincounter

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import okhttp3.*
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.charset.Charset

class MainActivity2 : AppCompatActivity() {

    var rates:changerates? = null
    var ap:apiCall = apiCall()
    val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

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

                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }


}