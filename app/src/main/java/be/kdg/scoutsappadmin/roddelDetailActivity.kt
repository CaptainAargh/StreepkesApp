package be.kdg.scoutsappadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_roddel_detail.*
import kotlinx.android.synthetic.main.fragment_social.*
import kotlinx.android.synthetic.main.roddel_item_photo.view.*

class roddelDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roddel_detail)
        val btnGaterug = findViewById<Button>(R.id.roddel_detail_btn_gaterug)

        val title = findViewById<TextView>(R.id.roddel_detail_title)
        val tekst = findViewById<TextView>(R.id.roddel_detail_tekst)
        val autheur = findViewById<TextView>(R.id.roddel_detail_autheur)
        val photo = findViewById<ImageView>(R.id.roddel_detail_iv)


        val intentTitle = intent.getStringExtra("title")
        val intentTekst = intent.getStringExtra("tekst")
        val intentautheur = intent.getStringExtra("autheur")
        val intentPhoto = intent.getStringExtra("image")
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

        Picasso.get().load(intentPhoto).into(photo)
        fun showHide(view: View) {
            view.visibility = if (view.visibility == View.VISIBLE){
                View.INVISIBLE
            } else{
                View.VISIBLE
            }
        }
        photo.setOnClickListener {
            Picasso.get().load(intentPhoto).into(roddel_detail_fullImg)
            roddel_detail_fullImg.bringToFront()
            roddel_detail_fullImg.rotation = 90F
            showHide(roddel_detail_fullImg)
        }



        btnGaterug.setOnClickListener {
            finish()

        }


    }
}