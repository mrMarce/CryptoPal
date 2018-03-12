package com.financialplugins.cryptocurrencynavigator.models;



public class CurrencyItem {
    private String symbol, name, symbol_native, code,name_plural;
    private int decimal_digits, rounding;

    public CurrencyItem() {
    }

    @Override
    public String toString() {
        return code;
    }

    public CurrencyItem(String symbol, String name, String symbol_native, String code, String name_plural, int decimal_digits, int rounding) {
        this.symbol = symbol;
        this.name = name;
        this.symbol_native = symbol_native;
        this.code = code;
        this.name_plural = name_plural;
        this.decimal_digits = decimal_digits;
        this.rounding = rounding;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSymbol_native(String symbol_native) {
        this.symbol_native = symbol_native;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName_plural(String name_plural) {
        this.name_plural = name_plural;
    }

    public void setDecimal_digits(int decimal_digits) {
        this.decimal_digits = decimal_digits;
    }

    public void setRounding(int rounding) {
        this.rounding = rounding;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getSymbol_native() {
        return symbol_native;
    }

    public String getCode() {
        return code;
    }

    public String getName_plural() {
        return name_plural;
    }

    public int getDecimal_digits() {
        return decimal_digits;
    }

    public int getRounding() {
        return rounding;
    }
}
