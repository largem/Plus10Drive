package com.plus10.drive;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 27/05/2016.
 */
public final class GDOperations {

    static String uploadFile(Drive service, String parent, String title, String file2Upload, Map<String, String> props) {
        File fileMetadata = new File();
        fileMetadata.setName(title);
        fileMetadata.setMimeType("application/vnd.google-apps.document");  //https://developers.google.com/drive/v3/web/mime-types
        fileMetadata.setParents(Collections.singletonList(parent));
        if (props!= null && !props.isEmpty()) {
            fileMetadata.setAppProperties(props);
        }

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

    public static String createFolder(Drive service, String parent, String title) throws IOException {
        return createFolder(service, parent, title, null);
    }

    public static String createFolder(Drive service, String parent, String title, Map<String, String> props) throws IOException
    {
        File fileMetadata = new File();
        fileMetadata.setName(title);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setParents(Collections.singletonList(parent));
        if (props != null && !props.isEmpty()) {
            fileMetadata.setAppProperties(props);
        }

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
        Map<String, String> props =  new HashMap<>();
        props.put("plus10", "true");
        props.put("size", "100");
        fileMetaData.setAppProperties(props);

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

    public static String updateProperty(Drive service, String id, Map<String, String> props) {
        try {
            File metaData = new File();
            metaData.setAppProperties(props);

            File file = service.files().update(id, metaData).execute();
            return file.getId();
        }catch (IOException e) {}
        return null;
    }

    static void downloadFile(Drive service, String fileId, String pathTarget)    {
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

    static List<File> getSubFolders(Drive service, String parentId) {
        try {
            FileList result = service.files().list()
                    .setFields("*")
                    .setQ("'"+parentId+"' in parents and trashed=false and mimeType='application/vnd.google-apps.folder'")       //https://developers.google.com/drive/v3/web/search-parameters
                    .execute();

            return result.getFiles();

        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String getFileId(Drive service, String parentId, String name)    {
        return getId(service, parentId, name, "application/vnd.google-apps.document");
    }

    public static String getFolderId(Drive service, String parentId, String name) {
        return getId(service, parentId, name, "application/vnd.google-apps.folder");
    }

    static boolean fileExist(Drive service, String parentId, String name) {
        return (null != getFileId(service, parentId, name));
    }

    static boolean folderExist(Drive service, String parentId, String name) {
        return (null != getFolderId(service, parentId, name));
    }
}


