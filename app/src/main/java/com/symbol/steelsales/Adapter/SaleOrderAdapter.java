package com.symbol.steelsales.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.symbol.steelsales.Application.ApplicationClass;
import com.symbol.steelsales.Interface.BaseActivityInterface;
import com.symbol.steelsales.Object.SaleOrder;
import com.symbol.steelsales.R;

import java.util.ArrayList;

public class SaleOrderAdapter extends ArrayAdapter<SaleOrder> implements BaseActivityInterface, Filterable {

    Context context;
    int layoutRsourceId;
    ArrayList data;
   /* String lastPart;//마지막에 추가된 품목,규격
    public int lastPosition;//마지막에 변화된 행값*/
    //int adapterType;//0번instruction(지시어뎁터), 1번스캔(input어뎁터)

    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    //private ArrayList<Stock> filteredItemList;
    //TextView txtBadge;
    //Filter listFilter;
    int checkedQty = 0;


    public SaleOrderAdapter(Context context, int layoutResourceID, ArrayList data) {
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

        final SaleOrder item = (SaleOrder) data.get(position);
        if (item != null) {
            row.setTag(item);
            ImageView imvRemove;
            imvRemove= row.findViewById(R.id.imvRemove);
            TextView txtPartName=row.findViewById(R.id.txtPartName);
            TextView txtPartSpecName=row.findViewById(R.id.txtPartSpecName);
            TextInputEditText edtQty=row.findViewById(R.id.edtQty);
            TextInputEditText edtDiscountRate=row.findViewById(R.id.edtDiscountRate);
            TextView txtUnitPrice=row.findViewById(R.id.txtUnitPrice);
            TextView txtPrice=row.findViewById(R.id.txtPrice);

            txtPartName.setText(item.partName);
            txtPartSpecName.setText(item.partSpecName);

            if(item.standardPrice.equals("")){
                txtUnitPrice.setText("0");
                txtPrice.setText("0");
            }
            else{
                txtUnitPrice.setText("0");
                txtPrice.setText("0");
            }

            edtQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int orderQty=0;
                    int discountRate=0;
                    int unitPrice=0;
                    if(!s.toString().equals("")){
                        orderQty=Integer.parseInt(s.toString());
                    }
                    if(!edtDiscountRate.getText().toString().equals("")){
                        discountRate=Integer.parseInt(edtDiscountRate.getText().toString());
                    }
                    unitPrice =

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
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

