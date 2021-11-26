package com.symbol.steelsales;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.symbol.steelsales.Adapter.SaleOrderAdapter;
import com.symbol.steelsales.Object.SaleOrder;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyWatcher implements TextWatcher {

    private TextInputEditText edit;

    private TextInputEditText edtDiscountRate;
    private TextView txtOrderPrice;
    private TextView txtOrderAmount;
    private SaleOrderAdapter saleOrderAdapter;
    private TextView txtTotal;
    private TextView txtTotalWeight;
    private ArrayList data;
    private SaleOrder item;
    private View row;
    TextView txtWeight;
    public MyWatcher(TextInputEditText edit,TextInputEditText edtDiscountRate,TextView txtOrderPrice,TextView txtOrderAmount,
                     SaleOrderAdapter saleOrderAdapter,TextView txtTotal, ArrayList data, View row, TextView txtWeight, TextView txtTotalWeight) {
        this.edit = edit;
        this.edtDiscountRate=edtDiscountRate;
        this.txtOrderPrice=txtOrderPrice;
        this.txtOrderAmount=txtOrderAmount;
        this.saleOrderAdapter=saleOrderAdapter;
        this.txtTotal=txtTotal;
        this.data=data;
        this.row=row;
        this.txtWeight=txtWeight;
        this.txtTotalWeight=txtTotalWeight;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //Log.d("TAG", "onTextChanged: " + s);
        this.item = (SaleOrder) edit.getTag();
        if (item != null) {
            if(!s.toString().equals(item.orderQty)){
                //row.setBackgroundColor(Color.YELLOW);
                item.isChanged=true;
            }
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
        if(!edtDiscountRate.getText().toString().equals("") && !edtDiscountRate.getText().toString().equals("-")){
            discountRate=Double.parseDouble(edtDiscountRate.getText().toString());
        }
        orderPrice=Math.round(discountRate * Double.parseDouble(item.marketPrice)/100+Double.parseDouble(item.marketPrice));
        orderAmount=orderQty*(int)orderPrice;

        if(orderPrice!=0){
            String strOrderAmount = myFormatter.format(orderAmount);
            txtOrderAmount.setText(strOrderAmount);
            item.orderAmount=orderAmount;
        }

        item.orderPrice=Integer.toString((int)orderPrice);

        item.Weight=item.logicalWeight*orderQty;
        String strWeight = myFormatter.format(item.Weight);
        txtWeight.setText(strWeight);

        /*item.orderQty=Integer.toString(orderQty);
        item.orderPrice=Integer.toString((int)orderPrice);
        item.orderAmount=orderAmount;*/
        //item.discountRate=Integer.toString((int)discountRate);

        double totalAmount=0;
        double totalWeight = 0;
        for(int i=0;i<saleOrderAdapter.getCount();i++){
            SaleOrder item = (SaleOrder) data.get(i);
            totalAmount+=item.orderAmount;
            totalWeight += item.Weight;
        }

        if(item.isChanged)
            row.setBackgroundColor(Color.parseColor("#FFF5F5DC"));
        else
            row.setBackgroundColor(Color.TRANSPARENT);

        String strTotalPrice = myFormatter.format((int)totalAmount);
        String strTotalWeight = myFormatter.format((int) totalWeight);
        txtTotal.setText("합계: "+strTotalPrice+" 원");
        txtTotalWeight.setText("중량: "+strTotalWeight+" KG");
    }
}