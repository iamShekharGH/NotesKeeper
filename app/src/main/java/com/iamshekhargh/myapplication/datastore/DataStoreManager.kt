package com.iamshekhargh.myapplication.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.iamshekhargh.myapplication.data.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Singleton

/**
 * Created by <<-- iamShekharGH -->>
 * on 15 April 2021
 * at 2:15 PM.
 */
@Singleton
class DataStoreManager(c: Context) {
    private val TAG = "DataStoreManager"

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "com.iamshekhargh.myapplication")
    private val mDataStore = c.dataStore

    private object PreferenceKeys {
        val LABEL_KEY = stringPreferencesKey("labelList")
    }

    private val labelKey = stringPreferencesKey("labelList")
    val emptyList: MutableList<String> = arrayListOf()

    val prefFlow = mDataStore.data
        .catch { e ->
            if (e is IOException) {
                Log.i(TAG, ": ${e.message}")
                e.printStackTrace()
                emit(emptyPreferences())
            } else throw e
        }
        .map { prefs ->
            val label = prefs[PreferenceKeys.LABEL_KEY]

        }

//    val labelList: Flow<MutableList<String>> = mDataStore.data.catch {
//        if (it is IOException) {
//            it.printStackTrace()
//            emit(emptyPreferences())
//        } else {
//            throw it
//        }
//    }.map { preferences ->
//        preferences[labelKey] ?: emptyList
//
//    }

    suspend fun saveLabelList(ll: MutableList<String>) {
        val g: Gson = Gson()
        val llStr = g.toJson(ll)
        mDataStore.edit { pref ->
            pref[labelKey] = llStr
        }
    }

    fun getLabelList(): MutableList<String> {
        val g: Gson = Gson()
        var list: MutableList<String> = arrayListOf()
        mDataStore.data.map { prefs ->
            val s = prefs[labelKey]
            if (g.fromJson(s, Note::class.java) != null)
                list =
                    (g.fromJson(s, Note::class.java) ?: arrayListOf(String)) as MutableList<String>
        }
        return list
    }

    val label: Flow<String> = mDataStore.data.map { preferences ->
        preferences[labelKey] ?: ""
    }


    private suspend fun writeToDataStore(label: String) {

//        requireContext().dataStore.edit { file ->
        mDataStore.edit { file ->
            file[labelKey] = label
        }
    }

    private suspend fun readFromDataStore(): String {
//        val labelFlow: Flow<String> = requireContext().dataStore.data
        val labelFlow: Flow<String> = mDataStore.data.map { preferences ->
            preferences[labelKey] ?: ""
        }
        var l = ""
        labelFlow.collect { value ->
            l = value
        }
        Log.i("", "testingDatastore: Write returning $l")

        return l
    }

    private fun testingDatastore() {
        val text = " Trying to save this #####"


    }


}