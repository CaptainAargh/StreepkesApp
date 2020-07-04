package be.kdg.scoutsappadmin

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_roddel.*

class addRoddelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_roddel)
        val btnAddPhoto = findViewById<Button>(R.id.frg_add_roddel_add_photo)
        val btnAddRoddel = findViewById<Button>(R.id.frg_add_roddel_btnAdd)

        val txtTitle = findViewById<EditText>(R.id.frg_add_roddel_txttitle)
        val txtTekst = findViewById<EditText>(R.id.frg_add_roddel_txtTekst)

        val anoniem = findViewById<CheckBox>(R.id.frg_add_roddel_cbAnoniem)

        btnAddPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        btnAddRoddel.setOnClickListener {
            uploadImageToFirebaseStorage()
        }

    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            //bitmapDrawable.bitmap.height = nav_header_imageView.height
            // bitmapDrawable.bitmap.width = nav_header_imageView.width
            val persoonNaam = intent!!.extras!!.get("gebruiksnaam") as String

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadImageToFirebaseStorage() {

        val gebruikerNaam = intent!!.extras!!.get("gebruiksnaam") as String
        val filename = (title.toString()+ System.currentTimeMillis().toString())
        if (selectedPhotoUri == null) saveRoddelToDatabase("",gebruikerNaam)

        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("roddel", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("roddel", "File Location: $it")

                    saveRoddelToDatabase(it.toString(),gebruikerNaam)
                }
            }
            .addOnFailureListener {
                Log.d("roddel", "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun saveRoddelToDatabase(photoString: String, autheur: String) {
        val key = FirebaseDatabase.getInstance().getReference("/roddels").push().key
        if (key == null) {
            Log.d("periodes", "keys push niet aangekregen van roddels")
            return
        }
        var anoniem = true
        if (!frg_add_roddel_cbAnoniem.isChecked) {
            anoniem = false
        }

        val refRoddel = FirebaseDatabase.getInstance().getReference("/roddels/$key")
        val roddel = Roddel(key,frg_add_roddel_txttitle.text.toString(),autheur,frg_add_roddel_txtTekst.text.toString(),photoString,anoniem)
        refRoddel.setValue(roddel)
            .addOnSuccessListener {
                Log.d("roddel", "Finally we saved the roddel to Firebase Database")
            }
            .addOnFailureListener {
                Log.d("roddel", "Failed to set value to database: ${it.message}")
            }
    }

}

class Roddel(val key:String, val title: String, val autheur:String, val tekst:String, val photo:String, val anoniem : Boolean) {
    constructor() : this("", "", "","","",true)
}