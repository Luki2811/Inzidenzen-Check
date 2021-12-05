package de.luki2811.dev.inzidenzencheck

import android.content.Context
import org.json.JSONObject
import java.util.*

class Corona(str: String, type: Int, context: Context) {
    var location: String? = null
    var incidence = 0.0

    companion object {
        const val LANDKREIS = 0
        const val STADTKREIS = 1
        const val BUNDESLAND = 2
    }

    init {
        var str = str
        str = str.lowercase(Locale.getDefault()).trim { it <= ' ' }
        println(str)
        val location: String
        val file: Datein
        if (type == LANDKREIS || type == STADTKREIS) {
            location = if (type == LANDKREIS) "lk $str" else "sk $str"
            file = Datein(MainActivity.fileNameDataKreise)
            try {
                val jsonArray = JSONObject(file.loadFromFile(context)).getJSONArray("features")
                for (i in 0 until jsonArray.length()) {
                    if (location.equals(
                            jsonArray.getJSONObject(i).getJSONObject("attributes")
                                .getString("county"), ignoreCase = true
                        )
                    ) {
                        this.location = jsonArray.getJSONObject(i).getJSONObject("attributes")
                            .getString("county")
                        incidence = MainActivity.round(
                            jsonArray.getJSONObject(i).getJSONObject("attributes")
                                .getString("cases7_per_100k").toDouble(), 2
                        )
                    }
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        } else {
            location = str
            file = Datein(MainActivity.fileNameDataBundeslaender)
            try {
                val jsonArray = JSONObject(file.loadFromFile(context)).getJSONArray("features")
                for (i in 0 until jsonArray.length()) {
                    if (location.equals(
                            jsonArray.getJSONObject(i).getJSONObject("attributes")
                                .getString("LAN_ew_GEN"), ignoreCase = true
                        )
                    ) {
                        this.location = jsonArray.getJSONObject(i).getJSONObject("attributes")
                            .getString("LAN_ew_GEN")
                        incidence = MainActivity.round(
                            jsonArray.getJSONObject(i).getJSONObject("attributes")
                                .getString("cases7_bl_per_100k").toDouble(), 2
                        )
                    }
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }
    }
}