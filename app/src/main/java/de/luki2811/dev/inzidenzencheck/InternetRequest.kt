package de.luki2811.dev.inzidenzencheck

import android.content.Context
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.IOException
import java.lang.NullPointerException
import java.net.URL
import java.net.URLConnection
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.system.exitProcess

class InternetRequest internal constructor(private var url: URL, private var context: Context, private var fileName: String) : Thread() {
    private var jsonObject: JSONObject? = null

    override fun run() {
        jsonObject = getJSONfromURL(url)
        val file = Datein(fileName)
        context.let { file.writeInFile(jsonObject.toString(), it) }
    }

    companion object {
        fun getJSONfromURL(url: URL?): JSONObject? {
            var jsonObject: JSONObject? = null
            var urlConnection: URLConnection? = null
            try {
                urlConnection = url!!.openConnection()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                BufferedInputStream(urlConnection!!.getInputStream()).use { `in` ->
                    val scanner = Scanner(`in`, StandardCharsets.UTF_8.name())
                    jsonObject = JSONObject(scanner.useDelimiter("//Z").next())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: NullPointerException) {
                e.printStackTrace()
                System.err.println("URL konnte nicht gefunden werden")
                exitProcess(-1)
            }
            return jsonObject
        }
    }
}