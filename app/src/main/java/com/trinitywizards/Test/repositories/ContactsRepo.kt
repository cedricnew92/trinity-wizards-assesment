package com.trinitywizards.Test.repositories

import android.content.Context
import com.trinitywizards.Test.models.Contact
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.util.logging.Logger


object ContactsRepo {

    private const val filename = "data.json"
    private var data : File? = null

    private const val KEY_ID = "id"
    private const val KEY_FIRSTNAME = "firstName"
    private const val KEY_LASTNAME = "lastName"
    private const val KEY_EMAIL = "email"
    private const val KEY_DOB = "dob"

    private fun read(data: File) : String {
        val builder = StringBuilder()
        BufferedInputStream(FileInputStream(data)).use { bis ->
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (bis.read(buffer).also { bytesRead = it } != -1)
                builder.append(String(buffer, 0, bytesRead))
        }
        return builder.toString()
    }

    private fun write(json: String, data: File) {
        val out = FileWriter(data)
        out.write(json)
        out.close()
    }

    private fun init(context: Context) : Boolean {
        var result = false
        try {
            val cache = File(context.cacheDir, filename)
            if (!cache.exists()) {
                val `in` = context.assets.open(filename)
                val out = FileOutputStream(cache)
                val buffer = ByteArray(1024)
                var read: Int
                while (`in`.read(buffer).also { read = it } != -1)
                    out.write(buffer, 0, read)
                `in`.close()
                out.flush()
                out.close()
            }
            data = cache
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
        return result
    }

    private fun toArrayList(array: JSONArray) : java.util.ArrayList<Contact> {
        val result = ArrayList<Contact>()
        for (i in 0 until array.length()) {
            val json = array.getJSONObject(i)
            val id =  json.optString(KEY_ID)
            val firstname = json.optString(KEY_FIRSTNAME)
            val lastname = json.optString(KEY_LASTNAME)
            val email = json.optString(KEY_EMAIL)
            val dob = json.optString(KEY_DOB)
            val contact = Contact(
                id,
                firstname,
                lastname,
                email,
                dob
            )
            result.add(contact)
        }
        result.sortBy { it.firstname }
        return result
    }

    private fun toJsonArray(contacts: java.util.ArrayList<Contact>) : JSONArray {
        val array = JSONArray()
        for (contact in contacts) {
            val json = JSONObject()
            json.put(KEY_ID, contact.id)
            json.put(KEY_FIRSTNAME, contact.firstname)
            json.put(KEY_LASTNAME, contact.lastname)
            json.put(KEY_EMAIL, contact.email)
            json.put(KEY_DOB, contact.dob)
            array.put(json)
        }
        return array
    }

    suspend fun reset(context: Context) {
        val cache = File(context.cacheDir, filename)
        val `in` = context.assets.open(filename)
        val out = FileOutputStream(cache)
        val buffer = ByteArray(1024)
        var read: Int
        while (`in`.read(buffer).also { read = it } != -1)
            out.write(buffer, 0, read)
        `in`.close()
        out.flush()
        out.close()
        data = cache
    }

    suspend fun all(context: Context) : java.util.ArrayList<Contact> {
        init(context)
        val array = JSONArray(read(data!!))
        val result = toArrayList(array)
        return result
    }

    suspend fun get(context: Context, id: String) : Contact {
        val contacts = all(context)
        val filtered = contacts.filter { it.id == id }
        if (filtered.isEmpty())
            throw Exception("Contact not found")
        val contact = contacts[0]
        return contact
    }

    suspend fun delete(context: Context, id: String) : Boolean {
        var contacts = all(context)
        val filters = contacts.filter { it.id == id }
        if (filters.isEmpty())
            throw Exception("Contact not found")
        contacts.remove(filters[0])
        val array = toJsonArray(contacts)
        write(array.toString(), data!!)
        return true
    }

    suspend fun insert(context: Context, contact: Contact) : Boolean {
        var contacts = all(context)
        contacts.add(contact)
        val array = toJsonArray(contacts)
        write(array.toString(), data!!)
        return true
    }

}