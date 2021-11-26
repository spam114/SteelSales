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

import com.symbol.steelsales.Activity.CollectionViewActivity;
import com.symbol.steelsales.Application.ApplicationClass;
import com.symbol.steelsales.Interface.BaseActivityInterface;
import com.symbol.steelsales.Object.CollectionData;
import com.symbol.steelsales.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomerCollectionAdapter extends ArrayAdapter<CollectionData> implements BaseActivityInterface {

    Context context;
    int layoutRsourceId;
    ArrayList data;
    String type = "";


    public CustomerCollectionAdapter(Context context, int layoutResourceID, ArrayList data, String type) {
        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
        this.type = type;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutRsourceId, null);
        }

        final CollectionData item = (CollectionData) data.get(position);
        if (item != null) {

            TextView txtCustomerName = (TextView) row.findViewById(R.id.txtCustomerName);
            txtCustomerName.setText(item.CustomerName);

            TextView txtUnCollectionAmt = (TextView) row.findViewById(R.id.txtUnCollectionAmt);
            DecimalFormat myFormatter = new DecimalFormat("###,###");

            double dbUnCollectionAmt=Double.parseDouble(item.UnCollectionAmt);
            String strUnCollectionAmt = myFormatter.format(dbUnCollectionAmt);

            txtUnCollectionAmt.setText(strUnCollectionAmt);

            Intent i = new Intent(getContext(), CollectionViewActivity.class);
            i.putExtra("customerCode", item.CustomerCode);
            i.putExtra("customerName", item.CustomerName);
            row.setOnClickListener(v -> {
                context.startActivity(i);
            });
        }
        return row;
    }

    @Override
    public int checkTagState(String tag) {
        return 0;
    }

    @Override
    public void progressON() {
        ApplicationClass.getInstance().progressON((Activity) context, null);
    }

    @Override
    public void progressON(String message) {
        ApplicationClass.getInstance().progressON((Activity) context, message);
    }

    @Override
    public void progressOFF() {
        ApplicationClass.getInstance().progressOFF();
    }

    @Override
    public void showErrorDialog(Context context, String message, int type) {
        ApplicationClass.getInstance().showErrorDialog(context, message, type);
    }

    @Override
    public void HideKeyBoard(Context context) {

    }

    private void startProgress() {

        progressON("Loading...");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF();
            }
        }, 3500);

    }

}

