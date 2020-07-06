package be.kdg.scoutsappadmin.ui.home_admin

import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.kdg.scoutsappadmin.R
import be.kdg.scoutsappadmin.fireBaseModels.FireBasePeriode
import be.kdg.scoutsappadmin.fireBaseModels.FireBasePeriode_Persoon
import be.kdg.scoutsappadmin.fireBaseModels.periodeItem
import be.kdg.scoutsappadmin.model.*
import be.kdg.scoutsappadmin.ui.addPeriodeActivity
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment_admin : Fragment() {

    private lateinit var homeViewModelAdmin: HomeViewModel_admin

    private lateinit var txtVan: TextView
    private lateinit var txtTot: TextView

    private lateinit var txtPersoonNaam: TextView
    private lateinit var txtPersoonPass: TextView
    private lateinit var spinnerPersoonRol: Spinner


    private lateinit var btnAddPersoon: Button
    private lateinit var btnAddPeriode: Button

    private lateinit var spinnerPeriode: Spinner

    private lateinit var rvPersonen: RecyclerView

    val adapter = GroupAdapter<ViewHolder>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModelAdmin =
            ViewModelProviders.of(this).get(HomeViewModel_admin::class.java)
        val root = inflater.inflate(R.layout.fragment_home_admin, container, false)
        //val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModelAdmin.text.observe(viewLifecycleOwner, Observer {
            //textView.text = it
        })
        txtVan = root.findViewById(R.id.frgHome_txt_Van)
        txtTot = root.findViewById(R.id.frgHome_txt_tot)

        txtPersoonNaam = root.findViewById(R.id.frgHome_txt_persoonName)
        txtPersoonPass = root.findViewById(R.id.frgHome_txt_persoonPass)
        spinnerPersoonRol = root.findViewById(R.id.frgHome_Spinner_selectRol)

        btnAddPersoon = root.findViewById(R.id.frgHome_btn_addPersoon)
        btnAddPeriode = root.findViewById(R.id.frgHome_btn_addPeriode)

        spinnerPeriode = root.findViewById(R.id.frgHome_Spinner_selectPeriode)

        rvPersonen = root.findViewById(R.id.frgHome_rv_personen)
        rvPersonen.layoutManager = LinearLayoutManager(this.context)

        fetchAllPeriodes()

        rvPersonen.adapter = adapter


        btnAddPeriode.setOnClickListener {
            val intent = Intent(root.context, addPeriodeActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    private fun getPeriodeNames(periodeList: ArrayList<Periode>): ArrayList<String?> {
        val periodes: ArrayList<String?> = ArrayList()
        for (i in 1..periodeList.size) {
            periodes.add(periodeList[i].periodeNaam!!)
        }
        return periodes
    }

    private fun fetchAllPeriodes() {
        val ref = FirebaseDatabase.getInstance().getReference("/periodes")
        val periodeList: MutableList<Periode> = ArrayList<Periode>()
        val namenList: MutableList<String> = ArrayList<String>()
        val list: MutableList<Periode?> = ArrayList<Periode?>()


        ref.addChildEventListener(object : ChildEventListener {

            @SuppressLint("UseRequireInsteadOfGet")
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val periodeFb = p0.getValue(FireBasePeriode::class.java)
                val dagenList = periodeFb!!.periodeDagen.values.toList()
                val dagenListDatums = periodeFb!!.periodeDagen.values.toList()

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
                                    tussenLijstConsumptie[x].key,
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
                    Log.d("dagdatumparsing", "in begin ocode dagenlist datm eement:" +  dagenList[i].dagDatum)
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


                if (periodeFb != null) {
                    val periode = Periode(
                        periodeFb.key,
                        periodeFb.periodeNaam,
                        periodeDagenList,
                        periodePersonenList
                    )
                    val listDates = sortDates(periode)

                    if (listDates.size > 1) {
                        adapter.add(
                            periodeItem(
                                periode,
                                listDates.first().toString(),
                                listDates.last().toString()
                            )
                        )
                        namenList.add(periode.periodeNaam!!)
                        periodeList.add(periode)

                        Log.d("getPeriodes", periode.toString())
                        Log.d("getPeriodes", periode.periodeDagen.toString())

                    }

                }
                val spinnerArrayAdapter = ArrayAdapter<String>(
                    this@HomeFragment_admin.context!!,
                    R.layout.support_simple_spinner_dropdown_item,
                    namenList
                )
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
                            for (i in 0 until periodeList.size) {
                                if (periodeList[i].periodeNaam.equals(
                                        parent!!.getItemAtPosition(
                                            position
                                        ).toString()
                                    )
                                ) {
                                    val listDates = sortDates(periodeList[i])
                                    txtVan.text =
                                        listDates.first().toString()
                                    txtTot.text =
                                        listDates.last().toString()

                                }
                            }
                            if (spinnerPeriode.selectedItem == null) {
                                btnAddPersoon.setEnabled(false)
                            }
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

        val refDagen = FirebaseDatabase.getInstance().getReference("/periodes")
        refDagen.addChildEventListener(object : ChildEventListener {
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
                for (i in 0..personenList.size-1) {
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


                if (periodeFb != null) {
                    val periode = Periode(
                        periodeFb.key,
                        periodeFb.periodeNaam,
                        periodeDagenList,
                        periodePersonenList
                    )
                    val listDates = sortDates(periode)

                    if (listDates.size > 1) {
                        adapter.add(
                            periodeItem(
                                periode,
                                listDates.first().toString(),
                                listDates.last().toString()
                            )
                        )
                    }

                    namenList.add(periode.periodeNaam!!)
                    periodeList.add(periode)

                    Log.d("getPeriodes", periode.toString())
                    Log.d("getPeriodes", periode.periodeDagen.toString())

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
                    if (periode.periodePersonen != null) {
                        var r: Rol = Rol.GROEPSLEIDING
                        if (spinnerPersoonRol.getSelectedItem().toString()
                                .equals("Leiding")
                        ) {
                            r = Rol.LEIDER
                        }
                        if (spinnerPersoonRol.getSelectedItem().toString().equals("Fourage")) {
                            r = Rol.FOURAGE
                        }
                        val refPeriode_Personen =
                            FirebaseDatabase.getInstance().reference.child("periodes/$keyPeriode/periodePersonen")
                                .push()
                        val refPeriode_PersonenKey =
                            refPeriode_Personen.key

                        if (refPeriode_PersonenKey == null) {
                            Log.d("periodes", "keys push niet aangekregen van periodes")
                            return@setOnClickListener
                        }
                        val fbPeriodePersonen = FireBasePeriode_Persoon(
                            refPeriode_PersonenKey,
                            txtPersoonNaam.text.toString(),
                            txtPersoonPass.text.toString(),
                            emptyMap(),
                            r
                        )
                        refPeriode_Personen.setValue(fbPeriodePersonen)
                        Log.d("addPeriodePersoon", "Jeej succes" + fbPeriodePersonen)
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
        val sdf3 =
            SimpleDateFormat(
                "EEE MMM dd HH:mm:ss zzz yyyy",
                Locale.ENGLISH
            )
        for (i in 0..periode.periodeDagen!!.size - 1) {
            Log.d(
                "dagdatumparsing",
                "Volledig van Periodeagen:" + periode.periodeDagen!![i].toString()
            )

            Log.d(
                "dagdatumparsing", "Element 1 van Periodeagen:" + periode.periodeDagen!![i].dagDatum
            )

        }
        val tussenLijst = periode.periodeDagen
        Log.d("dagdatumparsing", "Size :" + tussenLijst!!.size)

        for (j in 0..tussenLijst.size - 1) {
            Log.d("dagdatumparsing", tussenLijst.get(j).dagDatum!!)
            var d1: Date? = null
            try {
                d1 = sdf3.parse(
                    tussenLijst[j].dagDatum!!
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

}





