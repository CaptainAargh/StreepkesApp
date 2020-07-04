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
import androidx.core.graphics.drawable.toDrawable
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
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.streepke_row.view.*
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList
import kotlin.time.ExperimentalTime

class OverzichtFragment : Fragment() {

    private lateinit var overzichtViewModel: OverzichtViewModel
    private lateinit var spinnerGegevenDoor: Spinner
    private lateinit var spinnerNaam: Spinner
    private lateinit var spinnerDag: Spinner

    private lateinit var rvStreepkes: RecyclerView

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onResume() {
        val periode2 = activity!!.intent.getParcelableExtra<Periode>(LoginActivity.PERIODE)
        fetchAllStreepkes(periode2.key, 0, "totaal")
        super.onResume()
    }

    @ExperimentalTime
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
        // val rvAdapter = GroupAdapter<ViewHolder>()
        // rvStreepkes.adapter = rvAdapter
        fetchAllStreepkes(periode.key, 0, "totaal")
        val rvAdapter = GroupAdapter<ViewHolder>()
        rvStreepkes.adapter = rvAdapter
        val labelsFilter =
            listOf<String>(
                "Totaal aantal streepjes",
                "Vandaag",
                "Deze Week",
                "Deze maand",
                "Uureke geleden"
            )

        val spinnerArrayAdapter = ArrayAdapter<String>(
            this@OverzichtFragment.context!!,
            R.layout.spinner_overzicht,
            labelsFilter
        )
        spinnerSorteer.adapter = spinnerArrayAdapter
        spinnerSorteer.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                fun getDaysAgo(daysAgo: Int = 0, hoursAgo: Int = 0): Date {
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
                    return calendar.time
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (spinnerSorteer.selectedItem.toString().equals("Vandaag")) {
                        val duration = Toast.LENGTH_SHORT
                        val toast = Toast.makeText(
                            activity!!.applicationContext,
                            "filter vandaag geselecteerd",
                            duration
                        )
                        toast.show()
                        rvAdapter.clear()
                        val dateMill = System.currentTimeMillis().minus(86400000)
                        fetchAllStreepkes(periode.key, dateMill, "totaal")
                        //  rvAdapter.addAll(getgesorteerd(periode, 1583535600000).reversed())
                        rvAdapter.notifyDataSetChanged()
                    } else if (spinnerSorteer.selectedItem.toString().equals("Deze Week")) {
                        val duration = Toast.LENGTH_SHORT
                        val toast = Toast.makeText(
                            activity!!.applicationContext,
                            "filter nu geselecteerd",
                            duration
                        )
                        toast.show()
                        rvAdapter.clear()
                        // rvAdapter.addAll(getgesorteerd(periode, 1593739738900).reversed())
                        val dateMill = System.currentTimeMillis().minus(604800000)
                        fetchAllStreepkes(periode.key, dateMill, "totaal")

                        //  rvAdapter.addAll(getgesorteerd(periode, 1593777201000, "Week").reversed())

                        rvAdapter.notifyDataSetChanged()
                    } else if (spinnerSorteer.selectedItem.toString().equals("Deze maand")) {
                        val duration = Toast.LENGTH_SHORT
                        val toast = Toast.makeText(
                            activity!!.applicationContext,
                            "filter maand geselecteerd",
                            duration
                        )

                        toast.show()
                        rvAdapter.clear()
                        fetchAllStreepkes(periode.key, 2628002880, "totaal")
                        rvAdapter.notifyDataSetChanged()
                    } else if (spinnerSorteer.selectedItem.toString().equals("Uureke geleden")) {
                        val duration = Toast.LENGTH_SHORT
                        val toast = Toast.makeText(
                            activity!!.applicationContext,
                            "filter uureke geselecteerd",
                            duration
                        )
                        var count = 0
                        periode.periodePersonen!!.forEach {
                            count += it.persoonConsumpties!!.size
                        }
                        toast.show()
                        rvAdapter.clear()
                        fetchAllStreepkes(periode.key, 0, "Uureke geleden")
                        rvAdapter.add(consumptieItem(" a moeder ", count, 1))
                        rvAdapter.notifyDataSetChanged()
                    } else {
                        rvAdapter.clear()
                        //rvAdapter.addAll(getgesorteerd(periode, 0, "totaal").reversed())
                        fetchAllStreepkes(periode.key, 0, "totaal")
                        rvAdapter.notifyDataSetChanged()

                    }


                }
            }
        return root
    }

    fun getgesorteerd(periode: Periode, tijdfilter: Long, filter: String): List<consumptieItem> {
        var gesorteerd: MutableList<consumptieItem> = ArrayList<consumptieItem>()
        val consumptieLijst: MutableList<Consumptie> = ArrayList<Consumptie>()

        @SuppressLint("NewApi")
        @RequiresApi(Build.VERSION_CODES.O)
        fun checkRanking(p: Persoon): Int {
            val ts = Math.round((Date().time / 1000).toDouble())

            var rank = 0
            val now: Instant = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Instant.now()
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val yesterday: Instant = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                now.minus(1, ChronoUnit.DAYS)
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val yesterday48: Instant = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                now.minus(2, ChronoUnit.DAYS)
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            val top3: MutableList<Pair<String, Int>> = ArrayList<Pair<String, Int>>()
            periode.periodePersonen!!.forEach {
                var count = 0
                it.persoonConsumpties!!.forEach {
                    if (it.timeStamp / 1000 <= yesterday.toEpochMilli() / 1000 && yesterday48.toEpochMilli() / 1000 >= it.timeStamp / 1000) {
                        count++
                    }
                }
                val persoonNaam = it.persoonNaam.toString()
                val pair = Pair(persoonNaam, count)
                top3.add(pair)
            }
            top3.sortBy {
                it.second
            }
            top3.reverse()

            var count = 0
            top3.subList(0, 3)
            top3.forEach {
                count++
                if (it.first.equals(p.persoonNaam)) {
                    rank = count
                }
            }
            return rank
        }
        periode.periodePersonen!!.forEach {
            var count = 0
            for (i in 0 until it.persoonConsumpties!!.size) {
                // val timeStamppc = it.persoonConsumpties!![i].timeStamp
                // if ((timeStamppc.compareTo(tijdfilter)) > 0) {
                if ((it.persoonConsumpties!![i].timeStamp / 1000) >= tijdfilter / 1000) {
                    Log.d(

                        "ciitems",
                        "(it.persoonConsumpties!![i].timeStamp/1000).toULong()   =     \n             "
                                + (it.persoonConsumpties!![i].timeStamp / 1000).toULong()
                                + "\n (tijdfilter/1000).toULong() =    \n" +
                                "             "
                                + (tijdfilter / 1000).toULong()
                    )
                    count++
                }
            }
            if (count > 0 && filter != "totaal") {
                gesorteerd.add(
                    consumptieItem(
                        it.persoonNaam!!,
                        count
                        , checkRanking(it)
                    )
                )
            } else if (filter == "totaal") {
                gesorteerd.add(
                    consumptieItem(
                        it.persoonNaam!!,
                        count
                        , checkRanking(it)
                    )
                )
            }
        }
        gesorteerd.sortBy { ci ->
            ci.aantal
        }
        gesorteerd.distinctBy {
            it.naam
        }
        Log.d("ciitems", "totale lijst :======================\n" + gesorteerd.toString())
        gesorteerd.forEach {
            "item \n" + it.toString() +
                    "Naam \n" + it.naam +
                    "aantal \n" + it.aantal.toString()
        }
        Log.d("ciitems", "totale lijst :======================\n" + gesorteerd.toString())

        return gesorteerd.toList()
    }


    private fun fetchAllStreepkes(key: String, tijdFilter: Long, filter: String): Periode {
        val ref = FirebaseDatabase.getInstance().getReference("/periodes/${key}")
        val list: MutableList<Periode?> = ArrayList<Periode?>()
        val namenList: MutableList<String> = ArrayList<String>()

        var periodeReturn = Periode()

        ref.addValueEventListener(object : ValueEventListener, ChildEventListener {
            fun getPeriode(periodeFb: FireBasePeriode) {
                val dagenList = periodeFb!!.periodeDagen.values.toList()
                val personenList = periodeFb.periodePersonen.values.toList()
                var periodeFilterTijd = ArrayList<consumptieItem>()

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
                    val returnPeriode = getgesorteerd(periode, tijdFilter, filter)
                    val rvAdapter = GroupAdapter<ViewHolder>()
                    rvStreepkes.adapter = rvAdapter
                    var count = 0
                    periode.periodePersonen!!.forEach {
                        count += it.persoonConsumpties!!.size
                    }
                    if (filter == "Uureke geleden") {
                        rvAdapter.add(consumptieItem(" a moeder ", count, 1))
                        rvAdapter.notifyDataSetChanged()

                    } else {
                        rvAdapter.addAll(returnPeriode.reversed())
                        rvAdapter.notifyDataSetChanged()
                        list.add(periode)
                        Log.d("getOverzicht", "periode " + periode)
                        list.forEach { e ->
                            Log.d("getOverzicht2", "periode toegeveogd en returned " + e)
                        }
                    }


                }
            }

            @SuppressLint("UseRequireInsteadOfGet")
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                val periodeFb = p0.getValue(FireBasePeriode::class.java)
                periodeFb?.let { getPeriode(it) }
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val periodeFb = p0.getValue(FireBasePeriode::class.java)
                periodeFb?.let { getPeriode(it) }
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val periodeFb = p0.getValue(FireBasePeriode::class.java)
                periodeFb?.let { getPeriode(it) }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                val periodeFb = p0.getValue(FireBasePeriode::class.java)
                periodeFb?.let { getPeriode(it) }
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(p0: DataSnapshot) {
                val periodeFb = p0.getValue(FireBasePeriode::class.java)
                periodeFb?.let { getPeriode(it) }
            }

        })
        return periodeReturn
    }

}

fun show(view: View) {
    view.visibility = View.INVISIBLE
}

class consumptieItem(
    val naam: String, val aantal: Int, val rank: Int
) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.streepke_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.streepke_row_ivMedaille.bringToFront()
        if (rank == 1) {
            viewHolder.itemView.streepke_row_ivMedaille.setBackgroundResource(R.drawable.rank1_v2)
        } else if (rank == 2) {
            viewHolder.itemView.streepke_row_ivMedaille.setBackgroundResource(R.drawable.rank2_v2)
        } else if (rank == 3) {
            viewHolder.itemView.streepke_row_ivMedaille.setBackgroundResource(R.drawable.rank3_v2)
        } else {
            viewHolder.itemView.streepke_row_ivMedaille.setBackgroundResource(0)
        }
        viewHolder.itemView.streepke_row_txtNr.text = (position + 1).toString() + ". "
        viewHolder.itemView.streepke_row_txtNaam.text = naam
        viewHolder.itemView.streepke_row_txtAantal.text = aantal.toString()

    }
}
