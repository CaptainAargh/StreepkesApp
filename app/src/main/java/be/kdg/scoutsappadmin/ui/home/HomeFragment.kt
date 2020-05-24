package be.kdg.scoutsappadmin.ui.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import be.kdg.scoutsappadmin.R
import be.kdg.scoutsappadmin.model.Consumptie
import be.kdg.scoutsappadmin.model.Periode
import be.kdg.scoutsappadmin.model.Persoon
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.frgStreepke_btn_addStreepjes


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
             homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val periode = activity!!.intent!!.getParcelableExtra<Periode>("periode")
        val persoon = activity!!.intent!!.getParcelableExtra<Persoon>("gebruiker")

        Log.d("please", periode.toString())
        Log.d("please", persoon.toString())

        val periodeDagenNaam: MutableList<String> = ArrayList<String>()

        val spinner =  root.findViewById<Spinner>(R.id.frgStreepke_spinner_selectDag)
        val chipGroep =  root.findViewById<ChipGroup>(R.id.frgStreepke_chipsGroup)
        val addStreepke =  root.findViewById<Button>(R.id.frgStreepke_btn_addStreepjes)

        periode.periodeDagen!!.forEach {
                dag -> periodeDagenNaam.add(dag.dagDatum!!.substring(0,10))
        }
        val spinnerArrayAdapter = ArrayAdapter<String>(
            root.context,
            R.layout.support_simple_spinner_dropdown_item,
            periodeDagenNaam
        )
        spinner.adapter = spinnerArrayAdapter
        spinner.onItemSelectedListener =
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
            val chip  = Chip(root.context)
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
            chipGroep.addView(chip)
        }


        addStreepke.setOnClickListener {
            for (i in 0 until geselecteerdePersonen.size ) {
                val refPeriode_Personen_Streepkes =
                    FirebaseDatabase.getInstance().reference.child("periodes/${periode.key}/periodePersonen/${geselecteerdePersonen[i].key}/streepkes").push()

                val refPeriode_Personen_StreepkesKey =
                    refPeriode_Personen_Streepkes.key

                if (refPeriode_Personen_StreepkesKey == null) {
                    Log.d("periodes", "keys push niet aangekregen van periodes")
                    return@setOnClickListener
                }
                Log.d("overzichterror", persoon.toString())

                val c = Consumptie(refPeriode_Personen_StreepkesKey,persoon.persoonNaam,persoon.key,
                    System.currentTimeMillis())


                refPeriode_Personen_Streepkes.setValue(c)
                Log.d("addPeriodePersoon", "Jeej succes" + c.toString())
            }

        }
        return  root
    }

}

