package com.plus10.drive;

/**
 * Created by Administrator on 26/05/2016.
 */
//TODO, move MetaData out if src information is not included.
public class MetaData {
    private final String name;
    private final String md5;
    private final int size;

    public MetaData(String name, String md5, int size) {
        this.name = name;
        this.md5 = md5;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public String getMd5() {
        return md5;
    }

    public int getSize() {
        return size;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(':');
        sb.append(md5);
        sb.append(':');
        sb.append(size);
        return sb.toString();
    }
}