package com.symbol.steelsales.Activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.symbol.steelsales.Adapter.SaleOrderAdapter;
import com.symbol.steelsales.BackPressControl;
import com.symbol.steelsales.Dialog.SearchAvailablePartDialog;
import com.symbol.steelsales.Dialog.SearchByKeyinDialog;
import com.symbol.steelsales.Object.SaleOrder;
import com.symbol.steelsales.Object.Users;
import com.symbol.steelsales.R;
import com.symbol.steelsales.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//주문서를 작성,수정,확정 하는 액티비티
public class SaleOrderActivity extends BaseActivity {

    ArrayList<SaleOrder> saleOrderArrayList;
    ListView listview;
    TextInputEditText edtRemark;
    TextInputEditText edtRemark2;
    BackPressControl backpressed;
    String saleOrderNo = "";
    String customerCode = "";
    String locationNo = "";
    SaleOrderAdapter saleOrderAdapter;
    TextView txtTotal;
    TextView txtSaleOrderNo;
    Button btnSave;
    Button btnConfirm;
    MaterialButtonToggleGroup toggleOrder;
    ImageView imvRefresh;
    String fixDivision = "N";

    //1.앞쪽에서 선택한 데이터를 가져온다.
    //2.주문번호가 있으면, 주문번호로 DB에 저장된 데이터를 가져온다.
    //1번과 2번 데이터를 합쳐서
    //화면에 그려준다.

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saleorder);
        backpressed = new BackPressControl(this);
        listview = findViewById(R.id.listview);
        edtRemark = findViewById(R.id.edtRemark);
        edtRemark2 = findViewById(R.id.edtRemark2);
        saleOrderNo = getIntent().getStringExtra("saleOrderNo");
        customerCode = getIntent().getStringExtra("customerCode");
        locationNo = getIntent().getStringExtra("locationNo");
        saleOrderArrayList = (ArrayList<SaleOrder>) getIntent().getSerializableExtra("saleOrderArrayList");
        txtTotal = findViewById(R.id.txtTotal);
        txtSaleOrderNo = findViewById(R.id.txtSaleOrderNo);
        txtSaleOrderNo.setText("주문번호");
        txtTotal.setText("합계: 0 원");
        toggleOrder = findViewById(R.id.toggleOrder);
        btnSave = findViewById(R.id.btnSave);
        btnConfirm = findViewById(R.id.btnConfirm);
        imvRefresh = findViewById(R.id.imvRefresh);
        if (saleOrderNo.equals("")) {
            fixDivision = "N";
            btnConfirm.setEnabled(false);
        } else {
            btnConfirm.setEnabled(true);
        }

        imvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(SaleOrderActivity.this);
                materialAlertDialogBuilder.setTitle("품목 추가");
                CharSequence[] sequences = new CharSequence[2];
                sequences[0] = "목록에서 찾기";
                sequences[1] = "검색하여 찾기";
                materialAlertDialogBuilder.setItems(sequences, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {//목록에서 찾기
                            FragmentManager fm = getSupportFragmentManager();
                            SearchAvailablePartDialog custom = new SearchAvailablePartDialog(saleOrderArrayList);
                            custom.show(fm, "");
                        } else if (which == 1) {//검색하여 찾기

                            FragmentManager fm = getSupportFragmentManager();
                            SearchByKeyinDialog custom = new SearchByKeyinDialog(saleOrderArrayList);
                            custom.show(fm, "");
                        }
                    }
                });
                materialAlertDialogBuilder.show();
            }
        });

        //주문서 저장
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (saleOrderNo.equals("")) {//최초 주문 상태
                    new MaterialAlertDialogBuilder(SaleOrderActivity.this)
                            .setTitle("주문서 작성")
                            .setMessage("총금액: " + txtTotal.getText().toString().replace("합계: ", "") + "\n" + "주문서를 작성하시겠습니까?")
                            .setCancelable(true)
                            .setPositiveButton
                                    ("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SaveSalesOrderData();
                                        }
                                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(getContext(), "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                            showErrorDialog(SaleOrderActivity.this, "취소 되었습니다.", 1);
                            if (saleOrderNo.equals(""))
                                toggleOrder.uncheck(R.id.btnSave);
                            else
                                toggleOrder.check(R.id.btnSave);
                        }
                    }).show();
                } else {//주문 이미 한번이라도 작성한상태
                    new MaterialAlertDialogBuilder(SaleOrderActivity.this)
                            .setTitle("주문서 수정")
                            .setMessage("총금액: " + txtTotal.getText().toString().replace("합계: ", "") + "\n" + "주문서를 수정하시겠습니까?")
                            .setCancelable(true)
                            .setPositiveButton
                                    ("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SaveSalesOrderData();
                                        }
                                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(getContext(), "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                            showErrorDialog(SaleOrderActivity.this, "취소 되었습니다.", 1);
                            if (saleOrderNo.equals(""))
                                toggleOrder.uncheck(R.id.btnSave);
                            else
                                toggleOrder.check(R.id.btnSave);
                        }
                    }).show();
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrCancelSaleOrder();
            }
        });

        getSaleOrderDetail();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    private void confirmOrCancelSaleOrder() {

        String url;
        String cString = "";
        String cTitle = "";
        if (fixDivision.equals("Y")) {//확정취소를 한다.
            url = getString(R.string.service_address) + "cancelSaleOrder";
            cTitle = "주문서 확정취소";
            cString = "확정 취소시겠습니까";
        } else {//확정을 한다.
            url = getString(R.string.service_address) + "confirmSaleOrder";
            cTitle = "주문서 확정";
            cString = "확정하시겠습니까?";
        }
        new MaterialAlertDialogBuilder(SaleOrderActivity.this)
                .setTitle(cTitle)
                .setMessage("주문번호: " + txtSaleOrderNo.getText().toString() + "\n" + cString)
                .setCancelable(true)
                .setPositiveButton
                        ("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ContentValues values = new ContentValues();
                                values.put("UserID", Users.UserID);
                                values.put("ProductDBName", getString(R.string.product_dbname));
                                values.put("SaleOrderNo", saleOrderNo);

                                ConfirmOrCancelSaleOrder gsod = new ConfirmOrCancelSaleOrder(url, values);
                                gsod.execute();
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getContext(), "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                showErrorDialog(SaleOrderActivity.this, "취소 되었습니다.", 1);
                if (fixDivision.equals("N"))
                    toggleOrder.uncheck(R.id.btnConfirm);
                else
                    toggleOrder.check(R.id.btnConfirm);
            }
        }).show();
    }

    public class ConfirmOrCancelSaleOrder extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        ConfirmOrCancelSaleOrder(String url, ContentValues values) {
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
                    fixDivision = child.getString("FixDivision");
                    /*saleOrder = new SaleOrder();
                    saleOrderNo = child.getString("SaleOrderNo");
                    saleOrder.partCode = child.getString("PartCode");
                    saleOrder.partName = child.getString("PartName");
                    saleOrder.partSpec = child.getString("PartSpec");
                    saleOrder.partSpecName = child.getString("PartSpecName");
                    saleOrder.marketPrice = child.getString("MarketPrice");
                    saleOrder.orderQty= child.getString("OrderQty");
                    saleOrder.orderPrice= child.getString("OrderPrice");
                    saleOrder.standardPrice= child.getString("StandardPrice");
                    saleOrderArrayList.add(saleOrder);*/
                }
                if (fixDivision.equals("Y")) {
                    toggleOrder.check(R.id.btnConfirm);
                    btnSave.setEnabled(false);
                    setEnableFalseOrTrueEditText(false);
                    Toast.makeText(SaleOrderActivity.this, "확정 되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    toggleOrder.uncheck(R.id.btnConfirm);
                    btnSave.setEnabled(true);
                    setEnableFalseOrTrueEditText(true);
                    Toast.makeText(SaleOrderActivity.this, "확정 취소 되었습니다.", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF();
            }
        }
    }

    private void setEnableFalseOrTrueEditText(boolean state) {

        edtRemark.setEnabled(state);
        edtRemark2.setEnabled(state);

        for (int i = 0; i < listview.getCount(); i++) {
            try {
                Object soa =  listview.getItemAtPosition(i);
                String test="3";
                test="4";
            } catch (Exception ete) {
                Toast.makeText(SaleOrderActivity.this, ete.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

        /*for(int i=0; i<listview.getCount();i++){
            try {

            }
            catch (Exception ete){
                Toast.makeText(SaleOrderActivity.this,ete.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }*/


    private void SaveSalesOrderData() {
        String url = getString(R.string.service_address) + "saveSalesOrderData";
        ContentValues values = new ContentValues();
        values.put("UserID", Users.UserID);
        values.put("SaleOrderNo", saleOrderNo);
        values.put("CustomerCode", customerCode);
        values.put("LocationNo", locationNo);
        values.put("BusinessClassCode", "2");
        values.put("OutBusinessClassCode", "2");
        values.put("Remark1", edtRemark.getText().toString());
        values.put("Remark2", edtRemark2.getText().toString());

        for (int i = 0; i < saleOrderArrayList.size(); i++) {
            if (saleOrderArrayList.get(i).orderQty.equals("")) {
                Toast.makeText(SaleOrderActivity.this, "수량을 확인하시기 바랍니다.\n" +
                        "(" + saleOrderArrayList.get(i).partName + ", " + saleOrderArrayList.get(i).partSpecName + ")", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        SaveSalesOrderData gsod = new SaveSalesOrderData(url, values, saleOrderArrayList);
        gsod.execute();
    }


public class SaveSalesOrderData extends AsyncTask<Void, Void, String> {
    String url;
    ContentValues values;
    ArrayList<SaleOrder> list;

    SaveSalesOrderData(String url, ContentValues values, ArrayList<SaleOrder> list) {
        this.url = url;
        this.values = values;
        this.list = list;
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
        result = requestHttpURLConnection.request2(url, values, list);
        return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
    }

    @Override
    protected void onPostExecute(String result) {
        // 통신이 완료되면 호출됩니다.
        // 결과에 따른 UI 수정 등은 여기서 합니다
        try {
            JSONArray jsonArray = new JSONArray(result);
            String ErrorCheck = "";
            String tempSaleOrderNo = "";
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

                tempSaleOrderNo = child.getString("SaleOrderNo");
                fixDivision = child.getString("FixDivision");
                    /*saleOrder = new SaleOrder();
                    saleOrderNo = child.getString("SaleOrderNo");
                    saleOrder.partCode = child.getString("PartCode");
                    saleOrder.partName = child.getString("PartName");
                    saleOrder.partSpec = child.getString("PartSpec");
                    saleOrder.partSpecName = child.getString("PartSpecName");
                    saleOrder.marketPrice = child.getString("MarketPrice");
                    saleOrder.orderQty= child.getString("OrderQty");
                    saleOrder.orderPrice= child.getString("OrderPrice");
                    saleOrder.standardPrice= child.getString("StandardPrice");
                    saleOrderArrayList.add(saleOrder);*/
            }
            if (!tempSaleOrderNo.equals("")) {
                saleOrderNo = tempSaleOrderNo;
                txtSaleOrderNo.setText(saleOrderNo);
                Toast.makeText(SaleOrderActivity.this, "저장 되었습니다.", Toast.LENGTH_SHORT).show();
                btnSave.setText("저장");
                btnConfirm.setEnabled(true);
                toggleOrder.check(R.id.btnSave);
            }
            if (fixDivision.equals("Y")) {
                toggleOrder.check(R.id.btnConfirm);
            } else {
                toggleOrder.uncheck(R.id.btnConfirm);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            progressOFF();
        }
    }
}

    /**
     * 목록에서 찾기로 추가된 품목들 처리
     *
     * @param addedSaleOrderArrayList
     */
    public void addSaleOrderListByContents(ArrayList<SaleOrder> addedSaleOrderArrayList) {
        for (int i = 0; i < addedSaleOrderArrayList.size(); i++) {
            SaleOrder saleOrder = new SaleOrder();
            saleOrder.partCode = addedSaleOrderArrayList.get(i).partCode;
            saleOrder.partName = addedSaleOrderArrayList.get(i).partName;
            saleOrder.partSpec = addedSaleOrderArrayList.get(i).partSpec;
            saleOrder.partSpecName = addedSaleOrderArrayList.get(i).partSpecName;
            saleOrder.marketPrice = addedSaleOrderArrayList.get(i).marketPrice;
            saleOrder.orderQty = addedSaleOrderArrayList.get(i).orderQty;
            saleOrder.orderPrice = addedSaleOrderArrayList.get(i).orderPrice;
            saleOrder.standardPrice = addedSaleOrderArrayList.get(i).standardPrice;

            saleOrderArrayList.add(saleOrder);
        }
        saleOrderAdapter.notifyDataSetChanged();
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
        //주문하지 않은 데이터는 삭제합니다. 안내 메세지
        new MaterialAlertDialogBuilder(SaleOrderActivity.this)
                .setTitle("뒤로 가기")
                .setMessage("저장하지 않은 데이터는 삭제 됩니다.\n뒤로 가시겠습니까?")
                .setCancelable(true)
                .setPositiveButton
                        ("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            /*    Intent intent=new Intent();
                                intent.putExtra("saleOrderNo",saleOrderNo);
                                setResult(Activity.RESULT_OK, intent);*/
                                goBack();
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        }).show();
    }

    public void goBack() { // 종료
        finish();
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
                saleOrderNo = child.getString("SaleOrderNo");
                saleOrder.partCode = child.getString("PartCode");
                saleOrder.partName = child.getString("PartName");
                saleOrder.partSpec = child.getString("PartSpec");
                saleOrder.partSpecName = child.getString("PartSpecName");
                saleOrder.marketPrice = child.getString("MarketPrice");
                saleOrder.orderQty = child.getString("OrderQty");
                saleOrder.orderPrice = child.getString("OrderPrice");
                saleOrder.standardPrice = child.getString("StandardPrice");
                fixDivision = child.getString("FixDivision");
                saleOrderArrayList.add(saleOrder);

                    /*if (!partNameDic.contains(stock.PartName))
                        partNameDic.add(stock.PartName);
                    if (!partSpecNameDic.contains(stock.PartSpecName))
                        partSpecNameDic.add(stock.PartSpecName);*/
            }
            saleOrderAdapter = new SaleOrderAdapter
                    (SaleOrderActivity.this, R.layout.listview_saleorder_row, saleOrderArrayList, listview, edtRemark, edtRemark2, txtTotal);
            listview.setAdapter(saleOrderAdapter);
            txtSaleOrderNo.setText(saleOrderNo);
            if (fixDivision.equals("Y")) {//확정
                toggleOrder.check(R.id.btnConfirm);
            } else {
                toggleOrder.uncheck(R.id.btnConfirm);
            }

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
