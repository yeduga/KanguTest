package com.virtualtec.kangutest.Datas;

import com.virtualtec.kangutest.Datas.SubDatas.DataBilling;
import com.virtualtec.kangutest.Datas.SubDatas.DataLineItems;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Android on 10/04/18.
 */

public class DataOrders implements Serializable {

    private String id;
    private String status;
    private String currency;
    private String prices_include_tax;
    private String date_created;
    private String total;
    private String total_tax;
    private DataBilling billing;
    private ArrayList<DataLineItems> line_items;

    public DataOrders(String id, String status, String currency, String prices_include_tax, String date_created, String total, String total_tax, DataBilling billing, ArrayList<DataLineItems> line_items) {
        this.id = id;
        this.status = status;
        this.currency = currency;
        this.prices_include_tax = prices_include_tax;
        this.date_created = date_created;
        this.total = total;
        this.total_tax = total_tax;
        this.billing = billing;
        this.line_items = line_items;
    }

    public String getId() { return id; }

    public String getStatus() {
        return status;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPrices_include_tax() {
        return prices_include_tax;
    }

    public String getDate_created() {
        return date_created;
    }

    public String getTotal() {
        return total;
    }

    public String getTotal_tax() {
        return total_tax;
    }

    public DataBilling getBilling() {
        return billing;
    }

    public ArrayList<DataLineItems> getLine_items() {
        return line_items;
    }
}
