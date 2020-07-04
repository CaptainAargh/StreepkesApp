package be.kdg.scoutsappadmin.ui.log

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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.kdg.scoutsappadmin.R
import be.kdg.scoutsappadmin.fireBaseModels.FireBasePeriode
import be.kdg.scoutsappadmin.model.Consumptie
import be.kdg.scoutsappadmin.model.Dag
import be.kdg.scoutsappadmin.model.Periode
import be.kdg.scoutsappadmin.model.Persoon
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_dag.*
import kotlinx.android.synthetic.main.row_streepjes_log.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LogFragment : Fragment() {

    private lateinit var logViewModel: LogViewModel

    private lateinit var btnAddConsumptie: Button
    private lateinit var btndeleteConsumptie: Button

    private lateinit var spinnerNaamStreepjes: Spinner
    private lateinit var spinnerGegevenDoor: Spinner
    private lateinit var spinnerNaam: Spinner
    private lateinit var spinnerPeriode: Spinner
    private lateinit var spinnerDag: Spinner

    private lateinit var txtAantalStreepjes: TextView

    private var consumptieItems: MutableList<consumptieItem> = ArrayList<consumptieItem>()

    private lateinit var rvLog: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logViewModel =
            ViewModelProviders.of(this).get(LogViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_log, container, false)


        // val textView: TextView = root.findViewById(R.id.text_slideshow)
        logViewModel.text.observe(viewLifecycleOwner, Observer {
            //   textView.text = it
        })


        spinnerNaamStreepjes = root.findViewById(R.id.frgLog_Spinner_naamStreepjes)
        spinnerGegevenDoor = root.findViewById(R.id.frgLog_Spinner_gegevenDoor)
        spinnerNaam = root.findViewById(R.id.frgLog_Spinner_naam)
        spinnerPeriode = root.findViewById(R.id.frgLog_Spinner_Periode)
        spinnerDag = root.findViewById(R.id.frgLog_Spinner_dag)

        txtAantalStreepjes = root.findViewById(R.id.frgLog_txt_aantalStreepjes)

        rvLog = root.findViewById(R.id.frgOverzicht_rv_Streepkes)
        rvLog.layoutManager = LinearLayoutManager(this.context)
        fetchAllStreepkes()


        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN or ItemTouchHelper.UP
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                Toast.makeText(context, "on Move", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                Toast.makeText(context, "on Swiped ", Toast.LENGTH_SHORT).show()
                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.adapterPosition
                //arrayList.remove(position)
                // adapter.notifyDataSetChanged()
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(rvLog)
        return root

    }

    private fun getDateTime(s: String): String? {
        try {
            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val netDate = Date(s)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    private fun getAlleStreepjes(periode: Periode): MutableList<consumptieItem> {
        val lijstConsumpties: MutableList<consumptieItem> = ArrayList<consumptieItem>()
        val lijstConsumptiesUnParched: MutableList<consumptieMerged> = ArrayList<consumptieMerged>()
        for (i in 0 until periode.periodePersonen!!.size) {
            for (j in 0 until periode.periodePersonen!![i].persoonConsumpties!!.size) {
                if (periode.periodePersonen!![i].persoonConsumpties!!.isNotEmpty()) {
                    lijstConsumptiesUnParched.add(
                        consumptieMerged(
                            periode.periodePersonen!![i].persoonNaam!!,
                            periode.periodePersonen!![i].persoonConsumpties!![j].consumptieGegevenDoor!!,
                            periode.periodePersonen!![i].persoonConsumpties!![j].timeStamp
                        )
                    )
                }
            }
        }
        lijstConsumptiesUnParched.sortByDescending { it.op }
        lijstConsumptiesUnParched.forEach {
            val sdf = SimpleDateFormat("dd/MM/yy hh:mm:ss a")
            val netDate = Date(it.op)
            val date = sdf.format(netDate)
            lijstConsumpties.add(
                consumptieItem(
                    it.naam,
                    it.door,
                    date
                )
            )
        }

        /*
        periode.periodePersonen!!.forEach {
            lijstConsumptiesUnParched.add(it.persoonNaam,it.persoonConsumpties)
            }
        lijstConsumptiesUnParched.sortBy {
            it.timeStamp
        }
        lijstConsumptiesUnParched.forEach {
            val sdf = SimpleDateFormat("dd/MM/yy hh:mm:ss a")
            val netDate = Date(it.timeStamp)
            val date =sdf.format(netDate)
            lijstConsumpties.add(
                consumptieItem(
                    p.persoonNaam!!,
                    it.consumptieGegevenDoor!!,
                    date
                )
            )
        }

        val p = periode.periodePersonen!!.forEach { p ->
            p.persoonConsumpties!!.sortedByDescending {
                it.timeStamp
            }.forEach {
                val sdf = SimpleDateFormat("dd/MM/yy hh:mm:ss a")
                val netDate = Date(it.timeStamp)
                val date =sdf.format(netDate)
                lijstConsumpties.add(
                    consumptieItem(
                        p.persoonNaam!!,
                        it.consumptieGegevenDoor!!,
                        date
                    )
                )
            }
        }*/
        return lijstConsumpties
    }

    private fun fetchAllStreepkes() {
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


                    namenList.add(periode.periodeNaam!!)
                    periodeList.add(periode)

                    Log.d("getPeriodes", periode.toString())
                    Log.d("getPeriodes", periode.periodeDagen.toString())

                }
                val spinnerArrayAdapter = ArrayAdapter<String>(
                    this@LogFragment.context!!,
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
                                    val logAdapter = GroupAdapter<ViewHolder>()
                                    val streepjesLogitems = getAlleStreepjes(p)
                                    logAdapter.addAll(streepjesLogitems)
                                    rvLog.adapter = logAdapter


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
                            val spinnerArrayAdapterGegevenDoor = ArrayAdapter<String>(
                                this@LogFragment.context!!,
                                R.layout.support_simple_spinner_dropdown_item,
                                lijstPersonenString
                            )
                            spinnerGegevenDoor.adapter = spinnerArrayAdapterGegevenDoor
                            spinnerGegevenDoor.onItemSelectedListener =
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
                            for (x in 0 until lijstPersonen!!.size) {
                                lijstPersonenString.add(lijstPersonen[x].persoonNaam!!)
                            }
                            val spinnerArrayAdapterWijzigStreepjes = ArrayAdapter<String>(
                                this@LogFragment.context!!,
                                R.layout.support_simple_spinner_dropdown_item,
                                lijstPersonenString
                            )
                            spinnerNaamStreepjes.adapter = spinnerArrayAdapterWijzigStreepjes
                            spinnerNaamStreepjes.onItemSelectedListener =
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
                            val spinnerArrayAdapterPersonen = ArrayAdapter<String>(
                                this@LogFragment.context!!,
                                R.layout.support_simple_spinner_dropdown_item,
                                lijstPersonenString
                            )
                            spinnerNaam.adapter = spinnerArrayAdapterPersonen
                            spinnerNaam.onItemSelectedListener =
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
                                    "periodeDag To Stirng :" + " : " + periodeList[i].toString()
                                )

                                if (periodeList[i].periodeNaam.equals(
                                        spinnerPeriode.selectedItem.toString()
                                    )
                                ) {
                                    periodeDag = periodeList[i]
                                }
                            }
                            Log.d(
                                "datefucker2",
                                "periodeDag To Stirng :" + " : " + periodeDag.toString()

                            )
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
                                this@LogFragment.context!!,
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

                            if (spinnerPeriode.selectedItem == null) {
                                frgDag_btn_addPerson.setEnabled(false)
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


    }
}


class consumptieItem(
    val naam: String, val door: String, val op: String
) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.row_streepjes_log
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.row_log_txtNaam.text = naam
        viewHolder.itemView.row_log_txtDoor.text = door
        viewHolder.itemView.row_log_txtop.text = op

    }


}

class consumptieMerged(
    val naam: String, val door: String, val op: Long
) {

}