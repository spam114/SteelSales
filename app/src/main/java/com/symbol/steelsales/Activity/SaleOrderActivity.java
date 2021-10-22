package com.symbol.steelsales.Activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.textfield.TextInputEditText;
import com.symbol.steelsales.Adapter.AvailablePartAdapter;
import com.symbol.steelsales.Adapter.CustomerLocationAdapter;
import com.symbol.steelsales.Adapter.SaleOrderAdapter;
import com.symbol.steelsales.BackPressControl;
import com.symbol.steelsales.Object.SaleOrder;
import com.symbol.steelsales.Object.Stock;
import com.symbol.steelsales.R;
import com.symbol.steelsales.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

//주문서를 작성,수정,확정 하는 액티비티
public class SaleOrderActivity extends BaseActivity {

    ArrayList<SaleOrder> saleOrderArrayList;
    ListView listview;
    TextInputEditText edtRemark;
    TextInputEditText edtRemark2;
    BackPressControl backpressed;
    String saleOrderNo="";
    String customerCode="";
    String locationNo="";
    SaleOrderAdapter saleOrderAdapter;

    //1.앞쪽에서 선택한 데이터를 가져온다.
    //2.주문번호가 있으면, 주문번호로 DB에 저장된 데이터를 가져온다.
    //1번과 2번 데이터를 합쳐서
    //화면에 그려준다.

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saleorder);
        backpressed = new BackPressControl(this);
        listview=findViewById(R.id.listview);
        edtRemark=findViewById(R.id.edtRemark);
        edtRemark2=findViewById(R.id.edtRemark2);
        saleOrderNo=getIntent().getStringExtra("saleOrderNo");
        customerCode=getIntent().getStringExtra("customerCode");
        locationNo=getIntent().getStringExtra("locationNo");
        saleOrderArrayList=(ArrayList<SaleOrder>) getIntent().getSerializableExtra("saleOrderArrayList");
        listview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edtRemark.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                edtRemark.clearFocus();
                edtRemark2.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                edtRemark2.clearFocus();
                InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });

        getSaleOrderDetail();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    private void getSaleOrderDetail() {
        String url = getString(R.string.service_address) + "getSaleOrderDetail";
        ContentValues values = new ContentValues();
        values.put("SaleOrderNo", saleOrderNo);
        GetSaleOrderDetail gsod = new GetSaleOrderDetail(url, values);
        gsod.execute();
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

    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra("saleOrderNo",saleOrderNo);
        setResult(Activity.RESULT_OK, intent);

        //주문하지 않은 데이터는 삭제합니다. 안내 메세지
        super.onBackPressed();


    }

    public class GetSaleOrderDetail extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetSaleOrderDetail(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgress();
            //progress bar를 보여주는 등등의 행위
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }

        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다
            try {
                SaleOrder saleOrder;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                //partNameDic = new ArrayList<>();
                //partSpecNameDic = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(SaleOrderActivity.this, ErrorCheck, 2);
                        return;
                    }
                    saleOrder = new SaleOrder();
                    saleOrder.partCode = child.getString("PartCode");
                    saleOrder.partName = child.getString("PartName");
                    saleOrder.partSpec = child.getString("PartSpec");
                    saleOrder.partSpecName = child.getString("PartSpecName");
                    saleOrder.marketPrice = child.getString("MarketPrice");
                    saleOrder.orderQty= child.getString("OrderQty");
                    saleOrder.orderPrice= child.getString("OrderPrice");
                    saleOrder.standardPrice= child.getString("StandardPrice");
                    saleOrderArrayList.add(saleOrder);

                    /*if (!partNameDic.contains(stock.PartName))
                        partNameDic.add(stock.PartName);
                    if (!partSpecNameDic.contains(stock.PartSpecName))
                        partSpecNameDic.add(stock.PartSpecName);*/
                }
                saleOrderAdapter= new SaleOrderAdapter
                        (SaleOrderActivity.this, R.layout.listview_saleorder_row, saleOrderArrayList);
                listview.setAdapter(saleOrderAdapter);

              /*  partNameSequences = new CharSequence[partNameDic.size() + 1];
                partNameSequences[0] = "전체";
                for (int i = 1; i < partNameDic.size() + 1; i++) {
                    partNameSequences[i] = partNameDic.get(i - 1);
                }

               *//* partSpecNameSequences = new CharSequence[partSpecNameDic.size()+1];
                partSpecNameSequences[0] ="전체";
                for (int i = 1; i < partSpecNameDic.size()+1; i++) {
                    partSpecNameSequences[i] = partSpecNameDic.get(i-1);
                }*//*

                availablePartAdapter = new AvailablePartAdapter
                        (SearchAvailablePartActivity.this, R.layout.listview_available_part, stockArrayList, txtBadge);*/


            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF();
            }
        }
    }

}
