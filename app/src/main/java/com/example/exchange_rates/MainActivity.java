package com.example.exchange_rates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_NAME_CURRENCY = "com.example.exchange_rates.NAME_CURRENCY";
    public static final String EXTRA_VALUE_CURRENCY = "com.example.exchange_rates.NAME_VALUE";
    public static final String URL = "https://www.cbr-xml-daily.ru/daily_json.js";
    public static final String JSON_TEXT = "com.example.exchange_rates.JSON_TEXT";
    public static final String UPDATE_TEXT = "com.example.exchange_rates.UPDATE_TEXT";
    public static final String JSON_TEXT_FILE_NAME = "json_text.txt";
    public static final String UPDATE_TEXT_FILE_NAME = "UPDETE_text.txt";

    private TextView updatedText;
    private List<ItemExchangeRates> exchangeRatesListItems;
    private TextView textCoursesDate;
    private MyArrayAdapter adapter;
    private String coursesDate;
    private String exchangeRatesText;
    private Thread secondThread;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);
        Button updateButton = findViewById(R.id.buttonUpdate);
        updatedText = findViewById(R.id.textUpdated);
        textCoursesDate = findViewById(R.id.textCoursesDate);

        exchangeRatesListItems = new ArrayList<>();


        if (savedInstanceState == null) {
            String text = readFile(JSON_TEXT_FILE_NAME);

            if (text.length() != 0) {
                updateExchangeRatesList(text);

                exchangeRatesText = text;
                updatedText.setText(readFile(UPDATE_TEXT_FILE_NAME));
            } else {
                init();
            }
        }

        initWithTimer();

        adapter = new MyArrayAdapter(this, R.layout.list_item,
                exchangeRatesListItems, getLayoutInflater());

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String exchangeRatesName = exchangeRatesListItems.get(position).getName();
                String exchangeRatesValue = exchangeRatesListItems.get(position).getValue();

                openDisplayConversion(exchangeRatesName, exchangeRatesValue);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
                writeFile(exchangeRatesText, JSON_TEXT_FILE_NAME);
                writeFile(updatedText.getText().toString(), UPDATE_TEXT_FILE_NAME);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(JSON_TEXT, exchangeRatesText);

        outState.putString(UPDATE_TEXT, updatedText.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String text = savedInstanceState.getString(JSON_TEXT);

        if (text != null) {
            updateExchangeRatesList(text);

            exchangeRatesText = text;
            updatedText.setText(savedInstanceState.getString(UPDATE_TEXT));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (exchangeRatesText != null) {
            writeFile(exchangeRatesText, JSON_TEXT_FILE_NAME);
        }

        writeFile(updatedText.getText().toString(), UPDATE_TEXT_FILE_NAME);
    }

    private void openDisplayConversion(String exchangeRatesName, String exchangeRatesValue) {
        Intent intent = new Intent(this, DisplayConversionActivity.class);

        intent.putExtra(EXTRA_NAME_CURRENCY, exchangeRatesName);
        intent.putExtra(EXTRA_VALUE_CURRENCY, exchangeRatesValue);

        startActivity(intent);
    }

    private String getCurrentTime() {
        Date currentDate = new Date();

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy / HH:mm:ss",
                Locale.getDefault());
        return dateFormat.format(currentDate);
    }

    private void initWithTimer() {
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                init();
                initWithTimer();

                writeFile(exchangeRatesText, JSON_TEXT_FILE_NAME);
                writeFile(updatedText.getText().toString(), UPDATE_TEXT_FILE_NAME);
            }
        }.start();
    }

    private void init() {
        if (checkInternetConnection()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    DownloadJson downloadJson = new DownloadJson();

                    exchangeRatesText = downloadJson.getJsonText(URL);

                    updateExchangeRatesList(exchangeRatesText);
                }
            };
        }

        secondThread = new Thread(runnable);
        secondThread.start();

        updatedText.setText(getCurrentTime());
    }

    private void updateExchangeRatesList(final String text) {
        runnable = new Runnable() {
            @Override
            public void run() {
                ParsingJson parsingJson = new ParsingJson();

                if (exchangeRatesListItems != null) {
                    exchangeRatesListItems.clear();

                    exchangeRatesListItems.addAll(parsingJson.parseJson(text));

                    coursesDate = parsingJson.getExchangeRatesDate();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();

                        textCoursesDate.setText(coursesDate);
                    }
                });
            }
        };

        secondThread = new Thread(runnable);
        secondThread.start();
    }

    private void writeFile(String text, String nameFile) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(openFileOutput(nameFile,
                    MODE_PRIVATE)));

            bw.write(text);

            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFile(String nameFile) {
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(nameFile)));

            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
            }

            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text.toString();
    }

    private boolean checkInternetConnection() {
        ConnectivityManager connManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        if (networkInfo == null) {
            Toast.makeText(this, "No default network is currently active",
                    Toast.LENGTH_LONG).show();

            return false;
        }

        if (!networkInfo.isConnected()) {
            Toast.makeText(this, "Network is not connected",
                    Toast.LENGTH_LONG).show();

            return false;
        }

        if (!networkInfo.isAvailable()) {
            Toast.makeText(this, "Network not available",
                    Toast.LENGTH_LONG).show();

            return false;
        }

        return true;
    }
}