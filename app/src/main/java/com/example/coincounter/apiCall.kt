package com.example.coincounter

import android.content.Context
import okhttp3.*
import java.io.IOException
import com.google.gson.Gson
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.nio.charset.Charset
import java.util.*

class apiCall {
    private val client = OkHttpClient()
    private val url = "https://openexchangerates.org/api/latest.json?app_id="
    private val app_id = "30769ec2639e454d9e6abfc45a565a41"
    private val path = "changerates.json"

    var rates:changerates? = null


    fun run(c: Context, url: String = this.url + this.app_id):changerates? {
        val localpath:String =  c.getFilesDir().toPath().toString() + path

        var file = File(localpath)

        if(file.exists()){
            val t = file.readText(Charset.defaultCharset())
            try {
                this.rates = Gson().fromJson(t, changerates::class.java)
            }catch (e: Exception) {
                e.printStackTrace()
            }

            if(this.rates == null || this.rates!!.timestamp == null){
                call(localpath)
            }else{
                if(isNextDay(this.rates!!.timestamp)){
                    call(localpath)
                }
            }
        }else{
            call(localpath)
        }

        return this.rates
    }

    fun call(localpath:String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response){
                val t = response.body()?.string()
                try {
                    rates = Gson().fromJson(t, changerates::class.java)
                    try {
                        FileWriter(localpath).use { it.write(t) }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }


    fun isNextDay(timestamp: Long): Boolean {
        val inputDate = Calendar.getInstance()
        inputDate.timeInMillis = timestamp

        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        // Reset hour, minutes, seconds and milliseconds
        inputDate.set(Calendar.HOUR_OF_DAY, 0)
        inputDate.set(Calendar.MINUTE, 0)
        inputDate.set(Calendar.SECOND, 0)
        inputDate.set(Calendar.MILLISECOND, 0)

        // Add one day to inputDate for comparison
        inputDate.add(Calendar.DATE, 1)

        return today.time.equals(inputDate.time)
    }
}