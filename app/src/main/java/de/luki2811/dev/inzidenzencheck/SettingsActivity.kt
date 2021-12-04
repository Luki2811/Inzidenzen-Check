package de.luki2811.dev.inzidenzencheck

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.ActivityNotFoundException
import android.net.Uri
import android.view.View
import android.widget.Switch
import android.widget.Toast
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val file = File(applicationContext.filesDir, MainActivity.fileNameSettings)
        val datei = Datein(MainActivity.fileNameSettings)
        val settings_switch_0 = findViewById<Switch>(R.id.settings_switch_0)
        if (file.exists()) {
            try {
                val jsonObject = JSONObject(datei.loadFromFile(this))
                settings_switch_0.isChecked = jsonObject.getBoolean("automaticPlaceInput")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("automaticPlaceInput", true)
                datei.writeInFile(jsonObject.toString(), this)
                settings_switch_0.isChecked = true
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    fun openBrowserToMyPage(view: View?) {
        try {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://dev.luki2811.de/coronaInzidenzen/thirt-party-licenses.html")
            )
            startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                "No application can handle this request." + " Please install a webbrowser",
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
        }
    }

    fun saveSettings(view: View?) {
        val datein = Datein(MainActivity.fileNameSettings)
        val oldJSON = datein.loadFromFile(this)
        val settings_switch_0 = findViewById<Switch>(R.id.settings_switch_0)
        var JsonObject: JSONObject? = null
        try {
            JsonObject = JSONObject(oldJSON)
            JsonObject.put("automaticPlaceInput", settings_switch_0.isChecked)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        datein.writeInFile(JsonObject.toString(), this)
        Toast.makeText(this, getString(R.string.successfully_saved), Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}