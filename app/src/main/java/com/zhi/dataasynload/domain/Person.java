package com.zhi.dataasynload.domain;

/**
 * Created by Administrator on 2016/11/13.
 */
public class Person {
    private int id;
    private String name;
    private String headImage;
    public Person(){

    }
    public Person(int id, String name, String headImage){
        this.id = id;
        this.name = name;
        this.headImage = headImage;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }
}