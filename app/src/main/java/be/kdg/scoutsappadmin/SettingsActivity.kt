package be.kdg.scoutsappadmin

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import be.kdg.scoutsappadmin.model.Persoon
import com.google.firebase.database.FirebaseDatabase

class SettingsActivity : AppCompatActivity() {
    private lateinit var sharedPrefs : SharedPreferences
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
       sharedPrefs = getSharedPreferences("animatiePref", Context.MODE_PRIVATE)
        sharedPrefs = getSharedPreferences("animatiePref", Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val editor = sharedPrefs.edit()
        var animatieOnOff = sharedPrefs.getBoolean("animatie",true)
        val txtPass = findViewById<EditText>(R.id.activity_setting_txtpass)
        val btnSave = findViewById<Button>(R.id.activity_setting_btnconfirm)
        val swAnimatie = findViewById<Switch>(R.id.activity_instellingen_swAnimatie)

        val persoon = intent!!.getParcelableExtra<Persoon>("persoon")
        val keyperiode = intent!!.getStringExtra("periodekey")

        Log.d("sliderAnimatie", swAnimatie.isChecked.toString())
        swAnimatie.text = "Animatie streepkes ${(if (animatieOnOff){"AAN"} else {"UIT"} )}"
        swAnimatie.isChecked = sharedPrefs.getBoolean("animatie",true)
        swAnimatie.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                animatieOnOff = true
                //swAnimatie.isChecked = pref.getBoolean("animatie",true)
               // editor.putBoolean("animatie", isChecked).apply()
               swAnimatie.setText("Animatie streepkes AAN")
            } else {
                animatieOnOff = false
                // swAnimatie.isChecked = pref.getBoolean("animatie",true)
                //pref.edit().putBoolean("animatie", false).apply()
                swAnimatie.setText("Animatie streepkes UIT")
            }
            buttonView.width = 50

        }
       // swAnimatie.isChecked = pref.getBoolean("animatie",false)

        btnSave.setOnClickListener {
            if (!txtPass.text.isNullOrEmpty()) {
                val ref = FirebaseDatabase.getInstance().getReference("periodes/$keyperiode/periodePersonen/${persoon.key}/persoonPass")
                ref.setValue(txtPass.text.toString()).addOnSuccessListener {
                    Toast.makeText(applicationContext,"Uw wachtwoord is succesvol gewijzigd",Toast.LENGTH_SHORT).show()
                }

            }
            editor.putBoolean("animatie", animatieOnOff)
            editor.apply()
            finish()
        }

    }
}