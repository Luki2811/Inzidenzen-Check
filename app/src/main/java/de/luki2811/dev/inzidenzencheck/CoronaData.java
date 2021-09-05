package de.luki2811.dev.inzidenzencheck;

import android.app.Activity;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoronaData extends Thread{

    Context context;
    Activity activity;

    public CoronaData(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void run(){
        URL urlLand = null, urlBund = null;
        try {
            urlLand = new URL("https://services7.arcgis.com/mOBPykOjAyBO2ZKk/arcgis/rest/services/RKI_Landkreisdaten/FeatureServer/0/query?where=1%3D1&outFields=cases7_per_100k,county&returnGeometry=false&outSR=4326&f=json");
            urlBund = new URL("https://services7.arcgis.com/mOBPykOjAyBO2ZKk/arcgis/rest/services/Coronaf%C3%A4lle_in_den_Bundesl%C3%A4ndern/FeatureServer/0/query?where=1%3D1&outFields=cases7_bl_per_100k,LAN_ew_GEN&returnGeometry=false&outSR=4326&f=json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InternetRequest land = new InternetRequest(urlLand, context, MainActivity.fileNameDataKreise);
        land.start();
        InternetRequest bund = new InternetRequest(urlBund, context, MainActivity.fileNameDataBundeslaender);
        bund.start();
        try {
            bund.join();
            land.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setAutoCompleteList();
        Button sendButton = activity.findViewById(R.id.button);
        TextView output = activity.findViewById(R.id.textOutput);
        activity.runOnUiThread(() -> {
            sendButton.setEnabled(true);
            output.setText(activity.getString(R.string.click_to_start_abfrage));
        });
    }

    public void setAutoCompleteList() {
        int type = 0;
        RadioButton radio_lk = activity.findViewById(R.id.radioButton_landkreis);
        RadioButton radio_sk = activity.findViewById(R.id.radioButton_stadtkreis);
        RadioButton radio_bl = activity.findViewById(R.id.radioButton_bundesland);
        if(radio_bl.isChecked())
            type = Corona.BUNDESLAND;
        if(radio_lk.isChecked())
            type = Corona.LANDKREIS;
        if(radio_sk.isChecked())
            type = Corona.STADTKREIS;
        String[] list = null;
        System.out.println("setAuto: "+type);
        if(type == Corona.BUNDESLAND){
            Datein fileBund = new Datein(MainActivity.fileNameDataBundeslaender);
            try {
                JSONArray jsonArrayBund = new JSONObject(fileBund.loadFromFile(context)).getJSONArray("features");
                System.out.println(jsonArrayBund.toString());
                int length = jsonArrayBund.length();

                list = new String[length];
                for(int i = 0; i < jsonArrayBund.length(); i++){
                    list[i] = jsonArrayBund.getJSONObject(i).getJSONObject("attributes").getString("LAN_ew_GEN");
                }

                System.out.println(Arrays.toString(list));

                // Shows null positions
                for(int i = 0; i < length -1 ; i++){
                    if(list[i] == null){
                        System.err.println("BL null at "+ i);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(type == Corona.LANDKREIS || type == Corona.STADTKREIS){
            Datein fileLand = new Datein(MainActivity.fileNameDataKreise);
            try {
                JSONArray jsonArrayLand = new JSONObject(fileLand.loadFromFile(context)).getJSONArray("features");

                int length = jsonArrayLand.length();

                list = new String[length];
                for (int i = 0; i < jsonArrayLand.length(); i++) {
                    String SkOrLk = jsonArrayLand.getJSONObject(i).getJSONObject("attributes").getString("county");
                    if(SkOrLk.contains("SK") && type == Corona.STADTKREIS)
                        list[i] = jsonArrayLand.getJSONObject(i).getJSONObject("attributes").getString("county").replaceAll("SK ","");
                    if(SkOrLk.contains("LK") && type == Corona.LANDKREIS)
                        list[i] = jsonArrayLand.getJSONObject(i).getJSONObject("attributes").getString("county").replaceAll("LK ","");
                }

                List<String> liste = new ArrayList<>();

                for(String s : list) {
                    if(s != null && s.length() > 0) {
                        liste.add(s);
                    }
                }
                list = new String[liste.size()];
                liste.toArray(list);
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
        AutoCompleteTextView autoTextView = activity.findViewById(R.id.textInput);
        System.out.print(Arrays.toString(list));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, list);
        activity.runOnUiThread(() -> autoTextView.setAdapter(adapter));

    }

}
