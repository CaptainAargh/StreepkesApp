package be.kdg.scoutsappadmin.ui.dag

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import be.kdg.scoutsappadmin.R
import be.kdg.scoutsappadmin.fireBaseModels.FireBasePeriode
import be.kdg.scoutsappadmin.fireBaseModels.FireBasePeriode_Dag_Persoon
import be.kdg.scoutsappadmin.fireBaseModels.periodeDagItem
import be.kdg.scoutsappadmin.model.Consumptie
import be.kdg.scoutsappadmin.model.Dag
import be.kdg.scoutsappadmin.model.Periode
import be.kdg.scoutsappadmin.model.Persoon
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_dag.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DagFragment : Fragment() {


    private lateinit var overzichtViewModel: DagViewModel

    private lateinit var spinnerDag: Spinner
    private lateinit var spinnerPeriode: Spinner
    private lateinit var spinnerPersoon: Spinner
    private lateinit var btnAddPersoon: Button

    private lateinit var txtVan: TextView
    private lateinit var txtTot: TextView
    private lateinit var rvDagen: RecyclerView

    val adapter = GroupAdapter<ViewHolder>()
    var periodeAlgemeen = Periode()
    var dagAlgemeen = Dag()
    var persoonAlgemeen = Persoon()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        overzichtViewModel =
            ViewModelProviders.of(this).get(DagViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dag, container, false)
        //val textView: TextView = root.findViewById(R.id.text_gallery)
        overzichtViewModel.text.observe(viewLifecycleOwner, Observer {
            // textView.text = it
        })


        spinnerDag = root.findViewById(R.id.frgDag_Spinner_Dag)
        spinnerPeriode = root.findViewById(R.id.frgDag_Spinner_Periode)
        spinnerPersoon = root.findViewById(R.id.frgDag_Spinner_Persoon)

        btnAddPersoon = root.findViewById(R.id.frgDag_btn_addPerson)

        txtVan = root.findViewById(R.id.frgDag_txt_Van)
        txtTot = root.findViewById(R.id.frgDag_txt_Tot)

        rvDagen = root.findViewById(R.id.frgDag_rv_dagen)
        fetchAllPeriodes()
        rvDagen.adapter = adapter


        return root
    }


    private fun fetchAllPeriodes() {
        val ref = FirebaseDatabase.getInstance().getReference("/periodes")
        val periodeList: MutableList<Periode> = ArrayList<Periode>()
        val namenList: MutableList<String> = ArrayList<String>()
        var dagenDateList: List<Date> = ArrayList<Date>()
        var dagenDateListStrings: MutableList<String> = ArrayList<String>()
        val list: MutableList<Periode?> = ArrayList<Periode?>()
        dagenDateListStrings.clear()


        var dagenSortedStringList: MutableList<String> = ArrayList<String>()

        ref.addChildEventListener(object : ChildEventListener {
            @SuppressLint("UseRequireInsteadOfGet")
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val periodeFb = p0.getValue(FireBasePeriode::class.java)
                val dagenList = periodeFb!!.periodeDagen.values.toList()
                val personenList = periodeFb.periodePersonen.values.toList()

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
                    for (y in 0..consumpties.size-1) {
                        consumptiesSwitched.add(Consumptie(consumpties[y].key,consumpties[y].consumptieGegevenDoor,consumpties[y].consumptieGegevenDoorId, consumpties[y].timeStamp))
                    }
                    periodePersonenList.add(
                        Persoon(
                            personenList[i].key
                            , personenList[i].persoonNaam
                            , personenList[i].persoonPass
                            , consumptiesSwitched
                            , personenList[i].persoonRol
                        )
                    )
                }
                for (i in 0 until personenList.size) {
                    val consumpties = personenList[i]!!.persoonConsumpties.values.toList()
                    var consumptiesSwitched = ArrayList<Consumptie>()
                    for (y in 0..consumpties.size-1) {
                        consumptiesSwitched.add(Consumptie(consumpties[y].key,consumpties[y].consumptieGegevenDoor,consumpties[y].consumptieGegevenDoorId, consumpties[y].timeStamp))
                    }
                    periodePersonenList.add(
                        Persoon(
                            personenList[i]!!.key
                            , personenList[i]!!.persoonNaam
                            , personenList[i]!!.persoonPass
                            , consumptiesSwitched
                            , personenList[i]!!.persoonRol
                        )
                    )
                }

                if (periodeFb != null) {
                    val periode = Periode(
                        periodeFb.key,
                        periodeFb.periodeNaam,
                        periodeDagenList,
                        periodePersonenList
                    )
                    namenList.add(periode.periodeNaam!!)
                    periodeList.add(periode)





                    Log.d("getPeriodes", periode.toString())
                    Log.d("getPeriodes", periode.periodeDagen.toString())

                }
                val spinnerArrayAdapter = ArrayAdapter<String>(
                    this@DagFragment.context!!,
                    R.layout.support_simple_spinner_dropdown_item,
                    namenList
                )
                var p = Periode()
                spinnerPeriode.adapter = spinnerArrayAdapter
                spinnerPeriode.onItemSelectedListener =
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
                            for (i in 0..periodeList.size - 1) {
                                if (periodeList[i].periodeNaam.equals(
                                        parent!!.getItemAtPosition(
                                            position
                                        ).toString()
                                    )
                                ) {
                                    p = periodeList[i]
                                }
                            }
                            val listDates = sortDates(p)
                            if (listDates.size > 1) {
                                txtVan.text =
                                    listDates.first().toString().subSequence(0, 10)
                                txtTot.text =
                                    listDates.last().toString().subSequence(0, 10)

                            }

                            if (spinnerPeriode.selectedItem == null) {
                                frgDag_btn_addPerson.setEnabled(false)
                            }

                            loop@ for (y in 0..p.periodeDagen!!.size - 1) {
                                if  (p.periodeDagen!![y].dagDatum.toString().length > 1) {
                                    if (p.periodeDagen!![y].dagDatum.toString().subSequence(0, 10)
                                            .equals(spinnerDag.selectedItem.toString())
                                    ) {
                                        p.periodeDagen!![y].dagPersonen!!.forEach { dag ->
                                            adapter.add(
                                                periodeDagItem(
                                                    dag
                                                )
                                            )
                                        }
                                        break@loop
                                    }
                                }

                            }
                        }
                    }
                var periodeDag2 = Periode()
                for (i in 0..periodeList.size - 1) {
                    if (periodeList[i].periodeNaam.equals(
                            spinnerPeriode.selectedItem.toString()
                        )
                    ) {
                        periodeDag2 = periodeList[i]
                        break
                    }
                }
                val lijstPersonen = periodeDag2.periodePersonen
                val lijstPersonenString = ArrayList<String>()
                for (x in 0 until lijstPersonen!!.size) {
                    lijstPersonenString.add(lijstPersonen[x].persoonNaam!!)
                }
                val spinnerArrayAdapterPersonen = ArrayAdapter<String>(
                    this@DagFragment.context!!,
                    R.layout.support_simple_spinner_dropdown_item,
                    lijstPersonenString
                )
                spinnerPersoon.adapter = spinnerArrayAdapterPersonen
                spinnerPersoon.onItemSelectedListener =
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
                for (i in 0 until dagenDateListStrings.size) {

                }
                var periodeDag = Periode()
                for (i in 0..periodeList.size - 1) {
                    Log.d(
                        "datefucker2",
                        "Meendem? :" + " : " + periodeList[i].toString())

                        if (periodeList[i].periodeNaam.equals(
                            spinnerPeriode.selectedItem.toString()
                        )
                    ) {
                        periodeDag = periodeList[i]
                    }
                }
                Log.d(
                    "datefucker2",
                    "Gast print die string is :" + " : " + periodeDag.toString()

                )
                val listDates = sortDates(periodeDag)
                val dagenLijst = ArrayList<String>()
                for (i in 0 until listDates.size) {
                    dagenLijst.add(listDates[i].toString().substring(0, 10))
                    Log.d(
                        "dagenDateListStrings",
                        "Vanuit listDates via sort fun " + " : " + listDates[i].toString()
                            .substring(0, 10)
                    )
                }
                val spinnerDagArrayAdapter = ArrayAdapter<String>(
                    this@DagFragment.context!!,
                    R.layout.support_simple_spinner_dropdown_item,
                    dagenLijst
                )
                spinnerDag.adapter = spinnerDagArrayAdapter
                spinnerDag.onItemSelectedListener =
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
                var periode = Periode()
                var keyPeriode = ""
                btnAddPersoon.setOnClickListener {
                    for (i in 0 until periodeList.size) {
                        if (periodeList[i].periodeNaam.equals(
                                spinnerPeriode.selectedItem.toString()
                            )
                        ) {
                            keyPeriode = periodeList[i].key
                            periode = periodeList[i]

                        }
                    }
                    var persoon = Persoon()
                    loop@ for (aa in 0 until periode.periodePersonen!!.size) {
                        if (periode.periodePersonen!![aa].persoonNaam
                                .equals(spinnerPersoon.selectedItem.toString())
                        ) {
                            persoon = periode.periodePersonen!![aa]
                            break@loop
                        }
                    }
                    var dag = Dag()
                    val key = String()

                    if (periode.periodeDagen != null) {
                        loop@ for (aa in 0 until periode.periodeDagen!!.size) {
                            Log.d(
                                "addPeriodePersoon",
                                "String van elke dag in periodeDagen" + periode.periodeDagen!![aa].key
                            )

                            if (periode.periodeDagen!![aa].dagDatum.toString().substring(0, 10)
                                    .equals(spinnerDag.selectedItem.toString())

                            ) {
                                Log.d(
                                    "addPeriodePersoon",
                                    "String periode dag datum " + periode.periodeDagen!![aa].dagDatum
                                        .toString().substring(0, 10)
                                )
                                Log.d(
                                    "addPeriodePersoon",
                                    "String van spinnerDag.selectedItem.toStrin " + spinnerDag.selectedItem.toString()
                                )

                                dag = periode.periodeDagen!![aa]
                            }
                        }
                    }


                    if (periode.periodePersonen != null) {
                        val keyDagenPersonen =
                            FirebaseDatabase.getInstance().reference.child("periodes/${periode.key}/periodeDagen/dagPersonen")
                                .push().key
                        Log.d("addPeriodePersoon", "Key van dag " + dag.key)

                        val dagenPersonen =
                            FirebaseDatabase.getInstance().reference.child("periodes/${periode.key}/periodeDagen/${dag.key}")
                                .child("dagPersonen")
                                .push()
                        val dagenPersonenKey =
                            dagenPersonen
                                .key
                        val dagen =
                            FirebaseDatabase.getInstance().reference.child("periodes/${periode.key}/periodeDagen/${dag.key}")
                                .push()
                        val fbPeriodeDagPersoon = FireBasePeriode_Dag_Persoon(
                            dagenPersonenKey!!,
                            persoon.persoonNaam!!,
                            persoon.persoonPass!!,
                            emptyMap(),
                            persoon.persoonRol!!
                        )


                        dagenPersonen.setValue(fbPeriodeDagPersoon)
                        Log.d("addPeriodePersoon", "Jeej succes" + fbPeriodeDagPersoon)

                        //  dag.dagPersonen!!.toMutableList().add(persoon)
                        //  dagen.setValue(dag)

                    }
                }


            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })

    }

    fun sortDates(periode: Periode): List<Date> {
        val dates: MutableList<Date> =
            emptyList<Date>().toMutableList()
        for (i in 0..periode.periodeDagen!!.size - 1) {
            val sdf3 =
                SimpleDateFormat(
                    "EEE MMM dd HH:mm:ss zzz yyyy",
                    Locale.ENGLISH
                )

            var d1: Date? = null
            try {
                d1 = sdf3.parse(
                    periode.periodeDagen!![i].dagDatum
                )
                dates.add(d1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.d(
                "datefucker",
                "date geparsed van HomzFragment : " + d1.toString()
            )
        }
        dates.sort()
        return dates
    }

    fun sortDatesStrings(periode: Periode): MutableList<String> {
        val dates: MutableList<String> =
            emptyList<String>().toMutableList()
        for (i in 0..periode.periodeDagen!!.size - 1) {
            val sdf3 =
                SimpleDateFormat(
                    "EEE MMM dd HH:mm:ss zzz yyyy",
                    Locale.ENGLISH
                )


            for (j in 0 until periode.periodeDagen!!.size) {
                var d1: Date? = null
                try {
                    d1 = sdf3.parse(
                        periode.periodeDagen?.get(j)?.dagDatum
                    )
                    dates.add(d1.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Log.d(
                    "datefucker",
                    "date geparsed van HomzFragment : " + d1.toString()
                )
            }
        }
        return dates
    }


}




