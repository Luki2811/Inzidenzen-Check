package de.luki2811.dev.coronainzidenzen;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Corona {
    final static int LANDKREIS = 0;
    final static int STADTKREIS = 1;
    final static int BUNDESLAND = 2;

    String location;
    double incidence;

    public String getLocation() {
        return location;
    }

    public double getIncidence() {
        return incidence;
    }

    public Corona(String str, int type, Context context) throws JSONException {
        str = str.toLowerCase().trim();
        System.out.println(str);
        String location;
        Datein file;
        if(type == LANDKREIS || type == STADTKREIS){
            if (type == LANDKREIS)
                location = "lk " + str;
            else
                location = "sk " + str;
            file = new Datein(MainActivity.fileNameDataKreise);
            try {
                JSONArray jsonArray = new JSONObject(file.loadFromFile(context)).getJSONArray("features");
                for (int i = 0; i < jsonArray.length(); i++){
                    if(location.equalsIgnoreCase(jsonArray.getJSONObject(i).getJSONObject("attributes").getString("county"))){
                         this.location = jsonArray.getJSONObject(i).getJSONObject("attributes").getString("county");
                         this.incidence = MainActivity.round(Double.parseDouble(jsonArray.getJSONObject(i).getJSONObject("attributes").getString("cases7_per_100k")),2);
                    }
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        } else {
            location = str;

            file = new Datein(MainActivity.fileNameDataBundeslaender);

            try {
                JSONArray jsonArray = new JSONObject(file.loadFromFile(context)).getJSONArray("features");
                for (int i = 0; i < jsonArray.length(); i++){
                    if(location.equalsIgnoreCase(jsonArray.getJSONObject(i).getJSONObject("attributes").getString("LAN_ew_GEN"))){
                        this.location = jsonArray.getJSONObject(i).getJSONObject("attributes").getString("LAN_ew_GEN");
                        this.incidence = MainActivity.round(Double.parseDouble(jsonArray.getJSONObject(i).getJSONObject("attributes").getString("cases7_bl_per_100k")),2);
                    }
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }
}




