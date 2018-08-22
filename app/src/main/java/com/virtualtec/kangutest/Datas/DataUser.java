package com.virtualtec.kangutest.Datas;


import com.virtualtec.kangutest.Datas.SubDatas.DataBilling;

/**
 * Created by Android on 20/02/18.
 */

public class DataUser {

    private String id;
    String email = "";
    String first_name = "";
    String last_name = "";
    String username = "";
    String response = "";
    private DataBilling billing;

    public DataUser(String id, String email, String first_name, String last_name, String username, String response, DataBilling billing) {
        this.id = id;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.response = response;
        this.billing = billing;
    }

    public String getId() { return id; }

    public String getEmail() {
        return email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getUsername() {
        return username;
    }

    public String getResponse() {
        return response;
    }

    public DataBilling getBilling() {
        return billing;
    }
}

