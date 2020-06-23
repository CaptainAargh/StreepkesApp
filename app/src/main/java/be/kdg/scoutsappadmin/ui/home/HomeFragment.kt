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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
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

        for (i in 0 until periode.periodePersonen!!.size) {
            firebasePersonen.add(persoonItem(periode.periodePersonen!![i],this))
        }

        firebasePersonen.sortBy { fp ->
            fp.persoon.persoonNaam
        }
        personenAdapter.addAll(firebasePersonen)

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

            val i = item as persoonItem
            val persoon = i.persoon
            if (item.persoon.persoonNaam.equals(view. .txt_row_naam.text.toString())) {
                geselecteerdePersonen.remove(item.persoon)
                roundedImageView.setBackgroundResource(R.drawable.rounded_txt_naam)
                Log.d("personenLijst", "postition vanuit onlcik adapter=" +  selectedPosition.toString())
                return@setOnItemClickListener

            } else {
                geselecteerdePersonen.add(persoon)
                roundedImageView.setBackgroundResource(R.drawable.bakgroundbeer)
                Log.d("personenLijst", "postition vanuit onlcik adapter=" +  selectedPosition.toString())
                return@setOnItemClickListener
            }
        }
        personenAdapter.notifyItemChanged(selectedPosition)

        ivButton.setOnClickListener {
            var personenString: String =
                "Dubbelcheckt da nog is effekes \n"

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
    val fragment : HomeFragment
) : Item<ViewHolder>() {

    fun showHide(view: View) {
        view.visibility = if (view.visibility == View.VISIBLE) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    override fun getLayout(): Int {
        return R.layout.roundedstreepke_row_item
    }

    private val lastSelectedPosition = -1 // declare this variable


    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.streepke_item_row.streepke_card.txt_row_naam.text = persoon.persoonNaam


        //viewHolder.setIsRecyclable(false)
        //viewHolder.setIsRecyclable(false)
        fun showHide(view: View) {
            view.visibility = if (view.visibility == View.VISIBLE) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }
            cardViewList.add(viewHolder.itemView.streepke_item_row.streepke_card); //add all the cards to this list
        if(fragment.selected_item == position) {
            viewHolder.itemView.roundedImageView.setBackgroundResource(R.drawable.bakgroundbeer)
            Log.d("personenLijst", "postition vanuit bind=" +  position.toString())
            showHide(view.iv_row_del)
            showHide(view.iv_row_add)
           showHide(view.txt_row_count)
        } else{
            viewHolder.itemView.roundedImageView.setBackgroundResource(R.drawable.rounded_txt_naam)
          showHide(view.iv_row_del)
           showHide(view.iv_row_add)
          showHide(view.txt_row_count)
        }

/*     if (cardViewList.contains(viewHolder.itemView.streepke_card)) {
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
}


