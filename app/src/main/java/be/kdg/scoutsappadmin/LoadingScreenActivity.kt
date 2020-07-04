package be.kdg.scoutsappadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class LoadingScreenActivity : AppCompatActivity() {
    val SPLASH_TIME_OUT:Long = 500 // 5150 perfect sec
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