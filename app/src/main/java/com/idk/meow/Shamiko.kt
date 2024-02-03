package com.idk.meow

import android.content.Context
import android.widget.Toast
import com.topjohnwu.superuser.io.SuFile
import java.io.File
import java.io.IOException


internal object Shamiko {
    private const val FLAG_FILE = "/data/adb/shamiko/whitelist"
    fun toggleWhitelist(context: Context?, checked: Boolean):Boolean {
        val flagFile: File = SuFile.open(FLAG_FILE)
        return if (checked) {
            try {
                flagFile.createNewFile()
                true
            } catch (e: IOException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                e.printStackTrace()
                false
            }
        } else {
            flagFile.delete()
            false
        }
    }

    val isWhitelistModeOn: Boolean
        get() {
            val flagFile: File = SuFile.open(FLAG_FILE)
            return flagFile.exists()
        }
}