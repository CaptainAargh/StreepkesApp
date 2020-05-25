package be.kdg.scoutsappadmin.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import be.kdg.scoutsappadmin.LoginActivity
import be.kdg.scoutsappadmin.R
import be.kdg.scoutsappadmin.StreepkeActivity
import be.kdg.scoutsappadmin.model.Consumptie
import be.kdg.scoutsappadmin.model.Periode
import be.kdg.scoutsappadmin.model.Persoon
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.nav_header_streepke.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    @SuppressLint("UseRequireInsteadOfGet", "ResourceType")
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

        val navigationView: NavigationView = activity!!.findViewById(R.id.nav_view_Streepke)
        val header: View = navigationView.getHeaderView(0)
        val naam: TextView = header.findViewById(R.id.nav_header_txtNaam)
        val rol: TextView = header.findViewById(R.id.nav_header_txtRol)
        naam.text = persoon.persoonNaam
        rol.text = persoon.persoonRol.toString()
        val btnLogout: Button = header.findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            val intent = Intent(root.context, LoginActivity::class.java)
            startActivity(intent)
        }

        var bool = false

        val periodeDagenNaam: MutableList<String> = ArrayList<String>()

        val chipGroep = root.findViewById<ChipGroup>(R.id.frgStreepke_chipsGroup)
        val addStreepke = root.findViewById<Button>(R.id.frgStreepke_btn_addStreepjes)

        periode.periodeDagen!!.forEach { dag ->
            periodeDagenNaam.add(dag.dagDatum!!.substring(0, 10))
        }


        val geselecteerdePersonen: MutableList<Persoon> = ArrayList<Persoon>()


        for (i in 0 until periode.periodePersonen!!.size) {
            val chip = Chip(root.context,null, R.attr.CustomChipChoice)
            chip.isCheckable = true
            chip.isClickable = true
            chip.setOnCheckedChangeListener { addStreepke, isChecked ->
                if (isChecked) {
                    geselecteerdePersonen.add(periode.periodePersonen!![i])
                    chip.setBackgroundColor(Color.GREEN)
                } else {
                    geselecteerdePersonen.remove(periode.periodePersonen!![i])
                }

            }
            chip.setText(periode.periodePersonen!![i].persoonNaam)
            chipGroep.addView(chip)
        }

        addStreepke.setOnClickListener {
            var personenString: String =
                "Bevestig dat ge een streke wilt zetten voor volgende person: \n"

            for (i in 0 until geselecteerdePersonen.size) {
                personenString += "\n " + geselecteerdePersonen[i].persoonNaam
            }
            for (i in 0 until geselecteerdePersonen.size) {
                val refPeriode_Personen_Streepkes =
                    FirebaseDatabase.getInstance().reference.child("periodes/${periode.key}/periodePersonen/${geselecteerdePersonen[i].key}/persoonConsumpties")
                        .push()

                val refPeriode_Personen_StreepkesKey =
                    refPeriode_Personen_Streepkes.key

                if (refPeriode_Personen_StreepkesKey == null) {
                    Log.d("periodes", "keys push niet aangekregen van periodes")
                    return@setOnClickListener
                }

                val alertDialog =
                    AlertDialog.Builder(root.context)
                alertDialog.setTitle("Strepke dabei")
                Log.d("personenString", "String personen " + personenString)
                alertDialog.setMessage(personenString)
                alertDialog.setPositiveButton("OK", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        for (i in 0 until chipGroep.getChildCount()) {
                            val chip = chipGroep.getChildAt(i) as Chip
                            if (chip.isChecked) {
                                chip.isChecked = false
                            }
                        }
                        val c = Consumptie(
                            refPeriode_Personen_StreepkesKey, persoon.persoonNaam, persoon.key,
                            System.currentTimeMillis()
                        )
                        refPeriode_Personen_Streepkes.setValue(c)
                        Log.d("addPeriodePersoon", "Jeej succes" + c.toString())
                    }
                })
                alertDialog.show()
            }
        }
        return root
    }

}

