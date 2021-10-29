package com.symbol.steelsales.Object;

import java.io.Serializable;

public class SaleOrder implements Serializable {

    public int index=0;
    public boolean isDBSaved=false;
    public String saleOrderNo="";
    public String partCode="";
    public String partName="";
    public String partSpec="";
    public String partSpecName="";
    public String orderQty="";
    public double orderAmount=0;
    public String discountRate="";

    public String orderPrice="";//할인율 적용하여, 결정된 주문 단가
    public String marketPrice="";//PartSpec의 시장 단가
    public String standardPrice="";//SaelsOrderDetail의 저장 당시의 시장 단가: DataSet에서 최초 불러올때만 이걸로 셋팅

    public SaleOrder() {
        super();
    }
}
