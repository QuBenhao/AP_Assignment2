package model.database;

import controller.MainWindowController;
import javafx.scene.control.Alert;
import main.UniLinkGUI;
import model.post.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class PostDB {
    
    private final Connection con;
    private PreparedStatement search = null;
    private PreparedStatement insert = null;
    private PreparedStatement insertEventStatement = null;
    private PreparedStatement insertSaleStatement = null;
    private PreparedStatement insertJobStatement = null;
    private PreparedStatement deletePostStatement = null;
    private PreparedStatement deleteReplyStatement = null;
    private PreparedStatement status = null;
    private PreparedStatement updateEvent = null;
    private PreparedStatement updateSale = null;
    private PreparedStatement updateJob = null;

    public PostDB(){
        con = UniLinkGUI.con;
        SetUpSQL();
    }

    private void SetUpSQL() {
        try {
            search = con.prepareStatement("SELECT * FROM POST WHERE POST_ID LIKE ? AND STATUS LIKE ? AND USER_ID LIKE ? GROUP BY POST_ID ORDER BY DATE DESC");
            insert = con.prepareStatement("INSERT INTO POST (POST_ID,TITLE,DESCRIPTION,USER_ID,IMAGE) VALUES(?,?,?,?,?)");
            insertEventStatement = con.prepareStatement("INSERT INTO EVENT (POST_ID,VENUE,DATE,CAPACITY) VALUES(?,?,?,?)");
            insertSaleStatement = con.prepareStatement("INSERT INTO SALE (POST_ID,ASKING_PRICE,MINIMUM_RAISE,HIGHEST_OFFER) VALUES(?,?,?,null)");
            insertJobStatement = con.prepareStatement("INSERT INTO JOB (POST_ID,PROPOSED_PRICE,LOWEST_OFFER) VALUES(?,?,null)");
            deletePostStatement = con.prepareStatement("DELETE FROM POST WHERE POST_ID = ?");
            deleteReplyStatement = con.prepareStatement("DELETE FROM REPLY WHERE POST_ID = ?");
            status = con.prepareStatement("UPDATE POST SET STATUS = 'CLOSED' WHERE POST_ID = ?");
            updateEvent = con.prepareStatement("UPDATE EVENT SET ATTENDEES_COUNT = ATTENDEES_COUNT +1 WHERE POST_ID = ?");
            updateSale = con.prepareStatement("UPDATE SALE SET HIGHEST_OFFER = ? WHERE POST_ID = ?");
            updateJob = con.prepareStatement("UPDATE JOB SET LOWEST_OFFER = ? WHERE POST_ID = ?");
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
                        PreparedStatement postStatement = con.prepareStatement(searchPost.toString());
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
        try {
            insert.setString(1,post.getPostId());
            insert.setString(2,post.getTitle());
            insert.setString(3,post.getDescription());
            insert.setString(4,post.getCreatorId());
            insert.setString(5,post.getImage());
            int result = insert.executeUpdate();
            if(result!=1) {
                Alert alert = new Alert(Alert.AlertType.ERROR,"Create Post failed");
                alert.showAndWait();
            }
            else{
                deletePostStatement.setString(1,post.getPostId());
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
                if(i != Integer.parseInt(result.getString(1).substring(3)))
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

    public void closePost(String postID){
        try {
            status.setString(1,postID);
            status.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void update(Post post,Reply reply){
        try {
            if(post instanceof Event) {
                updateEvent.setString(1,post.getPostId());
                updateEvent.executeUpdate();
            }else if(post instanceof Sale){
                updateSale.setDouble(1,reply.getValue());
                updateSale.setString(2,post.getPostId());
                updateSale.executeUpdate();
            }else if(post instanceof Job){
                updateJob.setDouble(1,reply.getValue());
                updateJob.setString(2,post.getPostId());
                updateJob.executeUpdate();
            }
            ((MainWindowController)UniLinkGUI.controllers.get("MAIN")).UpdateView();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void updatePost(String postID, HashMap<String,String> changes){
        if(changes.isEmpty())
            return;
        String key;
        try {
            PreparedStatement update ;
            for(String k:changes.keySet()){
                key = k.split(":")[0];
                StringBuilder stringBuilder = new StringBuilder("UPDATE  SET  = ? WHERE POST_ID = ");
                stringBuilder.append(String.format("'%s'",postID));
                stringBuilder.insert(12,key.toUpperCase());
                if(key.compareToIgnoreCase("TITLE")==0 || key.compareToIgnoreCase("DESCRIPTION")==0)
                    stringBuilder.insert(7,"POST");
                else if (key.compareToIgnoreCase("VENUE") == 0)
                    stringBuilder.insert(7,"EVENT");
                else if (key.compareToIgnoreCase("DATE")==0)
                    stringBuilder.insert(7,"EVENT");
                else if (key.compareToIgnoreCase("CAPACITY")==0)
                    stringBuilder.insert(7,"EVENT");
                else if (key.compareToIgnoreCase("ASKINGPRICE")==0 || key.compareToIgnoreCase("MINIMUM_RAISE")==0)
                    stringBuilder.insert(7,"SALE");
                else if (key.compareToIgnoreCase("PROPOSEDPRICE")==0)
                    stringBuilder.insert(7,"JOB");
                update = con.prepareStatement(stringBuilder.toString());
                if(key.compareToIgnoreCase("TITLE")==0 || key.compareToIgnoreCase("DESCRIPTION")==0 || (key.compareToIgnoreCase("VENUE") == 0))
                    update.setString(1,changes.get(k));
                else if (key.compareToIgnoreCase("DATE")==0)
                    update.setDate(1,Date.valueOf(changes.get(k)));
                else if (key.compareToIgnoreCase("CAPACITY")==0)
                    update.setInt(1,Integer.parseInt(changes.get(k)));
                else if (key.compareToIgnoreCase("ASKINGPRICE")==0 || key.compareToIgnoreCase("MINIMUM_RAISE")==0 || key.compareToIgnoreCase("PROPOSEDPRICE")==0)
                    update.setDouble(1,Double.parseDouble(changes.get(k)));
                update.executeUpdate();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deletePost(String postId) {
        try {
            deleteReplyStatement.setString(1, postId);
            deletePostStatement.setString(1, postId);
            StringBuilder stringBuilder = new StringBuilder("DELETE FROM ");
            if(postId.substring(0,3).compareToIgnoreCase("EVE")==0)
                stringBuilder.append("EVENT");
            else if(postId.substring(0,3).compareToIgnoreCase("SAL")==0)
                stringBuilder.append("SALE");
            else
                stringBuilder.append("JOB");
            stringBuilder.append(" WHERE POST_ID = ?");
            PreparedStatement delete = con.prepareStatement(stringBuilder.toString());
            delete.setString(1,postId);
            deleteReplyStatement.executeUpdate();
            delete.executeUpdate();
            deletePostStatement.executeUpdate();
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
