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
    public static final String LOGIN_WINDOW = "/view/login_window.fxml";
    public static final String MAIN_WINDOW = "/view/main_window.fxml";

    public static void main(String[] args) {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            con = DriverManager.getConnection("jdbc:hsqldb:file:database/UniLink", "SA", "");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        launch(args);
        try {
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