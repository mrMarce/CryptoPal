package com.financialplugins.cryptocurrencynavigator.models;



public class PriceItem {
    private String MARKET, FROMSYMBOL, TOSYMBOL, FLAGS, TYPE, LASTMARKET, LASTTRADEID;
    private double PRICE, LASTUPDATE, LASTVOLUME, LASTVOLUMETO , VOLUMEDAY, VOLUMEDAYTO, VOLUME24HOUR, VOLUME24HOURTO,
            OPENDAY, HIGHDAY, LOWDAY, OPEN24HOUR, HIGH24HOUR, LOW24HOUR, CHANGE24HOUR, CHANGEPCT24HOUR,SUPPLY,MKTCAP;


    public PriceItem() {
    }

    public PriceItem(String MARKET, String FROMSYMBOL, String TOSYMBOL, String FLAGS, String TYPE, String LASTMARKET, double PRICE, double LASTUPDATE, double LASTVOLUME, double LASTVOLUMETO, String LASTTRADEID, double VOLUMEDAY, double VOLUMEDAYTO, double VOLUME24HOUR, double VOLUME24HOURTO, double OPENDAY, double HIGHDAY, double LOWDAY, double OPEN24HOUR, double HIGH24HOUR, double LOW24HOUR, double CHANGE24HOUR, double CHANGEPCT24HOUR, double SUPPLY, double MKTCAP) {
        this.MARKET = MARKET;
        this.FROMSYMBOL = FROMSYMBOL;
        this.TOSYMBOL = TOSYMBOL;
        this.FLAGS = FLAGS;
        this.TYPE = TYPE;
        this.LASTMARKET = LASTMARKET;
        this.PRICE = PRICE;
        this.LASTUPDATE = LASTUPDATE;
        this.LASTVOLUME = LASTVOLUME;
        this.LASTVOLUMETO = LASTVOLUMETO;
        this.LASTTRADEID = LASTTRADEID;
        this.VOLUMEDAY = VOLUMEDAY;
        this.VOLUMEDAYTO = VOLUMEDAYTO;
        this.VOLUME24HOUR = VOLUME24HOUR;
        this.VOLUME24HOURTO = VOLUME24HOURTO;
        this.OPENDAY = OPENDAY;
        this.HIGHDAY = HIGHDAY;
        this.LOWDAY = LOWDAY;
        this.OPEN24HOUR = OPEN24HOUR;
        this.HIGH24HOUR = HIGH24HOUR;
        this.LOW24HOUR = LOW24HOUR;
        this.CHANGE24HOUR = CHANGE24HOUR;
        this.CHANGEPCT24HOUR = CHANGEPCT24HOUR;
        this.SUPPLY = SUPPLY;
        this.MKTCAP = MKTCAP;
    }

    @Override
    public String toString() {
        return "PriceItem{" +
                "MARKET='" + MARKET + '\'' +
                ", FROMSYMBOL='" + FROMSYMBOL + '\'' +
                ", TOSYMBOL='" + TOSYMBOL + '\'' +
                ", FLAGS='" + FLAGS + '\'' +
                ", TYPE='" + TYPE + '\'' +
                ", LASTMARKET='" + LASTMARKET + '\'' +
                ", PRICE=" + PRICE +
                ", LASTUPDATE=" + LASTUPDATE +
                ", LASTVOLUME=" + LASTVOLUME +
                ", LASTVOLUMETO=" + LASTVOLUMETO +
                ", LASTTRADEID=" + LASTTRADEID +
                ", VOLUMEDAY=" + VOLUMEDAY +
                ", VOLUMEDAYTO=" + VOLUMEDAYTO +
                ", VOLUME24HOUR=" + VOLUME24HOUR +
                ", VOLUME24HOURTO=" + VOLUME24HOURTO +
                ", OPENDAY=" + OPENDAY +
                ", HIGHDAY=" + HIGHDAY +
                ", LOWDAY=" + LOWDAY +
                ", OPEN24HOUR=" + OPEN24HOUR +
                ", HIGH24HOUR=" + HIGH24HOUR +
                ", LOW24HOUR=" + LOW24HOUR +
                ", CHANGE24HOUR=" + CHANGE24HOUR +
                ", CHANGEPCT24HOUR=" + CHANGEPCT24HOUR +
                ", SUPPLY=" + SUPPLY +
                ", MKTCAP=" + MKTCAP +
                '}';
    }

    public void setMARKET(String MARKET) {
        this.MARKET = MARKET;
    }

    public void setFROMSYMBOL(String FROMSYMBOL) {
        this.FROMSYMBOL = FROMSYMBOL;
    }

