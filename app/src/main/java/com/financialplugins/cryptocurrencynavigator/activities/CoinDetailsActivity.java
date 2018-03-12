package com.financialplugins.cryptocurrencynavigator.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.JsonObject;
import com.wang.avi.AVLoadingIndicatorView;

import net.grandcentrix.tray.AppPreferences;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.financialplugins.cryptocurrencynavigator.R;
import com.financialplugins.cryptocurrencynavigator.models.CryptoCurrencyItem;
import com.financialplugins.cryptocurrencynavigator.models.HistoryObject;
import com.financialplugins.cryptocurrencynavigator.models.HistoryResponse;
import com.financialplugins.cryptocurrencynavigator.services.GetPricesClient;
import com.financialplugins.cryptocurrencynavigator.utils.ChartMarkerView;
import com.financialplugins.cryptocurrencynavigator.utils.Constants;
import com.financialplugins.cryptocurrencynavigator.utils.Utils;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.financialplugins.cryptocurrencynavigator.utils.Constants.DEFAULT_COINS_LIST;

public class CoinDetailsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "CoinDetailsActivity";
    String coin, coinFullName, str_lastmarket_value;
    double str_coin_change24, str_coin_percents, str_coin_sign, str_coin_price, str_lastupdate_value, str_marketcup_value,
            str_supply_value, str_volume24hours_ccy_value, str_volume24hours_value,
            str_low_value, str_HIGH_value, str_open_value;
    LineChart chart;
    Call<HistoryResponse> call;
    Call<HistoryResponse> call2;
    Call<JsonObject> callRefresh;
    List<HistoryObject> historyObjectsList;
    List<CryptoCurrencyItem> cryptoCurrencyItemList;
    GetPricesClient getPricesClient;
    Context context;
    AVLoadingIndicatorView avi;
    ImageView coinlogo, actionbar_back_icon, actionbar_search_icon, actionbar_fav_icon;
    TextView coin_change24, coin_percents, coin_sign, coin_price_int, coin_price_double, lastmarket_value, lastupdate_value, marketcup_value, supply_value, volume24hours_ccy_value, volume24hours_value,
            low_value, HIGH_value, open_value, actionbar_coin_name;
    RadioButton r_1d, r_1w, r_1m, r_6M, r_1y, r_all, r_3m;
    long millsLustUpdate;
    AppPreferences appPreferences;
    String currencies;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ScrollView scrollview;
    String currencySymbol, currencyName;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        appPreferences.put("coin",coin);
        appPreferences.put("coinfullname",coinFullName);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        coin =  appPreferences.getString("coin",coin);
        coinFullName = appPreferences.getString("coinfullname",coinFullName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primaryColor,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                refreshAdapter();
            }
        });
        r_1d.setChecked(true);

