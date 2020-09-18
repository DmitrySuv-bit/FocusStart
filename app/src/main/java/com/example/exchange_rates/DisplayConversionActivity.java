package com.example.exchange_rates;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class DisplayConversionActivity extends AppCompatActivity {
    private TextView text;
    private EditText convertibleValue;
    private TextView afterConversionValue;
    private Button convertButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_conversion);

        text = findViewById(R.id.currencyName);
        convertibleValue = findViewById(R.id.convertibleValue);
        afterConversionValue = findViewById(R.id.afterConversionValue);
        convertButton = findViewById(R.id.convertButton);

        Intent intent = getIntent();

        String ff = intent.getStringExtra(MainActivity.EXTRA_NAME_CURRENCY);
        text.setText(ff);

        final String value = intent.getStringExtra(MainActivity.EXTRA_VALUE_CURRENCY);

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConvert(value);
            }
        });

    }

    public void startConvert(String value) {

        String ll = convertibleValue.getText().toString();

        if (!ll.equals("")) {

            double result = Double.parseDouble(ll) / Double.parseDouble(value);

            afterConversionValue.setText(String.format(Locale.ENGLISH, "%.4f", result));
        } else {
            afterConversionValue.setText("Вы не ввели значение");
        }

    }
}