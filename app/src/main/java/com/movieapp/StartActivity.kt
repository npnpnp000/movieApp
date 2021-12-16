package com.movieapp

import android.app.AlertDialog
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.movieapp.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        binding = ActivityStartBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



        if(exceptionFunc()){  // if have internet wait 1.5 seconds and go to MainActivity if  haven't show no internet dialog
            Handler(mainLooper).postDelayed({
                startActivity(Intent(this,MainActivity::class.java))
            },1500)
        }

    }


    // check is have network
    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }
//    show no internet dialog
    private fun exceptionFunc() : Boolean {
        val alertDialog = AlertDialog.Builder(this)
        if (!isNetworkConnected()) {
            alertDialog.setTitle("No network connection")
            alertDialog.setMessage(
                "Please turn on your internet connection and restart the app."
            )
            val alert = alertDialog.create()
            alert.show()
            binding.loadingPgb.visibility = View.INVISIBLE  // remove loader icon
            return false
        }else{return true}
    }

}