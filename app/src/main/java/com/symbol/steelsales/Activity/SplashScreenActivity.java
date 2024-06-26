package com.symbol.steelsales.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.symbol.steelsales.BuildConfig;
import com.symbol.steelsales.MainActivity2;
import com.symbol.steelsales.Object.Dept;
import com.symbol.steelsales.Object.Location;
import com.symbol.steelsales.Object.Users;
import com.symbol.steelsales.PermissionUtil;
import com.symbol.steelsales.R;
import com.symbol.steelsales.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class SplashScreenActivity extends BaseActivity {
    /*
    버전다운로드 관련 변수
     */
    DownloadManager mDm;
    long mId = 0;
    //Handler mHandler;
    String serverVersion;
    String downloadUrl;
    ProgressDialog mProgressDialog;
    //버전 변수 끝

    SharedPreferences _pref;
    Boolean isShortcut = false;//아이콘의 생성
    private boolean mIsRegisterReceiver;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash_screen);

        _pref = getSharedPreferences("kumkang", MODE_PRIVATE);//sharedPreferences 이름: "kumkang"에 저장
        isShortcut = _pref.getBoolean("isShortcut", false);//"isShortcut"에 들어있는값을 가져온다.

        if (!isShortcut)//App을 처음 깔고 시작했을때 이전에 깐적이 있는지없는지 검사하고, 이름과 아이콘을 설정한다.
        {
            addShortcut(this);
        }

        getDPI();

        checkServerVersion();//버전을 체크-> 안쪽에 권한

      /*  Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (configuration.smallestScreenWidthDp < Application.minScreenWidth) {
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else {
            if (configuration.screenWidthDp < Application.minScreenWidth) {
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }*/

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        /* Duration of wait */
        int SPLASH_DISPLAY_LENGTH = 2000;
        /*new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                *//* Create an Intent that will start the Menu-Activity. *//*
                Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(mainIntent);
                SplashScreenActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);*/
    }

    private void getDPI() {
        int margin = 0;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int dpi = metrics.densityDpi;

        if (dpi <= 160) { // mdpi
            Users.Dpi = "mdpi";
        } else if (dpi <= 240) { // hdpi
            Users.Dpi = "hdpi";
        } else if (dpi <= 320) { // xhdpi
            Users.Dpi = "xhdpi";
        } else if (dpi <= 480) { // xxhdpi
            Users.Dpi = "xxhdpi";
        } else if (dpi <= 640) { // xxxhdpi
            Users.Dpi = "xxxhdpi";
        }
    }

    private void addShortcut(Context context) {

        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
        shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        shortcutIntent.setClassName(context, getClass().getName());
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        //FLAG_ACTIVITY_NEW_TASK: 실행한 액티비티와 관련된 태스크가 존재하면 동일한 태스크내에서 실행하고, 그렇지 않으면 새로운 태스크에서 액티비티를 실행하는 플래그
        //FLAG_ACTIVITY_RESET_TASK_IF_NEEDED: 사용자가 홈스크린이나 "최근 실행 액티비티목록"에서 태스크를 시작할 경우 시스템이 설정하는 플래그, 이플래그는 새로 태스크를
        //시작하거나 백그라운드 태스크를 포그라운드로 가지고 오는 경우가 아니라면 영향을 주지 않는다, "최근 실행 액티비티 목록":  홈 키를 오랫동안 눌렀을 떄 보여지는 액티비티 목록

        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);//putExtra(이름, 실제값)
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "KUMKANGREADER");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.drawable.logo2));
        //Intent.ShortcutIconResource.fromContext(context, R.drawable.img_kumkang);
        intent.putExtra("duplicate", false);
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

        sendBroadcast(intent);
        SharedPreferences.Editor editor = _pref.edit();
        editor.putBoolean("isShortcut", true);

        editor.commit();
    }

    private void checkServerVersion() {
        String url = getString(R.string.service_address) + "checkAppVersion";
        ContentValues values = new ContentValues();
        values.put("AppCode", getString(R.string.app_code));
        CheckAppVersion cav = new CheckAppVersion(url, values);
        cav.execute();
    }

    public class CheckAppVersion extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        CheckAppVersion(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
            startProgress();
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
                if (result.equals("")) {
                    //Toast.makeText(SplashScreenActivity.this, "서버연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    showErrorDialog(SplashScreenActivity.this, "서버연결에 실패하였습니다.", 2);
                    ActivityCompat.finishAffinity(SplashScreenActivity.this);
                }
                JSONArray jsonArray = new JSONArray(result);

                JSONObject child = jsonArray.getJSONObject(0);
                downloadUrl = child.getString("Message");
                serverVersion = child.getString("ResultCode");

                if (result.equals(""))
                    finish();
                else {
                    if (Double.parseDouble(serverVersion) > getCurrentVersion()) {//좌측이 DB에 있는 버전
                        newVersionDownload();
                    } else {
                        CheckPermission();
                    }
                }

            } catch (Exception er) {
                Toast.makeText(SplashScreenActivity.this, "확정 되었습니다.", Toast.LENGTH_SHORT).show();

            } finally {
            }
        }
    }

    private void newVersionDownload() {
        new android.app.AlertDialog.Builder(SplashScreenActivity.this).setMessage("새로운 버전이 있습니다. 다운로드 할까요?")
                .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mProgressDialog = ProgressDialog.show(SplashScreenActivity.this, "다운로드", "잠시만 기다려주세요");

                Uri uri = Uri.parse(downloadUrl);
                DownloadManager.Request req = new DownloadManager.Request(uri);
                req.setTitle("금강공업 출고관리 어플리케이션 다운로드");
                req.setDestinationInExternalFilesDir(SplashScreenActivity.this, Environment.DIRECTORY_DOWNLOADS, "KUMKANG.apk");

                //req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, pathSegments.get(pathSegments.size() - 1));
                //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();


                req.setDescription("금강공업 출고관리 어플리케이션 설치파일을 다운로드 합니다.");
                req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                mId = mDm.enqueue(req);
                IntentFilter filter = new IntentFilter();
                filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

                registerReceiver(mDownComplete2, filter);

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(SplashScreenActivity.this, "최신버전으로 업데이트 하시기 바랍니다.", Toast.LENGTH_LONG).show();
                showErrorDialog(SplashScreenActivity.this, "최신버전으로 업데이트 하시기 바랍니다.", 2);
                ActivityCompat.finishAffinity(SplashScreenActivity.this);
            }
        }).show();
    }

    /**
     * 다운로드 완료 이후의 작업을 처리한다.(다운로드 파일 열기)
     */
    BroadcastReceiver mDownComplete2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            Toast.makeText(context, "다운로드 완료", Toast.LENGTH_SHORT).show();
            //showErrorDialog(SplashScreenActivity.this, "다운로드 완료",1);

            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(mId);
            Cursor cursor = mDm.query(query);
            if (cursor.moveToFirst()) {

                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int status = cursor.getInt(columnIndex);

                //String fileName = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                //int uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    String fileUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    openFile(fileUri);
                }
            }
        }
    };

    protected void openFile(String uri) {

        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri)).toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        Intent open = new Intent(Intent.ACTION_VIEW);
        open.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        /*if(open.getFlags()!=Intent.FLAG_GRANT_READ_URI_PERMISSION){//권한 허락을 안한다면
            Toast.makeText(getBaseContext(), "Look!", Toast.LENGTH_LONG).show();
            finish();
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//누가 버전 이상이라면 FileProvider를 사용한다.
            //Toast.makeText(getBaseContext(), "test1", Toast.LENGTH_LONG).show();
            uri = uri.substring(7);
            File file = new File(uri);
            Uri u = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
            open.setDataAndType(u, mimetype);
        } else {
            open.setDataAndType(Uri.parse(uri), mimetype);
        }

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }


        Toast.makeText(getBaseContext(), "설치 완료 후, 어플리케이션을 다시 시작하여 주십시요.", Toast.LENGTH_LONG).show();
        startActivity(open);
        // finish();//startActivity 전일까 후일까 잘판단

    }

    public int getCurrentVersion() {

        int version;

        try {
            mDm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            PackageInfo i = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            version = i.versionCode;
            //Users.CurrentVersion = version;

            return version;

        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    @SuppressLint("MissingPermission")
    private void CheckPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {//29이상
            PermissionUtil.permissionList = new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_PHONE_NUMBERS
            };
        }
        else{
            PermissionUtil.permissionList = new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_PHONE_NUMBERS
            };
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtil.haveAllpermission(this, PermissionUtil.permissionList)) {//모든 퍼미션 허용
                try {
                    TelephonyManager systemService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    Users.AndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    if (Users.AndroidID == null)
                        Users.AndroidID = "";
                    Users.Model = Build.MODEL;
                    if (Users.Model == null)
                        Users.Model = "";
                    Users.PhoneNumber = systemService.getLine1Number();//없으면 null이들어갈수도있다 -> if(Users.PhoneNumber==null) 으로 활용가능
                    //Users.PhoneNumber = "010-6737-5288";//없으면 null이들어갈수도있다 -> if(Users.PhoneNumber==null) 으로 활용가능
                    if (Users.PhoneNumber == null)
                        Users.PhoneNumber = "";
                    else
                        Users.PhoneNumber = Users.PhoneNumber.replace("+82", "0");
                    Users.DeviceOS = Build.VERSION.RELEASE;
                    if (Users.DeviceOS == null)
                        Users.DeviceOS = "";
                    Users.Remark = "";
                    Users.DeviceName = "";
                } catch (Exception e) {
                    String str=e.getMessage();
                    String str2=str;
                } finally {
                    CheckAppProgramsPowerAndLoginHistory();
                }
            } else {//퍼미션 하나라도 허용 안함
                ActivityCompat.requestPermissions(this, PermissionUtil.permissionList, PermissionUtil.MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
        } else {//낮은 버전이면 바로 번호 받기가능
            try {
                TelephonyManager systemService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                Users.AndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                if (Users.AndroidID == null)
                    Users.AndroidID = "";
                Users.Model = Build.MODEL;
                if (Users.Model == null)
                    Users.Model = "";
                Users.PhoneNumber = systemService.getLine1Number();//없으면 null이들어갈수도있다 -> if(Users.PhoneNumber==null) 으로 활용가능
                //Users.PhoneNumber = "010-6737-5288";//없으면 null이들어갈수도있다 -> if(Users.PhoneNumber==null) 으로 활용가능
                if (Users.PhoneNumber == null)
                    Users.PhoneNumber = "";
                else
                    Users.PhoneNumber = Users.PhoneNumber.replace("+82", "0");
                Users.DeviceOS = Build.VERSION.RELEASE;
                if (Users.DeviceOS == null)
                    Users.DeviceOS = "";
                Users.Remark = "";
                Users.DeviceName = "";
            } catch (Exception e) {
                String str=e.getMessage();
                String str2=str;
            } finally {
                CheckAppProgramsPowerAndLoginHistory();
            }
        }
    }

    private void CheckAppProgramsPowerAndLoginHistory() {
        String url = getString(R.string.service_address) + "checkAppProgramsPowerAndLoginHistory";
        ContentValues values = new ContentValues();
        values.put("AppCode", getString(R.string.app_code));
        values.put("AndroidID", Users.AndroidID);
        values.put("Model", Users.Model);
        values.put("PhoneNumber", Users.PhoneNumber);
        values.put("DeviceName", Users.DeviceName);
        values.put("DeviceOS", Users.DeviceOS);
        values.put("AppVersion", getCurrentVersion());
        values.put("Remark", "");
        values.put("UserID", Users.PhoneNumber);
        CheckAppProgramsPowerAndLoginHistory ilh = new CheckAppProgramsPowerAndLoginHistory(url, values);
        ilh.execute();
    }


    public class CheckAppProgramsPowerAndLoginHistory extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        CheckAppProgramsPowerAndLoginHistory(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등의 행위
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
                //CheckAppProgramsPower();
                Users.authorityList = new ArrayList<>();
                Users.authorityNameList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";

                if (jsonArray.length() == 0) {//권한이 없을시, 로그인 화면 출력
                   /* Intent loginIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    return;*/
                    //Toast.makeText(getBaseContext(), "사용자 혹은 권한이 등록되어있지 않습니다.", Toast.LENGTH_SHORT).show();
                    showErrorDialog(SplashScreenActivity.this, "사용자 혹은 권한이 등록되어있지 않습니다.", 2);
                    Intent Intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(Intent);
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject child = jsonArray.getJSONObject(i);
                        if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                            ErrorCheck = child.getString("ErrorCheck");
                            //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                            showErrorDialog(SplashScreenActivity.this, ErrorCheck, 2);
                            return;
                        }
                        Users.UserName = child.getString("UserName");
                        Users.UserID = child.getString("UserID");
                        Users.authorityList.add(Integer.parseInt(child.getString("Authority")));
                        Users.authorityNameList.add(child.getString("AuthorityName"));
                        Users.CustomerCode = child.getString("CustomerCode");
                        Users.CustomerName = child.getString("CustomerName");
                        Users.DeptCode = child.getString("DeptCode");
                        Users.DeptName = child.getString("DeptName");
                    }
                    GetCustomerLocationAll();
                }

            } catch (Exception e) {
                progressOFF2(this.getClass().getName());

            } finally {
            }
        }
    }

    public void GetCustomerLocationAll() {
        String url = getString(R.string.service_address) + "getCustomerLocationAll";
        ContentValues values = new ContentValues();
        values.put("BusinessClassCode", 2);
        //values.put("SearchString", this.edtSearch.getText().toString());
        GetCustomerLocationAll gsod = new GetCustomerLocationAll(url, values);
        gsod.execute();
    }

    public class GetCustomerLocationAll extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetCustomerLocationAll(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                Location location;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                Users.locationArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(SplashScreenActivity.this, ErrorCheck, 2);
                        return;
                    }
                    location = new Location();
                    location.LocationNo = child.getString("LocationNo");
                    location.LocationName = child.getString("LocationName");
                    location.CustomerCode = child.getString("CustomerCode");
                    location.CustomerName = child.getString("CustomerName");
                    Users.locationArrayList.add(location);
                }
                Dept aDept = new Dept();
                aDept.deptCode = "-1";
                aDept.deptName = "전체";
                aDept.index = 0;
                Dept sDept = new Dept();
                sDept.deptCode = "14100";
                sDept.deptName = "서울철강영업팀";
                sDept.index = 1;
                Dept pDept = new Dept();
                pDept.deptCode = "14200";
                pDept.deptName = "부산철강영업팀";
                pDept.index = 2;
                Dept fDept = new Dept();
                fDept.deptCode = "14500";
                fDept.deptName = "해외철강영업파트";
                fDept.index = 3;
                Users.deptArrayList = new ArrayList<>();
                Users.deptArrayList.add(aDept);
                Users.deptArrayList.add(sDept);
                Users.deptArrayList.add(pDept);
                Users.deptArrayList.add(fDept);

                progressOFF2(this.getClass().getName());
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity2.class);
                startActivity(intent);

            } catch (Exception e) {

                e.printStackTrace();

            } finally {
            }
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtil.MY_PERMISSIONS_REQUEST_READ_PHONE_STATE &&
                grantResults.length == PermissionUtil.permissionList.length) {

            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            //허용
            if (check_result) {
                //허용했을때 로직
                try {
                    TelephonyManager systemService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    Users.AndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    if (Users.AndroidID == null)
                        Users.AndroidID = "";
                    Users.Model = Build.MODEL;
                    if (Users.Model == null)
                        Users.Model = "";
                    Users.PhoneNumber = systemService.getLine1Number();//없으면 null이들어갈수도있다 -> if(Users.PhoneNumber==null) 으로 활용가능
                    //Users.PhoneNumber = "010-6737-5288";//없으면 null이들어갈수도있다 -> if(Users.PhoneNumber==null) 으로 활용가능
                    if (Users.PhoneNumber == null)
                        Users.PhoneNumber = "";
                    else
                        Users.PhoneNumber = Users.PhoneNumber.replace("+82", "0");
                    Users.DeviceOS = Build.VERSION.RELEASE;
                    if (Users.DeviceOS == null)
                        Users.DeviceOS = "";
                    Users.Remark = "";
                    Users.DeviceName = "";
                } catch (Exception e) {
                    /*String str=e.getMessage();
                    String str2=str;*/
                } finally {
                    CheckAppProgramsPowerAndLoginHistory();
                }
            }
            //거부 ,재거부
            else {
                //거부 눌렀을 때 로직
                Toast.makeText(this, "애플리케이션을 실행하려면 권한이 허가되어야 합니다.", Toast.LENGTH_LONG).show();
                Intent intent = getIntent();
                setResult(RESULT_CANCELED, intent);
                finish();
                /*if (PermissionUtil.recheckPermission(this, PermissionUtil.permissionList)) {
                    //거부 눌렀을 때 로직
                    Toast.makeText(this, "앱에 로그인하기 위해 반드시 필요합니다.", Toast.LENGTH_LONG).show();
                    Intent intent = getIntent();
                    setResult(RESULT_CANCELED, intent);
                    finish();

                } else {
                    //재거부 눌렀을 때 로직
                    Toast.makeText(this, "앱에 로그인하기 위해 반드시 필요합니다.", Toast.LENGTH_LONG).show();
                    Intent intent = getIntent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }*/
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
