package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class MainWindowController {
    @FXML private Label User_ID;
    @FXML private ComboBox<String>[] comboBoxes = new ComboBox[3];

    protected void SetUserID(String ID){
        User_ID.setText(ID);
    }

    @FXML
    private void initialize(){
        
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

    public void NewEvent(ActionEvent actionEvent) {
    }

    public void NewSale(ActionEvent actionEvent) {
    }

    public void NewJob(ActionEvent actionEvent) {
    }

}
