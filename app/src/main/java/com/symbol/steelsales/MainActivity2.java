package com.symbol.steelsales;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.symbol.steelsales.Application.ApplicationClass;
import com.symbol.steelsales.Fragment.FragmentSaleOrder;
import com.symbol.steelsales.Fragment.FragmentViewCollection;
import com.symbol.steelsales.Fragment.FragmentViewSaleOrder;
import com.symbol.steelsales.Interface.BaseActivityInterface;
import com.symbol.steelsales.Object.Users;


public class MainActivity2 extends FragmentActivity implements BaseActivityInterface {
    TabLayout tabs;
    FragmentSaleOrder fragmentSaleOrder;
    FragmentViewSaleOrder fragmentViewSaleOrder;
    FragmentViewCollection fragmentViewCollection;
    //FragmentTest fragmentTest;
    boolean testFlag = false;
    ImageView imageView5;

    BackPressControl backpressed;
    TabLayout.Tab firstTab;
    TabLayout.Tab secondTab;
    TabLayout.Tab thirdTab;

    TextView textView7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        backpressed = new BackPressControl(this);
        fragmentSaleOrder = new FragmentSaleOrder(this);
        fragmentViewSaleOrder = new FragmentViewSaleOrder(this);
        fragmentViewCollection = new FragmentViewCollection(this);
 /*       this.stockOutDetailArrayList = new ArrayList<>();
        this.scanDataArrayList = new ArrayList<>();*/
        //this.productionInfoArrayList = new ArrayList<>();

        //fragmentTest = new FragmentTest();
        /* fragment3 = new Fragment3();*/
        tabs = findViewById(R.id.tabs);
        imageView5 = findViewById(R.id.imageView5);
        textView7 = findViewById(R.id.textView7);//테스트용
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        /*if(Users.authorityList.contains(0)){//관리자 권한이 있다면,
            textView7.setText(" 재고조사(클릭)");
        }

        textView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Users.authorityList.contains(0)){//관리자 권한이 있다면,
                    Intent i = new Intent(getBaseContext(), ActivityInventorySurvey.class);
                    startActivity(i);
                }
            }
        });*/

        //테스트용
        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialAlertDialogBuilder alertBuilder = new MaterialAlertDialogBuilder(MainActivity2.this);
                //alertBuilder.setIcon(R.drawable.ic_launcher);
                //alertBuilder.setTitle(partName + "(" + partSpecName + ")");
                // List Adapter 생성
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity2.this,
                        android.R.layout.simple_list_item_1);

                try {
                    String content = "\n어플리케이션: " + (String) getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(getPackageManager().getPackageInfo(getPackageName(), 0).packageName, PackageManager.GET_UNINSTALLED_PACKAGES)) + "\n" +
                            "버전: " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName + "\n" +
                            "사용자번호: " + Users.PhoneNumber + "\n" +
                            "사용자명: " + Users.UserName + "\n" +
                            "권한: ";
                    for (int i = 0; i < Users.authorityNameList.size(); i++) {
                        content += Users.authorityNameList.get(i).toString() + ", ";
                    }
                    content = content.substring(0, content.length() - 2) + "\n";
                    content += "작업조: " + Users.WorkClassName;

                    adapter.add(content);

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                // 버튼 생성
                alertBuilder.setNegativeButton("닫기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });

                // Adapter 셋팅
                alertBuilder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                            }
                        });
                alertBuilder.show();


            }
        });

        firstTab = tabs.newTab().setText("주문관리").setIcon(R.drawable.outline_local_grocery_store_black_48);
        secondTab = tabs.newTab().setText("주문현황").setIcon(R.drawable.outline_assignment_black_48);
        thirdTab = tabs.newTab().setText("미수금현황").setIcon(R.drawable.outline_payments_black_48);
        tabs.addTab(firstTab);
        tabs.addTab(secondTab);
        tabs.addTab(thirdTab);

/*
        tabs2=findViewById(R.id.tabs2);
        final TabLayout.Tab topTab;
        topTab = tabs2.newTab().setText("코일입고").setIcon(R.drawable.outline_build_white_24dp);
        final TabLayout.Tab topTab2;
        topTab2 = tabs2.newTab().setText("테스트").setIcon(R.drawable.outline_build_white_24dp);
        tabs2.addTab(topTab);
        tabs2.addTab(topTab2);*/

        firstTab.view.setClickable(false);
        secondTab.view.setClickable(false);
        thirdTab.view.setClickable(false);

        getSupportFragmentManager().beginTransaction().add(R.id.container, fragmentSaleOrder).commit();//첫실행 fragment
        firstTab.setIcon(R.drawable.baseline_local_grocery_store_black_48);//주문관리
        secondTab.setIcon(R.drawable.outline_assignment_black_48);//주문현황
        thirdTab.setIcon(R.drawable.outline_payments_black_48);//미수금현황
        tabs.selectTab(firstTab);


        /*else if (Users.authorityList.contains(2)) {//2:출고권한만 가졌을시
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragmentStockOut).commit();//첫실행 fragment
            firstTab.setIcon(R.drawable.outline_donut_small_black_48dp);
            secondTab.setIcon(R.drawable.outline_build_black_48dp);
            thirdTab.setIcon(R.drawable.baseline_local_shipping_black_48dp);
            tabs.selectTab(thirdTab);
        }*/


        if (Users.authorityList.contains(0)) {
            firstTab.view.setClickable(true);
            secondTab.view.setClickable(true);
            thirdTab.view.setClickable(true);
        }
        if (Users.authorityList.contains(1)) {
            firstTab.view.setClickable(true);
            secondTab.view.setClickable(true);
            thirdTab.view.setClickable(true);
        }


        /*tabs.addTab(tabs.newTab().setText("출하"));*/

       /* tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

        });*/

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selected = null;
                if (position == 0) {
                    selected = fragmentSaleOrder;
                    firstTab.setIcon(R.drawable.baseline_local_grocery_store_black_48);

                } else if (position == 1) {
                    selected = fragmentViewSaleOrder;
                    secondTab.setIcon(R.drawable.baseline_assignment_black_48);

                } else if (position == 2) {
                    selected = fragmentViewCollection;
                    thirdTab.setIcon(R.drawable.baseline_payments_black_48);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    firstTab.setIcon(R.drawable.outline_local_grocery_store_black_48);
                } else if (position == 1) {
                    secondTab.setIcon(R.drawable.outline_assignment_black_48);
                } else if (position == 2) {
                    thirdTab.setIcon(R.drawable.outline_payments_black_48);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
               /* int position = tab.getPosition();
                if(position == 0) {

                }
                else if(position == 1) {

                }*/
            }
        });
    }


    @Override
    public int checkTagState(String tag) {
        return 0;
    }

    @Override
    public void progressON() {
        ApplicationClass.getInstance().progressON(this, null);
    }

    @Override
    public void progressON(String message) {
        ApplicationClass.getInstance().progressON(this, message);
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

    @Override
    public void onBackPressed() {

        backpressed.onBackPressed();
    }
}
