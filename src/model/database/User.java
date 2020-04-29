package model.database;

import javafx.scene.control.Alert;
import model.exception.UserNotExistException;

import java.sql.*;

public class User {
    final private static String searchQuery = "SELECT * from USER WHERE USER_ID = ? AND USER_PASSWORD = ?";
    final private static String insertQuery = "INSERT INTO USER VALUES(?,?,?)";
    private static PreparedStatement search = null;
    private static PreparedStatement insert = null;
    private static ResultSet result = null;

    public static boolean Login(Connection con,String USER_ID,String password){
        try {
            if(!ID_Check(USER_ID))
                return false;
            search = con.prepareStatement(searchQuery);
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
            Alert alert = new Alert(Alert.AlertType.ERROR,ex.getMessage());
            alert.showAndWait();
        }
        return false;
    }

    public static boolean Register(Connection con,String[] input){
        try {
            if(!ID_Check(input[0])){
                return false;
            }
            insert = con.prepareStatement(insertQuery);
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

    private static boolean ID_Check(String ID){
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
