package com.financialplugins.cryptocurrencynavigator.activities;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.JsonObject;

import net.grandcentrix.tray.AppPreferences;

import java.util.List;

import com.financialplugins.cryptocurrencynavigator.R;
import com.financialplugins.cryptocurrencynavigator.adapters.SearchAdapter;
import com.financialplugins.cryptocurrencynavigator.models.CryptoCurrencyItem;
import com.financialplugins.cryptocurrencynavigator.models.CryptoCurrencyShortInfo;
import com.financialplugins.cryptocurrencynavigator.services.GetPricesClient;
import com.financialplugins.cryptocurrencynavigator.utils.Constants;
import com.financialplugins.cryptocurrencynavigator.utils.DividerItemDecoration;
import com.financialplugins.cryptocurrencynavigator.utils.Utils;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.financialplugins.cryptocurrencynavigator.utils.Constants.SEARCH_QUERY;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private SearchView mSearchView;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    List<CryptoCurrencyItem> list;
    Call<JsonObject> call;
    GetPricesClient getPricesClient;
    String currencies;
    List<CryptoCurrencyShortInfo> currList;
    AppPreferences appPreferences;
    String currencySymbol, currencyName, sort, sortDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appPreferences = new AppPreferences(this);
        currencySymbol = appPreferences.getString("currencySymbol","$");
        currencyName = appPreferences.getString("currencyName","USD");
        sort = appPreferences.getString("sort","Name");
        sortDirection = appPreferences.getString("sortDirection","Ascending");

        // Getting Coins Short info from local Json
        String jsonstring = Utils.loadJSONFromAsset(this,"coins.json");
        currList = Utils.getCurrencyShortlist(jsonstring);

        // Swipeable RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.search_rv);
        recyclerView.setVisibility(View.GONE);
        // Layout Managers:
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        // Item Decorator:
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        recyclerView.setItemAnimator(new FadeInLeftAnimator());

/*
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
*/
    }


    /**
     * Substitute for our onScrollListener for RecyclerView
     */
    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            Log.e("ListView", "onScrollStateChanged");
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            // Could hide open views here if you wanted. //
        }
    };


    public String findMatchCoins(String query){
        Log.d(TAG, "findMatchCoins: query: " + query);
        StringBuilder builder = new StringBuilder();
        Log.d(TAG, "findMatchCoins: starts");
        int currSize=0;
        for(CryptoCurrencyShortInfo item: currList){
            if(currSize<30){
//                Log.d(TAG, "findMatchCoins: Item = " + item.getName().toLowerCase() + " | Query = " + query.toLowerCase());
                if(item.getName().toLowerCase().contains(query.toLowerCase()) || item.getSymbol().toLowerCase().contains(query.toLowerCase())) {
                    builder.append(item.getSymbol()).append(",");
                    currSize++;
                }

            }

        }
        String queryCoinsList = builder.toString();
        Log.d(TAG, "findMatchCoins: Found Next Coins: " + builder.toString());
        if(queryCoinsList.length()>1){
            return queryCoinsList.substring(0,queryCoinsList.length()-1);
        } else{
            Toast.makeText(SearchActivity.this, R.string.nothing_found, Toast.LENGTH_SHORT).show();
            return "";
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: starts");
        getMenuInflater().inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView.setSearchableInfo(searchableInfo);
        Log.d(TAG, "onCreateOptionsMenu: " + getComponentName().toString());
        Log.d(TAG, "onCreateOptionsMenu: hint is "+ mSearchView.getQueryHint());
//        Log.d(TAG, "onCreateOptionsMenu: is " + searchableInfo.toString());
        mSearchView.setMaxWidth(Integer.MAX_VALUE);

        mSearchView.setIconified(false);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "onQueryTextSubmit: called " + s);
                AppPreferences appPreferences = new AppPreferences(SearchActivity.this);
                appPreferences.put(SEARCH_QUERY,s);
                mSearchView.clearFocus();


                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(interceptor);
                Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Constants.API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());

                Retrofit retrofit = builder.client(httpClient.build()).build();

                getPricesClient = retrofit.create(GetPricesClient.class);

                currencies = findMatchCoins(s);

                // Fetching data from server
                call = getPricesClient.getCurrencies(currencies, currencyName);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                        Log.d(TAG, "onResponse: response.body().toString()" + response.body());
                        list = Utils.getCurrencyItem(response.body().toString(), currencies.split(","), currencyName);
                        Log.d(TAG, "onResponse: list: " + list.toString());

                        list = Utils.sortListByParams(list,sort,sortDirection,SearchActivity.this);

                        // Adapter:
                        mAdapter = new SearchAdapter(SearchActivity.this, list, currList);
                        recyclerView.setAdapter(mAdapter);

        /* Listeners */
                        recyclerView.setOnScrollListener(onScrollListener);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.d(TAG, "onFailure: T: " + t.getMessage());
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });


                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();
                return false;
            }
        });

        Log.d(TAG, "onCreateOptionsMenu: returned " + true);
        return true;
    }


}