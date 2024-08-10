package com.trinitywizards.Test.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.trinitywizards.Test.DetailsContactActivity
import com.trinitywizards.Test.R
import com.trinitywizards.Test.adapters.ContactsAdapter
import com.trinitywizards.Test.models.Contact
import com.trinitywizards.Test.models.Contacts
import com.trinitywizards.Test.viewmodels.ContactsViewModel
import com.trinitywizards.Test.viewmodels.ProfileViewModel

class ContactsFragment : Fragment(), ContactsAdapter.OnItemClickListener {

    private val mViewModel: ContactsViewModel by viewModels { ContactsViewModel.Factory }

    private val lDetails = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode != RESULT_OK)
            return@registerForActivityResult
        pb.visibility = View.VISIBLE
        mViewModel.initialize(requireContext())
    }

    private lateinit var et_search : EditText
    private lateinit var sfl_contacts : SwipeRefreshLayout
    private lateinit var rv_contacts : RecyclerView
    private lateinit var adapter: ContactsAdapter
    private lateinit var pb : ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)

        et_search = view.findViewById(R.id.et_search)

        sfl_contacts = view.findViewById(R.id.srl_contacts)
        rv_contacts = view.findViewById(R.id.rv_contacts)
        view.findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener {
            detail(null)
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
            sfl_contacts.isRefreshing = false
            pb.visibility = View.GONE
            when(it.status) {
                Contacts.Companion.Status.INIT_COMPLETE -> {
                    val contacts = it.contacts!!
                    val mLayoutManager = LinearLayoutManager(requireContext())
                    rv_contacts.layoutManager = mLayoutManager
                    adapter = ContactsAdapter(requireContext(), contacts)
                    adapter.listener = this
                    rv_contacts.adapter = adapter
                }
                Contacts.Companion.Status.EDIT -> {
                    detail(it.contact!!)
                }
            }
        }

        sfl_contacts.setOnRefreshListener {
            mViewModel.reset(requireContext())
        }

        pb.visibility = View.VISIBLE
        mViewModel.initialize(requireContext())
    }

    override fun onItemClick(position: Int) {
        pb.visibility = View.VISIBLE
        mViewModel.get(position)
    }

    fun detail(contact: Contact?) {
        val intent = Intent(requireActivity(), DetailsContactActivity::class.java)
        if (contact != null)
            intent.putExtra(DetailsContactActivity.KEY_CONTACT, contact)
        lDetails.launch(intent)
    }

}