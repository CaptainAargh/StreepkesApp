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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.kdg.scoutsappadmin.LoginActivity
import be.kdg.scoutsappadmin.R
import be.kdg.scoutsappadmin.StreepkeActivity
import be.kdg.scoutsappadmin.fireBaseModels.FireBasePeriode_Persoon
import be.kdg.scoutsappadmin.fireBaseModels.FireBasePeriode_Persoon_Consumpties
import be.kdg.scoutsappadmin.fireBaseModels.periodeItem
import be.kdg.scoutsappadmin.model.Consumptie
import be.kdg.scoutsappadmin.model.Periode
import be.kdg.scoutsappadmin.model.Persoon
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.nav_header_streepke.*
import kotlinx.android.synthetic.main.persoon_row.view.*
import org.w3c.dom.Text


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


        val periodeDagenNaam: MutableList<String> = ArrayList<String>()
        val addStreepke = root.findViewById<Button>(R.id.frgStreepke_btn_addStreepjes)

        periode.periodeDagen!!.forEach { dag ->
            periodeDagenNaam.add(dag.dagDatum!!.substring(0, 10))
        }
        val personenAdapter = GroupAdapter<ViewHolder>()





        val firebasePersonen: MutableList<persoonItem> = ArrayList<persoonItem>()

        for (i in 0 until periode.periodePersonen!!.size) {
            firebasePersonen.add(persoonItem(periode.periodePersonen!![i]))
        }

        firebasePersonen.sortBy {
            fp -> fp.persoon.persoonNaam
        }
        personenAdapter.addAll(firebasePersonen)
        var recyclerView = root.findViewById<RecyclerView>(R.id.fragment_home_rvPersonenMain);
        var ivButton = root.findViewById<ImageView>(R.id.fragment_home_ivStreepke);

        recyclerView.setLayoutManager(LinearLayoutManager(activity!!.applicationContext));
        recyclerView.adapter = personenAdapter

        val geselecteerdePersonen: MutableList<Persoon> = ArrayList<Persoon>()

        val geselecteerdeViews: MutableList<TextView> = ArrayList<TextView>()
        val geselecteerdeImages: MutableList<ImageView> = ArrayList<ImageView>()

        personenAdapter.setOnItemClickListener { item, view ->
         //   view.persoon_row_naam.setBackgroundColor(Color.parseColor("#ff99cc00"))
            view.persoon_row_iv.bringToFront()
            view.persoon_row_iv.visibility = if (view.persoon_row_iv.visibility == View.VISIBLE){
                View.INVISIBLE
            } else{
                View.VISIBLE
            }
            if (view.persoon_row_iv.visibility == View.VISIBLE){
                view.persoon_row_naam.setBackgroundResource(R.drawable.bakgroundbeer)
                val persoonItem = item as persoonItem
                geselecteerdePersonen.add(persoonItem.persoon)
                var z = view.persoon_row_naam
            } else {
                view.persoon_row_naam.setBackgroundResource(R.drawable.rounded_txt_naam)
                val persoonItem = item as persoonItem
                geselecteerdePersonen.remove(persoonItem.persoon)


            }
        }
        ivButton.setOnClickListener {
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
                alertDialog.setNegativeButton("OK", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val c = Consumptie(
                            refPeriode_Personen_StreepkesKey, persoon.persoonNaam, persoon.key,
                            System.currentTimeMillis()
                        )
                        geselecteerdeViews.forEach {
                            v  -> v.setBackgroundResource(R.drawable.rounded_txt_naam)
                        }
                        geselecteerdeImages.forEach {
                            i -> i.visibility = View.INVISIBLE
                        }
                        refPeriode_Personen_Streepkes.setValue(c)
                        Log.d("addPeriodePersoon", "Jeej succes" + c.toString())
                        getFragmentManager()!!.beginTransaction().detach(root.findFragment()).attach(root.findFragment()).commit();

                    }

                })
                alertDialog.show()
            }
        }
        return root
    }
}


class persoonItem (
    val persoon:Persoon) : Item<ViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.persoon_row    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.persoon_row_naam.text = "\n      " + "   " + persoon.persoonNaam


    }

}
