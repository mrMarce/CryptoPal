package com.financialplugins.cryptocurrencynavigator.models;



public class CryptoCurrencyItem {
    public String name;
    public PriceItem priceItem;

    public CryptoCurrencyItem() {
    }

    public CryptoCurrencyItem(String name, PriceItem priceItem) {
        this.name = name;
        this.priceItem = priceItem;
    }

    @Override
    public String toString() {
        return "CryptoCurrencyItem{" +
                "name='" + name + '\'' + "\n" +
                ", priceItem=" + priceItem + "\n" +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriceItem(PriceItem priceItem) {
        this.priceItem = priceItem;
    }

    public String getName() {
        return name;
    }

    public PriceItem getPriceItem() {
        return priceItem;
    }
}
