package de.luki2811.dev.inzidenzencheck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        File file = new File(getApplicationContext().getFilesDir(),MainActivity.fileNameSettings);
        Datein datei = new Datein(MainActivity.fileNameSettings);
        Switch settings_switch_0 = findViewById(R.id.settings_switch_0);
        if(file.exists()){
            try {
                JSONObject jsonObject = new JSONObject(datei.loadFromFile(this));
                settings_switch_0.setChecked(jsonObject.getBoolean("automaticPlaceInput"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else{
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("automaticPlaceInput", Boolean.TRUE);
                datei.writeInFile(jsonObject.toString(), this);
                settings_switch_0.setChecked(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void openBrowserToMyPage(View view){
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://dev.luki2811.de/coronaInzidenzen/thirt-party-licenses.html"));
            startActivity(browserIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request." + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void saveSettings(View view){
        Datein datein = new Datein(MainActivity.fileNameSettings);
        String oldJSON = datein.loadFromFile(this);

        Switch settings_switch_0 = findViewById(R.id.settings_switch_0);
        JSONObject JsonObject = null;
        try {
            JsonObject = new JSONObject(oldJSON);
            JsonObject.put("automaticPlaceInput",settings_switch_0.isChecked());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        datein.writeInFile(JsonObject.toString(), this);

        Toast.makeText(this, getString(R.string.successfully_saved), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}