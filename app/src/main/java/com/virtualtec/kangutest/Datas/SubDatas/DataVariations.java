package com.virtualtec.kangutest.Datas.SubDatas;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Android on 24/04/18.
 */

public class DataVariations implements Serializable{

    private String id;
    private ArrayList<DataAttributes> attributes;

    public DataVariations(String id, ArrayList<DataAttributes> attributes) {
        this.id = id;
        this.attributes = attributes;
    }

    public String getId() { return id; }
    public ArrayList<DataAttributes> getAttributes() { return attributes; }
}
