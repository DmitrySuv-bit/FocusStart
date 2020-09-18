package com.example.exchange_rates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadJson {
    public String getJsonText(String inputUrl) throws IOException {
        StringBuilder jsonText = new StringBuilder();

        URL url = new URL(inputUrl);

        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

        httpConn.setAllowUserInteraction(false);
        httpConn.setInstanceFollowRedirects(true);
        httpConn.setRequestMethod("GET");
        httpConn.connect();

        int resCode = httpConn.getResponseCode();

        if (resCode == HttpURLConnection.HTTP_OK) {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonText.append(line);
            }
        }



        return jsonText.toString();
    }
}