package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import main.UniLinkGUI;
import model.database.UserRequest;
import model.exception.InputFormatException;

import java.io.IOException;
import java.util.Optional;

public class LoginWindowController implements Switchable {
    private final UserRequest userRequest = new UserRequest();

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField passwordTextField;

    @FXML
    private void initialize() {

    }

    private void reset() {
        nameTextField.setText("");
        passwordTextField.setText("");
    }

    @FXML
    private void LoginButtonHandler() {
        if (nameTextField.getText().equals("")) {
            try {
                throw new InputFormatException("User ID cannot be empty!");
            } catch (InputFormatException e) {
                e.display();
            }
        } else
            try {
                if (userRequest.Login(nameTextField.getText(), passwordTextField.getText()))
                    switchStage();
            } catch (InputFormatException ex) {
                ex.display();
            }
    }

    @FXML
    private void RegisterButtonHandler() {
        if (nameTextField.getText().equals("")) {
            try {
                throw new InputFormatException("User ID cannot be empty!");
            } catch (InputFormatException e) {
                e.display();
            }
        } else {
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
                try {
                    if (userRequest.Register(input))
                        switchStage();
                } catch (InputFormatException e) {
                    e.display();
                }
            });
        }
    }

    @Override
    public void switchStage() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(UniLinkGUI.MAIN_WINDOW));
        Parent main_Root = null;
        try {
            main_Root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MainWindowController controller = loader.getController();
        controller.SetUserID(nameTextField.getText());
        assert main_Root != null;
        Scene main_Scene = new Scene(main_Root);
        Stage stage = new Stage();
        stage.setTitle("Main Window");
        stage.setScene(main_Scene);
        stage.show();
        // Store stage and controller
        UniLinkGUI.stages.put("MAIN", stage);
        UniLinkGUI.controllers.put("LOGIN", this);
        UniLinkGUI.controllers.put("MAIN", controller);
        UniLinkGUI.stages.get("LOGIN").close();
        reset();
    }
}