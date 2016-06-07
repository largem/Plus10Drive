
import java.io.*;
import com.google.api.services.drive.Drive;
import com.plus10.drive.*;
import javafx.application.Application;
import javafx.stage.Stage;


public class Plus10Drive extends Application {

    private static void TestUpLoad(Drive service) throws IOException {
        final String filePath = "/Work/temp/Plus10Drive/P1070953.JPG";

        String appFolderId = GDOperations.getFolderId(service, "root", "Plus10Drive");
        if (appFolderId == null) {
            appFolderId = GDOperations.createFolder(service, "root", "Plus10Drive");
        }

        Plus10DriveHelper.upload(service, appFolderId, filePath);
    }

    private static void TestDownload(Drive service) throws  IOException {
        final String filePath = "P1070953.JPG";

        String appFolderId = GDOperations.getFolderId(service, "root", "Plus10Drive");
        Plus10DriveHelper.download(service, appFolderId, filePath, "/Work/temp/Plus10Drive/New Pic.JPG");
    }

    public static void main(String[] args) throws IOException {
        // Build a new authorized API client service.

        /*Drive service = GDSWrapper.getDriveService();

        //TestUpLoad(service);
        //TestDownload(service);

        String appFolderId = GDOperations.getFolderId(service, "root", "Plus10Drive");
        GDOperations.renameFolder(service, appFolderId, "Plus10Drive");

        GDNode root = new GDNode("root", "Plus10 Drive", 0, 0, false);
        Plus10DriveHelper.populateNodeTree(service, root);
        */

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }
}