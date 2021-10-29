package com.symbol.steelsales;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.symbol.steelsales.Object.SaleOrder;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyWatcher implements TextWatcher {

    private TextInputEditText edit;

    private TextInputEditText edtDiscountRate;
    private TextView txtOrderPrice;
    private TextView txtOrderAmount;
    private ListView listView;
    private TextView txtTotal;
    private ArrayList data;


    private SaleOrder item;
    public MyWatcher(TextInputEditText edit,TextInputEditText edtDiscountRate,TextView txtOrderPrice,TextView txtOrderAmount,
                     ListView listView,TextView txtTotal, ArrayList data) {
        this.edit = edit;
        this.edtDiscountRate=edtDiscountRate;
        this.txtOrderPrice=txtOrderPrice;
        this.txtOrderAmount=txtOrderAmount;
        this.listView=listView;
        this.txtTotal=txtTotal;
        this.data=data;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //Log.d("TAG", "onTextChanged: " + s);
        this.item = (SaleOrder) edit.getTag();
        if (item != null) {
            item.orderQty = s.toString();
        }
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
        item.orderAmount=orderAmount;
        item.orderPrice=Integer.toString((int)orderPrice);
        /*item.orderQty=Integer.toString(orderQty);
        item.orderPrice=Integer.toString((int)orderPrice);
        item.orderAmount=orderAmount;*/
        //item.discountRate=Integer.toString((int)discountRate);

        double totalAmount=0;
        for(int i=0;i<listView.getCount();i++){
            SaleOrder item = (SaleOrder) data.get(i);
            totalAmount+=item.orderAmount;
        }

        String strTotalPrice = myFormatter.format((int)totalAmount);
        txtTotal.setText("합계: "+strTotalPrice+" 원");
    }
}