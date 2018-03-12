package com.financialplugins.cryptocurrencynavigator.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import net.grandcentrix.tray.AppPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.financialplugins.cryptocurrencynavigator.R;
import com.financialplugins.cryptocurrencynavigator.models.CurrencyItem;
import com.financialplugins.cryptocurrencynavigator.utils.Utils;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    String jsonString;
    List<CurrencyItem> currencyItemList;
    Spinner display_curr_spinner, sort_order_spinner, sort_direction_spinner;
    List<String> sortList = new ArrayList<>();
    List<String> directionsList = new ArrayList<>();
    AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        display_curr_spinner = (Spinner) findViewById(R.id.display_curr_spinner);
        sort_order_spinner = (Spinner) findViewById(R.id.sort_order_spinner);
        sort_direction_spinner = (Spinner) findViewById(R.id.sort_direction_spinner);

        sortList.add(getString(R.string.name));
        sortList.add(getString(R.string.symbol));
        sortList.add(getString(R.string.volume));
        sortList.add(getString(R.string.marketcap));
        sortList.add(getString(R.string.percent_change));

        directionsList.add(getString(R.string.asc));
        directionsList.add(getString(R.string.desc));

        appPreferences = new AppPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        jsonString = Utils.loadJSONFromAsset(this,"currency.json");
        currencyItemList = getCurrencyItemsFromJson(jsonString);
        Log.d(TAG, "onResume: currencyItemList: " + currencyItemList);

        Collections.sort(currencyItemList, new Comparator<CurrencyItem>(){
            public int compare(CurrencyItem o1, CurrencyItem o2){
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
/*
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
*/
        // Currencies Spinner

        final ArrayAdapter<CurrencyItem> spinneradapter = new ArrayAdapter<CurrencyItem>(this, R.layout.support_simple_spinner_dropdown_item, currencyItemList);
        spinneradapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        display_curr_spinner.setAdapter(spinneradapter);
        display_curr_spinner.setPrompt("Title");
        display_curr_spinner.setSelection(getCurrencyPosition());
        display_curr_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.d(TAG, "onItemSelected: starts0");
                appPreferences.put("currencyName",currencyItemList.get(position).getCode());
                appPreferences.put("currencySymbol",currencyItemList.get(position).getSymbol());


            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        // Sort Spinner

        final ArrayAdapter<String> spinneradapter2 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, sortList);
        spinneradapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        sort_order_spinner.setAdapter(spinneradapter2);
        sort_order_spinner.setPrompt("Title");
        sort_order_spinner.setSelection(getSortPosition());
        sort_order_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.d(TAG, "onItemSelected: starts0");
                appPreferences.put("sort",sortList.get(position));



            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        // Sort Direction Spinner

        final ArrayAdapter<String> spinneradapter3 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, directionsList);
        spinneradapter3.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        sort_direction_spinner.setAdapter(spinneradapter3);
        sort_direction_spinner.setPrompt("Title");
        sort_direction_spinner.setSelection(getDirectionPosition());
        sort_direction_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.d(TAG, "onItemSelected: starts0");
                appPreferences.put("sortDirection",directionsList.get(position));

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


    }


    public int getCurrencyPosition(){
        String currName = appPreferences.getString("currencyName","USD");
        for(int i = 0;i<currencyItemList.size();i++){
            if(currName.equals(currencyItemList.get(i).getCode())) return i;
        }
         return 0;
    }

    public int getSortPosition(){
        String currName = appPreferences.getString("sort",getString(R.string.name));
        for(int i = 0;i<sortList.size();i++){
            if(currName.equals(sortList.get(i))) return i;
        }
        return 0;
    }

    public int getDirectionPosition(){
        String currName = appPreferences.getString("sortDirection",getString(R.string.asc));
        for(int i = 0;i<directionsList.size();i++){
            if(currName.equals(directionsList.get(i))) return i;
        }
        return 0;
    }

    public List<CurrencyItem> getCurrencyItemsFromJson(String jsonString){
        List<CurrencyItem> tempList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext())
            {
                // Get the key
                String key = keys.next();

                // Get the value
                JSONObject currencyObject = jsonObject.getJSONObject(key);

                CurrencyItem currencyItem = new CurrencyItem();
                currencyItem.setName(key);
                currencyItem.setSymbol(currencyObject.getString("symbol"));
                currencyItem.setSymbol_native(currencyObject.getString("symbol_native"));
                currencyItem.setCode(currencyObject.getString("code"));
                currencyItem.setName_plural(currencyObject.getString("name_plural"));
                currencyItem.setDecimal_digits(currencyObject.getInt("decimal_digits"));
                currencyItem.setRounding(currencyObject.getInt("rounding"));

                tempList.add(currencyItem);

            }

            return  tempList;


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  tempList;
    }

}
