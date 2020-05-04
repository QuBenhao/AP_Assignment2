package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

public class UniLinkGUI extends Application{

    public static Connection con;
    public static HashMap<String,Stage> stages = new HashMap<>();
    public static HashMap<String,Object> controllers = new HashMap<>();
    public static final String LOGIN_WINDOW = "/view/LoginWindow.fxml";
    public static final String MAIN_WINDOW = "/view/MainWindow.fxml";
    public static final String MORE_DETAILS_WINDOW = "/view/MoreDetails.fxml";
    public static final String NEW_POST_WINDOW = "/view/NewPost.fxml";

    public static void main(String[] args) {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            con = DriverManager.getConnection("jdbc:hsqldb:file:database/UniLink", "SA", "");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        launch(args);
        try {
            con.commit();
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(LOGIN_WINDOW));
            Scene scene = new Scene(root,600,400);
            stage.setTitle("Login Window");
            stage.setScene(scene);
            stage.show();
            stages.put("LOGIN",stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}