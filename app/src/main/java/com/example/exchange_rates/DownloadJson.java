package com.example.exchange_rates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class DownloadJson {
    public String getJsonText(String inputUrl) {
        StringBuilder jsonText = new StringBuilder();
        BufferedReader bufferedReader = null;
        HttpURLConnection httpConn = null;

        try {
            URL url = new URL(inputUrl);

            httpConn = (HttpURLConnection) url.openConnection();

            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            int resCode = httpConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                bufferedReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    jsonText.append(line);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (httpConn != null) {
                httpConn.disconnect();
            }
        }

        return jsonText.toString();
    }
}