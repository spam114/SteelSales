package com.symbol.steelsales.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.symbol.steelsales.Application.ApplicationClass;
import com.symbol.steelsales.Interface.BaseActivityInterface;
import com.symbol.steelsales.Object.StockOutDetail;
import com.symbol.steelsales.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StockOutDetailAdapter extends ArrayAdapter<StockOutDetail> implements BaseActivityInterface {

    Context context;
    int layoutRsourceId;
    ArrayList data;

    public StockOutDetailAdapter(Context context, int layoutResourceID, ArrayList data) {
        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutRsourceId, null);
        }

        final StockOutDetail item = (StockOutDetail) data.get(position);
        if (item != null) {
            DecimalFormat myFormatter = new DecimalFormat("###,###");
            TextView txtPart = (TextView) row.findViewById(R.id.txtPart);
            TextView txtWeight = (TextView) row.findViewById(R.id.txtWeight);
            TextView txtQty = row.findViewById(R.id.txtQty);
            TextView txtUnit = (TextView) row.findViewById(R.id.txtUnit);
            TextView txtAmount = row.findViewById(R.id.txtAmount);

            txtPart.setText(item.PartName+"("+item.PartSpecName+")");
            String strWeight = myFormatter.format(Double.parseDouble(item.Weight));
            String strQty = myFormatter.format(Double.parseDouble(item.OutQty));
            String strUnit = myFormatter.format(Double.parseDouble(item.OutUnitPrice));
            String strAmount = myFormatter.format(Double.parseDouble(item.Amount));
            txtWeight.setText(strWeight);
            txtQty.setText(strQty);
            txtUnit.setText(strUnit);
            txtAmount.setText(strAmount);
            /*row.setOnClickListener(v -> {
                Intent i = new Intent(getContext(), StockOutDetailActivity.class);
                i.putExtra("customerName", item.CustomerName);
                i.putExtra("locationName", item.LocationName);
                i.putExtra("stockOutNo", item.StockOutNo);
                i.putExtra("employeeName", item.EmployeeName);
                ((MainActivity2) (context)).startActivityResult.launch(i);
            });*/
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
    public void progressON(String message, Handler handler) {
        ApplicationClass.getInstance().progressON((Activity) context, message, handler);
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