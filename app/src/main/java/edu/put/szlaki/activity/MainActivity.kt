package edu.put.szlaki.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import edu.put.szlaki.R
import edu.put.szlaki.R.id.FirstFragment
import edu.put.szlaki.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        //setupActionBarWithNavController(navController, appBarConfiguration) <- Breaks nav drawer icon

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                FirstFragment -> binding.fab.visibility = View.VISIBLE
                else -> binding.fab.visibility = View.GONE
            }
        }

        binding.apply {
            toggle= ActionBarDrawerToggle(this@MainActivity, drawerLayout, R.string.open, R.string.close)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            navView.setNavigationItemSelectedListener {
                when(it.itemId) {
                    R.id.firstItem -> {
                        navController.navigate(R.id.FirstFragment)
                        drawerLayout.closeDrawer(GravityCompat.START)
                        true
                    }
                    R.id.secondItem->{
                        navController.navigate(R.id.newTrial)
                        drawerLayout.closeDrawer(GravityCompat.START)
                        true
                    }
                    R.id.thirdItem->{
                        navController.navigate(R.id.stoper)
                        drawerLayout.closeDrawer(GravityCompat.START)
                        true
                    }
                }
                true
            }
        }

        // Setup FAB click listener
        binding.fab.setOnClickListener { view ->
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_FirstFragment_to_newTrial)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            true
        }
        return super.onOptionsItemSelected(item)
    }


}
