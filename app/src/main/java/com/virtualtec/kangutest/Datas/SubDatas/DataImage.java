package com.virtualtec.kangutest.Datas.SubDatas;

import java.io.Serializable;

/**
 * Created by Android on 22/02/18.
 */

public class DataImage implements Serializable {

    String src = "";

    public DataImage(String src) {
        this.src = src;
    }

    public String getSrc() {
        return src;
    }
}
