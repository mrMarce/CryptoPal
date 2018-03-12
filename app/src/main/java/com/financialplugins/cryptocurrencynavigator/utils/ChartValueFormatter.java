package com.financialplugins.cryptocurrencynavigator.utils;


import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;



public class ChartValueFormatter implements IAxisValueFormatter {
    SimpleDateFormat dateformat ;
    private DecimalFormat mFormat;

    public ChartValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return null;
    }
}
