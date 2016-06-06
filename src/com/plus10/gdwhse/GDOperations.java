package com.plus10.gdwhse;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 27/05/2016.
 */
public final class GDOperations {
    public static String uploadFile(Drive service, String parent, String title, String file2Upload) {
        File fileMetadata = new File();
        fileMetadata.setName(title);
        fileMetadata.setMimeType("application/vnd.google-apps.document");  //https://developers.google.com/drive/v3/web/mime-types
        fileMetadata.setParents(Collections.singletonList(parent));

        java.io.File filePath = new java.io.File(file2Upload);
        FileContent mediaContent = new FileContent("text/plain", filePath);
        File file = null;
        try {
            file = service.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
        } catch (SocketTimeoutException e) {
          //don't do thing here.
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (file != null) {
            //System.out.println("File ID: " + file.getId());
            return file.getId();
        }
        else {
            //System.out.println("Upload file failed");
        }
        return null;
    }

    public static String createFolder(Drive service, String parent, String title) throws IOException
    {
        File fileMetadata = new File();
        fileMetadata.setName(title);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setParents(Collections.singletonList(parent));

        File file = service.files().create(fileMetadata)
                            .setFields("id")
                            .execute();
        return file.getId();
    }

    public static String renameFolder(Drive service, String id, String title)
    {
        File fileMetaData = new File() ;
        fileMetaData.setName(title);
        fileMetaData.setMimeType("application/vnd.google-apps.folder");

        File file=null;
        try
        {
            file = service.files().update(id, fileMetaData).execute();
        }catch(IOException e) {
            e.printStackTrace();
        }

        if (file!=null) {
            return file.getId();
        }

        return null;
    }

    public static void downloadFile(Drive service, String fileId, String pathTarget)    {
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(pathTarget))){
            service.files().export(fileId, "text/plain").executeMediaAndDownloadTo(out);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getId(Drive service, String parentId, String name, String type) {
        try {
            FileList result = service.files().list()
                    .setQ("'"+parentId+"' in parents and trashed=false and name='"+name+"' and mimeType='"+type+"'")       //https://developers.google.com/drive/v3/web/search-parameters
                    .execute();

            List<File> files = result.getFiles();
            if (files != null && files.size() != 0) {
                return files.get(0).getId();
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static List<File> getSubFolders(Drive service, String parentId) {
        try {
            FileList result = service.files().list()
                    .setQ("'"+parentId+"' in parents and trashed=false and mimeType='application/vnd.google-apps.folder'")       //https://developers.google.com/drive/v3/web/search-parameters
                    .execute();

            return result.getFiles();

        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFileId(Drive service, String parentId, String name)    {
        return getId(service, parentId, name, "application/vnd.google-apps.document");
    }

    public static String getFolderId(Drive service, String parentId, String name) {
        return getId(service, parentId, name, "application/vnd.google-apps.folder");
    }

    public static boolean fileExist(Drive service, String parentId, String name) {
        return (null != getFileId(service, parentId, name));
    }

    public static boolean folderExist(Drive service, String parentId, String name) {
        return (null != getFolderId(service, parentId, name));
    }
}


