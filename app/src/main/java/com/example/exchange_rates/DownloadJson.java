package com.example.exchange_rates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadJson {
    public String getTextJson() throws IOException {
        BufferedReader br;

        URL url = new URL("https://www.cbr-xml-daily.ru/daily_json.js");
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

        httpConn.setAllowUserInteraction(false);
        httpConn.setInstanceFollowRedirects(true);
        httpConn.setRequestMethod("GET");
        httpConn.connect();

        int resCode = httpConn.getResponseCode();

        if (resCode == HttpURLConnection.HTTP_OK) {

            br = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
                sb.append("\n");
            }

            return sb.toString();
        } else {
            return null;
        }
    }
}
