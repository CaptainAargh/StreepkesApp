/*
package be.kdg.scoutsappadmin.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.ColorSpace.Model
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.kdg.scoutsappadmin.R
import be.kdg.scoutsappadmin.model.Persoon
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.roundedstreepke_row_item.view.*


class streepkesAdapter(
    private val mRecyclerView: RecyclerView,
    private val context1: Context,
    private val personen: MutableList<Persoon>
) : RecyclerView.Adapter<streepkesAdapter.ViewHolder>() {



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.roundedstreepke_row_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return personen.size
    }

    override fun onBindViewHolder(holder: streepkesAdapter.ViewHolder, position: Int) {
        holder.itemView.txt_row_naam.setText(personen[position].persoonNaam)
        holder.itemView.fragment_home_rvPersonenMain.setOnClickListener {
            row_index = position
            notifyDataSetChanged()
        }
        if (row_index === position) {
            holder.itemView.roundedImageView.setBackgroundColor(Color.parseColor("#ffffff"))
        } else {
            holder.itemView.roundedImageView.setBackgroundColor(Color.parseColor("#000000"))
        }
    }


}
*/
