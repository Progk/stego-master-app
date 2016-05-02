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
import com.ssivulskiy.stegomaster.fragments.KoxaJaoFragment
import com.ssivulskiy.stegomaster.fragments.StegoLsbFragment

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

        replaceFragemnt(StegoLsbFragment.newInstance())
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
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
                fragment = StegoLsbFragment.newInstance()
                setToolbarTitle(R.string.lsb_method)
            }
            R.id.nav_koxa_jao -> {
                fragment = KoxaJaoFragment.newInstanse()
                setToolbarTitle(R.string.koxa_jao_method)

            }
            else -> {
                fragment = StegoLsbFragment.newInstance()
            }
        }

        replaceFragemnt(fragment)


        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout?
        drawer?.closeDrawer(GravityCompat.START)
        return true
    }
}
