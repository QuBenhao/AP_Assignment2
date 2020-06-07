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

public class UniLinkGUI extends Application {

    // DB connection
    public static Connection con;

    // Stages
    public static final HashMap<String, Stage> stages = new HashMap<>();

    // Controllers
    public static final HashMap<String, Object> controllers = new HashMap<>();

    public static final String LOGIN_WINDOW = "/view/LoginWindow.fxml";
    public static final String MAIN_WINDOW = "/view/MainWindow.fxml";
    public static final String MORE_DETAILS_WINDOW = "/view/MoreDetails.fxml";
    public static final String NEW_POST_WINDOW = "/view/NewPost.fxml";
    public static final String Default_ImageName = "No_image_available.png";

    public static void main(String[] args) {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            con = DriverManager.getConnection("jdbc:hsqldb:file:database/UniLink", "SA", "");
            launch(args);
            con.commit();
            con.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(LOGIN_WINDOW));
            Scene scene = new Scene(root);
            stage.setTitle("Login Window");
            stage.setScene(scene);
            stage.show();
            stages.put("LOGIN", stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}