package be.kdg.scoutsappadmin.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import be.kdg.scoutsappadmin.MainActivity
import be.kdg.scoutsappadmin.R
import be.kdg.scoutsappadmin.fireBaseModels.FireBasePeriode
import be.kdg.scoutsappadmin.fireBaseModels.FireBasePeriode_Dag
import be.kdg.scoutsappadmin.fireBaseModels.FireBasePeriode_Dag_Persoon
import be.kdg.scoutsappadmin.fireBaseModels.FireBasePeriode_Persoon
import be.kdg.scoutsappadmin.model.*
import com.google.firebase.database.*
import java.lang.Long.getLong
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class addPeriodeActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addperiode)

        val txtPeriodeNaam = findViewById<EditText>(R.id.frgAddPeriode_txt_periodeNaam)
        val txtPeriodeVan = findViewById<EditText>(R.id.frgAddPeriode_txt_periodeVan)
        val txtPeriodeTot = findViewById<EditText>(R.id.frgAddPeriode_txt_periodeTot)
        val txtPersoonNaam = findViewById<EditText>(R.id.frgAddPeriode_txt_persoonNaam)
        val txtPersoonPass = findViewById<EditText>(R.id.frgAddPeriode_txt_persoonPass)
        val txtPersoonRol = findViewById<Spinner>(R.id.frgAddPeriode_spinner_rol)
        val btnAddPeriode = findViewById<Button>(R.id.frgAddPeriode_btn_AddPeriode)
        val btnGaTerug = findViewById<Button>(R.id.frgAddPeriode_btn_gaterug)


        btnGaTerug.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        btnAddPeriode.setOnClickListener {
            val dateVan: Date? = SimpleDateFormat("dd/MM/yyyy").parse(txtPeriodeVan.text.toString())
            val dateTot: Date? = SimpleDateFormat("dd/MM/yyyy").parse(txtPeriodeTot.text.toString())
            Log.d("addData", "dateVan$dateVan")
            Log.d("addData", "dateTot$dateTot")


            val diff: Long = dateVan!!.time - dateTot!!.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            val vdays = Math.abs(days)
            Log.d("addData", "days$vdays")

            var dagenLijst: MutableList<Dag> = java.util.ArrayList<Dag>()
            var persoonLijst: MutableList<Persoon> = java.util.ArrayList<Persoon>()
            var consumptieLijst: MutableList<Consumptie> = java.util.ArrayList<Consumptie>()

            val persoon: Persoon =
                Persoon("", txtPersoonNaam.text.toString(), txtPersoonPass.text.toString(), consumptieLijst, Rol.GROEPSLEIDING);
            var timestamp = getLong(LocalDateTime.now().toString())
            var consumptie: Consumptie = Consumptie(
                "key",
                persoon.persoonNaam,
                persoon.key,
                System.currentTimeMillis()
            )

            consumptieLijst.add(consumptie)


            persoonLijst.add(persoon)



            for (i in 0..vdays) {
                var dtVan = dateVan
                val c = Calendar.getInstance()
                c.time = dtVan
                c.add(Calendar.DATE, i.toInt())
                dtVan = c.time
                Log.d("getPeriodes2","Date VAN to string :" + dtVan.toString())
                val dag: Dag = Dag("key", dtVan.toString(), persoonLijst)
                dagenLijst.add(dag)
            }
            val periode = Periode("key", txtPeriodeNaam.text.toString(), dagenLijst, persoonLijst)

            val myRef = FirebaseDatabase.getInstance().getReference("")
            val cL: MutableList<Consumptie> = java.util.ArrayList<Consumptie>()
            //val p = Persoon("key",txtPersoonNaam.text.toString(), txtPersoonPass.text.toString(), cL ,Rol.GROEPSLEIDING)
            addPeriode(periode, persoonLijst)

        }

    }

    private fun addPeriode(periode: Periode, personen: List<Persoon>) {

        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        val key = FirebaseDatabase.getInstance().reference.child("periodes").push().key
        val keyDagen =
            FirebaseDatabase.getInstance().reference.child("periodes/$key/periodeDagen").push().key
        val keyDagenPersonen =
            FirebaseDatabase.getInstance().reference.child("periodes/$key/periodeDagen/$keyDagen/dagPersonen")
                .push().key
        val keyDagenPersonenConsumpties =
            FirebaseDatabase.getInstance().reference.child("periodes/$key/periodeDagen/$keyDagen/dagPersonen/$keyDagenPersonen/persoonConsumptie")
                .push().key

        if (key == null) {
            Log.d("periodes", "keys push niet aangekregen van periodes")
            return
        }

        val fbPeriode = FireBasePeriode(key, periode.periodeNaam!!, emptyMap(), emptyMap())
        //fbPeriode.periodePersonen = personen
        val refPeriode = FirebaseDatabase.getInstance().reference.child("periodes/$key")
        refPeriode.setValue(fbPeriode)

        for (i in 0..periode.periodeDagen!!.size-1) {
            val refPeriode_Dagen =
                FirebaseDatabase.getInstance().reference.child("periodes/$key/periodeDagen").push()
            val refPeriode_DagenKey =
                refPeriode_Dagen.key

            if ( refPeriode_DagenKey == null) {
                Log.d("periodes", "keys push niet aangekregen van periodes")
                return
            }
            val fbPeriodeDagen = FireBasePeriode_Dag(
                refPeriode_DagenKey,
                periode.periodeDagen!![i].dagDatum!!,
                emptyMap()
            )
            refPeriode_Dagen.setValue(fbPeriodeDagen)

            for (j in 0..periode.periodeDagen!![i].dagPersonen!!.size-1) {
                val refPeriode_Dagen_Personen =
                    FirebaseDatabase.getInstance().reference.child("periodes/$key/periodeDagen/$refPeriode_DagenKey/dagPersonen").push()
                val refPeriode_DagenKey_Personen =
                    refPeriode_Dagen_Personen.key
                if ( refPeriode_DagenKey_Personen == null) {
                    Log.d("periodes", "keys push niet aangekregen van periodes")
                    return
                }
                val fbPeriodeDagPersonen = FireBasePeriode_Dag_Persoon(
                    refPeriode_DagenKey_Personen,
                    periode.periodeDagen!![i].dagPersonen!![j].persoonNaam!!,
                    periode.periodeDagen!![i].dagPersonen!![j].persoonPass!!,
                    emptyMap(),
                    periode.periodeDagen!![i].dagPersonen!![j].persoonRol!!
                )
                refPeriode_Dagen_Personen.setValue(fbPeriodeDagPersonen)

            }
        }


        for (i in 0..periode.periodePersonen!!.size-1) {
            val refPeriode_Personen =
                FirebaseDatabase.getInstance().reference.child("periodes/$key/periodePersonen").push()
            val refPeriode_PersonenKey =
                refPeriode_Personen.key
            if ( refPeriode_PersonenKey == null) {
                Log.d("periodes", "keys push niet aangekregen van periodes")
                return
            }

            val fbPeriodePersonen = FireBasePeriode_Persoon(
                refPeriode_PersonenKey,
                periode.periodePersonen!![i].persoonNaam!!,
                periode.periodePersonen!![i].persoonPass!!,
                emptyMap(),
                periode.periodePersonen!![i].persoonRol
                )
            refPeriode_Personen.setValue(fbPeriodePersonen)
        }

    }

    private fun addPersoon(persoon: Persoon, periodeKey: String) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        val key = FirebaseDatabase.getInstance().reference.child("periodes").push().key
        if (key == null) {
            Log.d("periodes", "keys push niet aangekregen van periodes")
            return
        }
        val fbPersoon = Persoon(
            key, persoon.persoonNaam, persoon.persoonPass, persoon.persoonConsumpties,
            persoon.persoonRol!!
        )
        val persoonValues = fbPersoon.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/periodes/$periodeKey/periodePersonen/$key"] = persoonValues

        FirebaseDatabase.getInstance().reference.updateChildren(childUpdates)
    }

}
