package com.trinitywizards.Test.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.trinitywizards.Test.DetailsContactActivity
import com.trinitywizards.Test.R
import com.trinitywizards.Test.adapters.ContactsAdapter
import com.trinitywizards.Test.models.Contact
import com.trinitywizards.Test.models.Contacts
import com.trinitywizards.Test.viewmodels.ContactsViewModel


class ContactsFragment : Fragment(), ContactsAdapter.OnItemClickListener {

    private val TAG = javaClass.simpleName

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

    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(p0: Editable?) {
            mViewModel.search(p0.toString())
        }

    }

    private val focus = object : OnFocusChangeListener {
        override fun onFocusChange(p0: View?, p1: Boolean) {
            if (p1)
                et_search.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(requireContext(),R.drawable.ic_search_blue), null)
            else
                et_search.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(requireContext(),R.drawable.ic_search_grey), null)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)

        view.findViewById<ConstraintLayout>(R.id.contacts).setOnClickListener {
            et_search.clearFocus()
            val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        et_search = view.findViewById(R.id.et_search)
        et_search.addTextChangedListener(watcher)
        et_search.onFocusChangeListener = focus

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
                Contacts.Companion.Status.SEARCH -> {
                    val contacts = it.contacts!!
                    val mLayoutManager = LinearLayoutManager(requireContext())
                    rv_contacts.layoutManager = mLayoutManager
                    adapter = ContactsAdapter(requireContext(), contacts)
                    adapter.listener = this
                    rv_contacts.adapter = adapter
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