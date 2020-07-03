package be.kdg.scoutsappadmin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import be.kdg.scoutsappadmin.model.Periode
import be.kdg.scoutsappadmin.model.Persoon
import be.kdg.scoutsappadmin.model.Rol
import be.kdg.scoutsappadmin.ui.home.HomeFragment
import be.kdg.scoutsappadmin.ui.overzicht.OverzichtFragment
import com.google.android.material.navigation.NavigationView

class StreepkeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streepke)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
/*
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

 */

        val periode = intent.getParcelableExtra<Periode>(LoginActivity.PERIODE)
        val persoon = intent.getParcelableExtra<Persoon>(LoginActivity.GEBRUIKER)
        val periodeDagenNaam : MutableList<String> = ArrayList<String>()


        var bundle = Bundle();
        bundle.putParcelable("periode", periode)
        bundle.putParcelable("persoon", persoon)
        val fragment = HomeFragment()
        fragment.setArguments(bundle)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view_Streepke)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_periodeInstellingen, R.id.nav_dagInstellingen
            ), drawerLayout
        )


        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val navigationView: NavigationView =  findViewById(R.id.nav_view_Streepke)
        val header: View = navigationView.getHeaderView(0)
        val text: TextView = header.findViewById(R.id.nav_header_txtNaam)


        navView.setBackgroundColor(getResources().getColor(R.color.reactiegGreyLight));
        if (persoon.persoonRol != Rol.GROEPSLEIDING) {
            navView.menu.findItem(R.id.nav_dagInstellingen).isVisible = false
            navView.menu.findItem(R.id.nav_periodeInstellingen).isVisible = false
            navView.menu.findItem(R.id.nav_slideshow).isVisible = false
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.streepke, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}
