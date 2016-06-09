package com.plus10.drive;

import java.util.Date;

/**
 * Created by Administrator on 06/06/2016.
 */
public interface IGDNode {
    IGDNode[] getChildren();
    String getId();
    String getName();
    Date getMtime();
    long getSize();
    boolean isP10Item();
}
