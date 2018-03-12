package com.financialplugins.cryptocurrencynavigator.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.grandcentrix.tray.AppPreferences;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import com.financialplugins.cryptocurrencynavigator.R;
import com.financialplugins.cryptocurrencynavigator.activities.CoinDetailsActivity;
import com.financialplugins.cryptocurrencynavigator.models.CryptoCurrencyItem;
import com.financialplugins.cryptocurrencynavigator.models.CryptoCurrencyShortInfo;




public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SimpleViewHolder> {
    private static final String TAG = "SearchAdapter";
    String currencySymbol;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView curr_name, curr_full_name, curr_symbol,curr_price, curr_percents;
        ImageView curr_logo;
        LinearLayout touch_layout;


        public SimpleViewHolder(final View itemView) {
            super(itemView);
            curr_name = (TextView) itemView.findViewById(R.id.curr_name);
            curr_full_name = (TextView) itemView.findViewById(R.id.curr_full_name);
            curr_symbol = (TextView) itemView.findViewById(R.id.curr_symbol);
            curr_price = (TextView) itemView.findViewById(R.id.curr_price);
            curr_percents = (TextView) itemView.findViewById(R.id.curr_percents);
            curr_logo = (ImageView) itemView.findViewById(R.id.curr_logo);
            touch_layout = (LinearLayout) itemView.findViewById(R.id.touch_layout);

        }
    }

    private Context mContext;
    private List<CryptoCurrencyItem> mDataset;
    List<CryptoCurrencyShortInfo> currShortList;
    final AppPreferences appPreferences;
    //protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

    public SearchAdapter(Context context, List<CryptoCurrencyItem> objects, List<CryptoCurrencyShortInfo> currShortList) {
        this.mContext = context;
        this.mDataset = objects;
        this.currShortList = currShortList;
        appPreferences = new AppPreferences(context);
        currencySymbol = appPreferences.getString("currencySymbol","$");
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        final CryptoCurrencyItem item = mDataset.get(position);

        viewHolder.touch_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                     Log.d(TAG, "onClick: click");
                Intent intent = new Intent(mContext, CoinDetailsActivity.class);
                intent.putExtra("coin", item.getName());
                intent.putExtra("name", getFullName(item.getName()));
                intent.putExtra("coin_change24", item.getPriceItem().getCHANGE24HOUR());
                intent.putExtra("coin_percents", item.getPriceItem().getCHANGEPCT24HOUR());
                intent.putExtra("coin_price", item.getPriceItem().getPRICE());
                intent.putExtra("lastmarket_value", item.getPriceItem().getLASTMARKET());
                intent.putExtra("lastupdate_value", item.getPriceItem().getLASTUPDATE());
                intent.putExtra("marketcup_value", item.getPriceItem().getMKTCAP());
                intent.putExtra("supply_value", item.getPriceItem().getSUPPLY());
                intent.putExtra("volume24hours_ccy_value", item.getPriceItem().getVOLUME24HOURTO());
                intent.putExtra("volume24hours_value", item.getPriceItem().getVOLUME24HOUR());
                intent.putExtra("low_value", item.getPriceItem().getLOWDAY());
                intent.putExtra("HIGH_value", item.getPriceItem().getHIGHDAY());
                intent.putExtra("open_value", item.getPriceItem().getOPENDAY());

                mContext.startActivity(intent);
            }
        });

        viewHolder.curr_name.setText(item.getName());
        viewHolder.curr_full_name.setText(getFullName(item.getName()));


        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("###,##0.00", otherSymbols);
        DecimalFormat pf4 = new DecimalFormat("0.0000", otherSymbols);
        DecimalFormat pf6 = new DecimalFormat("0.000000", otherSymbols);
        DecimalFormat pf8 = new DecimalFormat("0.00000000", otherSymbols);

       Log.d(TAG, "onBindViewHolder: item.getPriceItem().getPRICE():" + item.getName() + " - " + item.getPriceItem().getPRICE());
        if(item.getPriceItem().getPRICE()>=0.0001 && item.getPriceItem().getPRICE()<0.01) viewHolder.curr_price.setText(String.valueOf(pf4.format(item.getPriceItem().getPRICE())));
        else if(item.getPriceItem().getPRICE()>=0.000001 && item.getPriceItem().getPRICE()<0.0001) viewHolder.curr_price.setText(String.valueOf(pf6.format(item.getPriceItem().getPRICE())));
        else  if(item.getPriceItem().getPRICE()<0.000001 && item.getPriceItem().getPRICE() >= 0.000000001) viewHolder.curr_price.setText(String.valueOf(pf8.format(item.getPriceItem().getPRICE())));
        else  if(item.getPriceItem().getPRICE()==0.0)  viewHolder.curr_price.setText(String.valueOf(df.format(item.getPriceItem().getPRICE())));
        else viewHolder.curr_price.setText(String.valueOf(df.format(item.getPriceItem().getPRICE())));

        Log.d(TAG, "onBindViewHolder: % " + item.getPriceItem().getCHANGEPCT24HOUR() + " - " + item.getName());
        if(item.getPriceItem().getCHANGEPCT24HOUR()<0){
            viewHolder.curr_percents.setTextColor(ContextCompat.getColor(mContext,R.color.rounded_border_down));
            viewHolder.curr_percents.setBackground(ContextCompat.getDrawable(mContext,R.drawable.rounded_corners_red));
            viewHolder.curr_percents.setText(String.valueOf(df.format(Math.abs(item.getPriceItem().getCHANGEPCT24HOUR()))) + "%");

        }
        else if(item.getPriceItem().getCHANGEPCT24HOUR()==0){
            viewHolder.curr_percents.setTextColor(ContextCompat.getColor(mContext,R.color.percent_grey_color));
            viewHolder.curr_percents.setBackground(ContextCompat.getDrawable(mContext,R.drawable.rounded_corners_grey));
            viewHolder.curr_percents.setText(String.valueOf(df.format(item.getPriceItem().getCHANGEPCT24HOUR())) + "%");
        }
        else{
            viewHolder.curr_percents.setTextColor(ContextCompat.getColor(mContext,R.color.rounded_border_up));
            viewHolder.curr_percents.setBackground(ContextCompat.getDrawable(mContext,R.drawable.rounded_corners));
            viewHolder.curr_percents.setText(String.valueOf(df.format(item.getPriceItem().getCHANGEPCT24HOUR())) + "%");
        }


        viewHolder.curr_symbol.setText(currencySymbol);

        String pictureName = "cur_" + item.getName().toLowerCase();
        Log.d(TAG, "onBindViewHolder: pictureName: " + pictureName);
        int id = 0;
        if(item.getName().endsWith("*")){
            id = mContext.getResources().getIdentifier(pictureName.replace("*","_"), "drawable", mContext.getPackageName());
        } else{
            id = mContext.getResources().getIdentifier(pictureName, "drawable", mContext.getPackageName());
        }

//        } else if(item.getName().equals("CASH*")){
//            id = mContext.getResources().getIdentifier("cur_cash_", "drawable", mContext.getPackageName());
//        }
//        else{
//            id = mContext.getResources().getIdentifier(pictureName, "drawable", mContext.getPackageName());
//        }


//        viewHolder.curr_logo.setImageDrawable(ContextCompat.getDrawable(mContext,id));
        Glide.with(mContext)
                .load(id)
                .into(viewHolder.curr_logo);

    }


    public String getFullName(String name){
        for(CryptoCurrencyShortInfo item : currShortList){
            if(name.equals(item.getSymbol())) return item.getName();
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}