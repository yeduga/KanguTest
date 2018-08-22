package com.virtualtec.kangutest.Datas.SubDatas;

/**
 * Created by Android on 20/02/18.
 */

public class DataBilling {
    String email = "";
    String first_name = "";
    String last_name = "";
    String address_1 = "";
    String address_2 = "";
    String city = "";
    String state = "";
    String postcode = "";
    String country = "";
    String phone = "";

    public DataBilling(String email, String first_name, String last_name, String address_1, String address_2, String city, String state, String postcode, String country, String phone) {
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.address_1 = address_1;
        this.address_2 = address_2;
        this.city = city;
        this.state = state;
        this.postcode = postcode;
        this.country = country;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getAddress_1() {
        return address_1;
    }

    public String getAddress_2() {
        return address_2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getCountry() {
        return country;
    }

    public String getPhone() {
        return phone;
    }
}
