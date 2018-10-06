package com.virtualtec.kangutest.Datas.SubDatas;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Android on 24/04/18.
 */

public class DataAttributes implements Serializable{

    private String id;
    private String name;
    private String option;
    private String[] options;

    public DataAttributes(String id, String name, String option, String[] options) {
        this.id = id;
        this.name = name;
        this.option = option;
        this.options = options;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getOption() { return option; }
    public String[] getOptions() { return options; }
}
