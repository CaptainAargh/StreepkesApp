package be.kdg.scoutsappadmin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_loading_screen.*

class LoadingScreenActivity : AppCompatActivity() {
    val SPLASH_TIME_OUT:Long = 5150 // 5150 perfect sec
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        // This is the loading time of the splash screen
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_loading_screen)
                Handler().postDelayed({
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    startActivity(Intent(this,LoginActivity::class.java))
                    // close this activity
                    finish()
                }, SPLASH_TIME_OUT)
            }
    }
