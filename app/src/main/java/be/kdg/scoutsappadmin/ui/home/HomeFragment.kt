package be.kdg.scoutsappadmin.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.marginTop
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
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.roundedstreepke_row_item.view.*
import pl.droidsonroids.gif.GifImageView


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HomeFragment : Fragment() {
    var selected_item = 0

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var gifLoader: GifImageView
    private lateinit var txtTop: ImageView
    private lateinit var txtBottom: ImageView

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
        gifLoader = root.findViewById<GifImageView>(R.id.home_streepke_gif)
        txtTop = root.findViewById<ImageView>(R.id.homefrg_txtTop)
        txtBottom = root.findViewById<ImageView>(R.id.homefrg_txtBottom)

        naam.text = persoon.persoonNaam
        rol.text = persoon.persoonRol.toString()
        val btnLogout: Button = header.findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            val intent = Intent(root.context, LoginActivity::class.java)
            val sharedPreferences =
                root.context.getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE)
            val prefsEditor = sharedPreferences.edit()
            prefsEditor.putString("MemPersoon", "")
            prefsEditor.putString("MemPeriode", "")
            sharedPreferences.edit().putBoolean("autoLoginCheck", false).apply()
            prefsEditor.apply()
            startActivity(intent)
        }
        val periodeDagenNaam: MutableList<String> = ArrayList<String>()



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
        val pItem = persoonItem(persoon, this, false, false, false, 0)
        personenAdapter.add(persoonItem(persoon, this, false, false, false, 0))
        val removeExist =
            firebasePersonen.filter { p -> p.persoon.persoonNaam != persoon.persoonNaam }
        firebasePersonen.remove(pItem)
        personenAdapter.addAll(removeExist)
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
                    itemsGeselecteerd.remove(i)
                    geselecteerdePersonen.remove(item.persoon)
                    item.added = false
                    item.count = 0
                    personenAdapter.notifyItemChanged(itemPosition)

                    //     return@setOnItemClickListener
                } else {
                    itemsGeselecteerd.add(i)
                    ivButton.isClickable = true
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
        itemsGeselecteerd

        ivButton.setOnClickListener {
            if (itemsGeselecteerd.size < 1) return@setOnClickListener

            var personenString =
                "Dubbelcheckt da nog is effeke \n"
            var nrStreepke = 0
            var persoonNamen = ""
            val gesorteerd =
                itemsGeselecteerd
                    .groupBy { it.persoon.persoonNaam }
                    .forEach {
                        personenString
                    }
            itemsGeselecteerd.distinct()
            itemsGeselecteerd.forEach {
                //personenString += "\n ${it.persoon.persoonNaam}            =>          " +" ${it.count}"
                val aantalSpaces = (30 - it.persoon.persoonNaam!!.length)
                personenString += "\n${it.persoon.persoonNaam!!.padEnd(aantalSpaces)}=>${it.count.toString()
                    .padStart(10).removeSurrounding(" ")}\n"
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
            fun setVisible(v: View) {
                v.visibility = View.VISIBLE
            }

            fun setInvisible(v: View) {
                v.visibility = View.GONE
            }

            var totaalStreepkes = 0
            itemsGeselecteerd.forEach {
                totaalStreepkes += it.count
            }
            val originalBm = BitmapFactory.decodeResource(
                context!!.resources,
                drawable.bakgroundbeer
            )
            Log.d("totaalStreepkes ", totaalStreepkes.toString())


            fun bitmapToDrawable(bitmap: Bitmap): BitmapDrawable {
                return BitmapDrawable(resources, bitmap)
            }


            var count = 0;

            val pref = context!!.getSharedPreferences("animatiePref", Context.MODE_PRIVATE)
            val alertDialog =
                AlertDialog.Builder(root.context, R.style.PopUpConfirmConsumpties)




            alertDialog.setTitle("Strepke dabei")
            Log.d("personenString", "String personen " + personenString)


            alertDialog.setMessage(personenString)
            alertDialog.setPositiveButton(
                "Joat kzen zeker",
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        if (pref.getBoolean("animatie", true) == true) {
                            for (i in 0 until itemsGeselecteerd.size) {
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
                                        .addOnSuccessListener {
                                            nrStreepke++
                                            count++
                                            var totaalStreepkes2 = itemsGeselecteerd.size.toString()
                                            val spanString2 = SpannableString(nrStreepke.toString())

/*                                        for (w in 0 until 10) {
                                            for (y in 0 until 10)
                                                originalBm.setPixel(
                                                    w,
                                                    y,
                                                    android.R.color.transparent
                                                )
                                        }*/
                                            val dst: Bitmap = Bitmap.createBitmap(
                                                originalBm,
                                                0,
                                                0,
                                                originalBm.width,
                                                originalBm.height / count
                                            )


                                            val omgezet = bitmapToDrawable(dst)
                                            val totaalPersoon = itemsGeselecteerd[i].count
                                            var streepkeVoor =
                                                itemsGeselecteerd[i].persoon.persoonNaam.toString()
                                            val nr = j + 1
                                            val toast = Toast.makeText(
                                                root.context.applicationContext,
                                                "Streepke $nr van $totaalPersoon \n \nvoor onze \n$streepkeVoor \n \n toegevoegd aan de lijst ",
                                                Toast.LENGTH_SHORT
                                            )
                                            if (totaalPersoon < 2) {
                                                toast.setText("Streepke voor onze \n$streepkeVoor \n \n toegevoegd aan de lijst ")
                                            }

                                            val group = toast.view as ViewGroup
                                            val messageTextView =
                                                group.getChildAt(0) as TextView
                                            messageTextView.setTextSize(30f)
                                            val bgLoader = R.drawable.beerloader.toDrawable()
                                            bgLoader
                                            messageTextView.setTextColor(R.color.black)
                                            messageTextView.setBackgroundResource(R.color.transparant)
                                            //messageTextView.height = originalBm.height
                                            // toast.setGravity(0, 0, -1)
                                            //messageTextView.height = 800
                                            //  messageTextView.width = 800
                                            toast.view.background.alpha = 80
                                            toast.show()
                                            toast.setMargin(0F, -2F)
                                            setVisible(gifLoader)
                                            setVisible(txtBottom)
                                            setVisible(txtTop)
                                            setInvisible(header)
                                            setInvisible(navigationView)
                                        }

                                }
                                if (count == totaalStreepkes) {
                                    setInvisible(gifLoader)
                                    setInvisible(txtBottom)
                                    setInvisible(txtTop)
                                    setVisible(header)
                                    setVisible(navigationView)
                                }
                                val SPLASH_TIME_OUT: Long =
                                    (2200 * totaalStreepkes).toLong() // 5150 perfect sec
                                Handler().postDelayed({
                                    // This method will be executed once the timer is over
                                    setInvisible(gifLoader)
                                    setInvisible(txtBottom)
                                    setInvisible(txtTop)
                                    setVisible(header)
                                    setVisible(navigationView)

                                    // Start your app main activity
                                    // close this activity
                                }, SPLASH_TIME_OUT)

                            }
                        } else {

                            for (i in 0 until itemsGeselecteerd.size) {
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
                                }
                            }

                            if (totaalStreepkes > 1) {
                                val toast = Toast.makeText(
                                    root.context.applicationContext,
                                    "Streepkes toegevoegd aan de lijst",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val toast = Toast.makeText(
                                    root.context.applicationContext,
                                    "Streepke toegevoegd aan de lijst",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        // refPeriode_Personen_Streepkes.setValue(c)
                        getFragmentManager()!!.beginTransaction()
                            .detach(root.findFragment())
                            .attach(root.findFragment()).commit();
                    }

                })
            alertDialog.show()


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


