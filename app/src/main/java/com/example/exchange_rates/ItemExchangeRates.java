package com.example.exchange_rates;

public class ItemExchangeRates {
    private String charCode;
    private String name;
    private String value;
    private String previous;

    public ItemExchangeRates() {

    }

    public ItemExchangeRates(String charCode, String name, String value, String previous) {
        this.charCode = charCode;
        this.name = name;
        this.value = value;
        this.previous = previous;
    }

    public String getCharCode() {
        return charCode;
    }

    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }
}
