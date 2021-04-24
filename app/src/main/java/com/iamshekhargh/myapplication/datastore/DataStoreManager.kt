package com.iamshekhargh.myapplication.datastore

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by <<-- iamShekharGH -->>
 * on 15 April 2021
 * at 2:15 PM.
 */

data class InformationPrefs(
    val label: String,
    val labelList: String,
    val sortOrder: SortOrder,
    val searchQuery: String,
    val ascending: Boolean
)

enum class SortOrder { SORT_BY_NAME, SORT_BY_DATE_CREATED }

@Singleton
class DataStoreManager @Inject constructor(c: Application) {
    private val TAG = "DataStoreManager"

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "com.iamshekhargh.myapplication")
    private val mDataStore = c.dataStore

    private object PreferenceKeys {
        val LABEL_KEY = stringPreferencesKey("label")
        val LABEL_LIST_KEY = stringPreferencesKey("labelList")
        val SORT_ORDER = stringPreferencesKey("sortOrder")
        val SEARCH_QUERY = stringPreferencesKey("searchQuery")
        val ASCENDING_ORDER = booleanPreferencesKey("ascendingOrder")
    }

    val prefFlow = mDataStore.data
        .catch { e ->
            if (e is IOException) {
                Log.i(TAG, ": ${e.message}")
                e.printStackTrace()
                emit(emptyPreferences())
            } else throw e
        }.map { prefs ->
            val label = prefs[PreferenceKeys.LABEL_KEY] ?: ""
            val labelList = prefs[PreferenceKeys.LABEL_LIST_KEY] ?: ""
            val sortOrder =
                SortOrder.valueOf(prefs[PreferenceKeys.SORT_ORDER] ?: SortOrder.SORT_BY_NAME.name)
            val query = prefs[PreferenceKeys.SEARCH_QUERY] ?: ""
            val ascendingOrder = prefs[PreferenceKeys.ASCENDING_ORDER] ?: true

            InformationPrefs(label, labelList, sortOrder, query, ascendingOrder)
        }

    suspend fun saveLabelList(ll: MutableList<String>) {
        val g: Gson = Gson()
        val llStr = g.toJson(ll)
        mDataStore.edit { pref ->
            pref[PreferenceKeys.LABEL_LIST_KEY] = llStr
        }
    }

    fun getLabelList(): MutableList<String> {
        // I can also send the entire note object and then access the labels item from it.
        // But m doing this.

        val g: Gson = Gson()
        var list: MutableList<String> = arrayListOf()
        mDataStore.data.map { prefs ->
            val s = prefs[PreferenceKeys.LABEL_LIST_KEY] ?: ""

            if (g.fromJson(s, list::class.java) != null) {
                list = g.fromJson(s, list::class.java)
            }

//            if (g.fromJson(s, Note::class.java) != null)
//                list =
//                    (g.fromJson(s, Note::class.java).labels
//                        ?: arrayListOf(String)) as MutableList<String>
        }
        return list
    }

    suspend fun setSearchQuery(q: String) {
        mDataStore.edit { pref ->
            pref[PreferenceKeys.SEARCH_QUERY] = q
        }
    }

    fun getSearchQuery(): String {
        var q = ""
        mDataStore.data.map { pref ->
            q = pref[PreferenceKeys.SEARCH_QUERY] ?: ""
        }
        return q
    }

    suspend fun setAscendingOrderBool(order: Boolean) {
        mDataStore.edit { pref ->
            pref[PreferenceKeys.ASCENDING_ORDER] = order
        }
    }

    suspend fun setSortOrder(sortOrder: SortOrder) {
        mDataStore.edit { pref ->
            pref[PreferenceKeys.SORT_ORDER] = sortOrder.name
        }
    }

}