package com.symbol.steelsales.Object;

import java.io.Serializable;

public class StockOutDetail  implements Serializable {

    public String StockOutNo="";
    public String PartName="";
    public String PartSpecName="";
    public String OutQty="";
    public String OutUnitPrice="";
    public String Amount="";
    public String Weight="";
    public StockOutDetail() {
        super();
    }
}
