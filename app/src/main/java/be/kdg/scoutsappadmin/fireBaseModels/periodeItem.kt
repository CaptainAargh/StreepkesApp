package be.kdg.scoutsappadmin.fireBaseModels

import be.kdg.scoutsappadmin.R
import be.kdg.scoutsappadmin.model.Periode
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.periode_row.view.*

class periodeItem(val periode: Periode, val van: String, val tot: String) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.periode_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.periode_row_txtperiodeId.text = periode.key
        viewHolder.itemView.periode_row_txtperiodeNaam.text = periode.periodeNaam
        viewHolder.itemView.periode_row_txtperiodeVan.text = van.subSequence(0, 10)
        viewHolder.itemView.periode_row_txtperiodeTot.text = tot.subSequence(0, 10)
        var s: String = "Lijst personen in periode : \n ==============="
        for (i in periode.periodePersonen!!.indices) {
            val string: String =
                "\n " + periode.periodePersonen!![i].persoonNaam + " \n Aantal strepkes : " + periode.periodePersonen!![i].persoonConsumpties!!.size
            s += string
        }
        viewHolder.itemView.periode_row_txtperiodePersonen.text = s
    }

}
