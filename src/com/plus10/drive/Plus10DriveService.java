package com.plus10.drive;

import com.google.api.services.drive.Drive;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Administrator on 09/06/2016.
 */
public class Plus10DriveService {
    private Drive service;
    private GDNode plus10DriveNode;

    public Plus10DriveService() throws IOException{
        service = GDSWrapper.getDriveService();

        String appFolderId = GDOperations.getFolderId(service, "root", "Plus10Drive");
        if (appFolderId == null) {
            appFolderId = GDOperations.createFolder(service, "root", "Plus10Drive");
        }

        plus10DriveNode = new GDNode(appFolderId, "Plus10 Drive", 0, 0, false);
        Plus10DriveHelper.populateNodeTree(service, plus10DriveNode);
    }

    public IGDNode getPlus10DriveNode() {
        return plus10DriveNode;
    }

    public IGDNode createFolder(String parent, String folderName) {
        GDNode newNode=null;
        try {
            String id = GDOperations.createFolder(service, parent, folderName);
            newNode = new GDNode(id, folderName, (new Date()).getTime(), 0, false);
        } catch(IOException e) {
            e.printStackTrace();
        }

        return newNode;
    }

    public Drive getDriveService() {return service; }
}
