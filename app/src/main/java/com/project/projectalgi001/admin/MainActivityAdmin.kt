package com.project.projectalgi001.admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.project.projectalgi001.R
import com.project.projectalgi001.databinding.ActivityMainAdminBinding
import com.project.projectalgi001.user.auth.EmailPasswordActivity

class MainActivityAdmin : AppCompatActivity(){

    private lateinit var binding: ActivityMainAdminBinding
    private lateinit var auth: FirebaseAuth
    private var mainMenu:Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbarAdmin)
        setSupportActionBar(toolbar)

        auth = Firebase.auth

        val navView: BottomNavigationView = binding.navViewBottom

        val navController = findNavController(R.id.nav_host_fragment_admin)

        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_addCars, R.id.navigation_listSpkCash, R.id.navigation_listSpkCredit, R.id.navigation_listDO, R.id.navigation_setting))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        mainMenu = menu
        menuInflater.inflate(R.menu.logout_admin, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout_admin) {
            logout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        auth.signOut()
        val intent = Intent(this, EmailPasswordActivity::class.java)
        startActivity(intent)
        finish()
    }
}