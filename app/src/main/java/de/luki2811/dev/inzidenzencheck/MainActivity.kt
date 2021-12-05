package de.luki2811.dev.inzidenzencheck

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import kotlin.math.pow
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getCoronaData()

        val file = File(applicationContext.filesDir, fileNameSettings)
        val datei = Datein(fileNameSettings)
        if (file.exists()) {
            val jsonTEMP: JSONObject
            var setOld = true
            try {
                jsonTEMP = JSONObject(datei.loadFromFile(this))
                setOld = jsonTEMP.getBoolean("automaticPlaceInput")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (setOld) {
                val inputText = findViewById<EditText>(R.id.editTextInput)
                val chipLK = findViewById<Chip>(R.id.chip_landkreis)
                val chipSK = findViewById<Chip>(R.id.chip_stadtkreis)
                val chipBL = findViewById<Chip>(R.id.chip_bundesland)
                var oldType = -1
                val json: JSONObject
                var oldLocation: String? = null
                val dfile = Datein(fileNameSettings)
                try {
                    json = JSONObject(dfile.loadFromFile(this))
                    oldLocation = json.getString("oldLocation")
                    oldType = json.getInt("oldType")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                when (oldType) {
                    Corona.LANDKREIS -> {
                        chipBL.isChecked = false
                        chipLK.isChecked = true
                    }
                    Corona.STADTKREIS -> {
                        chipBL.isChecked = false
                        chipSK.isChecked = true
                    }
                    else -> chipBL.isChecked = true
                }
                inputText.setText(oldLocation)
            }
        }
        refreshTextHint()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_refresh ->{
                getCoronaData()
                Toast.makeText(this, R.string.refresh_data, Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun onClickRadioButtons(view: View?) {
        val data = CoronaData(this, this)
        data.setAutoCompleteList()
        refreshTextHint()
    }

    private fun getCoronaData(){
        val data: CoronaData
        val sendButton = findViewById<Button>(R.id.button)
        sendButton.isEnabled = false
        val output = findViewById<TextView>(R.id.textOutput)
        val textInzidenz = findViewById<TextView>(R.id.textInzidenz)
        textInzidenz.text = ""
        if (isInternetAvailable()) {
            data = CoronaData(this, this)
            data.start()
            output.text = resources.getString(R.string.download_data)
        } else {
            Toast.makeText(
                this,
                resources.getString(R.string.error_cant_load_data),
                Toast.LENGTH_LONG
            ).show()
            output.text = getString(R.string.twoStrings, getText(R.string.error_no_connection), getString(R.string.error_app_restart_to_update))

        }
    }

    private fun refreshTextHint(){
        val inputText = findViewById<TextInputLayout>(R.id.textInput)
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroup)
        when(chipGroup.checkedChipId){
            findViewById<Chip>(R.id.chip_bundesland).id -> inputText.hint = getString(R.string.bundesland)
            findViewById<Chip>(R.id.chip_landkreis).id -> inputText.hint = getString(R.string.landkreis)
            findViewById<Chip>(R.id.chip_stadtkreis).id -> inputText.hint = getString(R.string.stadtkreis)
        }
    }

    private fun isInternetAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
    fun clickedButton(view: View?) {
        val output = findViewById<TextView>(R.id.textOutput)
        val inputText = findViewById<EditText>(R.id.editTextInput)
        val textViewInzidenz = findViewById<TextView>(R.id.textInzidenz)
        val location = inputText.text.toString()
        var type = -1
        val chipLK = findViewById<Chip>(R.id.chip_landkreis)
        val chipSK = findViewById<Chip>(R.id.chip_stadtkreis)
        val chipBL = findViewById<Chip>(R.id.chip_bundesland)
        if(chipLK.isChecked) type = Corona.LANDKREIS
        if(chipSK.isChecked) type = Corona.STADTKREIS
        if(chipBL.isChecked) type = Corona.BUNDESLAND
        var corona: Corona? = null
        if (isInternetAvailable()) {
            val file = Datein(fileNameSettings)
            try {
                corona = Corona(location, type, this)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (corona!!.location == null) {
                when (type) {
                    Corona.LANDKREIS -> Toast.makeText(this, resources.getString(R.string.error_cant_find_landkreis), Toast.LENGTH_LONG).show()
                    Corona.STADTKREIS -> Toast.makeText(this, resources.getString(R.string.error_cant_find_stadtkreis), Toast.LENGTH_LONG).show()
                    Corona.BUNDESLAND -> Toast.makeText(this, resources.getString(R.string.error_cant_find_bundesland), Toast.LENGTH_LONG).show()
                    else -> Toast.makeText(this, resources.getString(R.string.error_analysis), Toast.LENGTH_LONG).show()
                }
            } else {
                // set settings
                val jsonInFile: JSONObject
                val tempFile = File(applicationContext.filesDir, fileNameSettings)
                try {
                    jsonInFile =
                        if (tempFile.exists()) JSONObject(file.loadFromFile(this)) else JSONObject()
                    jsonInFile.put("oldLocation", location)
                    jsonInFile.put("oldType", type)
                    file.writeInFile(jsonInFile.toString(), this)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                // set text
                output.text = getString(R.string.result, corona.location)
                textViewInzidenz.text = corona.incidence.toString()
                // set color for each incidence
                if (corona.incidence >= 2000)
                    textViewInzidenz.setTextColor(getColor(R.color.DarkMagenta))
                else if (corona.incidence >= 1000 && corona.incidence < 2000)
                    textViewInzidenz.setTextColor(getColor(R.color.DarkViolet))
                else if (corona.incidence >= 500 && corona.incidence < 1000)
                    textViewInzidenz.setTextColor(getColor(R.color.Magenta)
                ) else if (corona.incidence >= 200 && corona.incidence < 500)
                    textViewInzidenz.setTextColor(getColor(R.color.DarkRed)
                ) else if (corona.incidence >= 100 && corona.incidence < 200)
                    textViewInzidenz.setTextColor(getColor(R.color.Red)
                ) else if (corona.incidence >= 50 && corona.incidence < 100)
                    textViewInzidenz.setTextColor(getColor(R.color.Orange)
                ) else if (corona.incidence >= 25 && corona.incidence < 50)
                    textViewInzidenz.setTextColor(getColor(R.color.Yellow)
                ) else if (corona.incidence >= 10 && corona.incidence < 25)
                    textViewInzidenz.setTextColor(getColor(R.color.Green)
                ) else if (corona.incidence < 10)
                    textViewInzidenz.setTextColor(getColor(R.color.DarkGreen))
                else
                    textViewInzidenz.setTextColor(getColor(R.color.Gray)
                )
            }
        } else {
            output.text = resources.getString(R.string.error_no_connection)
        }
    }

    companion object {
        const val fileNameSettings = "settings.json"
        const val fileNameDataKreise = "data_kreise.json"
        const val fileNameDataBundeslaender = "data_bundeslaender.json"

        /**
         * Rundet den übergebenen Wert auf die Anzahl der übergebenen Nachkommastellen
         *
         * @param value ist der zu rundende Wert.
         * @param decimalPoints ist die Anzahl der Nachkommastellen, auf die gerundet werden soll.
         */
        fun round(value: Double, decimalPoints: Int): Double {
            val d = 10.0.pow(decimalPoints.toDouble())
            return (value * d).roundToInt() / d
        }
    }
}