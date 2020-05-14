package model.exception;

import javafx.scene.control.Alert;

public class AlertException extends Exception{

    public AlertException(String message){
        super(message);
    }

    public void display(){
        Alert alert = new Alert(Alert.AlertType.ERROR,super.getMessage());
        String[] spl = getClass().getName().split("exception.");
        alert.setTitle("EXCEPTION");
        alert.setHeaderText(spl[spl.length-1]);
        alert.showAndWait();
    }
}
