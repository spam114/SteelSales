package com.symbol.steelsales.Object;

import java.io.Serializable;

public class StockOut implements Serializable {
    public String CustomerCode="";
    public String CustomerName="";
    public String LocationNo="";
    public String LocationName="";
    public String OutPrice="";
    public String LogicalWeight="";
    public String OutQty="";
    public String StockOutNo="";
    public String CompanyName="";
    public String SRemark1="";
    public String SRemark2="";
    public String EmployeeName="";

    public StockOut() {
        super();
    }
}
