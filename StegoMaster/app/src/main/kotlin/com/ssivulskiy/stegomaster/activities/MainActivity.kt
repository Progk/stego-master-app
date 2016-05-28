package com.ssivulskiy.stegomaster.activities

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.view.View
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.ssivulskiy.stegomaster.R
import com.ssivulskiy.stegomaster.fragments.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout?
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer?.setDrawerListener(toggle)
        toggle.syncState()

        replaceFragemnt(LSBFragment.newInstance())
        setToolbarTitle(R.string.lsb_method)

        val navigationView = findViewById(R.id.nav_view) as NavigationView?
        navigationView?.apply {
            setNavigationItemSelectedListener(this@MainActivity)
            setCheckedItem(R.id.nav_lsb)
        }

    }


    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout?
        if (drawer?.isDrawerOpen(GravityCompat.START) != null) {
            drawer?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    private fun setToolbarTitle(@StringRes resId : Int) {
        supportActionBar.apply {
            title = getString(resId)
        }
    }

    private fun replaceFragemnt(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val fragment : Fragment;
        when(id) {
            R.id.nav_lsb -> {
                fragment = LSBFragment.newInstance()
                setToolbarTitle(R.string.lsb_method)
            }
            R.id.nav_koxa_jao -> {
                fragment = KoxaJaoFragment.newInstance()
                setToolbarTitle(R.string.koxa_jao_method)

            }
            R.id.nav_lsb_perm -> {
                fragment = LSBPermutationFragment.newInstance()
                setToolbarTitle(R.string.lsb_perm_method)
            }
            R.id.nav_quant -> {
                fragment = QuantizationFragment.newInstance()
                setToolbarTitle(R.string.quantization_method)
            }
            R.id.nav_lsb_interval -> {
                fragment = LSBIntervalFragment.newInstance()
                setToolbarTitle(R.string.lsb_interval_method)
            }
            R.id.benham_memon_yeo_yeung_method -> {
                fragment = BenhamMemonYeoYeungFragemnt.newInstance()
                setToolbarTitle(R.string.benham_memon_yeo_yeung_method)
            }
            else -> {
                fragment = LSBFragment.newInstance()
            }
        }

        replaceFragemnt(fragment)


        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout?
        drawer?.closeDrawer(GravityCompat.START)
        return true
    }
}
