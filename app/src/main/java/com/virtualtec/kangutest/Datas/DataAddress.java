package com.virtualtec.kangutest.Datas;

/**
 * Created by Android on 5/04/18.
 */

public class DataAddress {

    private String id_list_address;
    private String address;

    public DataAddress(String id_list_address, String address) {
        this.id_list_address = id_list_address;
        this.address = address;
    }

    public String getId_list_address() {
        return id_list_address;
    }

    public String getAddress() {
        return address;
    }
}
