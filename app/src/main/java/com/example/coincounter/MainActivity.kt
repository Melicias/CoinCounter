package com.example.coincounter

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.coincounter.databinding.ActivityMainBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    public var rates:changerates? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        this.actionBar?.hide()
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_camera, R.id.navigation_converter, R.id.navigation_historic, R.id.navigation_settings
            )
        )

        //var ap = apiCall()
        //rates = ap.run(applicationContext)
        //print(rates)
        var ap:apiCall = apiCall()
        rates = ap.run(applicationContext)
        val bundle = Bundle()
        bundle.putSerializable("rates", rates)
        navController.navigate(R.id.navigation_converter, bundle)


        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //val job1: Job = lifecycleScope.launch {
            /*var ap = apiCall()
            rates = ap.run(applicationContext)
            print(rates)
            val bundle = Bundle()
            bundle.putSerializable("rates", rates)
            navController.navigate(R.id.navigation_converter, bundle)*/
        //}
    }
}