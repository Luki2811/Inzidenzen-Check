package de.luki2811.dev.inzidenzencheck

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val file = File(applicationContext.filesDir, MainActivity.fileNameSettings)
        val datei = Datein(MainActivity.fileNameSettings)
        val settingsSwitchLastInput = findViewById<SwitchMaterial>(R.id.settings_switch_0)
        if (file.exists()) {
            try {
                val jsonObject = JSONObject(datei.loadFromFile(this))
                settingsSwitchLastInput.isChecked = jsonObject.getBoolean("automaticPlaceInput")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("automaticPlaceInput", true)
                datei.writeInFile(jsonObject.toString(), this)
                settingsSwitchLastInput.isChecked = true
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
        val settingsSwitchLastInput = findViewById<SwitchMaterial>(R.id.settings_switch_0)
        var jsonObject: JSONObject? = null
        try {
            jsonObject = JSONObject(oldJSON)
            jsonObject.put("automaticPlaceInput", settingsSwitchLastInput.isChecked)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        datein.writeInFile(jsonObject.toString(), this)
        Toast.makeText(this, getString(R.string.successfully_saved), Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}