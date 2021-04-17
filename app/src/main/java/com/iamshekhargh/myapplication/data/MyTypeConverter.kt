package com.iamshekhargh.myapplication.data

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by <<-- iamShekharGH -->>
 * on 08 April 2021
 * at 5:49 PM.
 */
class MyTypeConverter {
    private val TAG = "MyTypeConverter"

    @TypeConverter
    fun fromListToString(list: List<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun formStringToList(json: String): List<String> {
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type

//        val aa = List<String>(10){
//            "ye++"
//            "yeye"
//        }
//        val temp = gson.fromJson(json,aa::class.java)
//        temp.forEach {
//            Log.i(TAG, "formStringToList: $it")
//        }
        return gson.fromJson(json, type)
    }
}