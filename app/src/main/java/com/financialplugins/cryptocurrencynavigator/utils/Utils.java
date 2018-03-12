package com.financialplugins.cryptocurrencynavigator.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.financialplugins.cryptocurrencynavigator.R;
import com.financialplugins.cryptocurrencynavigator.models.CryptoCurrencyItem;
import com.financialplugins.cryptocurrencynavigator.models.CryptoCurrencyShortInfo;
import com.financialplugins.cryptocurrencynavigator.models.PriceItem;



public class Utils {
    private static final String TAG = "Utils";

    public static List<CryptoCurrencyItem> sortListByParams(List<CryptoCurrencyItem> list, String sortby, final String direction, final Context context){
        String jsonstring = Utils.loadJSONFromAsset(context,"coins.json");
        final List<CryptoCurrencyShortInfo> shortList = getCurrencyShortlist(jsonstring);

       if(sortby.equals(context.getString(R.string.name))){
           Collections.sort(list, new Comparator<CryptoCurrencyItem>(){
               public int compare(CryptoCurrencyItem o1, CryptoCurrencyItem o2){
                   String fullname1 = getFullName(shortList,o1.name);
                   String fullname2 = getFullName(shortList,o2.name);

                  if(direction.equals(context.getString(R.string.asc))) return fullname1.compareToIgnoreCase(fullname2);
                   else return fullname2.compareToIgnoreCase(fullname1);
               }
           });

       } else if(sortby.equals(context.getString(R.string.symbol))){
           Collections.sort(list, new Comparator<CryptoCurrencyItem>(){
               public int compare(CryptoCurrencyItem o1, CryptoCurrencyItem o2){
                   if(direction.equals(context.getString(R.string.asc))) return o1.name.compareToIgnoreCase(o2.name);
                   else return o2.name.compareToIgnoreCase(o1.name);
               }
           });
       }
       else if(sortby.equals(context.getString(R.string.volume))){
           Collections.sort(list, new Comparator<CryptoCurrencyItem>(){
               public int compare(CryptoCurrencyItem o1, CryptoCurrencyItem o2){
                   if(o1.getPriceItem().getVOLUMEDAY() == o2.getPriceItem().getVOLUMEDAY())
                       return 0;
                   if(direction.equals(context.getString(R.string.asc)))  return o1.getPriceItem().getVOLUMEDAY() < o2.getPriceItem().getVOLUMEDAY() ? -1 : 1;
                   else return o1.getPriceItem().getVOLUMEDAY() > o2.getPriceItem().getVOLUMEDAY() ? -1 : 1;
               }
           });
       }
       else if(sortby.equals(context.getString(R.string.marketcap))){
           Collections.sort(list, new Comparator<CryptoCurrencyItem>(){
               public int compare(CryptoCurrencyItem o1, CryptoCurrencyItem o2){
                   if(o1.getPriceItem().getMKTCAP() == o2.getPriceItem().getMKTCAP())
                       return 0;
                   if(direction.equals(context.getString(R.string.asc)))  return o1.getPriceItem().getMKTCAP() < o2.getPriceItem().getMKTCAP() ? -1 : 1;
                   else return o1.getPriceItem().getMKTCAP() > o2.getPriceItem().getMKTCAP() ? -1 : 1;
               }
           });
       }
       else if(sortby.equals(context.getString(R.string.percent_change))){
           Collections.sort(list, new Comparator<CryptoCurrencyItem>(){
               public int compare(CryptoCurrencyItem o1, CryptoCurrencyItem o2){
                   if(o1.getPriceItem().getCHANGEPCT24HOUR() == o2.getPriceItem().getCHANGEPCT24HOUR())
                       return 0;
                   if(direction.equals(context.getString(R.string.asc)))  return o1.getPriceItem().getCHANGEPCT24HOUR() < o2.getPriceItem().getCHANGEPCT24HOUR() ? -1 : 1;
                   else return o1.getPriceItem().getCHANGEPCT24HOUR() > o2.getPriceItem().getCHANGEPCT24HOUR() ? -1 : 1;
               }
           });
       }
       return list;
    }

    public static String getFullName(List<CryptoCurrencyShortInfo> currShortList, String name){
        for(CryptoCurrencyShortInfo item : currShortList){
            if(name.equals(item.getSymbol())) return item.getName();
        }
        return "";
    }

    public static String deleteItemFromFav(String fav, String itemToDelete){
        Log.d(TAG, "deleteItemFromFav: starts");
        Log.d(TAG, "deleteItemFromFav: String Before deleting: " + fav);
        String[] favArray = fav.split(",");
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<favArray.length;i++){
            if(!favArray[i].equalsIgnoreCase(itemToDelete)){
                builder.append(favArray[i]);
                if(i<favArray.length-1) builder.append(",");
            }

        }

