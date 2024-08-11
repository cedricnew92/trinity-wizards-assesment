package com.trinitywizards.Test.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.trinitywizards.Test.R
import com.trinitywizards.Test.models.Contact
import com.trinitywizards.Test.views.ContactView
import java.util.ArrayList

class ContactsAdapter(val context: Context, var contacts: ArrayList<Contact>) : RecyclerView.Adapter<ContactsAdapter.ViewHolder?>() {

    var listener : OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    val inflater = LayoutInflater.from(context)

    @NonNull
    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, i: Int): ViewHolder {
        var view: View
        if (i == Contact.HEADER)
            view = inflater.inflate(R.layout.view_contacts_header, viewGroup, false)
        else
            view = inflater.inflate(R.layout.view_contacts_cell, viewGroup, false)
        return ViewHolder(view, i)
    }

    override fun getItemViewType(position: Int): Int {
        val cell: Contact = contacts[position]
        return cell.type
    }

    override fun onBindViewHolder(@NonNull viewHolder: ViewHolder, i: Int) {
        val contact: Contact = contacts[i]
        when (contact.type) {
            Contact.HEADER -> {
                viewHolder.tv_header?.text = contact.text
            }
            else -> {
                val nickname = contact.firstname.first().uppercase() + contact.lastname.first().uppercase()
                viewHolder.cv?.setText(nickname)
                viewHolder.tv_name?.text = contact.firstname + " " + contact.lastname
                if (contact.you)
                    viewHolder.tv_you?.visibility = View.VISIBLE
                else
                    viewHolder.tv_you?.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    inner class ViewHolder(itemView: View, type: Int) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var tv_header : TextView? = null
        var cv : ContactView ? = null
        var tv_name : TextView? = null
        var tv_you : TextView? = null
        init {
            if (type == Contact.CELL) {
                cv = itemView.findViewById(R.id.cv)
                tv_name = itemView.findViewById(R.id.tv_name)
                tv_you = itemView.findViewById(R.id.tv_you)
                itemView.setOnClickListener(this)
            } else {
                tv_header = itemView.findViewById(R.id.tv_header)
            }
        }

        override fun onClick(p0: View?) {
            if (listener != null)
                listener?.onItemClick(adapterPosition)
        }
    }

}