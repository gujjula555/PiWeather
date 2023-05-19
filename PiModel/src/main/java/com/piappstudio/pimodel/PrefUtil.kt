package com.piappstudio.pimodel

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefUtil @Inject constructor(@ApplicationContext val context: Context) {

    private var sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun saveCity(cityName:String?) {
        if (cityName.isNullOrBlank()) {
            sharedPreferences.edit().remove("city").apply()
        } else {
            sharedPreferences.edit().putString("city", cityName).apply()
        }


    }

    fun getCity():String? {
        return sharedPreferences.getString("city", null)
    }
}