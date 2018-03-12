package com.financialplugins.cryptocurrencynavigator.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.financialplugins.cryptocurrencynavigator.R;

import static android.content.ContentValues.TAG;



public class ChartMarkerView extends MarkerView {

    private TextView tvContent;
    private int uiScreenWidth;
    private String histo;

    public ChartMarkerView(Context context, int layoutResource, Chart chart, String histo) {
        super(context, layoutResource);
        setChartView(chart);
        // find your layout components
        tvContent = (TextView) findViewById(R.id.tvContent);
        uiScreenWidth = getResources().getDisplayMetrics().widthPixels;
        this.histo = histo;
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        final SimpleDateFormat dateformat = new SimpleDateFormat("MMM d, yyyy, hh:mm aaa", Locale.US);


        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("###,##0.00", otherSymbols);
        DecimalFormat pf4 = new DecimalFormat("0.0000", otherSymbols);
        DecimalFormat pf6 = new DecimalFormat("0.000000", otherSymbols);
        DecimalFormat pf8 = new DecimalFormat("0.00000000", otherSymbols);
        
        String yValue = "";

        if(e.getY()>=0.0001 && e.getY()<0.01) yValue =String.valueOf(pf4.format(e.getY()));
        else if(e.getY()>=0.000001 && e.getY()<0.0001)yValue =String.valueOf(pf6.format(e.getY()));
        else  if(e.getY()<0.000001 && e.getY() >= 0.000000001)yValue =String.valueOf(pf8.format(e.getY()));
        else  if(e.getY()==0.0) yValue =String.valueOf(df.format(e.getY()));
        else yValue =String.valueOf(df.format(e.getY()));
        long millis = (long) e.getX();

        if(histo.equals("histoday")){
            millis = (long) e.getX() * 24 * 60 * 60 * 1000;
        } else if(histo.equals("histohour")){
            millis = (long) e.getX() * 60 * 60 * 1000;
        }


        Log.d(TAG, "refreshContent: e.getX(): " + e.getX());
        Log.d(TAG, "refreshContent: millis: " + millis);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        Date date = calendar.getTime();
        tvContent.setText(dateformat.format(date) + " : " + yValue);

        // this will perform necessary layouting
        super.refreshContent(e, highlight);
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        super.draw(canvas, posX, posY);
        getOffsetForDrawingAtPoint(posX, posY);
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {

        if(mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }

        return mOffset;
    }
}