package com.trinitywizards.Test.repositories

import android.content.Context

object CacheRepo {

    private const val SHARED_PREFS_KEY = "cache"

    private const val KEY_ID = "id"

    private fun set(context: Context, key: String?, data: String?) {
        val sharedPref = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(key, data)
        editor.commit()
    }

    private fun get(context: Context, key: String?): String? {
        val sharedPref = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)
        return sharedPref.getString(key, null)
    }

    suspend fun userId(context: Context) : String? {
        return get(context, KEY_ID)
    }

    suspend fun cache(context: Context, id: String) : Boolean {
        set(context, KEY_ID, id)
        return true
    }

    suspend fun delete(context: Context, id: String) : Boolean {
        set(context, KEY_ID, null)
        return true
    }

}