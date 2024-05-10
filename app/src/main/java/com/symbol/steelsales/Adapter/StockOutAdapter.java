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

import androidx.activity.result.ActivityResult;
import androidx.core.app.ActivityOptionsCompat;

import com.symbol.steelsales.Activity.StockOutDetailActivity;
import com.symbol.steelsales.Application.ApplicationClass;
import com.symbol.steelsales.Fragment.FragmentStockOut;
import com.symbol.steelsales.Interface.BaseActivityInterface;
import com.symbol.steelsales.MainActivity2;
import com.symbol.steelsales.Object.StockOut;
import com.symbol.steelsales.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StockOutAdapter extends ArrayAdapter<StockOut> implements BaseActivityInterface {

    Context context;
    int layoutRsourceId;
    ArrayList data;
    FragmentStockOut fragmentStockOut;
    String fromDate;
   /* String lastPart;//마지막에 추가된 품목,규격
    public int lastPosition;//마지막에 변화된 행값*/
    //int adapterType;//0번instruction(지시어뎁터), 1번스캔(input어뎁터)


    public StockOutAdapter(Context context, int layoutResourceID, ArrayList data, FragmentStockOut fragmentStockOut, String fromDate) {
        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
        this.fragmentStockOut = fragmentStockOut;
        this.fromDate = fromDate;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutRsourceId, null);
        }

        final StockOut item = (StockOut) data.get(position);
        if (item != null) {
            DecimalFormat myFormatter = new DecimalFormat("###,###");
            TextView txtStockOutNo = (TextView) row.findViewById(R.id.txtStockOutNo);
            TextView txtCustomerName = (TextView) row.findViewById(R.id.txtCustomerName);
            TextView txtAmount = row.findViewById(R.id.txtAmount);
            TextView txtEmplyoeeName2 = (TextView) row.findViewById(R.id.txtEmployeeName2);
            TextView txtRemark = row.findViewById(R.id.txtRemark);

            txtStockOutNo.setText(item.StockOutNo);
            txtCustomerName.setText(item.CustomerName + "(" + item.LocationName + ")");
            txtEmplyoeeName2.setText(item.EmployeeName);

            String strOutPrice = myFormatter.format(Double.parseDouble(item.OutPrice));
            String strLogicalWeight = myFormatter.format(Double.parseDouble(item.LogicalWeight));
            txtAmount.setText(strOutPrice + " (" + strLogicalWeight + ")");
            String strResult = item.SRemark1;
            if (!item.SRemark2.equals(""))
                strResult += "(" + item.SRemark2 + ")";
            txtRemark.setText(strResult);


            row.setOnClickListener(v -> {
                Intent i = new Intent(getContext(), StockOutDetailActivity.class);
                i.putExtra("customerName", item.CustomerName);
                i.putExtra("locationName", item.LocationName);
                i.putExtra("stockOutNo", item.StockOutNo);
                i.putExtra("employeeName", item.EmployeeName);
                ((MainActivity2) (context)).startActivityResult.launch(i);
            });

           /* TextView txtPartSpecName = (TextView) row.findViewById(R.id.txtPartSpecName);
            txtPartSpecName.setText(((Location) data.get(position)).PartSpecName);

            TextView txtQty = (TextView) row.findViewById(R.id.txtQty);
            txtQty.setText(String.format("%,d", Integer.parseInt(((Stock) data.get(position)).Qty)));*/

           /* if ((item.PartCode + "-" + item.PartSpec).equals(lastPart)) {//마지막 변경된 행 강조표시
                textViewPartName.setBackgroundColor(Color.YELLOW);
                textViewPartSpecName.setBackgroundColor(Color.YELLOW);
                layoutQty.setBackgroundColor(Color.YELLOW);
                this.lastPosition = position;
            }
            else{
                textViewPartName.setBackgroundColor(Color.TRANSPARENT);
                textViewPartSpecName.setBackgroundColor(Color.TRANSPARENT);
                layoutQty.setBackgroundColor(Color.TRANSPARENT);
            }*/


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

