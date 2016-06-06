
import java.io.*;
import java.nio.file.FileAlreadyExistsException;

import com.google.api.services.drive.Drive;
import com.plus10.gdwhse.*;
import com.sun.org.apache.bcel.internal.generic.RET;

public class Plus10Drive {
    private static final int RETRY = 5;
    private static final String METADATA_FILE =".plus10.meta";

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

    private static EncodeResult downloadEncodedFiles(Drive service, String folderId, String downloadFolder) throws IOException{
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

    private static void upload(Drive service, String gdParent, String filePath) throws IOException {
        final String uploadTemp = "/Work/temp/Plus10Drive/upload";

        File f = new File(filePath);
        if (!f.exists()) {
            throw new FileNotFoundException(filePath);
        }
        String fileName = f.getName();

        if (GDOperations.folderExist(service, gdParent, fileName)) {
            throw new FileAlreadyExistsException(fileName);
        }

        String uploadFolderId = GDOperations.createFolder(service, gdParent, fileName);

        EncodeResult res = FileEncoder.encode(filePath, uploadTemp);
        uploadEncodedFiles(service, uploadFolderId, res);

        String mateDataPath = uploadTemp + "/" + METADATA_FILE;
        res.dump(mateDataPath);

        GDOperations.uploadFile(service, uploadFolderId, METADATA_FILE, mateDataPath);

        //TODO, remove all the temp files
    }

    private static void download(Drive service, String gdParent, String fileName, String targetFile) throws IOException{
        final String downloadTemp = "/Work/temp/Plus10Drive/download";

        String downloadFolderId = GDOperations.getFolderId(service, gdParent, fileName);
        EncodeResult res1 = downloadEncodedFiles(service, downloadFolderId, downloadTemp);
        FileEncoder.decode(res1, targetFile);

        //TODO, remove all the temp files
    }

    private static void DriveWork(String filePath) throws IOException {
        // Build a new authorized API client service.

        Drive service = GDSWrapper.getDriveService();

        String appFolderId = GDOperations.getFolderId(service, "root", "Plus10Drive");
        if (appFolderId == null) {
            appFolderId = GDOperations.createFolder(service, "root", "Plus10Drive");
        }

        upload(service, appFolderId, filePath);
        download(service, appFolderId, new File(filePath).getName(), "/Work/temp/Plus10Drive/New Pic.JPG");
    }

    public static void main(String[] args) throws IOException {
        DriveWork("/Work/temp/Plus10Drive/P1070953.JPG");
    }
}