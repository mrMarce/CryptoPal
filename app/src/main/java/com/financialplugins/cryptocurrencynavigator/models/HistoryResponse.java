package com.financialplugins.cryptocurrencynavigator.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;



public class HistoryResponse {
    @SerializedName("Response")
    @Expose
    private String response;
    @SerializedName("Type")
    @Expose
    private Integer type;
    @SerializedName("Aggregated")
    @Expose
    private Boolean aggregated;
    @SerializedName("Data")
    @Expose
    private List<HistoryObject> data = null;
    @SerializedName("TimeTo")
    @Expose
    private Long timeTo;
    @SerializedName("TimeFrom")
    @Expose
    private Long timeFrom;
    @SerializedName("FirstValueInArray")
    @Expose
    private Boolean firstValueInArray;
    @SerializedName("ConversionType")
    @Expose
    private ConversionType conversionType;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getAggregated() {
        return aggregated;
    }

    public void setAggregated(Boolean aggregated) {
        this.aggregated = aggregated;
    }

    public List<HistoryObject> getData() {
        return data;
    }

    public void setData(List<HistoryObject> data) {
        this.data = data;
    }

    public Long getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(Long timeTo) {
        this.timeTo = timeTo;
    }

    public Long getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(Long timeFrom) {
        this.timeFrom = timeFrom;
    }

    public Boolean getFirstValueInArray() {
        return firstValueInArray;
    }

    public void setFirstValueInArray(Boolean firstValueInArray) {
        this.firstValueInArray = firstValueInArray;
    }

    public ConversionType getConversionType() {
        return conversionType;
    }

    public void setConversionType(ConversionType conversionType) {
        this.conversionType = conversionType;
    }

}

