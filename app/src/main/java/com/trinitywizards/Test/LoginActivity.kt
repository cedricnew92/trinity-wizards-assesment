package com.trinitywizards.Test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.trinitywizards.Test.models.Login
import com.trinitywizards.Test.viewmodels.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private val mViewModel: LoginViewModel by viewModels { LoginViewModel.Factory }

    private val pb by lazy { findViewById<ProgressBar>(R.id.pb) }
    private val et_id by lazy { findViewById<EditText>(R.id.et_id) }
    private val btn_login by lazy { findViewById<MaterialButton>(R.id.btn_login) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        btn_login.setOnClickListener {
            val id = et_id.text.toString()
            if (id.isEmpty()) {
                Toast.makeText(this, "Please enter user id to login", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            pb.visibility = View.VISIBLE
            mViewModel.login(this@LoginActivity, id)
        }
        mViewModel.mErrorData.observe(this) {
            pb.visibility = View.GONE
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
        mViewModel.mLiveData.observe(this) {
            pb.visibility = View.GONE
            when (it.status) {
                Login.Companion.Status.INIT_COMPLETE -> {
                    pb.visibility = View.VISIBLE
                    mViewModel.checkCached(this@LoginActivity)
                }
                Login.Companion.Status.CACHED -> {
                    home()
                }
                Login.Companion.Status.NOT_CACHED -> {

                }
                Login.Companion.Status.PASSED -> {
                    home()
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

}