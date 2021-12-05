package de.luki2811.dev.inzidenzencheck

import android.content.Context
import android.widget.Toast
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.IOException
import java.net.URL
import java.net.URLConnection
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.system.exitProcess

class InternetRequest internal constructor(private var url: URL, private var context: Context, private var fileName: String) :
    Thread() {
    private var jsonObject: JSONObject? = null

    override fun run() {
        jsonObject = getJSONfromURL(url)
        val file = Datein(fileName)
        if (jsonObject == null){
            Toast.makeText(context, context.getString(R.string.error_cant_load_data), Toast.LENGTH_LONG).show()
            return
        }
        file.writeInFile(jsonObject.toString(), context)
    }

    companion object {
        fun getJSONfromURL(url: URL?): JSONObject? {
            var jsonObject: JSONObject? = null
            val urlConnection: URLConnection
            try {
                urlConnection = url!!.openConnection()
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
            try {
                BufferedInputStream(urlConnection.getInputStream()).use { `in` ->
                    val scanner = Scanner(`in`, StandardCharsets.UTF_8.name())
                    jsonObject = JSONObject(scanner.useDelimiter("//Z").next())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: NullPointerException) {
                e.printStackTrace()
                exitProcess(-1)
            }
            return jsonObject
        }
    }
}