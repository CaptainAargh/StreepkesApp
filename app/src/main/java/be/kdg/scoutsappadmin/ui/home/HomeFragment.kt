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
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.kdg.scoutsappadmin.LoginActivity
import be.kdg.scoutsappadmin.R
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
import kotlinx.android.synthetic.main.roundedstreepke_row_item.*
import kotlinx.android.synthetic.main.roundedstreepke_row_item.view.*


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

        recyclerView.setLayoutManager(LinearLayoutManager(activity!!.applicationContext));
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

        val personenView: MutableList<View> = ArrayList<View>()
        val personenItems: MutableList<persoonItem> = ArrayList<persoonItem>()
        var added = false

        var selectedPosition: Int = -1

        personenAdapter.setOnItemClickListener { item, view ->
            selectedPosition = item.getPosition(item)
            val itemPosition = recyclerView.getChildAdapterPosition(view)

            val i = item as persoonItem
            val persoon = i.persoon
            view.iv_row_add.setOnClickListener {
                geselecteerdePersonen.add(persoon)
                val count = geselecteerdePersonen.filter {
                    p -> p.persoonNaam == persoon.persoonNaam
                }.count()
                item.count = count
                personenAdapter.notifyItemChanged(itemPosition)
                return@setOnClickListener

            }
            view.iv_row_del.setOnClickListener {
                geselecteerdePersonen.remove(persoon)
                val count = geselecteerdePersonen.filter {
                        p -> p.persoonNaam == persoon.persoonNaam
                }.count()
                item.count = count
                personenAdapter.notifyItemChanged(itemPosition)
                return@setOnClickListener

            }
/*  personenView.add(view)
            personenItems.add(item as persoonItem)
            val x = view
            val y = item
            val persoonItem = item as persoonItem
            if (geselecteerdePersonen.contains(persoonItem.persoon))
                Log.d(
                    "id",
                    "tag van items " + item.id + "Naam : " + persoonItem.persoon.persoonNaam + "tag van view " + view.getTag() + "View id"
                )
//           view.persoon_row_naam.setBackgroundColor(Color.parseColor("#ff99cc00"))
//           view.persoon_row_iv.bringToFront()
*/
            val itemid = item.id

            val itemPos = personenAdapter.getAdapterPosition(item)


            //allePersonen.get(itemPosition + 1) == item.persoon
            Log.d(
                "personenLijst",
                "   allePersonen.get(itemPosition + 1)=" + allePersonen.get(itemPosition).persoonNaam.toString() + "\n" + item.persoon.persoonNaam.toString()
            )

            if (itemPos == itemPosition) {
                if (geselecteerdePersonen.contains(item.persoon) && view.txt_row_count.text.toString().toInt() == 0
                ) {

                    geselecteerdePersonen.remove(item.persoon)
                    item.added = false
                    // view.roundedImageView.setBackgroundResource(R.drawable.rounded_txt_naam)
                    Log.d(
                        "personenLijst",
                        "postition vanuit onlcik adapter=" + selectedPosition.toString()

                    )
                    personenAdapter.notifyItemChanged(itemPosition)
                    return@setOnItemClickListener
                } else {
                    geselecteerdePersonen.add(persoon)
                    val count = geselecteerdePersonen.filter {
                            p -> p.persoonNaam == persoon.persoonNaam
                    }.count()
                    item.count = count

                            // view.roundedImageView.setBackgroundResource(R.drawable.bakgroundbeer)
                    item.added = true
                    personenAdapter.notifyItemChanged(itemPosition)
                    return@setOnItemClickListener
                }
            }
        }

        ivButton.setOnClickListener {
            var personenString: String =
                "Dubbelcheckt da nog is effekes \n"

            var count = 0
            var naam = ""
            val formatted = geselecteerdePersonen.groupingBy { it.persoonNaam }.eachCount().toString()
            print(formatted)
            for (i in 0 until geselecteerdePersonen.size) {
                if (geselecteerdePersonen[i].persoonNaam.equals(naam)) {
                    count++
                } else {
                    naam = geselecteerdePersonen[i].persoonNaam.toString()
                }
                personenString += "\n " + geselecteerdePersonen[i].persoonNaam + " : " + count
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
                alertDialog.setPositiveButton(
                    "Joat kzen zeker",
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            val c = Consumptie(
                                refPeriode_Personen_StreepkesKey, persoon.persoonNaam, persoon.key,
                                System.currentTimeMillis()
                            )
                            refPeriode_Personen_Streepkes.setValue(c)
                            Log.d("addPeriodePersoon", "Jeej succes" + c.toString())
                            getFragmentManager()!!.beginTransaction().detach(root.findFragment())
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
    var count : Int
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


        //viewHolder.setIsRecyclable(false)
        //viewHolder.setIsRecyclable(false)
        cardViewList.add(viewHolder.itemView.streepke_item_row.streepke_card); //add all the cards to this list
/*        viewHolder.itemView.iv_row_add.setOnClickListener {
            val oldVal = viewHolder.itemView.txt_row_count.text.toString().toInt()
            viewHolder.itemView.txt_row_count.text = (oldVal+1).toString()
            Log.d("PersonenLijst", "\n ====================== \n 1 streepje toegevoegd")
        }
        viewHolder.itemView.iv_row_add.setOnClickListener {
            val oldVal = viewHolder.itemView.txt_row_count.text.toString().toInt()
            viewHolder.itemView.txt_row_count.text = (oldVal-1).toString()
            Log.d("PersonenLijst", "\n ====================== \n 1 streepje verwijderd")
        }*/
/*        if (persoon.persoonPass.isNullOrEmpty()) {
            showHide(viewHolder.itemView.streepke_card)
        }*/

        if (added) {
            viewHolder.itemView.roundedImageView.setBackgroundResource(R.drawable.bakgroundbeer)
            //  viewHolder.itemView.iv_row_del.bringToFront()
            viewHolder.itemView.iv_row_add.setBackgroundResource(R.drawable.addstreepke)
            viewHolder.itemView.iv_row_del.setBackgroundResource(R.drawable.delstreepke)
            viewHolder.itemView.streepke_item_row.streepke_card.txt_row_count.text = count.toString()

            Log.d("PersonenLijst", "\n ====================== \n bg veranderd naar BIER")
        } else if (!added) {
            viewHolder.itemView.txt_row_count.text = ""
            viewHolder.itemView.iv_row_add.setBackgroundResource(0)
            viewHolder.itemView.iv_row_del.setBackgroundResource(0)
            viewHolder.itemView.streepke_item_row.streepke_card.txt_row_count.text = ""
            viewHolder.itemView.roundedImageView.setBackgroundResource(R.drawable.rounded_txt_naam)
            Log.d("PersonenLijst", "\n ====================== \n bg veranderd naar DEFAULT")
        }
/*
            if (fragment.selected_item == position) {
                viewHolder.itemView.roundedImageView.setBackgroundResource(R.drawable.bakgroundbeer)
                Log.d("personenLijst", "postition vanuit bind=" + position.toString())
                showHide(view.iv_row_del)
                showHide(view.iv_row_add)
                showHide(view.txt_row_count)
            } else {
                viewHolder.itemView.roundedImageView.setBackgroundResource(R.drawable.rounded_txt_naam)
                showHide(view.iv_row_del)
                showHide(view.iv_row_add)
                showHide(view.txt_row_count)
            }

    if (cardViewList.contains(viewHolder.itemView.streepke_card)) {
            cardViewList.add(viewHolder.itemView.streepke_card);
        } else { viewHolder.itemView.streepke_item_row.streepke_card.txt_row_naam.text = persoon.persoonNaam
            }

       viewHolder.itemView.streepke_card.setOnClickListener(View.OnClickListener {
                          //All card color is set to colorDefault
                          if (cardViewList.contains(it.streepke_card)) {
                              cardViewList.remove(it.streepke_card)
                              selectedCardViewList.add(it.streepke_card)
                              it.roundedImageView.setBackgroundResource(R.drawable.bakgroundbeer)

                          } else {
                              cardViewList.add(it.streepke_card)
                              it.roundedImageView.setBackgroundResource(R.drawable.bakgroundbeer)

                          }
                          for (cardView in cardViewList) {
                          }
                          for (cardView in selectedCardViewList) {
                          }
                          //The selected card is set to colorSelected
                          //viewHolder.itemView.roundedImageView.setBackgroundResource(R.drawable.bakgroundbeer)

                      }
       )*/


    }

}


