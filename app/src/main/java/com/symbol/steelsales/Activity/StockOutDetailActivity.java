package com.symbol.steelsales.Activity;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.symbol.steelsales.Adapter.StockOutDetailAdapter;
import com.symbol.steelsales.Object.StockOutDetail;
import com.symbol.steelsales.R;
import com.symbol.steelsales.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StockOutDetailActivity extends BaseActivity {
    ArrayList<StockOutDetail> stockOutDetailArrayList;
    ListView listview;
    String customerName = "";
    String locationName = "";
    String stockOutNo = "";
    String employeeName = "";
    StockOutDetailAdapter stockOutDetailAdapter;
    TextView txtCustomerLocation;
    TextView txtStockOutNoEmployee;
    TextView txtTotal;
    TextView txtTotalWeight;
    LinearLayout layoutTop;
    LinearLayout layoutRefresh;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stockoutdetail);
        listview = findViewById(R.id.listview);
        customerName = getIntent().getStringExtra("customerName");
        locationName = getIntent().getStringExtra("locationName");
        stockOutNo = getIntent().getStringExtra("stockOutNo");
        employeeName = getIntent().getStringExtra("employeeName");
        txtCustomerLocation = findViewById(R.id.txtCustomerLocation);
        txtStockOutNoEmployee = findViewById(R.id.txtStockOutNoEmployee);
        txtTotal = findViewById(R.id.txtTotal);
        txtTotalWeight = findViewById(R.id.txtTotalWeight);
        layoutTop = findViewById(R.id.layoutTop);
        layoutRefresh = findViewById(R.id.layoutRefresh);
        txtCustomerLocation.setText(customerName + "(" + locationName + ")");
        txtStockOutNoEmployee.setText(stockOutNo + "(" + employeeName + ")");

        layoutRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStockOutDetail();
            }
        });
        getStockOutDetail();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }


    private void getStockOutDetail() {
        String url = getString(R.string.service_address) + "GetStockOutDetailData";
        ContentValues values = new ContentValues();
        values.put("StockOutNo", stockOutNo);
        GetStockOutDetail gsod = new GetStockOutDetail(url, values);
        gsod.execute();
    }


    public class GetStockOutDetail extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetStockOutDetail(String url, ContentValues values) {
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
            double initTotalAmt = 0;
            double initTotalWeight = 0;
            try {
                stockOutDetailArrayList = new ArrayList<>();
                StockOutDetail stockOutDetail;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(StockOutDetailActivity.this, ErrorCheck, 2);
                        return;
                    }
                    stockOutDetail = new StockOutDetail();
                    stockOutDetail.StockOutNo = child.getString("StockOutNo");
                    stockOutDetail.PartName = child.getString("PartName");
                    stockOutDetail.PartSpecName = child.getString("PartSpecName");
                    stockOutDetail.OutQty = child.getString("OutQty");
                    stockOutDetail.OutUnitPrice = child.getString("OutUnitPrice");
                    stockOutDetail.Amount = child.getString("Amount");
                    stockOutDetail.Weight = child.getString("Weight");

                    initTotalAmt += Double.parseDouble(stockOutDetail.Amount);
                    initTotalWeight += Double.parseDouble(stockOutDetail.Weight);

                    stockOutDetailArrayList.add(stockOutDetail);

                    /*if (!partNameDic.contains(stock.PartName))
                        partNameDic.add(stock.PartName);
                    if (!partSpecNameDic.contains(stock.PartSpecName))
                        partSpecNameDic.add(stock.PartSpecName);*/
                }
                stockOutDetailAdapter = new StockOutDetailAdapter
                        (StockOutDetailActivity.this, R.layout.listview_stockoutdetail_row, stockOutDetailArrayList);
                listview.setAdapter(stockOutDetailAdapter);

                DecimalFormat myFormatter = new DecimalFormat("###,###");
                String strAmount = myFormatter.format(initTotalAmt);
                String strWeight = myFormatter.format(initTotalWeight);

                txtTotal.setText(strAmount + " 원");
                txtTotalWeight.setText(strWeight + " KG");
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF2(this.getClass().getName());
            }
        }
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
