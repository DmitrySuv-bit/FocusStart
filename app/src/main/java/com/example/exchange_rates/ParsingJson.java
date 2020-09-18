package com.example.exchange_rates;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParsingJson {
    String date;

    public String getDate() {
        return date.substring(0, 10);
    }

    public List<ItemExchangeRates> parseJson(String jsonText)  throws JSONException {
        List<ItemExchangeRates> list = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(jsonText);

        date = jsonObject.getString("Date");

        JSONObject currenciesObject = jsonObject.getJSONObject("Valute");

        JSONArray currencyNamesArray = currenciesObject.names();

        if (currencyNamesArray != null) {
            for (int i = 0; i < currencyNamesArray.length(); ++i) {
                ItemExchangeRates item = new ItemExchangeRates();

                item.setCharCode(currenciesObject.getJSONObject(currencyNamesArray.getString(i)).getString("CharCode"));
                item.setName(currenciesObject.getJSONObject(currencyNamesArray.getString(i)).getString("Name"));
                item.setValue(Double.toString(currenciesObject.getJSONObject(currencyNamesArray.getString(i)).getDouble("Value")));
                item.setPrevious(Double.toString(currenciesObject.getJSONObject(currencyNamesArray.getString(i)).getDouble("Previous")));

                list.add(item);
            }
        }
        return list;
    }
}
