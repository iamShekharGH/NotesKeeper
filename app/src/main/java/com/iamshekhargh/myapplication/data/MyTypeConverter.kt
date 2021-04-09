package com.iamshekhargh.myapplication.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by <<-- iamShekharGH -->>
 * on 08 April 2021
 * at 5:49 PM.
 */
class MyTypeConverter {

    @TypeConverter
    fun fromListToString(list: List<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun formStringToList(json: String): List<String> {
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
//        return gson.fromJson(json, type)
    }
}