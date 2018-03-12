package com.financialplugins.cryptocurrencynavigator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.JsonObject;

import net.grandcentrix.tray.AppPreferences;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.financialplugins.cryptocurrencynavigator.R;
import com.financialplugins.cryptocurrencynavigator.adapters.RecyclerViewAdapter;
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

import static com.financialplugins.cryptocurrencynavigator.utils.Constants.DEFAULT_COINS_LIST;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    List<CryptoCurrencyItem> list;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Call<JsonObject> call;
    GetPricesClient getPricesClient;
    String currencies;
    List<CryptoCurrencyShortInfo> currList;
    Set<String> coinsSet;
    AppPreferences appPreferences;
    DrawerLayout drawer;
    NavigationView navigationView;
    String currencySymbol, currencyName, sort, sortDirection;
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(null);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);




        ActionBar.LayoutParams lp1 = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        View customNav = LayoutInflater.from(this).inflate(R.layout.actionbar, null); // layout which contains your button.

        actionBar.setCustomView(customNav, lp1);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Getting Coins Short info from local Json
        String jsonstring = Utils.loadJSONFromAsset(this,"coins.json");
        currList = Utils.getCurrencyShortlist(jsonstring);
        Log.d(TAG, "onCreate: currlist: " + currList.toString());
    }




    @Override
    protected void onResume() {
        super.onResume();
        appPreferences = new AppPreferences(this);
        currencySymbol = appPreferences.getString("currencySymbol","$");
        currencyName = appPreferences.getString("currencyName","USD");
        sort = appPreferences.getString("sort","Name");
        sortDirection = appPreferences.getString("sortDirection","Ascending");

     //   adView = (AdView) findViewById(R.id.adView);

      //  AdRequest adRequest = new AdRequest.Builder().build();
        //adView.loadAd(adRequest);

        ImageView actionbar_search_icon = (ImageView) findViewById(R.id.actionbar_search_icon);
        final ImageView actionbar_menu_icon = (ImageView) findViewById(R.id.actionbar_menu_icon);
        actionbar_search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        actionbar_menu_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawer.openDrawer(navigationView);

            }
        });

        // Swipeable RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.main_rv);
        recyclerView.setVisibility(View.GONE);
        // Layout Managers:
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        // Item Decorator:
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        recyclerView.setItemAnimator(new FadeInLeftAnimator());


        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(interceptor).readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS);
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.client(httpClient.build()).build();

        getPricesClient = retrofit.create(GetPricesClient.class);


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
    }


    /**
     * This method is called when search query is not empty
     */




    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        refreshAdapter();
    }

    public void refreshAdapter() {
        mSwipeRefreshLayout.setRefreshing(true);


            currencies = appPreferences.getString("defaultCoinsList", DEFAULT_COINS_LIST);
            if(currencies.length()<2) Toast.makeText(MainActivity.this, R.string.fav_empty, Toast.LENGTH_SHORT).show();

        // Fetching data from server
        call = getPricesClient.getCurrencies(currencies, currencyName);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                Log.d(TAG, "onResponse: response.body().toString()" + response.body());
                list = Utils.getCurrencyItem(response.body().toString(), currencies.split(","), currencyName);
                Log.d(TAG, "onResponse: list: " + list.toString());

                // Adapter:

                list = Utils.sortListByParams(list,sort,sortDirection,MainActivity.this);

                mAdapter = new RecyclerViewAdapter(MainActivity.this, list, currList);
                ((RecyclerViewAdapter) mAdapter).setMode(Attributes.Mode.Single);
                recyclerView.setAdapter(mAdapter);

        /* Listeners */
                recyclerView.setOnScrollListener(onScrollListener);
                mSwipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "onFailure: T: " + t.getMessage());
                mSwipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            // Handle the camera action
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id==R.id.action_search){
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
