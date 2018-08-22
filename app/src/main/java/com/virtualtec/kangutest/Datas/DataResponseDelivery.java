package com.virtualtec.kangutest.Datas;

/**
 * Created by Android on 10/04/18.
 */

public class DataResponseDelivery {

    private String response;
    private String date_delivery;
    private String id_list;
    private String name_list;
    private String address;
    private String year;
    private String month;
    private String day;
    private String date_creation;
    private String reminder;

    public DataResponseDelivery(String response, String date_delivery, String id_list, String name_list, String address, String year, String month, String day, String date_creation, String reminder) {
        this.response = response;
        this.date_delivery = date_delivery;
        this.id_list = id_list;
        this.name_list = name_list;
        this.address = address;
        this.year = year;
        this.month = month;
        this.day = day;
        this.date_creation = date_creation;
        this.reminder = reminder;
    }

    public String getResponse() {
        return response;
    }

    public String getDate_delivery() {
        return date_delivery;
    }

    public String getId_list() {
        return id_list;
    }

    public String getName_list() {
        return name_list;
    }

    public String getAddress() { return address; }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public String getDate_creation() {
        return date_creation;
    }

    public String getReminder() { return reminder; }
}
