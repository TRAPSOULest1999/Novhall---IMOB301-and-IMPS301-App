package com.leope.novhall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class HomeScreenActivity : AppCompatActivity() {
    private lateinit var btn_logout: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val bottomNavigationView = findViewById<BottomNavigationView
                >(R.id.bottom_nav)

        val HomeFragment = HomeFragment()
        val ProfileFragment = ProfileFragment()
        val UserBookDetailsFragment = UserBookDetailsFragment()

        btn_logout =findViewById(R.id.ic_logout)

        setCurrentFragment(HomeFragment)

        btn_logout.setOnClickListener {
            auth.signOut()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_books -> setCurrentFragment(HomeFragment)
                R.id.ic_profile -> setCurrentFragment(ProfileFragment)
            }
            true
        }
    }
    private fun setCurrentFragment(fragment:Fragment)=
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.nav_fragment,fragment)
                commit()
            }

}