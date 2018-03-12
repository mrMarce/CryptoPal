package com.financialplugins.cryptocurrencynavigator.models;



public class CryptoCurrencyShortInfo {
    private String symbol, name, logo;

    public CryptoCurrencyShortInfo() {
    }

    public CryptoCurrencyShortInfo(String symbol, String name, String logo) {
        this.symbol = symbol;
        this.name = name;
        this.logo = logo;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @Override
    public String toString() {
        return "CryptoCurrencyShortInfo{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                '}';
    }
}
