package be.kdg.scoutsappadmin.ui.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import be.kdg.scoutsappadmin.R

class LogFragment : Fragment() {

    private lateinit var logViewModel: LogViewModel

    private lateinit var btnAddConsumptie: Button
    private lateinit var btndeleteConsumptie: Button

    private lateinit var spinnerNaamStreepjes: Spinner
    private lateinit var spinnerGegevenDoor: Spinner
    private lateinit var spinnerNaam: Spinner
    private lateinit var spinnerPeriode: Spinner
    private lateinit var spinnerDag: Spinner

    private lateinit var txtAantalStreepjes: TextView

    private lateinit var rvLog: RecyclerView






    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        logViewModel =
                ViewModelProviders.of(this).get(LogViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_log, container, false)


        // val textView: TextView = root.findViewById(R.id.text_slideshow)
        logViewModel.text.observe(viewLifecycleOwner, Observer {
         //   textView.text = it
        })

        btnAddConsumptie = root.findViewById(R.id.frgLog_btn_addConsumatie)
        btndeleteConsumptie = root.findViewById(R.id.frgLog_btn_deleteConsumatie)

        spinnerNaamStreepjes = root.findViewById(R.id.frgLog_Spinner_naamStreepjes)
        spinnerGegevenDoor = root.findViewById(R.id.frgLog_Spinner_gegevenDoor)
        spinnerNaam = root.findViewById(R.id.frgLog_Spinner_naam)
        spinnerPeriode = root.findViewById(R.id.frgLog_Spinner_Periode)
        spinnerDag = root.findViewById(R.id.frgLog_Spinner_dag)

        txtAantalStreepjes = root.findViewById(R.id.frgLog_txt_aantalStreepjes)

        rvLog = root.findViewById(R.id.frgOverzicht_rv_Streepkes)

        return root

    }
}
