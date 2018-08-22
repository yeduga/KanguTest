package com.virtualtec.kangutest.Datas.SubDatas;

/**
 * Created by Android on 10/04/18.
 */

public class DataLineItems {

    private String id;
    private String name;
    private String product_id;
    private String quantity;
    private String price;
    private String tax_class;
    private String subtotal;
    private String subtotal_tax;
    private String total;
    private String total_tax;
    private String unity;

    public DataLineItems(String id, String name, String product_id, String quantity, String price, String tax_class, String subtotal, String subtotal_tax, String total, String total_tax, String unity) {
        this.id = id;
        this.name = name;
        this.product_id = product_id;
        this.quantity = quantity;
        this.price = price;
        this.tax_class = tax_class;
        this.subtotal = subtotal;
        this.subtotal_tax = subtotal_tax;
        this.total = total;
        this.total_tax = total_tax;
        this.unity = unity;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    public String getTax_class() {
        return tax_class;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public String getSubtotal_tax() {
        return subtotal_tax;
    }

    public String getTotal() {
        return total;
    }

    public String getTotal_tax() {
        return total_tax;
    }

    public String getUnity() { return unity; }
}
