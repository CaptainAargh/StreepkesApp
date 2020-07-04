package be.kdg.scoutsappadmin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.toDrawable
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
import com.google.android.material.navigation.NavigationView
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.android.synthetic.main.nav_header_streepke.*
import kotlinx.android.synthetic.main.nav_header_streepke.view.*
import java.util.*
import kotlin.collections.ArrayList

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
        val periodeDagenNaam: MutableList<String> = ArrayList<String>()


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
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow,
               // R.id.nav_periodeInstellingen
                  R.id.nav_dagInstellingen
            ), drawerLayout
        )


        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val navigationView: NavigationView = findViewById(R.id.nav_view_Streepke)
        val header: View = navigationView.getHeaderView(0)
        val text: TextView = header.findViewById(R.id.nav_header_txtNaam)


        navView.setBackgroundColor(getResources().getColor(R.color.reactiegGreyLight));
        if (persoon.persoonRol != Rol.GROEPSLEIDING) {
            navView.menu.findItem(R.id.nav_dagInstellingen).isVisible = false
            //    navView.menu.findItem(R.id.nav_periodeInstellingen).isVisible = false


            navView.menu.findItem(R.id.nav_slideshow).isVisible = false
        }

        val iv_header = header.nav_header_imageView
        iv_header.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            //bitmapDrawable.bitmap.height = nav_header_imageView.height
            // bitmapDrawable.bitmap.width = nav_header_imageView.width

            nav_header_imageView.setBackgroundDrawable(bitmapDrawable)
            val gson = Gson()
            val sharedPreferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE)
            val jsonpersoon = sharedPreferences.getString("MemPersoon", "")
            val persoonMem: Persoon = gson.fromJson(jsonpersoon, Persoon::class.java)
            uploadImagetoFBdatabase(persoonMem.persoonNaam.toString())

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadImagetoFBdatabase(fileName: String) {
        if (selectedPhotoUri != null) return
        val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")
        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            val toast =
                Toast.makeText(applicationContext, "Photo upgeload naar db", Toast.LENGTH_SHORT)
            toast.show()

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
