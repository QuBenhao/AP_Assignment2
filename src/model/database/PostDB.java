package model.database;

import javafx.scene.control.Alert;
import main.UniLinkGUI;
import model.post.*;

import java.sql.*;
import java.util.ArrayList;

public class PostDB {
    
    private Connection con;
    private final String searchQuery = "SELECT * FROM POST WHERE POST_ID LIKE ? AND STATUS LIKE ? AND USER_ID LIKE ? GROUP BY POST_ID ORDER BY DATE DESC";
    private final String insertQuery = "INSERT INTO POST (POST_ID,TITLE,DESCRIPTION,USER_ID,IMAGE) VALUES(?,?,?,?,?)";
    private final String insertEvent = "INSERT INTO EVENT (POST_ID,VENUE,DATE,CAPACITY) VALUES(?,?,?,?)";
    private final String insertSale = "INSERT INTO SALE (POST_ID,ASKING_PRICE,MINIMUM_RAISE,HIGHEST_OFFER) VALUES(?,?,?,null)";
    private final String insertJob = "INSERT INTO JOB (POST_ID,PROPOSED_PRICE,LOWEST_OFFER) VALUES(?,?,null)";
    private final String deletePost = "DELETE FROM POST WHERE POST_ID = ?";
    private PreparedStatement search = null;
    private PreparedStatement postStatement = null;
    private PreparedStatement insert = null;
    private PreparedStatement insertEventStatement = null;
    private PreparedStatement insertSaleStatement = null;
    private PreparedStatement insertJobStatement = null;
    private PreparedStatement deletePostStatement = null;

    public PostDB(){
        con = UniLinkGUI.con;
        SetUpSQL();
    }

    private void SetUpSQL() {
        try {
            search = con.prepareStatement(searchQuery);
            insert = con.prepareStatement(insertQuery);
            insertEventStatement = con.prepareStatement(insertEvent);
            insertSaleStatement = con.prepareStatement(insertSale);
            insertJobStatement = con.prepareStatement(insertJob);
            deletePostStatement = con.prepareStatement(deletePost);
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

    public void newPost(Post post){
        if(post==null)
            return;
        int result = 0;
        try {
            insert.setString(1,post.getPostId());
            insert.setString(2,post.getTitle());
            insert.setString(3,post.getDescription());
            insert.setString(4,post.getCreatorId());
            insert.setString(5,post.getImage());
            result = insert.executeUpdate();
            if(result!=1) {
                Alert alert = new Alert(Alert.AlertType.ERROR,"Create Post failed");
                alert.showAndWait();
            }
            else{
                deletePostStatement.setString(1,post.getPostId());
                result = 0;
                if(post instanceof Event){
                    Event event = (Event)post;
                    insertEventStatement.setString(1,event.getPostId());
                    insertEventStatement.setString(2,event.getVenue());
                    insertEventStatement.setDate(3, Date.valueOf(event.getDate()));
                    insertEventStatement.setInt(4,event.getCapacity());
                    result = insertEventStatement.executeUpdate();
                }else if(post instanceof Sale){
                    Sale sale = (Sale)post;
                    insertSaleStatement.setString(1,sale.getPostId());
                    insertSaleStatement.setDouble(2,sale.getAskingPrice());
                    insertSaleStatement.setDouble(3,sale.getMinimumRaise());
                    result = insertSaleStatement.executeUpdate();
                }else{
                    Job job = (Job)post;
                    insertJobStatement.setString(1,job.getPostId());
                    insertJobStatement.setDouble(2,job.getProposedPrice());
                    result = insertJobStatement.executeUpdate();
                }
                if(result!=1) {
                    deletePostStatement.executeUpdate();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Create Post failed");
                    alert.showAndWait();
                }
            }
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.ERROR,throwables.toString());
            alert.showAndWait();
        }
    }

    public String ID_Generator(String type){
        String query = "SELECT POST_ID FROM POST WHERE POST_ID LIKE ? GROUP BY POST_ID ORDER BY POST_ID ASC";
        try {
            PreparedStatement searchID = con.prepareStatement(query);
            if(type.compareToIgnoreCase("JOB")==0){
                searchID.setString(1,type+"%");
            }
            else
                searchID.setString(1, type.substring(0, 3)+"%");
            ResultSet result = searchID.executeQuery();
            int i = 1;
            while(result.next()){
                if(i != Integer.valueOf(result.getString(1).substring(3)))
                    break;
                i++;
            }
            if(type.compareToIgnoreCase("JOB")!=0)
                type = type.substring(0,3);
            if(i<10)
                // 00x
                return String.format("%s00%d" ,type,i);
            else if(i<100)
                // 0xx
                return String.format("%s0%d" ,type, i);
            else
                // xxx
                return String.format("%s%d" ,type, i);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

}
