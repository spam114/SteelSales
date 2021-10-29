package com.symbol.steelsales.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.symbol.steelsales.Application.ApplicationClass;
import com.symbol.steelsales.Interface.BaseActivityInterface;
import com.symbol.steelsales.MyWatcher;
import com.symbol.steelsales.MyWatcher2;
import com.symbol.steelsales.Object.SaleOrder;
import com.symbol.steelsales.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SaleOrderAdapter extends ArrayAdapter<SaleOrder> implements BaseActivityInterface, Filterable {

    Context context;
    int layoutRsourceId;
    ArrayList data;
    ListView listView;

    EditText edtRemark;
    EditText edtRemark2;
    TextView txtTotal;

    TextInputEditText edtOrderQty;
    TextInputEditText edtDiscountRate;
    TextView txtOrderPrice;
    TextView txtOrderAmount;

   /* String lastPart;//마지막에 추가된 품목,규격
    public int lastPosition;//마지막에 변화된 행값*/
    //int adapterType;//0번instruction(지시어뎁터), 1번스캔(input어뎁터)

    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    //private ArrayList<Stock> filteredItemList;
    //TextView txtBadge;
    //Filter listFilter;
    int checkedQty = 0;


    public SaleOrderAdapter(Context context, int layoutResourceID, ArrayList data, ListView listView, EditText edtRemark, EditText edtRemark2, TextView txtTotal) {
        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
        this.listView=listView;
        this.edtRemark=edtRemark;
        this.edtRemark2=edtRemark2;
        this.txtTotal=txtTotal;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutRsourceId, null);

            txtOrderPrice=row.findViewById(R.id.txtOrderPrice);
            txtOrderAmount=row.findViewById(R.id.txtOrderAmount);
            edtOrderQty=row.findViewById(R.id.edtOrderQty);
            edtDiscountRate=row.findViewById(R.id.edtDiscountRate);
            edtOrderQty.addTextChangedListener(new MyWatcher(edtOrderQty, edtDiscountRate, txtOrderPrice, txtOrderAmount, listView, txtTotal, data));
            edtDiscountRate.addTextChangedListener(new MyWatcher2(edtDiscountRate, edtOrderQty, txtOrderPrice, txtOrderAmount, listView, txtTotal, data));
        }

        SaleOrder item = (SaleOrder) data.get(position);
        if (item != null) {
            //row.setTag(item);
            ImageView imvRemove;
            imvRemove= row.findViewById(R.id.imvRemove);
            TextView txtPartName=row.findViewById(R.id.txtPartName);
            TextView txtPartSpecName=row.findViewById(R.id.txtPartSpecName);
            txtOrderPrice=row.findViewById(R.id.txtOrderPrice);
            txtOrderAmount=row.findViewById(R.id.txtOrderAmount);

            txtPartName.setText(item.partName);
            txtPartSpecName.setText(item.partSpecName);

            edtOrderQty=row.findViewById(R.id.edtOrderQty);
            edtDiscountRate=row.findViewById(R.id.edtDiscountRate);

            edtOrderQty.setTag(item);
            edtOrderQty.setText(item.orderQty);

            edtDiscountRate.setTag(item);
            edtDiscountRate.setText(item.discountRate);

           /* if(item.standardPrice.equals("")){
                txtOrderPrice.setText("0");
                txtOrderAmount.setText("0");
            }
            else{
                txtOrderPrice.setText("0");
                txtOrderAmount.setText("0");
            }*/

            /*edtOrderQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.toString().equals("-"))
                        return;
                    DecimalFormat myFormatter = new DecimalFormat("###,###");

                    int orderQty=0;//주문수량
                    double discountRate=0;//할인율
                    double orderPrice=0;//주문단가
                    double orderAmount=0;//주문금액
                    if(!s.toString().equals("")){
                        orderQty=Integer.parseInt(s.toString());
                    }
                    if(!edtDiscountRate.getText().toString().equals("")){
                        discountRate=Double.parseDouble(edtDiscountRate.getText().toString());
                    }
                    orderPrice=Math.round(discountRate * Double.parseDouble(item.marketPrice)/100+Double.parseDouble(item.marketPrice));
                    orderAmount=orderQty*(int)orderPrice;

                    String strOrderPrice = myFormatter.format((int)orderPrice);
                    txtOrderPrice.setText(strOrderPrice);

                    String strOrderAmount = myFormatter.format(orderAmount);
                    txtOrderAmount.setText(strOrderAmount);

                    item.orderQty=Integer.toString(orderQty);
                    item.orderPrice=Integer.toString((int)orderPrice);
                    item.orderAmount=orderAmount;
                    item.discountRate=Integer.toString((int)discountRate);

                    double totalAmount=0;
                    for(int i=0;i<listView.getCount();i++){
                        SaleOrder item = (SaleOrder) data.get(i);
                        totalAmount+=item.orderAmount;
                    }

                    String strTotalPrice = myFormatter.format((int)totalAmount);
                    txtTotal.setText("합계: "+strTotalPrice+" 원");
                }
            });

            edtDiscountRate.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.toString().equals("-"))
                        return;
                    DecimalFormat myFormatter = new DecimalFormat("###,###");

                    int orderQty=0;//주문수량
                    double discountRate=0;//할인율
                    double orderPrice=0;//주문단가
                    double orderAmount=0;//주문금액
                    if(!edtOrderQty.getText().toString().equals("")){
                        orderQty=Integer.parseInt(edtOrderQty.getText().toString());
                    }
                    if(!s.toString().equals("")){
                        discountRate=Double.parseDouble(s.toString());
                    }
                    orderPrice=Math.round(discountRate * Double.parseDouble(item.marketPrice)/100+Double.parseDouble(item.marketPrice));
                    orderAmount=orderQty*(int)orderPrice;

                    String strOrderPrice = myFormatter.format((int)orderPrice);
                    txtOrderPrice.setText(strOrderPrice);

                    String strOrderAmount = myFormatter.format(orderAmount);
                    txtOrderAmount.setText(strOrderAmount);
                    item.orderQty=Integer.toString(orderQty);
                    item.orderPrice=Integer.toString((int)orderPrice);
                    item.orderAmount=orderAmount;
                    item.discountRate=Integer.toString((int)discountRate);

                    double totalAmount=0;
                    for(int i=0;i<listView.getCount();i++){
                        SaleOrder item = (SaleOrder) data.get(i);
                        totalAmount+=item.orderAmount;
                    }

                    String strTotalPrice = myFormatter.format((int)totalAmount);
                    txtTotal.setText("합계: "+strTotalPrice+" 원");

                    //notifyDataSetChanged();
                }


            });*/

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

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edtOrderQty.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
                edtOrderQty.clearFocus();
                edtDiscountRate.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
                edtDiscountRate.clearFocus();
                edtRemark.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                edtRemark.clearFocus();
                edtRemark2.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                edtRemark2.clearFocus();
                HideKeyBoard(context);
                return false;
            }
        });
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
        ApplicationClass.getInstance().HideKeyBoard((Activity)context);
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

