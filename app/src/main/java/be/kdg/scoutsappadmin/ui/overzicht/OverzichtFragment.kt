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
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        overzichtViewModel =
            ViewModelProviders.of(this).get(OverzichtViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_overzicht, container, false)
        val periode = activity!!.intent.getParcelableExtra<Periode>(LoginActivity.PERIODE)
        Log.d("overzicht", periode.toString())

        spinnerGegevenDoor = root.findViewById(R.id.frgOverzicht_spinner_GegevenDoor)
        spinnerDag = root.findViewById(R.id.frgOverzicht_spinner_Dag)
        spinnerNaam = root.findViewById(R.id.frgOverzicht_spinner_Naam)

        rvStreepkes = root.findViewById(R.id.frgOverzicht_rv_Streepkes)

        val rvAdapter = GroupAdapter<ViewHolder>()
        rvStreepkes.adapter = rvAdapter
        fetchAllPeriodes()
        for (i in 0 until periode.periodePersonen!!.size) {
            Log.d("size", periode.periodePersonen!![i].toString())
            rvAdapter.add(
                consumptieItem(
                    periode.periodePersonen!![i].persoonNaam!!,

                    periode.periodePersonen!![i].persoonConsumpties!!.size
                )
            )
        }




        return root
    }

    private fun fetchAllPeriodes() {
        val ref = FirebaseDatabase.getInstance().getReference("/periodes")
        val list: MutableList<Periode?> = ArrayList<Periode?>()
        val namenList: MutableList<String> = ArrayList<String>()


        ref.addChildEventListener(object : ChildEventListener {

            @SuppressLint("UseRequireInsteadOfGet")
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val periodeFb = p0.getValue(FireBasePeriode::class.java)
                val dagenList = periodeFb!!.periodeDagen.values.toList()
                val personenList = periodeFb.periodePersonen.values.toList()
                val periodeFbNaam = p0.getValue(FireBasePeriode::class.java)!!.periodeNaam

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
                    Log.d("getOverzicht","persoon " + p)
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
                    Log.d("getOverzicht","periode " + periode)

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

class consumptieItem(val naam: String, val aantal: Int) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.streepke_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.streepke_row_txtNaam.text = naam
        viewHolder.itemView.streepke_row_txtAantal.text = aantal.toString()
    }

}
