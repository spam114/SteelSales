package com.symbol.steelsales.Adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;

import com.symbol.steelsales.Activity.CollectionViewActivity;
import com.symbol.steelsales.Activity.SearchAvailablePartActivity;
import com.symbol.steelsales.Application.ApplicationClass;
import com.symbol.steelsales.Interface.BaseActivityInterface;
import com.symbol.steelsales.Object.Location;
import com.symbol.steelsales.R;

import java.util.ArrayList;

public class CustomerLocationAdapter extends ArrayAdapter<Location> implements BaseActivityInterface {

    Context context;
    int layoutRsourceId;
    ArrayList data;
    String type="";


    public CustomerLocationAdapter(Context context, int layoutResourceID, ArrayList data, String type) {
        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
        this.type=type;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutRsourceId, null);
        }

        final Location item = (Location) data.get(position);
        if (item != null) {

            TextView txtCustomerName = (TextView) row.findViewById(R.id.txtCustomerName);
            txtCustomerName.setText(item.CustomerName+" ["+item.CustomerCode+"]");

            if(type.equals("주문관리")){
                Intent i = new Intent(getContext(), SearchAvailablePartActivity.class);
                i.putExtra("locationNo", item.LocationNo);
                i.putExtra("locationName", item.LocationName);
                i.putExtra("customerCode", item.CustomerCode);
                i.putExtra("customerName", item.CustomerName);
                row.setOnClickListener(v -> {
                    context.startActivity(i);
                });
            }
            else if(type.equals("미수금현황")){
                Intent i = new Intent(getContext(), CollectionViewActivity.class);
                i.putExtra("customerCode", item.CustomerCode);
                i.putExtra("customerName", item.CustomerName);
                row.setOnClickListener(v -> {
                    context.startActivity(i);
                });
            }



        }
        return row;
    }

    @Override
    public int checkTagState(String tag) {
        return 0;
    }

    @Override
    public void progressON() {
        ApplicationClass.getInstance().progressON((Activity)context, null);
    }

    @Override
    public void progressON(String message) {
        ApplicationClass.getInstance().progressON((Activity)context, message);
    }

    @Override
    public void progressON(String message, Handler handler) {
        ApplicationClass.getInstance().progressON((Activity)context, message, handler);
    }

    @Override
    public void progressOFF(String className) {
        ApplicationClass.getInstance().progressOFF(className);
    }

    @Override
    public void progressOFF2(String className) {
        ApplicationClass.getInstance().progressOFF2(className);
    }

    @Override
    public void showErrorDialog(Context context, String message, int type) {
        ApplicationClass.getInstance().showErrorDialog(context, message, type);
    }

    @Override
    public void HideKeyBoard(Context context) {

    }

    private void startProgress() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF2(this.getClass().getName());
            }
        }, 10000);
        progressON("Loading...", handler);
    }

}

