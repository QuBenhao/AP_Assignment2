package model.database;

import javafx.scene.control.Alert;
import main.UniLinkGUI;
import model.post.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PostDB {
    
    private Connection con;
    private final String searchQuery = "SELECT * FROM POST WHERE POST_ID LIKE ? AND STATUS LIKE ? AND USER_ID LIKE ? GROUP BY POST_ID ORDER BY DATE DESC";
    private final String insertQuery = "INSERT INTO POST (POST_ID,TITLE,DESCRIPTION,USER_ID,IMAGE) VALUES(?,?,?,?,?)";
    private PreparedStatement search = null;
    private PreparedStatement postStatement = null;
    private PreparedStatement insert = null;

    public PostDB(){
        con = UniLinkGUI.con;
        SetUpSQL();
    }

    private void SetUpSQL() {
        try {
            search = con.prepareStatement(searchQuery);
            insert = con.prepareStatement(insertQuery);
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.ERROR,throwables.toString());
            alert.showAndWait();
        }
    }

    public ArrayList<Post> getPosts(String Type,String Status,String Creator){
        ArrayList<Post> posts = new ArrayList<>();
        try {
            if(Type.compareToIgnoreCase("ALL")==0)
                search.setString(1, "%");
            else{
                if(Type.compareToIgnoreCase("JOB")==0){
                    search.setString(1,Type+"%");
                }
                else
                    search.setString(1, Type.substring(0, 3)+"%");
            }

            if(Status.compareToIgnoreCase("ALL")==0)
                search.setString(2,"%");
            else
                search.setString(2,Status);

            if(Creator.compareToIgnoreCase("ALL")==0)
                search.setString(3,"%");
            else
                search.setString(3,Creator);

            ResultSet result = search.executeQuery();
            ResultSet resultSet;
            while (result.next()){
                StringBuilder searchPost = new StringBuilder("SELECT * FROM  WHERE POST_ID = ?");
                switch (result.getString(1).substring(0,3).toUpperCase()){
                    case "EVE":
                        searchPost.insert(14,"EVENT");
                        postStatement = con.prepareStatement(searchPost.toString());
                        postStatement.setString(1,result.getString(1));
                        resultSet = postStatement.executeQuery();
                        if(resultSet.next())
                            posts.add(new Event(result.getString(1),
                                    result.getString(2),
                                    result.getString(3),
                                    result.getString(4),
                                    result.getString(5),
                                    result.getString(7),
                                    resultSet.getString(2),
                                    resultSet.getDate(3).toString(),
                                    resultSet.getInt(4),
                                    resultSet.getInt(5)));
                        break;
                    case "SAL":
                        searchPost.insert(14,"SALE");
                        postStatement = con.prepareStatement(searchPost.toString());
                        postStatement.setString(1,result.getString(1));
                        resultSet = postStatement.executeQuery();
                        if(resultSet.next())
                            posts.add(new Sale(result.getString(1),
                                    result.getString(2),
                                    result.getString(3),
                                    result.getString(4),
                                    result.getString(5),
                                    result.getString(7),
                                    resultSet.getDouble(2),
                                    resultSet.getDouble(3),
                                    resultSet.getDouble(4)));
                        break;
                    case "JOB":
                        searchPost.insert(14,"JOB");
                        postStatement = con.prepareStatement(searchPost.toString());
                        postStatement.setString(1,result.getString(1));
                        resultSet = postStatement.executeQuery();
                        if(resultSet.next())
                            posts.add(new Job(result.getString(1),
                                    result.getString(2),
                                    result.getString(3),
                                    result.getString(4),
                                    result.getString(5),
                                    result.getString(7),
                                    resultSet.getDouble(2),
                                    resultSet.getDouble(3)));
                        break;
                    default:
                        continue;
                }
            }
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return posts;
    }
}
