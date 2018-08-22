package com.virtualtec.kangutest.Datas;

import com.virtualtec.kangutest.Datas.SubDatas.DataImage;

/**
 * Created by Android on 23/02/18.
 */

public class DataCategory {

    private String id;
    private String name;
    private DataImage image;

    public DataCategory(String id, String name, DataImage image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public String getId() { return id; }

    public String getName() {
        return name;
    }

    public DataImage getImage() {
        return image;
    }
}