        Log.d(TAG, "deleteItemFromFav: String after deleting: " + builder.toString());
        return builder.toString();

    }

    public static List<CryptoCurrencyShortInfo> getCurrencyShortlist(String jsonString){
        List<CryptoCurrencyShortInfo> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for(int i=0;i<jsonArray.length();i++){
                CryptoCurrencyShortInfo cryptoCurrencyShortInfo = new CryptoCurrencyShortInfo();
                cryptoCurrencyShortInfo.setName(jsonArray.getJSONObject(i).getString("name"));
                cryptoCurrencyShortInfo.setLogo(jsonArray.getJSONObject(i).getString("logo"));
                cryptoCurrencyShortInfo.setSymbol(jsonArray.getJSONObject(i).getString("symbol"));
                list.add(cryptoCurrencyShortInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static String loadJSONFromAsset(Context context, String name) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(name);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static List<CryptoCurrencyItem> getCurrencyItem(String jsonString, String[] currenciesArray, String currency){
        Log.d(TAG, "getCurrencyItem: starts");
        List<CryptoCurrencyItem> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject raw = jsonObject.getJSONObject("RAW");
            for(int i=0;i<currenciesArray.length;i++){
                CryptoCurrencyItem cryptoCurrencyItem = new CryptoCurrencyItem();
                PriceItem priceItem = new PriceItem();
                JSONObject currObject = new JSONObject();
                if(!raw.isNull(currenciesArray[i])) currObject = raw.getJSONObject(currenciesArray[i]);
                cryptoCurrencyItem.setName(currenciesArray[i]);
                cryptoCurrencyItem.setPriceItem(priceItem);

                JSONObject priceObj = new JSONObject();
                if(!currObject.isNull(currency)) priceObj = currObject.getJSONObject(currency);

                if(!priceObj.isNull("MARKET")) priceItem.setMARKET(priceObj.getString("MARKET"));
                if(!priceObj.isNull("FROMSYMBOL"))  priceItem.setFROMSYMBOL(priceObj.getString("FROMSYMBOL"));
                if(!priceObj.isNull("TOSYMBOL")) priceItem.setTOSYMBOL(priceObj.getString("TOSYMBOL"));
                if(!priceObj.isNull("FLAGS")) priceItem.setFLAGS(priceObj.getString("FLAGS"));
                if(!priceObj.isNull("TYPE")) priceItem.setTYPE(priceObj.getString("TYPE"));
                if(!priceObj.isNull("LASTMARKET"))  priceItem.setLASTMARKET(priceObj.getString("LASTMARKET"));
                if(!priceObj.isNull("PRICE")) priceItem.setPRICE(priceObj.getDouble("PRICE"));
                if(!priceObj.isNull("LASTUPDATE")) priceItem.setLASTUPDATE(priceObj.getDouble("LASTUPDATE"));
                if(!priceObj.isNull("LASTVOLUME")) priceItem.setLASTVOLUME(priceObj.getDouble("LASTVOLUME"));
                if(!priceObj.isNull("LASTVOLUMETO")) priceItem.setLASTVOLUMETO(priceObj.getDouble("LASTVOLUMETO"));
                if(!priceObj.isNull("LASTTRADEID")) priceItem.setLASTTRADEID(priceObj.getString("LASTTRADEID"));
                if(!priceObj.isNull("VOLUMEDAY")) priceItem.setVOLUMEDAY(priceObj.getDouble("VOLUMEDAY"));
                if(!priceObj.isNull("VOLUMEDAYTO")) priceItem.setVOLUMEDAYTO(priceObj.getDouble("VOLUMEDAYTO"));
                if(!priceObj.isNull("VOLUME24HOUR")) priceItem.setVOLUME24HOUR(priceObj.getDouble("VOLUME24HOUR"));
                if(!priceObj.isNull("VOLUME24HOURTO")) priceItem.setVOLUME24HOURTO(priceObj.getDouble("VOLUME24HOURTO"));
                if(!priceObj.isNull("OPENDAY")) priceItem.setOPENDAY(priceObj.getDouble("OPENDAY"));
                if(!priceObj.isNull("HIGHDAY")) priceItem.setHIGHDAY(priceObj.getDouble("HIGHDAY"));
                if(!priceObj.isNull("LOWDAY")) priceItem.setLOWDAY(priceObj.getDouble("LOWDAY"));
                if(!priceObj.isNull("OPEN24HOUR")) priceItem.setOPEN24HOUR(priceObj.getDouble("OPEN24HOUR"));
                if(!priceObj.isNull("HIGH24HOUR")) priceItem.setHIGH24HOUR(priceObj.getDouble("HIGH24HOUR"));
                if(!priceObj.isNull("LOW24HOUR")) priceItem.setLOW24HOUR(priceObj.getDouble("LOW24HOUR"));
                if(!priceObj.isNull("CHANGEPCT24HOUR")) priceItem.setCHANGEPCT24HOUR(priceObj.getDouble("CHANGEPCT24HOUR"));
                if(!priceObj.isNull("SUPPLY")) priceItem.setSUPPLY(priceObj.getDouble("SUPPLY"));
                if(!priceObj.isNull("MKTCAP")) priceItem.setMKTCAP(priceObj.getDouble("MKTCAP"));
                if(!priceObj.isNull("CHANGE24HOUR")) priceItem.setCHANGE24HOUR(priceObj.getDouble("CHANGE24HOUR"));

                list.add(cryptoCurrencyItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

}
