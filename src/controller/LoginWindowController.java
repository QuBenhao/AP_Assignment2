package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import main.UniLinkGUI;
import model.database.UserRequest;

import java.io.IOException;
import java.util.Optional;

public class LoginWindowController implements Switchable{
    private final UserRequest userRequest = new UserRequest();
    private final Alert emptyIDError = new Alert(Alert.AlertType.ERROR,"ID cannot be empty!");

    @FXML private TextField nameTextField;
    @FXML private TextField passwordTextField;

    @FXML private void initialize(){

    }

    private void reset(){
        nameTextField.setText("");
        passwordTextField.setText("");
    }

    @FXML private void LoginButtonHandler(ActionEvent actionEvent){
        if(nameTextField.getText().equals("")){
            emptyIDError.showAndWait();
        }
        else{
            if(userRequest.Login(nameTextField.getText(),passwordTextField.getText())) {
                switchStage();
            }
        }
    }

    @FXML private void RegisterButtonHandler(ActionEvent actionEvent){
        if(nameTextField.getText().equals("")){
            emptyIDError.showAndWait();
        }
        else {
            String[] input = new String[3];
            input[0] = nameTextField.getText();
            input[2] = passwordTextField.getText();
            TextInputDialog nameInput = new TextInputDialog("Default");
            nameInput.setTitle("Register");
            nameInput.setHeaderText(null);
            nameInput.setContentText("Please enter your name");
            Optional<String> result = nameInput.showAndWait();
            result.ifPresent(name -> {
                input[1] = name;
                if (userRequest.Register(input)) {
                    switchStage();
                }
            });
        }
    }

    @Override
    public void switchStage() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainWindow.fxml"));
        Parent main_Root = null;
        try {
            main_Root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MainWindowController controller = loader.getController();
        controller.SetUserID(nameTextField.getText());
        assert main_Root != null;
        Scene main_Scene = new Scene(main_Root,1200,800);
        Stage stage = new Stage();
        stage.setTitle("Main Window");
        stage.setScene(main_Scene);
        stage.show();
        UniLinkGUI.stages.put("MAIN",stage);
        UniLinkGUI.controllers.put("LOGIN",this);
        UniLinkGUI.controllers.put("MAIN",controller);
        UniLinkGUI.stages.get("LOGIN").close();
        reset();
    }
}