    public void setTOSYMBOL(String TOSYMBOL) {
        this.TOSYMBOL = TOSYMBOL;
    }

    public void setFLAGS(String FLAGS) {
        this.FLAGS = FLAGS;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public void setLASTMARKET(String LASTMARKET) {
        this.LASTMARKET = LASTMARKET;
    }

    public void setPRICE(double PRICE) {
        this.PRICE = PRICE;
    }

    public void setLASTUPDATE(double LASTUPDATE) {
        this.LASTUPDATE = LASTUPDATE;
    }

    public void setLASTVOLUME(double LASTVOLUME) {
        this.LASTVOLUME = LASTVOLUME;
    }

    public void setLASTVOLUMETO(double LASTVOLUMETO) {
        this.LASTVOLUMETO = LASTVOLUMETO;
    }

    public void setLASTTRADEID(String LASTTRADEID) {
        this.LASTTRADEID = LASTTRADEID;
    }

    public void setVOLUMEDAY(double VOLUMEDAY) {
        this.VOLUMEDAY = VOLUMEDAY;
    }

    public void setVOLUMEDAYTO(double VOLUMEDAYTO) {
        this.VOLUMEDAYTO = VOLUMEDAYTO;
    }

    public void setVOLUME24HOUR(double VOLUME24HOUR) {
        this.VOLUME24HOUR = VOLUME24HOUR;
    }

    public void setVOLUME24HOURTO(double VOLUME24HOURTO) {
        this.VOLUME24HOURTO = VOLUME24HOURTO;
    }

    public void setOPENDAY(double OPENDAY) {
        this.OPENDAY = OPENDAY;
    }

    public void setHIGHDAY(double HIGHDAY) {
        this.HIGHDAY = HIGHDAY;
    }

    public void setLOWDAY(double LOWDAY) {
        this.LOWDAY = LOWDAY;
    }

    public void setOPEN24HOUR(double OPEN24HOUR) {
        this.OPEN24HOUR = OPEN24HOUR;
    }

    public void setHIGH24HOUR(double HIGH24HOUR) {
        this.HIGH24HOUR = HIGH24HOUR;
    }

    public void setLOW24HOUR(double LOW24HOUR) {
        this.LOW24HOUR = LOW24HOUR;
    }

    public void setCHANGE24HOUR(double CHANGE24HOUR) {
        this.CHANGE24HOUR = CHANGE24HOUR;
    }

    public void setCHANGEPCT24HOUR(double CHANGEPCT24HOUR) {
        this.CHANGEPCT24HOUR = CHANGEPCT24HOUR;
    }

    public void setSUPPLY(double SUPPLY) {
        this.SUPPLY = SUPPLY;
    }

    public void setMKTCAP(double MKTCAP) {
        this.MKTCAP = MKTCAP;
    }

    public String getMARKET() {
        return MARKET;
    }

    public String getFROMSYMBOL() {
        return FROMSYMBOL;
    }

    public String getTOSYMBOL() {
        return TOSYMBOL;
    }

    public String getFLAGS() {
        return FLAGS;
    }

    public String getTYPE() {
        return TYPE;
    }

    public String getLASTMARKET() {
        return LASTMARKET;
    }

    public double getPRICE() {
        return PRICE;
    }

    public double getLASTUPDATE() {
        return LASTUPDATE;
    }

    public double getLASTVOLUME() {
        return LASTVOLUME;
    }

    public double getLASTVOLUMETO() {
        return LASTVOLUMETO;
    }

    public String getLASTTRADEID() {
        return LASTTRADEID;
    }

    public double getVOLUMEDAY() {
        return VOLUMEDAY;
    }

    public double getVOLUMEDAYTO() {
        return VOLUMEDAYTO;
    }

    public double getVOLUME24HOUR() {
        return VOLUME24HOUR;
    }

    public double getVOLUME24HOURTO() {
        return VOLUME24HOURTO;
    }

    public double getOPENDAY() {
        return OPENDAY;
    }

    public double getHIGHDAY() {
        return HIGHDAY;
    }

    public double getLOWDAY() {
        return LOWDAY;
    }

    public double getOPEN24HOUR() {
        return OPEN24HOUR;
    }

    public double getHIGH24HOUR() {
        return HIGH24HOUR;
    }

    public double getLOW24HOUR() {
        return LOW24HOUR;
    }

    public double getCHANGE24HOUR() {
        return CHANGE24HOUR;
    }

    public double getCHANGEPCT24HOUR() {
        return CHANGEPCT24HOUR;
    }

    public double getSUPPLY() {
        return SUPPLY;
    }

    public double getMKTCAP() {
        return MKTCAP;
    }
}
