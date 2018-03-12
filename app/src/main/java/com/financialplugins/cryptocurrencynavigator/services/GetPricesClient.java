package com.financialplugins.cryptocurrencynavigator.services;

import com.google.gson.JsonObject;

import com.financialplugins.cryptocurrencynavigator.models.HistoryResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;



public interface GetPricesClient {
    @GET("/data/pricemultifull")
    Call<JsonObject> getCurrencies(@Query("fsyms") String currencies, @Query("tsyms") String currency);

    @GET("/data/{histo}")
    Call<HistoryResponse> getHistoryResponse(@Path("histo") String histo, @Query("fsym") String currencies, @Query("tsym") String currency, @Query("limit") String limit);

    @GET("/data/{histo}")
    Call<HistoryResponse> getHistoryResponseAll(@Path("histo") String histo, @Query("fsym") String currencies, @Query("tsym") String currency, @Query("allData") String allData, @Query("aggregate") String aggregate);
}
