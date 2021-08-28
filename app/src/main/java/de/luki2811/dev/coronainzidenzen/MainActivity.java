package de.luki2811.dev.coronainzidenzen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    final static String fileName = "settings.json";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File file = new File(getApplicationContext().getFilesDir(),fileName);
        if(file.exists()){
            EditText inputText = findViewById(R.id.textInput);
            RadioButton radio_lk = findViewById(R.id.radioButton_landkreis);
            RadioButton radio_sk = findViewById(R.id.radioButton_stadtkreis);
            RadioButton radio_bl = findViewById(R.id.radioButton_bundesland);

            JSONObject json;
            String oldLocation = null;
            int oldType = -1;
            Datein dfile = new Datein(fileName);
            try {
                json = new JSONObject(dfile.loadFromFile(this));
                oldLocation = json.getString("oldLocation");
                oldType = json.getInt("oldType");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(oldType == Corona.LANDKREIS){
                radio_bl.setChecked(false);
                radio_lk.setChecked(true);

            }else if(oldType == Corona.STADTKREIS){
                radio_bl.setChecked(false);
                radio_sk.setChecked(true);
            }else
                radio_bl.setChecked(true);

            inputText.setText(oldLocation);
        }

    }

    public boolean availableConnection(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Rundet den übergebenen Wert auf die Anzahl der übergebenen Nachkommastellen
     *
     * @param value ist der zu rundende Wert.
     * @param decimalPoints ist die Anzahl der Nachkommastellen, auf die gerundet werden soll.
     */
    public static double round(double value, int decimalPoints) {
        double d = Math.pow(10, decimalPoints);
        return Math.round(value * d) / d;
    }

    @SuppressLint("SetTextI18n")
    public void clickedButton(View view){

        TextView output = findViewById(R.id.textOutput);
        EditText inputText = findViewById(R.id.textInput);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        TextView textView_inzidenz = findViewById(R.id.textInzidenz);

        String location = inputText.getText().toString();
        int type = -1;

        if(radioGroup.getCheckedRadioButtonId() == R.id.radioButton_bundesland)
            type = Corona.BUNDESLAND;
        if(radioGroup.getCheckedRadioButtonId() == R.id.radioButton_landkreis)
            type = Corona.LANDKREIS;
        if(radioGroup.getCheckedRadioButtonId() == R.id.radioButton_stadtkreis)
            type = Corona.STADTKREIS;

        Corona corona = null;
        if(availableConnection()){
            try {
                corona = new Corona(location, type);
                JSONObject jsonInFile = new JSONObject();
                jsonInFile.put("oldLocation", location);
                jsonInFile.put("oldType", type);
                Datein file = new Datein(fileName);
                file.writeInFile(jsonInFile.toString(), this);
            } catch (JSONException e) {
                if(type == Corona.LANDKREIS)
                    Toast.makeText(this, getResources().getString(R.string.error_cant_find_landkreis) , Toast.LENGTH_LONG).show();
                if(type == Corona.STADTKREIS)
                    Toast.makeText(this, getResources().getString(R.string.error_cant_find_stadtkreis), Toast.LENGTH_LONG).show();
                if(type == Corona.BUNDESLAND)
                    Toast.makeText(this, getResources().getString(R.string.error_cant_find_bundesland), Toast.LENGTH_LONG).show();
            }

            if (corona != null) {
                // set text
                output.setText(corona.getLocation() + " hat eine Coronainzidenz von:");
                textView_inzidenz.setText("" + corona.getIncidence() + "");
                // set color for each incidence
                if(corona.getIncidence() >= 200)
                    textView_inzidenz.setTextColor(this.getColor(R.color.DarkRed));
                else if(corona.getIncidence() >= 100 && corona.getIncidence() < 200)
                    textView_inzidenz.setTextColor(this.getColor(R.color.Red));
                else if(corona.getIncidence() >= 50 && corona.getIncidence() < 100)
                    textView_inzidenz.setTextColor(this.getColor(R.color.Orange));
                else if(corona.getIncidence() >= 25 && corona.getIncidence() < 50)
                    textView_inzidenz.setTextColor(this.getColor(R.color.Yellow));
                else if(corona.getIncidence() >= 10 && corona.getIncidence() < 25)
                    textView_inzidenz.setTextColor(this.getColor(R.color.Green));
                else if(corona.getIncidence() < 10)
                    textView_inzidenz.setTextColor(this.getColor(R.color.DarkGreen));
                else
                    textView_inzidenz.setTextColor(Color.GRAY);
            }
        }else{
            output.setText(getResources().getString(R.string.error_no_connection));
        }


    }
}