package com.plus10.drive.UI;

/**
 * Created by Megral on 6/3/2016.
 */
public class DriveItem {
    private String name;
    private String date;
    int size;
    String id;

    public DriveItem() {
        name = "";
        date = "";
        size = 0;
        id = "";
    }

    public DriveItem(String name, String date, int size, String id) {
        this.name = name;
        this.date = date;
        this.size = size;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
