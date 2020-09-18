package com.example.exchange_rates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

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
    public static final String TEXT = "com.example.exchange_rates.TEXT";
    public static final String JSON_TEXT_FILE = "json_text.txt";
    public static final String UPDATE_TEXT_FILE = "UPDETE_text.txt";

    private ListView listView;
    private Button buttonUpdate;
    private TextView textUpdated;
    private List<ItemExchangeRates> listItemsExchangeRates;
    private TextView textCoursesDate;
    private CustomArrayAdapter adapter;
    private String coursesDate;
    private String jsonText;


    private Thread secondThread;
    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listView = findViewById(R.id.listView);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        textUpdated = findViewById(R.id.textUpdated);
        textCoursesDate = findViewById(R.id.textCoursesDate);

        if (savedInstanceState == null) {
            String text = readFile(JSON_TEXT_FILE);

            if (!text.equals("")) {
                parsgJson(text);

                jsonText = text;

                textUpdated.setText(readFile(UPDATE_TEXT_FILE));
            }else {
                init();
            }

        }

        listItemsExchangeRates = new ArrayList<>();
        adapter = new CustomArrayAdapter(this, R.layout.list_item, listItemsExchangeRates, getLayoutInflater());
        listView.setAdapter(adapter);


        // Обработчик на Item List view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String exchangeRatesName = listItemsExchangeRates.get(position).getName();
                String exchangeRatesValue = listItemsExchangeRates.get(position).getValue();

                openDisplayConversion(exchangeRatesName, exchangeRatesValue);
            }
        });

        //Обработчик кнопки обновить
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(TEXT, jsonText);


        String up = textUpdated.getText().toString();

        outState.putString("up", up);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String text = savedInstanceState.getString(TEXT);

        if (text != null) {
            parsgJson(text);

            jsonText = text;

            textUpdated.setText(savedInstanceState.getString("up"));
        }

    }

    //Переход к Activity display conversion
    private void openDisplayConversion(String nameExchangeRates, String exchangeRatesValue) {
        Intent intent = new Intent(this, DisplayConversionActivity.class);

        intent.putExtra(EXTRA_NAME_CURRENCY, nameExchangeRates);
        intent.putExtra(EXTRA_VALUE_CURRENCY, exchangeRatesValue);

        startActivity(intent);
    }

    private String getCurrentTime() {
        Date currentDate = new Date();

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy / HH:mm:ss", Locale.getDefault());
        return dateFormat.format(currentDate);
    }

    private void initWithTimer() {
        init();

        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                initWithTimer();
            }
        }.start();
    }

    private void init() {

        runnable = new Runnable() {
            @Override
            public void run() {
                getRatesJson();
            }
        };

        secondThread = new Thread(runnable);
        secondThread.start();

        textUpdated.setText(getCurrentTime());
    }

    private void getRatesJson() {

        try {
            DownloadJson downloadJson = new DownloadJson();

            jsonText = downloadJson.getJsonText(URL);


        } catch (IOException e) {
            e.printStackTrace();

        }

        parsgJson(jsonText);

    }

    private void parsgJson(final String text) {
        runnable = new Runnable() {
            @Override
            public void run() {
                ParsingJson parsingJson = new ParsingJson();
                if (listItemsExchangeRates != null){
                    listItemsExchangeRates.clear();
                    try {

                        listItemsExchangeRates.addAll(parsingJson.parseJson(text)) ;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    coursesDate = parsingJson.getDate();
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

   @Override
    protected void onDestroy() {
        super.onDestroy();
        if (jsonText != null) {
            writeFile(jsonText, JSON_TEXT_FILE);
        }

        writeFile(textUpdated.getText().toString(), UPDATE_TEXT_FILE);
    }

    public void writeFile(String text, String nameFile) {
        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(openFileOutput(nameFile, MODE_PRIVATE)));
            // пишем данные
            bw.write(text);
            // закрываем поток
            bw.close();
            Log.e(STORAGE_SERVICE, "Файл записан");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFile(String nameFile) {
        StringBuilder text = new StringBuilder();
        try {

            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(nameFile)));
            String line;
            // читаем содержимое
            while ((line = br.readLine()) != null) {
                text.append(line);
                Log.d(STORAGE_SERVICE, line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }


}