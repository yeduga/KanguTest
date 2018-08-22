package com.virtualtec.kangutest.Datas;

import com.google.gson.annotations.SerializedName;
import com.virtualtec.kangutest.Datas.SubDatas.DataAttributes;
import com.virtualtec.kangutest.Datas.SubDatas.DataImage;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Android on 22/02/18.
 */

public class DataProducts implements Serializable {

    private String id;
    private String id_detail_list;
    private String name;
    private String type;
    private String price;
    private String regular_price;
    private String description;
    private String stock_quantity;
    private String short_description;
    private String unity;
    private ArrayList<DataImage> images;
    private ArrayList<DataAttributes> attributes;

    public DataProducts(String id, String id_detail_list, String name, String type, String price, String regular_price, String description, String stock_quantity, String short_description, String unity, ArrayList<DataImage> images, ArrayList<DataAttributes> attributes) {
        this.id = id;
        this.id_detail_list = id_detail_list;
        this.name = name;
        this.type = type;
        this.price = price;
        this.regular_price = regular_price;
        this.description = description;
        this.stock_quantity = stock_quantity;
        this.short_description = short_description;
        this.unity = unity;
        this.images = images;
        this.attributes = attributes;
    }

    public String getId() {
        return id;
    }

    public String getId_detail_list() { return id_detail_list; }

    public String getName() {
        return name;
    }

    public String getType() { return type; }

    public String getPrice() {
        return price;
    }

    public String getRegular_price() {
        return regular_price;
    }

    public String getLong_description() {
        return description;
    }

    public String getStock_quantity() {
        return stock_quantity;
    }

    public String getShort_description() {
        return short_description;
    }

    public String getUnity() { return unity; }

    public ArrayList<DataImage> getImages() {
        return images;
    }

    public ArrayList<DataAttributes> getAttributes() { return attributes; }

}
