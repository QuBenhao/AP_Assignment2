package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.UniLinkGUI;
import model.data.ExportData;
import model.data.ImportData;
import model.database.PostDB;
import model.post.Post;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainWindowController implements Switchable{
    String[] selectType = {"ALL","EVENT","SALE","JOB"};
    String[] selectStatus = {"ALL","OPEN","CLOSED"};
    String[] selectCreator = {"ALL","MY POST"};

    @FXML private Label User_ID;
    @FXML private ComboBox Type;
    @FXML private ComboBox Status;
    @FXML private ComboBox Creator;
    @FXML private ListView<VBox> mainContent;

    private ObservableList<VBox> mainView = FXCollections.observableArrayList();
    private ObservableList<HBox> postView = FXCollections.observableArrayList();

    protected void SetUserID(String ID){
        User_ID.setText(ID);
        getPosts();
    }

    @FXML
    private void initialize(){
        Type.setItems(FXCollections.observableArrayList(selectType));
        Status.setItems(FXCollections.observableArrayList(selectStatus));
        Creator.setItems(FXCollections.observableArrayList(selectCreator));
        Type.setValue("ALL");
        Status.setValue("ALL");
        Creator.setValue("ALL");
    }

    private void getPosts() {
        PostDB postDB = new PostDB();
        ArrayList<Post> posts;
        if(Creator.getValue().toString().compareToIgnoreCase("ALL")==0)
            posts = postDB.getPosts(Type.getValue().toString(),Status.getValue().toString(),Creator.getValue().toString());
        else
            posts = postDB.getPosts(Type.getValue().toString(),Status.getValue().toString(),User_ID.getText());
        for(Post post: posts){
            postView.add(post.visualize(User_ID.getText()));
        }
        VBox vBox = new VBox();
        vBox.setSpacing(15);
        vBox.getChildren().addAll(postView);
        mainView.add(vBox);
        mainContent.setItems(mainView);
    }

    @FXML
    protected void DisplayDeveloperInfo(ActionEvent actionEvent){
        Alert alert = new Alert( Alert.AlertType.INFORMATION,"Benhao Qu\ns3773865");
        alert.setTitle("Developer Info");
        alert.setHeaderText("Developed by");
        alert.showAndWait();
    }

    @FXML
    private void Quit(ActionEvent actionEvent) {
        switchStage();
    }

    @FXML
    private void ExportFile(ActionEvent actionEvent){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Export Data File To");
        directoryChooser.setInitialDirectory(new File("././file/"));
        File selectedDirectory = directoryChooser.showDialog(UniLinkGUI.stages.get("MAIN"));
        ExportData exportData = new ExportData(selectedDirectory.getAbsolutePath());
        try {
            exportData.export();
            Alert alert = new Alert(Alert.AlertType.INFORMATION,String.format("Export file success\nFile path: %s/export_data.txt",selectedDirectory.getAbsolutePath()));
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Invalid filepath");
            alert.showAndWait();
        }
    }

    @FXML
    private void ImportFile(ActionEvent actionEvent){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Import Data File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files","*.txt"));
        File selectedFile = fileChooser.showOpenDialog(UniLinkGUI.stages.get("MAIN"));
        if(selectedFile!=null){
            ImportData importData = new ImportData(selectedFile);
            try {
                PostDB postDB = new PostDB();
                postDB.uploadData(importData.readData());
                Alert alert = new Alert(Alert.AlertType.INFORMATION,"Import file successfully");
                alert.showAndWait();
                UpdateView();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR,"Invalid input file");
                alert.showAndWait();
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR,"Invalid file");
            alert.showAndWait();
        }
    }

    @FXML
    public void NewEvent(ActionEvent actionEvent) {
        switchStage("EVENT");
    }

    @FXML
    public void NewSale(ActionEvent actionEvent) {
        switchStage("SALE");
    }

    @FXML
    public void NewJob(ActionEvent actionEvent) {
        switchStage("JOB");
    }

    public void UpdateView(){
        postView = FXCollections.observableArrayList();
        mainView = FXCollections.observableArrayList();
        getPosts();
        mainContent.refresh();
    }

    @FXML
    public void Update(ActionEvent actionEvent) {
        UpdateView();
    }

    @Override
    public void switchStage() {
        UniLinkGUI.stages.get("LOGIN").show();
        UniLinkGUI.stages.get("MAIN").close();
        UniLinkGUI.stages.remove("MAIN");
        UniLinkGUI.controllers.remove("MAIN");
    }

    public void switchStage(String type){
        FXMLLoader loader = new FXMLLoader(getClass().getResource(UniLinkGUI.NEW_POST_WINDOW));
        Parent main_Root = null;
        try {
            main_Root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        NewPostController controller = loader.getController();
        controller.setUp(type,User_ID.getText());
        assert main_Root != null;
        Scene main_Scene = new Scene(main_Root);
        Stage stage = new Stage();
        stage.setTitle("New Post");
        stage.setScene(main_Scene);
        stage.show();
        UniLinkGUI.stages.put(type.toUpperCase(),stage);
        UniLinkGUI.controllers.put(type.toUpperCase(),controller);
        UniLinkGUI.stages.get("MAIN").hide();
    }
}
