package be.kdg.scoutsappadmin.ui.log

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import be.kdg.scoutsappadmin.*
import be.kdg.scoutsappadmin.model.Persoon
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.roddel_item.view.*
import kotlinx.android.synthetic.main.roddel_item_photo.view.*

class SocialFragment : Fragment() {

    val adapter = GroupAdapter<ViewHolder>()

    private lateinit var socialViewModel: SocialViewModel
    private lateinit var addRoddelbtn: FloatingActionButton
    private lateinit var rvRoddels: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        socialViewModel =
            ViewModelProviders.of(this).get(SocialViewModel::class.java)
        val root = inflater.inflate(
            R.layout.fragment_social, container, false
        )

        addRoddelbtn = root.findViewById(R.id.frg_social_add_btn)

        val persoon = requireActivity().intent!!.getParcelableExtra<Persoon>("gebruiker")
        Log.d("roddels", persoon.persoonNaam)
        addRoddelbtn.setOnClickListener {
            val intent = Intent(this.context, addRoddelActivity::class.java).apply {
                putExtra("gebruiksnaam", persoon.persoonNaam)
            }
            startActivity(intent)
        }

        return root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvRoddels = view.findViewById<RecyclerView>(R.id.frg_soocial_rvRoddels)
        val linearLayoutManager =  LinearLayoutManager(activity)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd  = true
        val layoutManager: RecyclerView.LayoutManager = linearLayoutManager
        rvRoddels.setLayoutManager(layoutManager)
        fetchAllRoddels()

        adapter.setOnItemClickListener { item, view ->
            Log.d("roddels", "item " + item.toString() + "view : " + view.toString())
            if (item.toString().contains("roddelPhotoItem")) {
                val intent = Intent(this.context,roddelDetailActivity::class.java).apply {
                    val roddel = item as roddelPhotoItem
                    putExtra("title", roddel.title)
                    putExtra("tekst", roddel.tekst)
                    putExtra("autheur", roddel.naam)
                    putExtra("anoniem", roddel.anoniem)
                    putExtra("image", roddel.photo)
                }
                startActivity(intent)
            } else {
                val intent = Intent(this.context,roddeldetailzonderphotoactivity::class.java).apply {
                    val roddel = item as roddelItem
                    putExtra("title", roddel.title)
                    putExtra("tekst", roddel.tekst)
                    putExtra("autheur", roddel.naam)
                    putExtra("anoniem", roddel.anoniem)
                }
                startActivity(intent)
            }

        }
    }


    private fun fetchAllRoddels() {
        val ref = FirebaseDatabase.getInstance().getReference("/roddels")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {

                p0.children.forEach {
                    val roddel: Roddel = it.getValue(Roddel::class.java)!!
                    Log.d("roddels ", roddel.toString())
                    if (roddel.photo.isNullOrBlank()) {
                        adapter.add(
                            roddelItem(
                                roddel.autheur,
                                roddel.title,
                                roddel.tekst,
                                roddel.anoniem

                            )
                        )

                    } else {
                        adapter.add(
                            roddelPhotoItem(
                                roddel.autheur,
                                roddel.title,
                                roddel.tekst,
                                roddel.photo,
                                roddel.anoniem
                            )
                        )
                    }
                }
                rvRoddels.adapter = adapter
            }


            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

}

class roddelPhotoItem(
    val naam: String, val title: String, val tekst: String, val photo: String, val anoniem: Boolean
) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.roddel_item_photo
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        if (anoniem) {
            viewHolder.itemView.roddel_auteur.text = "Ingestuurd door Anoniempje xx"
        } else {
            viewHolder.itemView.roddel_auteur.text = "Ingestuurd door " + naam
        }
        viewHolder.itemView.roddel_title.text = title
        viewHolder.itemView.roddel_tekst.text = tekst
        Picasso.get().load(photo).into(viewHolder.itemView.roddel_iv)
    }
}

class roddelItem(
    val naam: String, val title: String, val tekst: String, val anoniem: Boolean
) : Item<ViewHolder>() {
    constructor() : this("", "", "", true)

    override fun getLayout(): Int {
        return R.layout.roddel_item
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        if (anoniem) {
            viewHolder.itemView.roddel_auteur2.text = "Ingestuurd door Anoniempje xx"
        } else {
            viewHolder.itemView.roddel_auteur2.text = "Ingestuurd door " + naam
        }
        viewHolder.itemView.roddel_title2.text = title
        viewHolder.itemView.roddel_tekst2.text = tekst
    }


}