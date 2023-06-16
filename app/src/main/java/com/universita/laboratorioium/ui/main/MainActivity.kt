package com.universita.laboratorioium.ui.main

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.universita.laboratorioium.databinding.ActivityMainBinding

import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar

import androidx.fragment.app.Fragment
import android.view.Menu
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

import com.universita.laboratorioium.R

import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import com.universita.laboratorioium.ui.fragments.*
import com.universita.laboratorioium.ui.support.ListableListViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navigationView: NavigationView


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home)
            binding.drawerLayout.openDrawer(GravityCompat.START)

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CookieHandler.setDefault(CookieManager(null, CookiePolicy.ACCEPT_ALL))
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.myToolbar)

        supportFragmentManager.beginTransaction().replace(
            R.id.nav_host_fragment,
            LoginFragment.newInstance()
        ).commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.menu)
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        navigationView = binding.navView
        val model: ListableListViewModel by viewModels()

        model.user.value = null
        model.user.observe(this) {
            Log.w("Logged user", it.toString())
            navigationView.menu.clear()
            navigationView.inflateMenu(R.menu.navigation_menu)
            when {
                it == null -> {//NOT LOGGED
                    navigationView.menu.removeItem(R.id.nav_my_bookings)
                    navigationView.menu.removeItem(R.id.nav_add_things)
                    navigationView.menu.removeItem(R.id.nav_remove_things)
                }
                it.isAdmin -> {
                    navigationView.menu.findItem(R.id.nav_login).title = "Logout"
                    navigationView.menu.findItem(R.id.nav_my_bookings).title = "Prenotazioni utenti"
                }
                else -> { //LOGGED NOT ADMIN
                    navigationView.menu.findItem(R.id.nav_login).title = "Logout"
                    navigationView.menu.removeItem(R.id.nav_add_things)
                    navigationView.menu.removeItem(R.id.nav_remove_things)
                }
            }
        }

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener {
            val fragment = when (it.itemId) {
                R.id.nav_login -> LoginFragment.newInstance()
                R.id.nav_my_bookings -> MyBooksFragment.newInstance()
                R.id.nav_book_tutoring -> BookTutoringFragment.newInstance()
                R.id.nav_add_things -> AddThingsFragment.newInstance()
                R.id.nav_remove_things -> RemoveThingsFragment.newInstance()
                else -> null
            }
            if (fragment != null) {
                supportFragmentManager.beginTransaction().replace(
                    R.id.nav_host_fragment,
                    fragment
                ).commit()
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    companion object {
        fun closeKeyboard(context: Context) {
            val imm: InputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow((context as Activity).window.currentFocus?.windowToken, 0)
        }
    }
}