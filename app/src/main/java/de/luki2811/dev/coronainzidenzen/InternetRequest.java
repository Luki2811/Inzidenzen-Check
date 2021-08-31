package de.luki2811.dev.coronainzidenzen;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class InternetRequest extends Thread {

    URL url;
    JSONObject jsonObject;
    Context context;
    String fileName;

    InternetRequest(URL url, Context context, String fileName){
        setUrl(url);
        setContext(context);
        setFileName(fileName);
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }


    @Override
    public void run() {
        setJsonObject(getJSONfromURL(url));
        System.out.println("run(): " + jsonObject);
        Datein file = new Datein(fileName);
        file.writeInFile(jsonObject.toString(), context);
    }
    public static JSONObject getJSONfromURL(URL url) {
        JSONObject jsonObject = null;

        HttpURLConnection urlConnection = null;
        if (url == null){
            System.err.println("URL is null");
        }
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name());
            jsonObject = new JSONObject(scanner.useDelimiter("//Z").next());
        } catch(IOException | JSONException e){
            e.printStackTrace();
        }
        catch(NullPointerException e){
            e.printStackTrace();
            System.err.println("URL konnte nicht gefunden werden");
            System.exit(-1);
        }
        finally {
            urlConnection.disconnect();
        }
        return jsonObject;
    }
}