/*
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
*/
        loadData("histohour", coin, currencyName, "24");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appPreferences = new AppPreferences(this);
        currencySymbol = appPreferences.getString("currencySymbol","$");
        Log.d(TAG, "onResume: currencySymbol: " + currencySymbol);
        currencyName = appPreferences.getString("currencyName","USD");

        // Custom ActionBar

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);


        ActionBar.LayoutParams lp1 = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        View customNav = LayoutInflater.from(this).inflate(R.layout.actionbar2, null); // layout which contains your button.

        actionBar.setCustomView(customNav, lp1);

        actionbar_back_icon = (ImageView) findViewById(R.id.actionbar_back_icon);
        actionbar_search_icon = (ImageView) findViewById(R.id.actionbar_search_icon);
        actionbar_fav_icon = (ImageView) findViewById(R.id.actionbar_fav_icon);
        actionbar_coin_name = (TextView) findViewById(R.id.actionbar_coin_name);
        scrollview = (ScrollView) findViewById(R.id.scrollview);
        scrollview.setVisibility(View.GONE);

        // Intent

        context = CoinDetailsActivity.this;
        Intent intent = getIntent();
        if (intent != null) {
            coin = intent.getExtras().getString("coin");
            coinFullName = intent.getExtras().getString("name");
            str_coin_change24 = intent.getExtras().getDouble("coin_change24");
            str_coin_percents = intent.getExtras().getDouble("coin_percents");
            str_coin_price = intent.getExtras().getDouble("coin_price");
            str_lastmarket_value = intent.getExtras().getString("lastmarket_value");
            str_lastupdate_value = intent.getExtras().getDouble("lastupdate_value");
            long secondsLastUpdate = (long) intent.getExtras().getDouble("lastupdate_value");
            millsLustUpdate = secondsLastUpdate * 1000;
            Log.d(TAG, "onCreate: millsLustUpdate: " + millsLustUpdate);
            str_marketcup_value = intent.getExtras().getDouble("marketcup_value");
            str_supply_value = intent.getExtras().getDouble("supply_value");
            str_volume24hours_ccy_value = intent.getExtras().getDouble("volume24hours_ccy_value");
            str_volume24hours_value = intent.getExtras().getDouble("volume24hours_value");
            str_low_value = intent.getExtras().getDouble("low_value");
            str_HIGH_value = intent.getExtras().getDouble("HIGH_value");
            str_open_value = intent.getExtras().getDouble("open_value");
        }



        // Views

        chart = (LineChart) findViewById(R.id.chart);
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        coinlogo = (ImageView) findViewById(R.id.coinlogo);
        coin_change24 = (TextView) findViewById(R.id.coin_change24);
        coin_percents = (TextView) findViewById(R.id.coin_percents);
        coin_sign = (TextView) findViewById(R.id.coin_sign);
        coin_price_int = (TextView) findViewById(R.id.coin_price_int);
        coin_price_double = (TextView) findViewById(R.id.coin_price_double);
        lastmarket_value = (TextView) findViewById(R.id.lastmarket_value);
        lastupdate_value = (TextView) findViewById(R.id.lastupdate_value);
        marketcup_value = (TextView) findViewById(R.id.marketcup_value);
        supply_value = (TextView) findViewById(R.id.supply_value);
        volume24hours_ccy_value = (TextView) findViewById(R.id.volume24hours_ccy_value);
        volume24hours_value = (TextView) findViewById(R.id.volume24hours_value);
        low_value = (TextView) findViewById(R.id.low_value);
        HIGH_value = (TextView) findViewById(R.id.HIGH_value);
        open_value = (TextView) findViewById(R.id.open_value);
        r_1d = (RadioButton) findViewById(R.id.r_1d);
        r_1w = (RadioButton) findViewById(R.id.r_1w);
        r_1m = (RadioButton) findViewById(R.id.r_1m);
        r_6M = (RadioButton) findViewById(R.id.r_6M);
        r_1y = (RadioButton) findViewById(R.id.r_1y);
        r_all = (RadioButton) findViewById(R.id.r_all);
        r_3m = (RadioButton) findViewById(R.id.r_3m);

        scrollview.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                chart.highlightValues(null);
                return false;
            }
        });


        chart.setVisibility(View.GONE);
        avi.setVisibility(View.VISIBLE);
        avi.show();

        // Initiate data

        actionbar_coin_name.setText(coinFullName);
        actionbar_back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        actionbar_search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SearchActivity.class);
                startActivity(intent);
            }
        });


        isCoinInFavorites();
        actionbar_fav_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCoinInFavorites()) {
                    deleteFromFav();
                } else {
                    saveTofav();
                }
            }
        });


        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');

        coin_sign.setText(currencySymbol);
        coin_change24.setText(getFormattedNumber("", str_coin_change24));
        coin_percents.setText("(" + getFormattedNumber("", str_coin_percents) + "%)");
        coin_price_int.setText(getFormattedNumber("", str_coin_price));

        final SimpleDateFormat dateformat = new SimpleDateFormat("MMM d, yyyy, hh:mm aaa", Locale.US);

        lastmarket_value.setText(str_lastmarket_value);
        lastupdate_value.setText(dateformat.format(new Date(millsLustUpdate)));
        marketcup_value.setText(getFormattedNumber2(currencySymbol, str_marketcup_value));
        supply_value.setText(getFormattedNumber2(currencySymbol, str_supply_value));
        volume24hours_ccy_value.setText(getFormattedNumber2(currencySymbol, str_volume24hours_ccy_value));
        volume24hours_value.setText(getFormattedNumber2(currencySymbol, str_volume24hours_value));
        low_value.setText(getFormattedNumber(currencySymbol, str_low_value));
        HIGH_value.setText(getFormattedNumber(currencySymbol, str_HIGH_value));
        open_value.setText(getFormattedNumber(currencySymbol, str_open_value));

        // Set image

        String pictureName = "cur_" + coin.toLowerCase();
        Log.d(TAG, "onBindViewHolder: pictureName: " + pictureName);
        int id = 0;
        if (coin.endsWith("*")) {
            id = context.getResources().getIdentifier(pictureName.replace("*", "_"), "drawable", context.getPackageName());
        } else {
            id = context.getResources().getIdentifier(pictureName, "drawable", context.getPackageName());
        }

        coinlogo.setImageDrawable(ContextCompat.getDrawable(context, id));

        // Retrofit Initiate

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(interceptor).readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS);
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.client(httpClient.build()).build();
        getPricesClient = retrofit.create(GetPricesClient.class);

        // RadioButtons listener

        View.OnClickListener radiolistener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chart.setVisibility(View.GONE);
                avi.setVisibility(View.VISIBLE);
                avi.show();
                switch (view.getId()) {
                    case R.id.r_1d:
                        loadData("histohour", coin, currencyName, "24");
                        break;
                    case R.id.r_1w:
                        loadData("histoday", coin, currencyName, "7");
                        break;
                    case R.id.r_1m:
                        loadData("histoday", coin, currencyName, "30");
                        break;
                    case R.id.r_3m:
                        loadData("histoday", coin, currencyName, "92");
                        break;
                    case R.id.r_6M:
                        loadData("histoday", coin, currencyName, "183");
                        break;
                    case R.id.r_1y:
                        loadData("histoday", coin, currencyName, "365");
                        break;
                    case R.id.r_all:
                        loadAllData("histoday", coin, currencyName, "1", "7");
                        break;
                }
            }
        };


        r_1d.setOnClickListener(radiolistener);
        r_1m.setOnClickListener(radiolistener);
        r_1w.setOnClickListener(radiolistener);
        r_1y.setOnClickListener(radiolistener);
        r_6M.setOnClickListener(radiolistener);
        r_all.setOnClickListener(radiolistener);
        r_3m.setOnClickListener(radiolistener);

        r_1d.setOnCheckedChangeListener(this);
        r_1m.setOnCheckedChangeListener(this);
        r_1w.setOnCheckedChangeListener(this);
        r_1y.setOnCheckedChangeListener(this);
        r_6M.setOnCheckedChangeListener(this);
        r_all.setOnCheckedChangeListener(this);
        r_3m.setOnCheckedChangeListener(this);


    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        refreshAdapter();
    }

    public void refreshAdapter() {
        mSwipeRefreshLayout.setRefreshing(true);

        // Fetching data from server
        callRefresh = getPricesClient.getCurrencies(coin, currencyName);
        callRefresh.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> callRefresh, Response<JsonObject> response) {
                Log.d(TAG, "onResponse: response.body().toString()" + response.body());
                cryptoCurrencyItemList = Utils.getCurrencyItem(response.body().toString(), new String[]{coin} , currencyName);
                Log.d(TAG, "onResponse: list: " + cryptoCurrencyItemList.toString());

                coin_sign.setText(currencySymbol);
                coin_change24.setText(getFormattedNumber("", Math.abs(cryptoCurrencyItemList.get(0).getPriceItem().getCHANGE24HOUR())));
                coin_percents.setText("(" + getFormattedNumber("", Math.abs(cryptoCurrencyItemList.get(0).getPriceItem().getCHANGEPCT24HOUR())) + "%)");
                coin_price_int.setText(getFormattedNumber("", cryptoCurrencyItemList.get(0).getPriceItem().getPRICE()));

// color 24
                if(cryptoCurrencyItemList.get(0).getPriceItem().getCHANGE24HOUR()<0){
                    coin_change24.setTextColor(ContextCompat.getColor(context,R.color.rounded_border_down));
                }
                else if(cryptoCurrencyItemList.get(0).getPriceItem().getCHANGE24HOUR()==0){
                    coin_change24.setTextColor(ContextCompat.getColor(context,R.color.percent_grey_color));
                }
                else{
                    coin_change24.setTextColor(ContextCompat.getColor(context,R.color.rounded_border_up));
                }

// Color percents

                if(cryptoCurrencyItemList.get(0).getPriceItem().getCHANGEPCT24HOUR()<0){
                    coin_percents.setTextColor(ContextCompat.getColor(context,R.color.rounded_border_down));
                }
                else if(cryptoCurrencyItemList.get(0).getPriceItem().getCHANGEPCT24HOUR()==0){
                    coin_percents.setTextColor(ContextCompat.getColor(context,R.color.percent_grey_color));
                }
                else{
                    coin_percents.setTextColor(ContextCompat.getColor(context,R.color.rounded_border_up));
                }



                final SimpleDateFormat dateformat = new SimpleDateFormat("MMM d, yyyy, hh:mm aaa", Locale.US);

                lastmarket_value.setText(cryptoCurrencyItemList.get(0).getPriceItem().getLASTMARKET());
                lastupdate_value.setText(dateformat.format(new Date(millsLustUpdate)));
                marketcup_value.setText(getFormattedNumber2(currencySymbol, cryptoCurrencyItemList.get(0).getPriceItem().getMKTCAP()));
                supply_value.setText(getFormattedNumber2("", cryptoCurrencyItemList.get(0).getPriceItem().getSUPPLY()));
                volume24hours_ccy_value.setText(getFormattedNumber2(currencySymbol, cryptoCurrencyItemList.get(0).getPriceItem().getVOLUME24HOURTO()));
                volume24hours_value.setText(getFormattedNumber2("", cryptoCurrencyItemList.get(0).getPriceItem().getVOLUME24HOUR()));
                low_value.setText(getFormattedNumber(currencySymbol, cryptoCurrencyItemList.get(0).getPriceItem().getLOWDAY()));
                HIGH_value.setText(getFormattedNumber(currencySymbol, cryptoCurrencyItemList.get(0).getPriceItem().getHIGHDAY()));
                open_value.setText(getFormattedNumber(currencySymbol, cryptoCurrencyItemList.get(0).getPriceItem().getOPENDAY()));

                mSwipeRefreshLayout.setRefreshing(false);
                scrollview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<JsonObject> callRefresh, Throwable t) {
                Log.d(TAG, "onFailure: T: " + t.getMessage());
                mSwipeRefreshLayout.setRefreshing(false);
                scrollview.setVisibility(View.VISIBLE);
            }
        });

    }

    public String getFormattedNumber(String currency, double value) {
        String formatted = "";
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("###,##0.00", otherSymbols);
        DecimalFormat pf4 = new DecimalFormat("0.0000", otherSymbols);
        DecimalFormat pf6 = new DecimalFormat("0.000000", otherSymbols);
        DecimalFormat pf8 = new DecimalFormat("0.00000000", otherSymbols);

        if (value >= 0.0001 && value < 0.01) formatted = String.valueOf(pf4.format(value));
        else if (value >= 0.000001 && value < 0.0001) formatted = String.valueOf(pf6.format(value));
        else if (value < 0.000001 && value >= 0.000000001)
            formatted = String.valueOf(pf8.format(value));
        else if (value == 0.0) formatted = String.valueOf(df.format(value));
        else formatted = String.valueOf(df.format(value));
        if(currency.length()>0){
            return currency + formatted;
        } else return formatted;


    }

    public String getFormattedNumber2(String currency, double value) {
        String formatted = "";
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("###,##0", otherSymbols);
        DecimalFormat pf4 = new DecimalFormat("0.0000", otherSymbols);
        DecimalFormat pf6 = new DecimalFormat("0.000000", otherSymbols);
        DecimalFormat pf8 = new DecimalFormat("0.00000000", otherSymbols);

        if (value >= 0.0001 && value < 0.01) formatted = String.valueOf(pf4.format(value));
        else if (value >= 0.000001 && value < 0.0001) formatted = String.valueOf(pf6.format(value));
        else if (value < 0.000001 && value >= 0.000000001)
            formatted = String.valueOf(pf8.format(value));
        else if (value == 0.0) formatted = String.valueOf(df.format(value));
        else formatted = String.valueOf(df.format(value));

        if(currency.length()>0){
            return currency + formatted;
        } else return formatted;

    }


    public void loadData(final String histo, String coin, String currency, String period) {
        // Fetching data from server
        call = getPricesClient.getHistoryResponse(histo, coin, currency, period);
        call.enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
                historyObjectsList = response.body().getData();
                Log.d(TAG, "onResponse: historyObjectsList: " + historyObjectsList);

                // Chart Initiate

                chart.clear();


                HistoryObject[] dataObjects = new HistoryObject[historyObjectsList.size()];
                for (int i = 0; i < historyObjectsList.size(); i++) {
                    dataObjects[i] = historyObjectsList.get(i);
                }

                List<Entry> entries = new ArrayList<Entry>();

                final SimpleDateFormat dateformat = new SimpleDateFormat("MMM d", Locale.US);
                Calendar cal = Calendar.getInstance();

                for (HistoryObject data : dataObjects) {
                    long days;
                    long hours;
                    if (histo.equals("histoday")) {
                        days = data.getTime() / 60 / 60 / 24;
                        entries.add(new Entry(days, Float.valueOf(String.valueOf(data.getClose()))));
                    } else if (histo.equals("histohour")) {
                        hours = data.getTime() / 60 / 60;
                        entries.add(new Entry(hours, Float.valueOf(String.valueOf(data.getClose()))));
                    }

                }

                final HashMap<Float, String> numMap = new HashMap<>();
                for (HistoryObject item : historyObjectsList) {
                    cal.setTimeInMillis(item.getTime());
                    long days = item.getTime() / 60 / 60 / 24;
                    long hours = item.getTime() / 60 / 60;
                    if (histo.equals("histoday")) {
                        days = item.getTime() / 60 / 60 / 24;
                        numMap.put((float) days, dateformat.format(new Date(item.getTime())));
                    } else if (histo.equals("histohour")) {
                        hours = item.getTime() / 60 / 60;
                        numMap.put((float) hours, dateformat.format(new Date(item.getTime())));
                    }

                }
                Log.d(TAG, "onResponse: Map: " + numMap);


                LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
                dataSet.setLineWidth(4);
                dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                dataSet.setCircleColor(R.color.primaryColor);
                dataSet.setValueTextSize(14f);
                dataSet.setDrawValues(false);
                dataSet.setDrawCircles(false);
                dataSet.setColors(new int[]{R.color.primaryColor}, context);
                LineData lineData = new LineData(dataSet);

                XAxis xAxis = chart.getXAxis();
                xAxis.setLabelCount(historyObjectsList.size());
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    Calendar cal2 = Calendar.getInstance();

                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        long mills = (long) value * 24 * 3600 * 1000;
                        cal2.setTimeInMillis(mills);
//                        Log.d(TAG, "getFormattedValue: Calendar.getTimeInMillis" + cal2.getTimeInMillis());
                        return dateformat.format(cal2.getTime());
                    }


                });

                // remove axis
                YAxis leftAxis = chart.getAxisLeft();
                leftAxis.setEnabled(false);
                YAxis rightAxis = chart.getAxisRight();
                rightAxis.setEnabled(false);

                xAxis.setEnabled(false);

                // hide legend
                Legend legend = chart.getLegend();
                legend.setEnabled(false);

                Description desc = new Description();
                desc.setText("");
                chart.setDescription(desc);
                chart.setNoDataText("No data from server");
                chart.setDrawGridBackground(false);
                chart.setDrawBorders(false);
                chart.setData(lineData);
                chart.setAutoScaleMinMaxEnabled(true);
                chart.setTouchEnabled(true);
                chart.setDragEnabled(true);
                chart.setScaleEnabled(true);
                chart.setPinchZoom(true);
                chart.setDoubleTapToZoomEnabled(true);
                chart.getLegend().setEnabled(false);
                chart.getAxisLeft().setDrawGridLines(false);
                IMarker marker = new ChartMarkerView(context, R.layout.marker, chart, histo);
                chart.setMarker(marker);
                chart.getXAxis().setDrawGridLines(false);
                chart.animateXY(1000, 1000);
                chart.setVisibleXRangeMaximum(historyObjectsList.size());
                chart.setViewPortOffsets(0f, 0f, 0f, 0f);
                chart.invalidate(); // refresh

                chart.setVisibility(View.VISIBLE);
                avi.hide();
                avi.setVisibility(View.GONE);


            }

            @Override
            public void onFailure(Call<HistoryResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: T: " + t.getMessage());
                chart.setVisibility(View.VISIBLE);
                avi.hide();
                avi.setVisibility(View.GONE);
            }
        });
    }


    public void loadAllData(final String histo, String coin, String currency, String alldata, String aggregate) {
        // Fetching data from server
        call2 = getPricesClient.getHistoryResponseAll(histo, coin, currency, alldata, aggregate);
        call2.enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(Call<HistoryResponse> call2, Response<HistoryResponse> response) {
                historyObjectsList = response.body().getData();
                Log.d(TAG, "onResponse: historyObjectsList: " + historyObjectsList);

                // Chart Initiate

                HistoryObject[] dataObjects = new HistoryObject[historyObjectsList.size()];
                for (int i = 0; i < historyObjectsList.size(); i++) {
                    dataObjects[i] = historyObjectsList.get(i);
                }

                List<Entry> entries = new ArrayList<Entry>();

                final SimpleDateFormat dateformat = new SimpleDateFormat("MMM d", Locale.US);
                Calendar cal = Calendar.getInstance();
                if (dataObjects.length > 0) {
                    for (HistoryObject data : dataObjects) {
                        long days;
                        long hours;
                        if (histo.equals("histoday")) {
                            days = data.getTime() / 60 / 60 / 24;
                            entries.add(new Entry(days, Float.valueOf(String.valueOf(data.getClose()))));
                        } else if (histo.equals("histohour")) {
                            hours = data.getTime() / 60 / 60;
                            entries.add(new Entry(hours, Float.valueOf(String.valueOf(data.getClose()))));
                        }

                    }

                    final HashMap<Float, String> numMap = new HashMap<>();
                    for (HistoryObject item : historyObjectsList) {
                        cal.setTimeInMillis(item.getTime());
                        long days = item.getTime() / 60 / 60 / 24;
                        long hours = item.getTime() / 60 / 60;
                        if (histo.equals("histoday")) {
                            days = item.getTime() / 60 / 60 / 24;
                            numMap.put((float) days, dateformat.format(new Date(item.getTime())));
                        } else if (histo.equals("histohour")) {
                            hours = item.getTime() / 60 / 60;
                            numMap.put((float) hours, dateformat.format(new Date(item.getTime())));
                        }

                    }
                    Log.d(TAG, "onResponse: Map: " + numMap);


                    LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
                    dataSet.setLineWidth(4);
                    dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    dataSet.setCircleColor(R.color.primaryColor);
                    dataSet.setValueTextSize(14f);
                    dataSet.setDrawValues(false);
                    dataSet.setDrawCircles(false);
                    dataSet.setColors(new int[]{R.color.primaryColor}, context);
                    LineData lineData = new LineData(dataSet);

                    XAxis xAxis = chart.getXAxis();
                    xAxis.setLabelCount(historyObjectsList.size());
                    xAxis.setValueFormatter(new IAxisValueFormatter() {
                        Calendar cal2 = Calendar.getInstance();

                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            long mills = (long) value * 24 * 3600 * 1000;
                            cal2.setTimeInMillis(mills);
//                        Log.d(TAG, "getFormattedValue: Calendar.getTimeInMillis" + cal2.getTimeInMillis());
                            return dateformat.format(cal2.getTime());
                        }


                    });

                    // remove axis
                    YAxis leftAxis = chart.getAxisLeft();
                    leftAxis.setEnabled(false);
                    YAxis rightAxis = chart.getAxisRight();
                    rightAxis.setEnabled(false);

                    xAxis.setEnabled(false);

                    // hide legend
                    Legend legend = chart.getLegend();
                    legend.setEnabled(false);

                    Description desc = new Description();
                    desc.setText("");
                    chart.setDescription(desc);
                    chart.setNoDataText("No data from server");
                    chart.setDrawGridBackground(false);
                    chart.setDrawBorders(false);
                    chart.setData(lineData);
                    chart.setAutoScaleMinMaxEnabled(true);
                    chart.setTouchEnabled(true);
                    chart.setDragEnabled(true);
                    chart.setScaleEnabled(true);
                    chart.setPinchZoom(true);
                    chart.setDoubleTapToZoomEnabled(true);
                    chart.getLegend().setEnabled(false);
                    chart.getAxisLeft().setDrawGridLines(false);
                    IMarker marker = new ChartMarkerView(context, R.layout.marker, chart, histo);
                    chart.setMarker(marker);
                    chart.getXAxis().setDrawGridLines(false);
                    chart.animateXY(1000, 1000);
//                chart.setVisibleXRangeMaximum(historyObjectsList.size());
                    chart.setViewPortOffsets(0f, 0f, 0f, 0f);
                    chart.invalidate(); // refresh
                } else {
                    chart.clear();
                    chart.invalidate();
                }


                chart.setVisibility(View.VISIBLE);
                avi.hide();
                avi.setVisibility(View.GONE);


            }

            @Override
            public void onFailure(Call<HistoryResponse> call2, Throwable t) {
                Log.d(TAG, "onFailure: T: " + t.getMessage());
                chart.setVisibility(View.VISIBLE);
                avi.hide();
                avi.setVisibility(View.GONE);
            }
        });
    }


    public boolean isCoinInFavorites() {
        currencies = appPreferences.getString("defaultCoinsList", DEFAULT_COINS_LIST);
        if (currencies.length() < 2) {
            actionbar_fav_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_action_fav));
            return false;
        }

        String[] favs = currencies.split(",");
        for (int i = 0; i < favs.length; i++) {
            Log.d(TAG, "isCoinInFavorites: COIN\fav" + coin + "\\" + favs[i]);
            if (coin.equalsIgnoreCase(favs[i])) {
                actionbar_fav_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_action_fav_yes));
                return true;
            } else {
                actionbar_fav_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_action_fav));
            }
        }
        return false;
    }


    public void saveTofav() {
        Log.d(TAG, "saveToFav: starts");
        currencies = appPreferences.getString("defaultCoinsList", DEFAULT_COINS_LIST);
        StringBuilder builder = new StringBuilder(currencies);
        if (currencies.endsWith(",")) {
            builder.append(coin);
        } else {
            builder.append(",").append(coin);
        }
        Log.d(TAG, "saveToFav: NewFav = " + builder.toString());
        appPreferences.put("defaultCoinsList", builder.toString());
        Toast.makeText(context, R.string.saved_to_fav, Toast.LENGTH_SHORT).show();
        isCoinInFavorites();
    }

    public void deleteFromFav() {
        currencies = appPreferences.getString("defaultCoinsList", DEFAULT_COINS_LIST);
        StringBuilder builder = new StringBuilder();
        String[] favs = currencies.split(",");

        for (int i = 0; i < favs.length; i++) {
            if (!coin.equalsIgnoreCase(favs[i])) {
                builder.append(favs[i]);
                if (i < favs.length - 1) {
                    builder.append(",");
                }
            }

        }

        Log.d(TAG, "deleteFromFav: New defaultCoinsList after deleting: " + builder.toString());
        appPreferences.put("defaultCoinsList", builder.toString());
        Toast.makeText(context, R.string.deleted_from_favs, Toast.LENGTH_SHORT).show();
        isCoinInFavorites();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        buttonView.setTypeface(isChecked ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
    }

}
