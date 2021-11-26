package com.symbol.steelsales.Object;

import java.util.ArrayList;
import java.util.List;

public class Users {
    public static String UserName = "";
    public static String UserID = "";
    //LoginDate는 서버시간
    //AppCode는 strings에서
    public static String AndroidID="";
    public static String Model = "";
    public static String PhoneNumber = "";
    public static String DeviceName = "";
    public static String DeviceOS = "";
    //Appversion은 build에서
    public static String Remark = "";
    public static String fromDate="";
    public static String DeptCode="";
    public static String DeptName="";

    public static String CustomerCode="";

    public static int REQUEST_SCRAP=4;
    //스캐너관련

    //권한 리스트
    public static List<Integer> authorityList;
    public static List<String> authorityNameList;
    /**
     * 주간(A), 야간(B), 1교대(C), 2교대(D), 3교대(E)
     */
    public static String WorkClassCode = "A";
    public static String WorkClassName = "주간";

    public static ArrayList<Location> locationArrayList;

    public static ArrayList<Dept> deptArrayList;

    public static String Dpi="";

    //true면 비가동상태
    //public static boolean CostCenterStopOperationStatus=true;
}
