package com.iamshekhargh.myapplication.ui

import android.app.PictureInPictureParams
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.iamshekhargh.myapplication.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private val viewModel: MainActivityViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navFragment =
            supportFragmentManager.findFragmentById(R.id.mainactiv_fragment_container) as NavHostFragment
        navController = navFragment.navController

        setupActionBarWithNavController(navController)

        //viewModel.notesSyncFunctions()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun enterPIP(){
        val params = PictureInPictureParams.Builder().build()
        enterPictureInPictureMode(params)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
