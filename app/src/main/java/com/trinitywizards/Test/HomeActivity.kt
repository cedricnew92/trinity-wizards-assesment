package com.trinitywizards.Test

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.trinitywizards.Test.fragments.ContactsFragment
import com.trinitywizards.Test.fragments.ProfileFragment

class HomeActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    private val f_contacts = ContactsFragment()
    private val f_profile = ProfileFragment()
    private val bnv_home by lazy { findViewById<BottomNavigationView>(R.id.bnv_home) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //contacts()
        Thread{
            supportFragmentManager.beginTransaction()
                .add(R.id.fl_home, f_contacts)
                .add(R.id.fl_home, f_profile)
                .hide(f_profile)
                .commit()
        }.start()
        bnv_home.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.m_contacts ->  {
                    contacts()
                }
                R.id.m_profile -> {
                    profile()
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    fun contacts() {
        Thread{
            supportFragmentManager.beginTransaction()
                .show(f_contacts)
                .hide(f_profile)
                .commit()
        }.start()
    }

    fun profile() {
        Thread{
            supportFragmentManager.beginTransaction()
                .show(f_profile)
                .hide(f_contacts)
                .commit()
        }.start()

    }

}