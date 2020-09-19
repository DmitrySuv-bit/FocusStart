package com.example.exchange_rates;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class DisplayConversionActivity extends AppCompatActivity {
    private EditText convertibleValue;
    private TextView afterConversionValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_conversion);

        TextView text = findViewById(R.id.currencyName);
        convertibleValue = findViewById(R.id.convertibleValue);
        afterConversionValue = findViewById(R.id.afterConversionValue);
        Button convertButton = findViewById(R.id.convertButton);

        Intent intent = getIntent();

        text.setText(intent.getStringExtra(MainActivity.EXTRA_NAME_CURRENCY));
        final String value = intent.getStringExtra(MainActivity.EXTRA_VALUE_CURRENCY);

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConvert(value);
            }
        });
    }

    public void startConvert(String value) {
        String convertibleValueText = convertibleValue.getText().toString();

        if (convertibleValueText.length() != 0) {
            double result = Double.parseDouble(convertibleValueText) / Double.parseDouble(value);

            afterConversionValue.setText(String.format(Locale.ENGLISH, "%.4f", result));
        } else {
            afterConversionValue.setText("Введите значение для конвертации!");
        }
    }
}