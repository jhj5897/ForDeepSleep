package org.jhj.fordeepsleep

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager {
    companion object {
        private const val PREFERENCES_NAME = "save_option_preference"
        private const val DEFAULT_VALUE_STRING = ""
        private const val DEFAULT_VALUE_BOOLEAN = false
        private const val DEFAULT_VALUE_INT = -1

        private fun getPreferences(context: Context):SharedPreferences{
            return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        }

        fun setString(context:Context, key:String, value:String) {
            val prefs = getPreferences(context)
            val editor = prefs.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun setBoolean(context:Context, key:String, value:Boolean) {
            val prefs = getPreferences(context)
            val editor = prefs.edit()
            editor.putBoolean(key, value)
            editor.apply()
        }

        fun setInt(context:Context, key:String, value:Int) {
            val prefs = getPreferences(context)
            val editor = prefs.edit()
            editor.putInt(key, value)
            editor.apply()
        }

        fun getString(context:Context, key:String):String {
            val prefs = getPreferences(context)
            return prefs.getString(key, DEFAULT_VALUE_STRING)!!
        }

        fun getBoolean(context:Context, key:String):Boolean {
            val prefs = getPreferences(context)
            return prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN)
        }

        fun getInt(context:Context, key:String):Int {
            val prefs = getPreferences(context)
            return prefs.getInt(key, DEFAULT_VALUE_INT)
        }

        fun removeKey(context:Context, key:String) {
            val prefs = getPreferences(context)
            val editor = prefs.edit()
            editor.remove(key)
            editor.apply()
        }
    }
}