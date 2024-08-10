package com.trinitywizards.Test.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.trinitywizards.Test.DetailsContactActivity
import com.trinitywizards.Test.LoginActivity
import com.trinitywizards.Test.R
import com.trinitywizards.Test.models.Detail
import com.trinitywizards.Test.models.Profile
import com.trinitywizards.Test.viewmodels.ProfileViewModel
import com.trinitywizards.Test.views.ContactView

class ProfileFragment : Fragment() {

    private val TAG = javaClass.simpleName

    private val lDetails = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode != RESULT_OK)
            return@registerForActivityResult
        pb.visibility = View.VISIBLE
        mViewModel.initialize(requireContext())
    }

    private val mViewModel: ProfileViewModel by viewModels { ProfileViewModel.Factory }

    private lateinit var btn_logout : Button
    private lateinit var cv: ContactView
    private lateinit var tv_name : TextView
    private lateinit var tv_email : TextView
    private lateinit var tv_dob : TextView
    private lateinit var btn_update : Button
    private lateinit var pb : ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        btn_logout = view.findViewById(R.id.btn_logout)
        btn_logout.setOnClickListener {
            pb.visibility = View.VISIBLE
            mViewModel.logout(requireContext())
        }

        cv = view.findViewById(R.id.cv)
        tv_name = view.findViewById(R.id.tv_name)
        tv_email = view.findViewById(R.id.tv_email)
        tv_dob = view.findViewById(R.id.tv_dob)

        btn_update = view.findViewById(R.id.btn_update)
        btn_update.setOnClickListener {
            pb.visibility = View.VISIBLE
            mViewModel.update()
        }

        pb = view.findViewById(R.id.pb)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.mErrorData.observe(viewLifecycleOwner) {
            pb.visibility = View.GONE
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
        }
        mViewModel.mLiveData.observe(viewLifecycleOwner) {
            pb.visibility = View.GONE
            when(it.status) {
                Profile.Companion.Status.INIT_COMPLETE -> {
                    val contact = it.contact!!
                    val nickname = contact.firstname.first().uppercase() + contact.lastname.first().uppercase()
                    cv.setText(nickname)
                    tv_name.text = contact.firstname + " " + contact.lastname
                    if (contact.email.isNullOrEmpty())
                        tv_email.visibility = View.GONE
                    else {
                        tv_email.visibility = View.VISIBLE
                        tv_email.text = contact.email
                    }
                    if (contact.dob.isNullOrEmpty())
                        tv_dob.visibility = View.GONE
                    else {
                        tv_dob.visibility = View.VISIBLE
                        tv_dob.text = contact.dob
                    }
                }
                Profile.Companion.Status.UPDATE -> {
                    val intent = Intent(requireActivity(), DetailsContactActivity::class.java)
                    intent.putExtra(DetailsContactActivity.KEY_CONTACT, it.contact!!)
                    lDetails.launch(intent)
                }
                else -> {
                    val intent = Intent(requireActivity(), LoginActivity::class.java)
                    requireActivity().startActivity(intent)
                    requireActivity().finish()
                }
            }
        }

        pb.visibility = View.VISIBLE
        mViewModel.initialize(requireContext())
    }

}