package com.trinitywizards.Test

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.trinitywizards.Test.models.Splash
import com.trinitywizards.Test.viewmodels.SplashViewModel

class SplashActivity : AppCompatActivity() {

    private val mViewModel: SplashViewModel by viewModels { SplashViewModel.Factory }

    private val pb by lazy { findViewById<ProgressBar>(R.id.pb) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        mViewModel.mErrorData.observe(this) {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
        mViewModel.mLiveData.observe(this) {
            pb.visibility = View.GONE
            when (it.status) {
                Splash.Companion.Status.INIT_COMPLETE -> {
                    pb.visibility = View.VISIBLE
                    mViewModel.checkCached(this@SplashActivity)
                }
                Splash.Companion.Status.CACHED -> {
                    home()
                }
                Splash.Companion.Status.NOT_CACHED -> {
                    login()
                }
                else -> {

                }
            }
        }
        pb.visibility = View.VISIBLE
        mViewModel.initialize(this)
    }

    private fun home() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun login() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}