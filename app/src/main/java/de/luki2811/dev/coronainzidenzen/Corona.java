package de.luki2811.dev.coronainzidenzen;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

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

    public Corona(String str, int type) throws JSONException {
        str.toLowerCase().replaceAll(" ", "%20").replaceAll("landkreis", "lk").replaceAll("stadtkreis", "sk").replaceAll("\u00e4", "%C3%A4").replaceAll("\u00f6", "%C3%B6").replaceAll("\u00fc", "%C3%BC");
        System.out.println(str);
        String location;
        if(type == LANDKREIS || type == STADTKREIS){
            if (type == LANDKREIS)
                location = "lk " + str;
            else
                location = "sk " + str;

            URL url = null;
            try {
                url = new URL("https://services7.arcgis.com/mOBPykOjAyBO2ZKk/arcgis/rest/services/RKI_Landkreisdaten/FeatureServer/0/query?where=county%20%3D%20'" + location + "'&outFields=cases7_per_100k,county&returnGeometry=false&returnDistinctValues=true&outSR=4326&f=json");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                JSONObject jsonObject;
                InternetRequest t = new InternetRequest(url);
                t.start();
                Thread.sleep(3000);
                jsonObject = t.getJsonObject();
                String incidenceTEMP = jsonObject.getJSONArray("features").getJSONObject(0).getJSONObject("attributes").getString("cases7_per_100k");
                this.incidence = MainActivity.round(Double.parseDouble(incidenceTEMP),2);
                this.location = jsonObject.getJSONArray("features").getJSONObject(0).getJSONObject("attributes").getString("county");
            }catch (InterruptedException | NullPointerException e){
                e.printStackTrace();
            }
        } else {
            URL url;
            location = str;
            try {
                url = new URL("https://services7.arcgis.com/mOBPykOjAyBO2ZKk/arcgis/rest/services/Coronaf%C3%A4lle_in_den_Bundesl%C3%A4ndern/FeatureServer/0/query?where=LAN_ew_GEN%20%3D%20'" + location + "'&outFields=LAN_ew_GEN,cases7_bl_per_100k&returnGeometry=false&outSR=4326&f=json");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                url = null;
            }
            try {
                JSONObject jsonObject;
                InternetRequest t = new InternetRequest(url);
                t.start();
                Thread.sleep(3000);
                jsonObject = t.getJsonObject();
                String incidenceTEMP = jsonObject.getJSONArray("features").getJSONObject(0).getJSONObject("attributes").getString("cases7_bl_per_100k");
                this.incidence = MainActivity.round(Double.parseDouble(incidenceTEMP),2);
                this.location = jsonObject.getJSONArray("features").getJSONObject(0).getJSONObject("attributes").getString("LAN_ew_GEN");

            } catch (InterruptedException | NullPointerException e) {
                e.printStackTrace();
            }
        }

    }
}




