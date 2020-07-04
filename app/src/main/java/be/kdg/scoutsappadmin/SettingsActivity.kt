package be.kdg.scoutsappadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import be.kdg.scoutsappadmin.model.Persoon
import com.google.firebase.database.FirebaseDatabase

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val txtPass = findViewById<EditText>(R.id.activity_setting_txtpass)
        val btnSave = findViewById<Button>(R.id.activity_setting_btnconfirm)


        val persoon = intent!!.getParcelableExtra<Persoon>("persoon")
        val keyperiode = intent!!.getStringExtra("periodekey")

        btnSave.setOnClickListener {
            if (!txtPass.text.isNullOrEmpty()) {
                val ref = FirebaseDatabase.getInstance().getReference("periodes/$keyperiode/periodePersonen/${persoon.key}/persoonPass")
                ref.setValue(txtPass.text.toString()).addOnSuccessListener {
                    Toast.makeText(applicationContext,"Uw wachtwoord is succesvol gewijzigd",Toast.LENGTH_SHORT).show()
                }
            }
            finish()
        }

    }
}