package be.kdg.scoutsappadmin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import be.kdg.scoutsappadmin.fireBaseModels.FireBasePeriode
import be.kdg.scoutsappadmin.fireBaseModels.FireBasePeriode_Persoon_Consumpties
import be.kdg.scoutsappadmin.model.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase


class LoginActivity : AppCompatActivity() {
    private lateinit var spinnerPeriode: Spinner
    private lateinit var btnLogin: Button

    private lateinit var txtNaam: TextView
    private lateinit var txtPass: TextView
    private lateinit var cbLogin: CheckBox

    companion object {
        const val PERIODE = "periode"
        const val GEBRUIKER = "gebruiker"
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        val sharedPreferences = getSharedPreferences("LOGIN_INFO",Context.MODE_PRIVATE)


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        spinnerPeriode = findViewById(R.id.login_spinner_periode)
        btnLogin = findViewById(R.id.login_btn_login)
        cbLogin = findViewById(R.id.cbSave)

        txtNaam = findViewById(R.id.login_txt_name)
        txtPass = findViewById(R.id.login_txt_pass)
        fetchAllPeriodes()


        cbLogin.setOnCheckedChangeListener{ compoundButton: CompoundButton, b: Boolean ->
            if (cbLogin.isChecked) {

            } else {

            }
        }

        btnLogin.setOnClickListener {
            if (cbLogin.isChecked) {

            }
            val periodeSelected = getSelectedPeriod()
            checkLogin(periodeSelected)

        }


    }


    private fun getSelectedPeriod(): Periode {
        val ref = FirebaseDatabase.getInstance().getReference("/periodes")
        val selectedPeriode = Periode()
        ref.addChildEventListener(object : ChildEventListener {

            @SuppressLint("UseRequireInsteadOfGet")
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val periodeFbNaam = p0.getValue(FireBasePeriode::class.java)
                val consumpties = ArrayList<FireBasePeriode_Persoon_Consumpties>()
                periodeFbNaam!!.periodePersonen.toList().forEach { e ->
                    e.second.persoonConsumpties.toList().forEach { pc ->
                        consumpties.add(pc.second)
                        Log.d("consumptes", pc.second.consumptieGegevenDoor)

                    }
                }

                Log.d("consumptes", consumpties.toString())
                consumpties.forEach { c -> Log.d("consumptes", "c" + c.toString()) }

                if (periodeFbNaam!!.periodeNaam.equals(spinnerPeriode.selectedItem.toString())) {
                    val dagenList = periodeFbNaam.periodeDagen.values.toList()
                    val personenList = periodeFbNaam.periodePersonen.values.toList()

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
                        Log.d("Login", "Consumpties " + consumpties.toString())

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
                    val periode = Periode(
                        periodeFbNaam.key,
                        periodeFbNaam.periodeNaam,
                        periodeDagenList,
                        periodePersonenList
                    )

                    checkLogin(periode)
                    Log.d("Login", "Namen van firebasePeriode " + periode)

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
        return selectedPeriode

    }

    @SuppressLint("ResourceType", "CommitPrefEdits")
    private fun checkLogin(p: Periode) {
        val naamLogin = txtNaam.text.toString()
        val naamPass = txtPass.text.toString()

        if (naamLogin.equals("admin") && naamPass.equals("admin")) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            for (i in 0 until p.periodePersonen!!.size) {
                if (p.periodePersonen!![i].persoonNaam.equals(naamLogin) && p.periodePersonen!![i].persoonPass.equals(
                        naamPass
                    )
                ) {
                    if (p.periodePersonen!![i].persoonRol.equals(Rol.GROEPSLEIDING)) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    val intent = Intent(this, StreepkeActivity::class.java)
                    intent.putExtra(PERIODE, p)
                    intent.putExtra(GEBRUIKER, p.periodePersonen!![i])
                    startActivity(intent)
                }
            }
        }
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

                    val spinnerArrayAdapter = ArrayAdapter<String>(
                        this@LoginActivity,
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



