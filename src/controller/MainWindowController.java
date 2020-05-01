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
import javafx.stage.Stage;
import main.UniLinkGUI;
import model.database.PostDB;
import model.post.Post;

import java.io.IOException;
import java.util.ArrayList;

public class MainWindowController implements Switchable{
    String selectType[] = {"ALL","EVENT","SALE","JOB"};
    String selectStatus[] = {"ALL","OPEN","CLOSED"};
    String selectCreator[] = {"ALL","MY POST"};

    @FXML private Label User_ID;
    @FXML private ComboBox Type;
    @FXML private ComboBox Status;
    @FXML private ComboBox Creator;
    @FXML private ListView<HBox> mainContent;

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
        mainContent.setItems(postView);
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
    }

    @FXML
    private void ImportFile(ActionEvent actionEvent){
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

    private void UpdateView(){
        postView = FXCollections.observableArrayList();
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/NewPost.fxml"));
        Parent main_Root = null;
        try {
            main_Root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        NewPostController controller = (NewPostController) loader.getController();
        controller.setUp(type,User_ID.getText());
        Scene main_Scene = new Scene(main_Root,1200,800);
        Stage stage = new Stage();
        stage.setTitle("New Post");
        stage.setScene(main_Scene);
        stage.show();
        UniLinkGUI.stages.put(type.toUpperCase(),stage);
        UniLinkGUI.controllers.put(type.toUpperCase(),controller);
        UniLinkGUI.stages.get("MAIN").hide();
    }
}
