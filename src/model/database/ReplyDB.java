package model.database;

import javafx.scene.control.Alert;
import main.UniLinkGUI;
import model.exception.AlreadyReplyException;
import model.post.Reply;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReplyDB {
    private final Connection con;
    private PreparedStatement check = null;
    private PreparedStatement event = null;
    private PreparedStatement sale = null;
    private PreparedStatement job = null;
    private PreparedStatement search = null;

    public ReplyDB(){
        con = UniLinkGUI.con;
        SetUpSQL();
    }

    private void SetUpSQL() {
        try {
            check = con.prepareStatement("SELECT * FROM REPLY WHERE POST_ID = ? AND USER_ID LIKE ? ORDER BY TIME ASC");
            event = con.prepareStatement("SELECT CAPACITY,ATTENDEES_COUNT FROM EVENT WHERE POST_ID = ?");
            sale = con.prepareStatement("SELECT ASKING_PRICE,MINIMUM_RAISE,HIGHEST_OFFER FROM SALE WHERE POST_ID = ?");
            job = con.prepareStatement("SELECT LOWEST_OFFER FROM JOB WHERE POST_ID = ?");
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.ERROR,throwables.toString());
            alert.showAndWait();
        }
    }

    public ArrayList<Reply> checkExist(String postID){
        ArrayList<Reply> replies = new ArrayList<>();
        try {
            check.setString(1, postID);
            check.setString(2, "%");
            ResultSet result = check.executeQuery();
            while(result.next()){
                replies.add(new Reply(result.getString(1),result.getString(2),result.getDouble(4)));
            }
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return replies;
    }

    public boolean checkExist(Reply reply){
        try {
            check.setString(1, reply.getPostId());
            check.setString(2, reply.getResponderId());
            ResultSet result = check.executeQuery();
            if(result.next())
                throw new AlreadyReplyException("You have already reply to this post!");
            return true;
        }
         catch (SQLException throwables) {
            throwables.printStackTrace();
        }catch (AlreadyReplyException ex){
            ex.display();
        }
        return false;
    }

    public int[] checkEvent(Reply reply){
        int[] capacityCheck = new int[2];
        try{
            event.setString(1,reply.getPostId());
            ResultSet result = event.executeQuery();
            if(result.next()){
                capacityCheck[0] = Integer.parseInt(result.getString(1));
                capacityCheck[1] = Integer.parseInt(result.getString(2));
                return capacityCheck;
            }
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public double[] checkSale(Reply reply){
        double[] priceCheck = new double[3];
        try{
            sale.setString(1,reply.getPostId());
            ResultSet result = sale.executeQuery();
            if(result.next()){
                priceCheck[0] = Double.parseDouble(result.getString(1));
                priceCheck[1] = Double.parseDouble(result.getString(2));
                if(result.getString(3)==null)
                    priceCheck[2] = 0;
                else
                    priceCheck[2] = Double.parseDouble(result.getString(3));
                return priceCheck;
            }
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public double checkJob(Reply reply){
        double priceCheck;
        try{
            job.setString(1,reply.getPostId());
            ResultSet result = job.executeQuery();
            if(result.next())  {
                if(result.getString(1)==null)
                    priceCheck = Double.MAX_VALUE;
                else
                    priceCheck = Double.parseDouble(result.getString(1));
                return priceCheck;
            }
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return -1;
    }

    public void join(Reply reply) {
        try{
            PreparedStatement insert = con.prepareStatement("INSERT INTO REPLY (POST_ID,USER_ID,REPLY) VALUES (?,?,?)");
            insert.setString(1,reply.getPostId());
            insert.setString(2,reply.getResponderId());
            insert.setDouble(3,reply.getValue());
            insert.executeUpdate();
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
    }
}
