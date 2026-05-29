package com.example.romajijp.searchhistorymanager

import android.content.Context
import androidx.activity.contextaware.ContextAware
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryManager(context : Context) {
    private val prefs = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val KEY = "queries"
    private val MAX = 50

    fun saveQuery(query: String) {
        if (query.isBlank()) return

        val list = getQueries().toMutableList()

        list.remove(query)        // remove duplicate if it already exists
        list.add(0, query)        // add to top (most recent first)

        if (list.size > MAX) {
            list.removeLast()     // drop oldest if over limit
        }

        prefs.edit()
            .putString(KEY, gson.toJson(list))
            .apply()
    }

    fun getQueries(): List<String> {
        val json = prefs.getString(KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
    fun deleteQuery(query: String) {
        val list = getQueries().toMutableList()
        list.remove(query)
        prefs.edit().putString(KEY, gson.toJson(list)).apply()
    }

    fun clearAll() {
        prefs.edit().remove(KEY).apply()
    }
}