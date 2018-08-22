package com.virtualtec.kangutest.Datas;

/**
 * Created by Android on 22/03/18.
 */

public class DataResponse {

    String id;
    String response;

    public DataResponse(String id, String response) {
        this.id = id;
        this.response = response;
    }

    public String getId() {
        return id;
    }

    public String getResponse() {
        return response;
    }
}
