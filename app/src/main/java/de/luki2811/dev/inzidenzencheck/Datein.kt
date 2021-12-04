package de.luki2811.dev.inzidenzencheck

import android.app.Application
import android.content.Context
import android.util.Log
import java.io.*
import java.nio.charset.StandardCharsets

class Datein(var name: String) : Application() {
    fun loadFromFile(context: Context): String {
        var fis: FileInputStream? = null
        try {
            fis = context.openFileInput(name)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val inputStreamReader = InputStreamReader(fis, StandardCharsets.UTF_8)
        val stringBuilder = StringBuilder()
        try {
            BufferedReader(inputStreamReader).use { reader ->
                var line = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line).append('\n')
                    line = reader.readLine()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }

    fun writeInFile(text: String?, context: Context) {
        try {
            val outputStreamWriter = OutputStreamWriter(context.openFileOutput(name, MODE_PRIVATE))
            outputStreamWriter.write(text)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }
    }
}