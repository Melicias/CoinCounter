package com.example.coincounter.classes

import android.content.Context
import com.google.gson.Gson
import okhttp3.*
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.charset.Charset
import java.util.*


class apiCall {
     val client = OkHttpClient()
     val url = "https://openexchangerates.org/api/latest.json?app_id="
     val app_id = "30769ec2639e454d9e6abfc45a565a41"
     val path = "changerates.json"
     val url_final = this.url + this.app_id
     val c:Context? = null
     var localpath:String = ""

    var rates:changerates? = null

    fun run(c: Context, url: String = url_final):changerates? {
        localpath =  c.getFilesDir().toPath().toString() + path

        var file = File(localpath)

        if(file.exists()){
            val t = file.readText(Charset.defaultCharset())
            try {
                this.rates = Gson().fromJson(t, changerates::class.java)
            }catch (e: Exception) {
                e.printStackTrace()
                file.delete()
            }
            if(this.rates == null || this.rates!!.timestamp == null){
                file.delete()
                return null
            }else{
                if(isNextDay(this.rates!!.timestamp)){
                    file.delete()
                    return null
                }
            }
        }else{
            return null
        }
        return this.rates
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