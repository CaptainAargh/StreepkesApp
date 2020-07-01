package be.kdg.scoutsappadmin.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.kdg.scoutsappadmin.LoginActivity
import be.kdg.scoutsappadmin.R
import be.kdg.scoutsappadmin.R.drawable
import be.kdg.scoutsappadmin.model.Consumptie
import be.kdg.scoutsappadmin.model.Periode
import be.kdg.scoutsappadmin.model.Persoon
import be.kdg.scoutsappadmin.model.Rol
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.persoon_row.view.*
import kotlinx.android.synthetic.main.roundedstreepke_row_item.*
import kotlinx.android.synthetic.main.roundedstreepke_row_item.view.*
import kotlinx.android.synthetic.main.roundedstreepke_row_item.view.iv_row_del


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HomeFragment : Fragment() {
    var selected_item = 0

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
        val allePersonen: MutableList<Persoon> = ArrayList<Persoon>()
        periode.periodePersonen!!.forEach { p ->
            allePersonen.add(p)
        }
        allePersonen.sortBy { p ->
            p.persoonNaam
        }

        val firebasePersonen: MutableList<persoonItem> = ArrayList<persoonItem>()
        val emptyPersonen: MutableList<persoonItem> = ArrayList<persoonItem>()

        for (i in 0 until periode.periodePersonen!!.size) {
            firebasePersonen.add(
                persoonItem(
                    periode.periodePersonen!![i],
                    this,
                    false,
                    false,
                    false,
                    0
                )
            )
        }

        firebasePersonen.sortBy { fp ->
            fp.persoon.persoonNaam
        }
        personenAdapter.addAll(firebasePersonen)
        //  personenAdapter.addAll(emptyPersonen)

        var recyclerView = root.findViewById<RecyclerView>(R.id.fragment_home_rvPersonenMain);
        var ivButton = root.findViewById<ImageView>(R.id.fragment_home_ivStreepke);

        recyclerView.layoutManager = LinearLayoutManager(activity!!.applicationContext);
//        val streepAdapter = streepkesAdapter(recyclerView, this.context!!,allePersonen)
        recyclerView.adapter = personenAdapter

        val geselecteerdePersonen: MutableList<Persoon> = ArrayList<Persoon>()

        val geselecteerdeViews: MutableList<TextView> = ArrayList<TextView>()
        val geselecteerdeImages: MutableList<ImageView> = ArrayList<ImageView>()

        fun showHide(view: View) {
            view.visibility = if (view.visibility == View.VISIBLE) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }
        }

        var itemsGeselecteerd: MutableList<persoonItem> = ArrayList<persoonItem>()

        personenAdapter.setOnItemClickListener { item, view ->
            val i = item as persoonItem
            val itemPosition = recyclerView.getChildAdapterPosition(view)

            /*    if (view.iv_row_add == item) {
                    geselecteerdePersonen.add(persoon)
                    i.addStreepke = true
                    Log.d("streepjeswijzigen", "er is geklikt op iv_row_add")
                    personenAdapter.notifyItemChanged(itemPosition)

                }
                if (view.iv_row_del == item) {
                    geselecteerdePersonen.add(persoon)
                    i.delStreepke = true
                    Log.d("streepjeswijzigen", "er is geklikt op iv_row_del")
                    personenAdapter.notifyItemChanged(itemPosition)

                }*/
            //selectedPosition = item.getPosition(item)
            val addStreepke = false
            val delStreepke = false
            val persoon = i.persoon
            val itemPos = personenAdapter.getAdapterPosition(item)
            if (itemPos == itemPosition) {
/*                if (view.txt_row_count.text == "") {
                    view.txt_row_count.text = 0.toString()
                }
                */

                if (geselecteerdePersonen.contains(item.persoon)
                // && view.txt_row_count.text.toString().toInt() == 0
                ) {
                    geselecteerdePersonen.remove(item.persoon)
                    item.added = false
                    item.count = 0
                    personenAdapter.notifyItemChanged(itemPosition)

                    //     return@setOnItemClickListener
                } else {
                    itemsGeselecteerd.add(i)
                    geselecteerdePersonen.add(persoon)
                    val count = geselecteerdePersonen.filter { p ->
                        p.persoonNaam == persoon.persoonNaam
                    }.count()
                    // item.count = count
                    // view.card_row_background.setBackgroundResource(R.drawable.bakgroundbeer)
                    item.added = true
                    personenAdapter.notifyItemChanged(itemPosition)

                    //   return@setOnItemClickListener
                }
            }
        }

        ivButton.setOnClickListener {
            var personenString =
                "Dubbelcheckt da nog is effekes \n"

            var count = 0
            var persoonNamen = ""
            val gesorteerd =
                itemsGeselecteerd
                    .groupBy { it.persoon.persoonNaam }
                    .forEach {
                        personenString
                    }
            itemsGeselecteerd.forEach {
                personenString += "\n " + it.persoon.persoonNaam + " : " + it.count
            }
/*            val formatted =
                geselecteerdePersonen.groupingBy { it.persoonNaam }.eachCount().toString()
            print(formatted)
            for (i in 0 until geselecteerdePersonen.size) {
                if (geselecteerdePersonen[i].persoonNaam.equals(persoonNamen)) {
                    count++
                } else {
                    persoonNamen = geselecteerdePersonen[i].persoonNaam.toString()
                }
                personenString += "\n " + geselecteerdePersonen[i].persoonNaam + " : " + count
            }*/
            for (i in 0 until itemsGeselecteerd.size) {
                val alertDialog =
                    AlertDialog.Builder(root.context)
                alertDialog.setTitle("Strepke dabei")
                Log.d("personenString", "String personen " + personenString)
                alertDialog.setMessage(personenString)
                alertDialog.setPositiveButton(
                    "Joat kzen zeker",
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            for (j in 0 until itemsGeselecteerd[i].count) {
                                val refPeriode_Personen_Streepkes =
                                    FirebaseDatabase.getInstance().reference.child("periodes/${periode.key}/periodePersonen/${itemsGeselecteerd[i].persoon.key}/persoonConsumpties")
                                        .push()
                                val refPeriode_Personen_StreepkesKey =
                                    refPeriode_Personen_Streepkes.key

                                if (refPeriode_Personen_StreepkesKey == null) {
                                    Log.d("periodes", "keys push niet aangekregen van periodes")
                                }
                                val c = Consumptie(
                                    refPeriode_Personen_StreepkesKey,
                                    persoon.persoonNaam,
                                    persoon.key,
                                    System.currentTimeMillis()
                                )
                                refPeriode_Personen_Streepkes.setValue(c)
                                Log.d("addPeriodePersoon", "Jeej succes" + c.toString())

                            }
                            // refPeriode_Personen_Streepkes.setValue(c)
                            getFragmentManager()!!.beginTransaction()
                                .detach(root.findFragment())
                                .attach(root.findFragment()).commit();

                        }

                    })
                alertDialog.show()
            }
        }
        return root
    }
}


