package com.trinitywizards.Test

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.trinitywizards.Test.models.Detail
import com.trinitywizards.Test.models.Login
import com.trinitywizards.Test.viewmodels.DetailViewModel
import com.trinitywizards.Test.viewmodels.LoginViewModel
import com.trinitywizards.Test.views.ContactView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class DetailsContactActivity : AppCompatActivity() {

    companion object {
        const val KEY_CONTACT = "contact"
    }

    private val mViewModel: DetailViewModel by viewModels { DetailViewModel.Factory }

    private val btn_back by lazy { findViewById<ImageButton>(R.id.btn_back) }
    private val cv by lazy { findViewById<ContactView>(R.id.cv) }
    private val et_firstname by lazy { findViewById<EditText>(R.id.et_firstname) }
    private val et_lastname by lazy { findViewById<EditText>(R.id.et_lastname) }
    private val et_email by lazy { findViewById<EditText>(R.id.et_email) }
    private val et_dob by lazy { findViewById<EditText>(R.id.et_dob) }
    private val btn_action by lazy { findViewById<MaterialButton>(R.id.btn_action) }
    private val btn_remove by lazy { findViewById<MaterialButton>(R.id.btn_remove) }
    private val pb by lazy { findViewById<ProgressBar>(R.id.pb) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_details_contact)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btn_back.setOnClickListener {
            finish()
        }
        btn_action.setOnClickListener {
            val firstname = et_firstname.text.toString()
            if (firstname.isNullOrEmpty()) {
                Toast.makeText(this, "Firstname cannot be blank", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val lastname = et_lastname.text.toString()
            if (lastname.isNullOrEmpty()) {
                Toast.makeText(this, "Lastname cannot be blank", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val email = et_email.text.toString()
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val dob = et_dob.text.toString()
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            formatter.isLenient = false
            try {
                formatter.parse(dob)
            } catch (e: Exception) {
                Toast.makeText(this, "Invalid date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            pb.visibility = View.VISIBLE
            mViewModel.addOrUpdate(this, firstname, lastname, email, dob)
        }
        btn_remove.setOnClickListener {
            pb.visibility = View.VISIBLE
            mViewModel.delete(this)
        }
    }

    override fun onStart() {
        super.onStart()
        mViewModel.mErrorData.observe(this) {
            pb.visibility = View.GONE
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
        mViewModel.mLiveData.observe(this) {
            pb.visibility = View.GONE
            when (it.status) {
                Detail.Companion.Status.INIT_COMPLETE -> {
                    btn_remove.visibility = View.GONE
                }
                Detail.Companion.Status.INIT_COMPLETE_WITH_CONTACT -> {
                    val contact = it.contact!!
                    val nickname = contact.firstname.first().uppercase() + contact.lastname.first().uppercase()
                    cv.setText(nickname)
                    et_firstname.setText(contact.firstname)
                    et_lastname.setText(contact.lastname)
                    if (!contact.email.isNullOrEmpty())
                        et_email.setText(contact.email)
                    if (!contact.dob.isNullOrEmpty())
                        et_dob.setText(contact.dob)
                    btn_action.text = "Update"
                    btn_remove.visibility = View.VISIBLE
                }
                Detail.Companion.Status.SAVED_OR_UPDATED -> {
                    Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                Detail.Companion.Status.DELETED -> {
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                else -> {

                }
            }
        }

        pb.visibility = View.VISIBLE
        mViewModel.initialize(this, intent)
    }

}