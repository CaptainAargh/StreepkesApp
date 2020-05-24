package be.kdg.scoutsappadmin

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import be.kdg.scoutsappadmin.model.Consumptie
import be.kdg.scoutsappadmin.model.Periode
import be.kdg.scoutsappadmin.model.Persoon
import com.google.android.material.chip.Chip
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_main.*


class StreepkeMainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main)

        val periode = intent.getParcelableExtra<Periode>(LoginActivity.PERIODE)
        val persoon = intent.getParcelableExtra<Persoon>(LoginActivity.GEBRUIKER)
        val periodeDagenNaam : MutableList<String> = ArrayList<String>()


        periode.periodeDagen!!.forEach {
            dag -> periodeDagenNaam.add(dag.dagDatum!!.substring(0,10))
        }
        val spinnerArrayAdapter = ArrayAdapter<String>(
            this@StreepkeMainActivity,
            R.layout.support_simple_spinner_dropdown_item,
            periodeDagenNaam
        )
        frgStreepke_spinner_selectDag.adapter = spinnerArrayAdapter
        frgStreepke_spinner_selectDag.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                }
            }

        val geselecteerdePersonen : MutableList<Persoon> = ArrayList<Persoon>()


        for (i in 0 until periode.periodePersonen!!.size) {
            val chip  = Chip(this)
            chip.isCheckable = true
            chip.isClickable = true
            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
                    geselecteerdePersonen.add(periode.periodePersonen!![i])
                    chip.setBackgroundColor(Color.GREEN)
                }else{
                    geselecteerdePersonen.remove(periode.periodePersonen!![i])
                }

            }
            chip.setText(periode.periodePersonen!![i].persoonNaam)
            frgMain_chipsGroup.addView(chip)
        }


        frgStreepke_btn_addStreepjes.setOnClickListener {
           for (i in 0 until geselecteerdePersonen.size ) {
               val refPeriode_Personen_Streepkes =
                   FirebaseDatabase.getInstance().reference.child("periodes/${periode.key}/periodePersonen/${geselecteerdePersonen[i].key}/streepkes").push()

               val refPeriode_Personen_StreepkesKey =
                   refPeriode_Personen_Streepkes.key

               if (refPeriode_Personen_StreepkesKey == null) {
                   Log.d("periodes", "keys push niet aangekregen van periodes")
                   return@setOnClickListener
               }
               val c = Consumptie(refPeriode_Personen_StreepkesKey,persoon.persoonNaam,persoon.key,
                 System.currentTimeMillis())


               refPeriode_Personen_Streepkes.setValue(c)
               Log.d("addPeriodePersoon", "Jeej succes" + c.toString())
           }

        }
    }

}
