package be.kdg.scoutsappadmin

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fragment_social.*


class roddeldetailzonderphotoactivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.roddel_detail_nophoto)
        val btnGaterug = findViewById<Button>(R.id.roddel_detail_btn_gaterug2)

        val title = findViewById<TextView>(R.id.roddel_detail_title2)
        val tekst = findViewById<TextView>(R.id.roddel_detail_tekst2)
        val autheur = findViewById<TextView>(R.id.roddel_detail_autheur2)


        val intentTitle = intent.getStringExtra("title")
        val intentTekst = intent.getStringExtra("tekst")
        val intentautheur = intent.getStringExtra("autheur")
        val intentAnoniem = intent.getBooleanExtra("anoniem",true)

        title.text = intentTitle
        tekst.text = intentTekst
        if (intentAnoniem) {
            autheur.text = "Voor verdere vragen en opmerkingen moet ge maar is rond vragen bij de rest vd leiding "+
                    " \n De ontwikkelaar van de app is niet verantwoordelijk voor mogelijk drama's, gebroken hartjes en andere emotionele gevolgen"

        } else {
            autheur.text = "Voor verdere vragen en opmerkingen verwijs ik u door naar de auteur van dit bericht : $intentautheur" +
                    " \n De ontwikkelaar van de app is niet verantwoordelijk voor mogelijk drama's, gebroken hartjes en andere emotionele gevolgen"

        }
        autheur.setMovementMethod(ScrollingMovementMethod())






        btnGaterug.setOnClickListener {
            val intent = Intent(this, socialFragment::class.java)
            startActivity(intent)
        }


    }
}