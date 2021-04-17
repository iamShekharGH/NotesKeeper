package com.iamshekhargh.myapplication.ui

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.iamshekhargh.myapplication.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var searchView: SearchView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navFragment =
            supportFragmentManager.findFragmentById(R.id.mainactiv_fragment_container) as NavHostFragment
        navController = navFragment.navController

        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_activ_menu, menu)
        if (menu != null) {
            val searchItem = menu.findItem(R.id.menu_search)
            searchView = searchItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = true

                override fun onQueryTextChange(newText: String?): Boolean {
                    TODO("Not yet implemented")
                }
            })
        }

        return super.onCreateOptionsMenu(menu)
    }
}


/*  TODO
 *  1. create firebase login and store notes in firebase.
 *  2. figure out data store and store relevant details
 *      2.1 for add Frag label list from view model.
 *  3. setup reminder for the notes. and provide alarm facility.
 *  4. Search notes in toolbar.
 *  5.
 *  Glide
    .with(myFragment)
    .load(url)
    .centerCrop()
    .placeholder(R.drawable.loading_spinner)
    .into(myImageView);

 *     Now :~
 *          0. upload this to github.
 *          1. login with google.
 *          2. user profile.
 *          3. toolbar se open profile
 *          4. implement search view
 *          5. implement logout.
 *          6. DataStore to maybe store data for login etc.
 *          7. Firebase db
 *
 *
 *
 */