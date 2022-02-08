package com.project.projectalgi001.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.projectalgi001.R
import com.project.projectalgi001.databinding.ActivityMainBinding
import com.project.projectalgi001.user.auth.EmailPasswordActivity
import com.project.projectalgi001.user.model.Users
import com.project.projectalgi001.user.ui.profile.ProfileActivity
import java.util.*

class MainActivity : AppCompatActivity(), MenuItem.OnMenuItemClickListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        auth = Firebase.auth

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_addCustomer, R.id.nav_customerList,R.id.nav_customerList,R.id.nav_SpkList,R.id.nav_SpkListCredit, R.id.nav_logout, R.id.nav_setting), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener(this)

        val header: View = navView.getHeaderView(0)
        header.findViewById<View>(R.id.imageHeader)
        val imageHeader = header.findViewById<View>(R.id.imageHeader) as ImageView

        imageHeader.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        fetchUsers()
    }

    private fun fetchUsers() {
        val navView: NavigationView = findViewById(R.id.nav_view)
        val header: View = navView.getHeaderView(0)
        val nameHeader = header.findViewById<View>(R.id.nameHeader) as TextView
        val emailHeader = header.findViewById<View>(R.id.emailHeader) as TextView
        val imageHeader = header.findViewById<View>(R.id.imageHeader) as ImageView

        val uid = auth.currentUser?.uid
        val db = Firebase.firestore.document("users/$uid")
        db.addSnapshotListener {snapshot, e ->
            if (e != null) {
                Log.w("MainActivity", "Listed Failed", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                Log.d("MainActivity", "Current data: ${snapshot.data}")
                val user = snapshot.toObject(Users::class.java)

                nameHeader.text = user?.username
                emailHeader.text = user?.emailUser
                Glide.with(applicationContext).load(user?.profileImageUrl).into(imageHeader)
            }else {
                Log.d("MainActivity", "Current Data : Null")
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.nav_logout -> {
                auth.signOut()
                val intent = Intent(this, EmailPasswordActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return true
    }
}