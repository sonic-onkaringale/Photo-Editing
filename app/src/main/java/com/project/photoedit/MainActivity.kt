package com.project.photoedit

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.color.DynamicColors
import com.project.photoedit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        DynamicColors.applyToActivitiesIfAvailable(application)
//        val context = DynamicColors.wrapContextIfAvailable(
//            applicationContext
//        )
//        val attrsToResolve = intArrayOf(
//            com.google.android.material.R.attr.backgroundColor,
//            com.google.android.material.R.attr.colorOnBackground
//        )
//        @SuppressLint("ResourceType") val ta = context.obtainStyledAttributes(attrsToResolve)
//        @SuppressLint("ResourceType") val backgroundcolor = ta.getColor(0, 0)
//        @SuppressLint("ResourceType") val onbackground = ta.getColor(1, 0)
//
//        ta.recycle()

//        window.navigationBarColor = backgroundcolor
//        window.statusBarColor = backgroundcolor
//        window.isStatusBarContrastEnforced = true
//        window.isNavigationBarContrastEnforced=true

        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}