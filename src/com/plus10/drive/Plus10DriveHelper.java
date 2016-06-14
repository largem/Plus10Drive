package com.plus10.drive;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

/**
 * Created by Administrator on 06/06/2016.
 */
public class Plus10DriveHelper {
    private static final int RETRY = 5;
    private static final String METADATA_FILE =".plus10.meta";
    private static final String UPLOAD_TEMP = "/Work/temp/Plus10Drive/upload";
    private static final String DOWNLOAD_TEMP = "/Work/temp/Plus10Drive/download";

    private static Boolean plus10MetaExist(Drive service, String parentId) {
        return GDOperations.fileExist(service, parentId, METADATA_FILE);
    }

    private static long getPlusItemSize(Drive service, String id) {
        return 0;
    }


    public static void populateNodeTree(Drive service, GDNode parent) {
        List<File> subFolders = GDOperations.getSubFolders(service, parent.getId());

        if (subFolders != null && subFolders.size() > 0) {
            for (File d : subFolders) {
                String id = d.getId();
                Boolean isPlus10Item =plus10MetaExist(service, id);
                long size = 0;
                if (isPlus10Item) size = getPlusItemSize(service, id);
                GDNode child = new GDNode(id, d.getName(), 0, size, isPlus10Item);
                parent.addChild(child);
                if (!isPlus10Item) {
                    populateNodeTree(service, child);
                }
            }
        }
    }

    private static void sleep(int sec) {
        try {
            Thread.sleep(sec * 1000);
        }catch (InterruptedException e) {}
    }

    private static void uploadEncodedFiles(Drive service, String gdParent, EncodeResult res) {
        final String root = res.getRoot();
        int retry=0;
        while (retry++ < RETRY) {
            int count = 0;
            for (MetaData md : res.getMetaData()) {
                String path = root + "/" + md.getName();
                System.out.println("Uploading " + md.getName() + ", retry=" + new Integer(retry).toString());
                if (!GDOperations.fileExist(service, gdParent, md.getName())) {
                    GDOperations.uploadFile(service, gdParent, md.getName(), path);
                } else {
                    count++;
                    System.out.println("Upload " + md.getName() + " successfully.");
                }
                sleep(1);
            }
            if (count == res.getMetaData().length) {
                System.out.println("Upload successfully.");
                break;
            } else {
                sleep(5);
            }
        }
    }

    private static EncodeResult downloadEncodedFiles(Drive service, String folderId, String downloadFolder) throws IOException {
        EncodeResult res = new EncodeResult(downloadFolder);

        String metaDataId = GDOperations.getFileId(service, folderId, METADATA_FILE);
        if (null == metaDataId) {
            throw new FileNotFoundException(METADATA_FILE);
        }

        String metaDataPath = downloadFolder + "/" + METADATA_FILE;
        GDOperations.downloadFile(service, metaDataId, metaDataPath);

        res.importFrom(metaDataPath);

        for (MetaData md : res.getMetaData()) {
            String fileId = GDOperations.getFileId(service, folderId, md.getName());
            if (null != fileId) {
                String path = downloadFolder + "/" + md.getName();
                GDOperations.downloadFile(service, fileId, path);
            } else {
                throw new FileNotFoundException(md.getName());
            }
        }

        return res;
    }

    public static String upload(Drive service, String gdParent, String filePath) throws IOException {

        java.io.File f = new java.io.File(filePath);
        if (!f.exists()) {
            throw new FileNotFoundException(filePath);
        }
        String fileName = f.getName();

        if (GDOperations.folderExist(service, gdParent, fileName)) {
            throw new FileAlreadyExistsException(fileName);
        }

        String uploadFolderId = GDOperations.createFolder(service, gdParent, fileName);

        EncodeResult res = FileEncoder.encode(filePath, UPLOAD_TEMP);
        uploadEncodedFiles(service, uploadFolderId, res);

        String mateDataPath = UPLOAD_TEMP + "/" + METADATA_FILE;
        res.dump(mateDataPath);

        return GDOperations.uploadFile(service, uploadFolderId, METADATA_FILE, mateDataPath);

        //TODO, remove all the temp files
    }

    public static void download(Drive service, String gdParent, String fileName, String targetFile) throws IOException{

        String downloadFolderId = GDOperations.getFolderId(service, gdParent, fileName);
        EncodeResult res1 = downloadEncodedFiles(service, downloadFolderId, DOWNLOAD_TEMP);
        FileEncoder.decode(res1, targetFile);

        //TODO, remove all the temp files
    }
}
