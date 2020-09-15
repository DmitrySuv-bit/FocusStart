package com.example.exchange_rates;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private Button buttonUpdate;
    private TextView textUpdated;
    private List<ItemCurrency> listItemsCurrency;
    private CustomArrayAdapter adapter;

    private Thread secondThread;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        textUpdated = findViewById(R.id.textUpdated);

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });

        init();
    }

    private void init() {
        listItemsCurrency = new ArrayList<>();

        Date currentDate = new Date();

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy / HH:mm:ss", Locale.getDefault());
        String updatedDataAndTime = dateFormat.format(currentDate);

        textUpdated.setText(updatedDataAndTime);

        adapter = new CustomArrayAdapter(this, R.layout.list_item, listItemsCurrency, getLayoutInflater());
        listView.setAdapter(adapter);

        runnable = new Runnable() {
            @Override
            public void run() {
                getRatesJson();
            }
        };

        secondThread = new Thread(runnable);
        secondThread.start();
    }

    private void getRatesJson() {
        BufferedReader br;

        try {
            URL url = new URL("https://www.cbr-xml-daily.ru/daily_json.js");

            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            int resCode = httpConn.getResponseCode();
            StringBuilder sb = new StringBuilder();
            if (resCode == HttpURLConnection.HTTP_OK) {

                br = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));


                String s;
                while ((s = br.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            }

            JSONObject jsonObject = new JSONObject(sb.toString());

            JSONObject g = jsonObject.getJSONObject("Valute");

            JSONArray c = g.names();


            if (c != null) {
                for (int i = 0; i < c.length(); ++i) {
                    ItemCurrency item = new ItemCurrency();

                    item.setCharCode((String) g.getJSONObject(c.getString(i)).get("CharCode"));
                    item.setName((String) g.getJSONObject(c.getString(i)).get("Name"));
                    item.setValue(Double.toString(g.getJSONObject(c.getString(i)).getDouble("Value")));
                    item.setPrevious(Double.toString(g.getJSONObject(c.getString(i)).getDouble("Previous")));

                    listItemsCurrency.add(item);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}