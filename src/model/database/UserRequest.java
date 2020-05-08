package model.database;

import javafx.scene.control.Alert;
import main.UniLinkGUI;
import model.exception.InputFormatException;
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

    public boolean Login(String USER_ID,String password) throws InputFormatException {
        try {
            ID_Check(USER_ID);
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

    public boolean Register(String[] input) throws InputFormatException {
        try {
            ID_Check(input[0]);
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

    private void ID_Check(String ID) throws InputFormatException {
        if (ID.length() != 8) {
            throw new InputFormatException("The length of User_ID should be 8");
        }
        if (ID.charAt(0) != 's') {
            if (ID.charAt(0) != 'S')
                throw new InputFormatException("User_ID should start with the letter S");
        }
        try {
            Integer.parseInt(ID.substring(1));
        } catch (NumberFormatException ex) {
            throw new InputFormatException("User_ID should have all numbers starting with S");
        }
    }
}
