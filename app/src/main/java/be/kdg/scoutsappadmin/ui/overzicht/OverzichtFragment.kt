package be.kdg.scoutsappadmin.ui.overzicht

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import be.kdg.scoutsappadmin.LoginActivity
import be.kdg.scoutsappadmin.R
import be.kdg.scoutsappadmin.fireBaseModels.FireBasePeriode
import be.kdg.scoutsappadmin.model.Consumptie
import be.kdg.scoutsappadmin.model.Dag
import be.kdg.scoutsappadmin.model.Periode
import be.kdg.scoutsappadmin.model.Persoon
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.streepke_row.view.*

class OverzichtFragment : Fragment() {

    private lateinit var overzichtViewModel: OverzichtViewModel
    private lateinit var spinnerGegevenDoor: Spinner
    private lateinit var spinnerNaam: Spinner
    private lateinit var spinnerDag: Spinner

    private lateinit var rvStreepkes: RecyclerView

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onResume() {
        val periode2 = activity!!.intent.getParcelableExtra<Periode>(LoginActivity.PERIODE)
        fetchAllStreepkes(periode2.key, "totaal")
        super.onResume()


    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        overzichtViewModel =
            ViewModelProviders.of(this).get(OverzichtViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_overzicht, container, false)
        val periode = activity!!.intent.getParcelableExtra<Periode>(LoginActivity.PERIODE)
        val persoon = activity!!.intent!!.getParcelableExtra<Persoon>("gebruiker")

        Log.d("overzicht", periode.toString())

        val spinnerSorteer = root.findViewById<Spinner>(R.id.frgOverzicht_spinner)
        rvStreepkes = root.findViewById(R.id.frgOverzicht_rv_Streepkes)


        fetchAllStreepkes(periode.key, "totaal")

        val labelsFilter =
            listOf<String>("Totaal aantal streepjes", "Vandaag", "Deze Week", "Deze maand")

        var gesorteerd: MutableList<consumptieItem> = ArrayList<consumptieItem>()
        for (i in 0 until periode.periodePersonen!!.size) {
            Log.d("size", periode.periodePersonen!![i].toString())
            gesorteerd.add(
                consumptieItem(
                    periode.periodePersonen!![i].persoonNaam!!,

                    periode.periodePersonen!![i].persoonConsumpties!!.size
                )
            )
        }
        gesorteerd.sortBy { ci ->
            ci.aantal
        }


        val spinnerArrayAdapter = ArrayAdapter<String>(
            this@OverzichtFragment.context!!,
            R.layout.support_simple_spinner_dropdown_item,
            labelsFilter
        )
        spinnerSorteer.adapter = spinnerArrayAdapter
        spinnerSorteer.onItemSelectedListener =
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
                    if (spinnerSorteer.selectedItem.toString().equals("Vandaag")) {
                        fetchAllStreepkes(periode.key, "vandaag")

                    } else if (spinnerSorteer.selectedItem.toString().equals("Deze Week")) {
                        fetchAllStreepkes(periode.key, "week")

                    } else if (spinnerSorteer.selectedItem.toString().equals("Deze maand")) {
                        fetchAllStreepkes(periode.key, "maand")

                    } else {
                        fetchAllStreepkes(periode.key, "totaal")
                    }


                }
            }
        return root
    }

    private fun fetchAllStreepkes(key: String, filter: String) {
        val ref = FirebaseDatabase.getInstance().getReference("/periodes/${key}")
        val list: MutableList<Periode?> = ArrayList<Periode?>()
        val namenList: MutableList<String> = ArrayList<String>()
        val rvAdapter = GroupAdapter<ViewHolder>()


        ref.addValueEventListener(object : ValueEventListener {
            @SuppressLint("UseRequireInsteadOfGet")
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val periodeFb = p0.getValue(FireBasePeriode::class.java)
                val dagenList = periodeFb!!.periodeDagen.values.toList()
                val personenList = periodeFb.periodePersonen.values.toList()
                val periodeFilterTijd = ArrayList<consumptieItem>()

                var periodeDagenList = ArrayList<Dag>()
                var periodePersonenList = ArrayList<Persoon>()

                for (i in 0 until dagenList.size) {
                    var periode_dag_personen_lijst: MutableList<Persoon> = ArrayList<Persoon>()
                    for (j in 0 until dagenList[i].dagPersonen.size) {
                        var tussenLijst = dagenList[i].dagPersonen.values.toList()
                        var periode_dag_personen_consumpties_lijst: MutableList<Consumptie> =
                            ArrayList<Consumptie>()
                        for (x in 0 until tussenLijst[j].persoonConsumpties.size) {
                            var tussenLijstConsumptie =
                                tussenLijst[j].persoonConsumpties.values.toList()
                            periode_dag_personen_consumpties_lijst.add(
                                Consumptie(
                                    tussenLijstConsumptie[x].key.toString(),
                                    tussenLijstConsumptie[x].consumptieGegevenDoor
                                    ,
                                    tussenLijstConsumptie[x].consumptieGegevenDoorId
                                    ,
                                    tussenLijstConsumptie[x].timeStamp
                                )
                            )
                        }
                        periode_dag_personen_lijst.add(
                            Persoon(
                                tussenLijst[j].key,
                                tussenLijst[j].persoonNaam
                                , tussenLijst[j].persoonPass
                                , periode_dag_personen_consumpties_lijst
                                , tussenLijst[j].persoonRol
                            )
                        )
                    }
                    periodeDagenList.add(
                        Dag(
                            dagenList[i].key,
                            dagenList[i].dagDatum,
                            periode_dag_personen_lijst

                        )
                    )

                }
                for (i in 0 until personenList.size) {
                    val consumpties = personenList[i].persoonConsumpties.values.toList()
                    var consumptiesSwitched = ArrayList<Consumptie>()
                    for (y in 0..consumpties.size - 1) {
                        consumptiesSwitched.add(
                            Consumptie(
                                consumpties[y].key,
                                consumpties[y].consumptieGegevenDoor,
                                consumpties[y].consumptieGegevenDoorId,
                                consumpties[y].timeStamp
                            )
                        )
                    }
                    val p = Persoon(
                        personenList[i].key
                        , personenList[i].persoonNaam
                        , personenList[i].persoonPass
                        , consumptiesSwitched
                        , personenList[i].persoonRol
                    )
                    Log.d("getOverzicht", "persoon " + p)
                    periodePersonenList.add(
                        p
                    )
                }

                if (periodeFb != null) {
                    val periode = Periode(
                        periodeFb.key,
                        periodeFb.periodeNaam,
                        periodeDagenList,
                        periodePersonenList
                    )
                    list.add(periode)
                    Log.d("getOverzicht", "periode " + periode)
                    list.forEach { e ->
                        Log.d("getOverzicht2", "periode toegeveogd en returned " + e)
                    }

                    fun getgesorteerd(tijdfiler:Long) {

                        var gesorteerd: MutableList<consumptieItem> = ArrayList<consumptieItem>()
                        for (i in 0 until periode.periodePersonen!!.size) {

                            var consumptieLijst: MutableList<Consumptie> = ArrayList<Consumptie>()
                            var persoonLijst: MutableList<Consumptie> = ArrayList<Consumptie>()
                            periode.periodePersonen!![i].persoonConsumpties!!.forEach { pc ->
                                if (pc.timeStamp > tijdfiler) {
                                    consumptieLijst.add(pc)
                                }
                            }
                            Log.d("size", periode.periodePersonen!![i].toString())
                            gesorteerd.add(
                                consumptieItem(
                                    periode.periodePersonen!![i].persoonNaam!!,

                                    periode.periodePersonen!![i].persoonConsumpties!!.size
                                )
                            )

                            gesorteerd.sortBy { ci ->
                                ci.aantal
                            }
                            gesorteerd.forEach { e ->
                                periodeFilterTijd.add(e)
                            }
                            rvAdapter.clear()
                            rvAdapter.addAll(gesorteerd.reversed())
                            rvStreepkes.adapter = rvAdapter
                            list.add(periode)
                            Log.d("getOverzicht", "periode " + periode)
                            list.forEach { e ->
                                Log.d("getOverzicht2", "periode toegeveogd en returned " + e)
                            }
                        }
                    }


                    if (filter.equals("maand")) {


                        Log.d("oncreate", "Rerun van oncreate met paramter " + filter)
                    } else if (filter.equals("week")) {


                        Log.d("oncreate", "Rerun van oncreate met paramter " + filter)
                    } else if (filter.equals("vandaag")) {
                        val duration = Toast.LENGTH_SHORT
                        val toast = Toast.makeText(activity!!.applicationContext, "filter vandaag geselecteerd", duration)
                        toast.show()
                        getgesorteerd(1590357600000)

                        Log.d("oncreate", "Rerun van oncreate met paramter " + filter)
                        Log.d("oncreate", "Rerun van oncreate met paramter " + filter)
                    } else {
                        getgesorteerd(0)

                    }

                }


            }
        })

    }
}

class consumptieItem(
    val naam: String, val aantal: Int
) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.streepke_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.streepke_row_txtNaam.text = naam
        viewHolder.itemView.streepke_row_txtAantal.text = aantal.toString()
    }

}
