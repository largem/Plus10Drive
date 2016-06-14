
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.plus10.drive.*;
import com.plus10.drive.UI.DriveItem;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class Plus10Drive extends Application {

    /*
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

   */
    public static void main(String[] args) throws IOException {
        // Build a new authorized API client service.

        /*Drive service = GDSWrapper.getDriveService();

        //TestUpLoad(service);
        //TestDownload(service);

        String appFolderId = GDOperations.getFolderId(service, "root", "Plus10Drive");
        GDOperations.renameFolder(service, appFolderId, "Plus10Drive");

        GDNode root = new GDNode("root", "Plus10 Drive", 0, 0, false);
        Plus10DriveHelper.populateP10Drive(service, root);
        */

        launch(args);
    }

    private Stage window;
    private TreeView<IGDNode> driveTree;
    private TableView<IGDNode> driveTable;
    private Button connectBtn;
    private Label statusLabel;
    private Plus10DriveService service;
    private TextField inputText;

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("Plus10 Drive");
        //window.setWidth(800);
        //window.setHeight(600);

        BorderPane mainPane = new BorderPane();
        mainPane.setPadding(new Insets(10, 10, 10, 10));

        //Hbox on the top
        HBox hbox = new HBox();
        hbox.setSpacing(5);
        connectBtn = new Button("Connect to Drive");
        connectBtn.setOnAction(e -> connectDrive());
        Button createDirBtn = new Button("Create Folder");
        createDirBtn.setOnAction(e -> createFolder());
        inputText = new TextField();
        hbox.getChildren().addAll(connectBtn, createDirBtn, inputText);
        mainPane.setTop(hbox);

        //Tree on the left
        //TreeItem<String> root = new TreeItem<>("Plus10 Drive");
        //root.setExpanded(true);
        driveTree = new TreeView<>();
        driveTree.getSelectionModel().selectedItemProperty()
                .addListener((v, oldValue, newValue) -> driveTreeItemSelected(v, oldValue, newValue));
        mainPane.setLeft(driveTree);

        //Table on the center (along with some buttons)
        HBox hbox1 = new HBox();
        hbox1.setPadding(new Insets(0, 10, 0, 0));
        hbox1.setSpacing(5);
        Button uploadBtn = new Button("Upload");
        uploadBtn.setOnAction(e -> uploadBtnClicked());
        Button downloadBtn = new Button("Download");
        Button propertyBtn = new Button("Property");
        Button previewBtn = new Button("Preview");
        hbox1.getChildren().addAll(uploadBtn, downloadBtn, propertyBtn, previewBtn);

        //create Table Columns
        TableColumn<IGDNode, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setMinWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<IGDNode, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setMinWidth(80);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("mtime"));

        TableColumn<IGDNode, Long> sizeColumn = new TableColumn<>("Size");
        sizeColumn.setMinWidth(60);
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));

        driveTable = new TableView<>();
        ObservableList<IGDNode> items = FXCollections.observableArrayList();
        items.add(new GDNode("abc", "name1", (new Date()).getTime(), 100, true));
        driveTable.setItems(items);
        driveTable.getColumns().addAll(nameColumn, dateColumn, sizeColumn);

        VBox vbox = new VBox();
        vbox.setVgrow(driveTable, Priority.ALWAYS);
        vbox.getChildren().addAll(hbox1, driveTable);

        mainPane.setCenter(vbox);

        Label status = new Label("Status:");
        statusLabel = new Label("Not Connected");
        HBox hBoxStatus = new HBox();
        hBoxStatus.setSpacing(5);
        hBoxStatus.getChildren().addAll(status, statusLabel);
        mainPane.setBottom(hBoxStatus);

        Scene scene = new Scene(mainPane, 800, 600);
        window.setScene(scene);
        window.show();
    }

    private void connectDrive() {
        try {
            service = new Plus10DriveService();
            IGDNode root = service.getPlus10DriveNode();
            TreeItem<IGDNode> r = new TreeItem<>(root);
            driveTree.setRoot(r);
            populateP10Drive(r, root);

            connectBtn.setDisable(true);
            statusLabel.setText("Connected");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFolder() {
        TreeItem<IGDNode> node = driveTree.getSelectionModel().getSelectedItem();
        String folderName = inputText.getText();
        TreeItem<IGDNode> newNode = new TreeItem<>(service.createFolder(node.getValue().getId(), folderName));
        node.getChildren().add(newNode);
        inputText.clear();
    }

    private void populateP10Drive(TreeItem<IGDNode> treeNode, IGDNode gdNode) {
        if (gdNode != null) {
            IGDNode[] children = gdNode.getChildren();
            if (children!=null) {
                for (IGDNode child : children) {
                    if (!child.isP10Item()) {
                        TreeItem<IGDNode> me = new TreeItem<>(child);
                        treeNode.getChildren().add(me);
                        populateP10Drive(me, child);
                    } else {
                        //add it to tableview, or wait until the node is selected
                    }
                }
            }
        }
    }

    private void driveTreeItemSelected(ObservableValue<? extends TreeItem<IGDNode>> v, TreeItem<IGDNode> oldValue, TreeItem<IGDNode> newValue) {
        driveTable.setItems(FXCollections.observableList(Arrays.asList(newValue.getValue().getChildren())));

        System.out.println(oldValue +" "+newValue);
    }

    private void uploadBtnClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload File");
        File file = fileChooser.showOpenDialog(window);

        if (file != null) {
            System.out.println(file.getAbsolutePath());
            IGDNode selectedNode = driveTree.getSelectionModel().getSelectedItem().getValue();
            String id = service.uploadFile(selectedNode.getId(),
                                           file.getAbsolutePath());
            Date now = new Date();
            GDNode newNode = new GDNode(id, file.getName(), now.getTime(), file.length(), true);
            ((GDNode)selectedNode).addChild(newNode);

            driveTable.setItems(FXCollections.observableList(Arrays.asList(selectedNode.getChildren())));
        }
    }

}