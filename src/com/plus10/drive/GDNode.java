package com.plus10.drive;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 06/06/2016.
 */
public class GDNode {
    private List<GDNode> childern;
    private String id;
    private boolean isFolder;       //so far we only scan folders. this flag is for future
    private boolean isP10Item;      //plus10 item is a folder
    private String name;
    private long size;       //useful for plus10 item
    private Date mtime;

    public GDNode() {
        this("", "", 0, 0, false);
    }

    public GDNode(String id, String name, long mTimeValue, long size, boolean isP10Item) {
        this.id = id;
        this.name = name;
        this.mtime = new Date(mTimeValue);
        this.size = size;
        this.isP10Item =isP10Item;
        this.isFolder = true;
        childern = new ArrayList<GDNode>();
    }

    public void addChild(GDNode node) {
        childern.add(node);
    }

    public String getId() {
        return id;
    }

    public boolean isP10Item() {
        return isP10Item;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public Date getMtime() {
        return mtime;
    }
}