var cardViewList: MutableList<CardView> = ArrayList()
var selectedCardViewList: MutableList<CardView> = ArrayList()

class persoonItem(
    val persoon: Persoon,
    val fragment: HomeFragment,
    var added: Boolean,
    var addStreepke: Boolean,
    var delStreepke: Boolean,
    var count: Int
) : Item<ViewHolder>() {

    fun showHide(view: View) {
        view.visibility = if (view.visibility == View.VISIBLE) {
            View.INVISIBLE
        } else if (view.visibility == View.INVISIBLE || view.visibility == View.GONE) {
            View.VISIBLE
        } else {
            return
        }
    }

    override fun getLayout(): Int {
        return R.layout.roundedstreepke_row_item
    }

    private val lastSelectedPosition = -1 // declare this variable

    override fun getPosition(item: Item<*>): Int {
        return super.getPosition(item)
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.streepke_item_row.streepke_card.txt_row_naam.text = persoon.persoonNaam
        if (added) {
            if (count == -1) {
                notifyChanged()
            }
            if (count == 0) {
                count = 1
            }
            //viewHolder.itemView.txt_row_count.text = count.toString()
            viewHolder.itemView.iv_row_add.setOnClickListener {
                count += 1
                viewHolder.itemView.txt_row_count.text = count.toString()
                addStreepke = false
                notifyChanged()

            }
            viewHolder.itemView.iv_row_del.setOnClickListener {
                this.count -= 1
                viewHolder.itemView.txt_row_count.text = count.toString()
                addStreepke = false
                notifyChanged()


            }

            viewHolder.itemView.card_row_background.setBackgroundResource(drawable.bakgroundbeer)
            viewHolder.itemView.iv_row_add.setBackgroundResource(drawable.addstreepke)
            viewHolder.itemView.iv_row_del.setBackgroundResource(drawable.delstreepke)
            viewHolder.itemView.txt_row_count.text = count.toString()
            //  viewHolder.itemView.iv_row_del.bringToFront()
            Log.d("PersonenLijst", "\n ====================== \n bg veranderd naar BIER")
        } else if (!added && count == 0) {
            viewHolder.itemView.iv_row_add.setBackgroundResource(0)
            viewHolder.itemView.iv_row_del.setBackgroundResource(0)
            viewHolder.itemView.card_row_background.setBackgroundResource(drawable.rounded_txt_naam)
            viewHolder.itemView.txt_row_count.text = ""
            Log.d("PersonenLijst", "\n ====================== \n bg veranderd naar DEFAULT")
        }
    }

}


