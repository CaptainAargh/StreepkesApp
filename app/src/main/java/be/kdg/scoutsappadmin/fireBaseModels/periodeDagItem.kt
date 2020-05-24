package be.kdg.scoutsappadmin.fireBaseModels

import be.kdg.scoutsappadmin.R
import be.kdg.scoutsappadmin.model.Persoon
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.periodedag_row.view.*

class periodeDagItem(val persoon: Persoon) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.periodedag_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txtNaam.text = persoon.persoonNaam
        viewHolder.itemView.txtConsumpties.text = " "

    }

}

