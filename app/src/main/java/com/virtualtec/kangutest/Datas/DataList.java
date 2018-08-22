package com.virtualtec.kangutest.Datas;

import java.io.Serializable;

/**
 * Created by Android on 20/03/18.
 */

public class DataList implements Serializable {

    private String id_list;
    private String name_list;
    private String address;
    private String date_creation;
    private String date_delivery;
    private String reminder;
    private boolean isSelected;

    public DataList(String id_list, String name_list, String address, String date_creation, String date_delivery, String reminder) {
        this.id_list = id_list;
        this.name_list = name_list;
        this.address = address;
        this.date_creation = date_creation;
        this.date_delivery = date_delivery;
        this.reminder = reminder;
        this.isSelected = false;
    }

    public String getId_list() { return id_list; }
    public String getName_list() {
        return name_list;
    }
    public String getAddress() {
        return address;
    }
    public String getDate_creation() {
        return date_creation;
    }
    public String getDate_delivery() {return date_delivery; }
    public void setSelected(boolean selected) {isSelected = selected; }
    public boolean isSelected() { return isSelected; }
    public String getReminder() { return reminder; }

    public void setDate_delivery(String date_delivery) { this.date_delivery = date_delivery; }
    public void setReminder(String reminder) { this.reminder = reminder; }
}
