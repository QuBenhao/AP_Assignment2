package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import model.database.PostDB;
import model.post.Post;

import java.util.ArrayList;

public class MainWindowController {
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
    }

    @FXML
    private void ExportFile(ActionEvent actionEvent){
    }

    @FXML
    private void ImportFile(ActionEvent actionEvent){
    }

    @FXML
    public void NewEvent(ActionEvent actionEvent) {
    }

    @FXML
    public void NewSale(ActionEvent actionEvent) {
    }

    @FXML
    public void NewJob(ActionEvent actionEvent) {
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
}
