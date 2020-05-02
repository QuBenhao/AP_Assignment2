package model.database;

import javafx.scene.control.Alert;
import main.UniLinkGUI;
import model.exception.UserNotExistException;

import java.sql.*;

public class UserRequest {
    private Connection con;
    private final String searchQuery = "SELECT * FROM USER WHERE USER_ID = ? AND USER_PASSWORD = ?";
    private final String insertQuery = "INSERT INTO USER VALUES(?,?,?)";
    private PreparedStatement search = null;
    private PreparedStatement insert = null;
    private ResultSet result = null;

    public UserRequest(){
        this.con = UniLinkGUI.con;
        SetUpSQL();
    }

    public UserRequest(Connection con){
        this.con = con;
        SetUpSQL();
    }

    private void SetUpSQL(){
        try {
            search = con.prepareStatement(searchQuery);
            insert = con.prepareStatement(insertQuery);
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.ERROR,throwables.toString());
            alert.showAndWait();
        }
    }

    public boolean Login(String USER_ID,String password){
        try {
            if(!ID_Check(USER_ID))
                return false;
            search.setString(1,USER_ID);
            search.setString(2,password);
            result = search.executeQuery();
            if(result.next())
                return true;
            throw new UserNotExistException("Invalid User_ID or User_Password!");
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.ERROR,throwables.toString());
            alert.showAndWait();
        } catch (UserNotExistException ex){
            ex.display();
        }
        return false;
    }

    public boolean Register(String[] input){
        try {
            if(!ID_Check(input[0])){
                return false;
            }
            for(int i = 0 ; i < input.length; i++){
                insert.setString(i+1,input[i]);
            }
            int result = insert.executeUpdate();
            if(result == 1){
                return true;
            }
        } catch (SQLIntegrityConstraintViolationException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR,input[0] + " already exists!");
            alert.showAndWait();
        }catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.ERROR,throwables.toString());
            alert.showAndWait();
        }
        return false;
    }

    private boolean ID_Check(String ID){
        if(ID.length()!=8){
            Alert alert = new Alert(Alert.AlertType.ERROR,"The length of User_ID should be 8");
            alert.showAndWait();
            return false;
        }
        if(ID.charAt(0)!='s'){
            if(ID.charAt(0)!='S') {
                Alert alert = new Alert(Alert.AlertType.ERROR,"User_ID should start with letter S");
                alert.showAndWait();
                return false;
            }
        }
        try{
            Integer.parseInt(ID.substring(1));
        }catch (Exception ex){
            Alert alert = new Alert(Alert.AlertType.ERROR,"User_ID should have all numbers startign with S");
            alert.showAndWait();
            return false;
        }
        return true;
    }
}
