package model.database;

import controller.MainWindowController;
import javafx.scene.control.Alert;
import main.UniLinkGUI;
import model.exception.AlreadyReplyException;
import model.exception.InvalidOfferPriceException;
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

    public ReplyDB(){
        con = UniLinkGUI.con;
        SetUpSQL();
    }

    private void SetUpSQL() {
        try {
            check = con.prepareStatement("SELECT * FROM REPLY WHERE POST_ID = ? AND USER_ID LIKE ?");
            event = con.prepareStatement("SELECT CAPACITY FROM EVENT WHERE POST_ID = ?");
            sale = con.prepareStatement("SELECT ASKING_PRICE,MINIMUM_RAISE FROM SALE WHERE POST_ID = ?");
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
                replies.add(new Reply(result.getString(1),result.getString(2),result.getDouble(3)));
            }
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return replies;
    }

    public boolean checkExist(Reply reply,boolean exception){
        try {
            PreparedStatement check = con.prepareStatement("SELECT * FROM REPLY WHERE POST_ID = ? AND USER_ID = ? AND REPLY = ?");
            check.setString(1, reply.getPostId());
            check.setString(2, reply.getResponderId());
            check.setDouble(3,reply.getValue());
            ResultSet result = check.executeQuery();
            if(exception)
                if(result.next())
                    throw new AlreadyReplyException("You have already reply to this post!");
            if(!exception)
                return !result.next();
            return true;
        }
         catch (SQLException throwables) {
            throwables.printStackTrace();
        }catch (AlreadyReplyException ex){
            ex.display();
        }
        return false;
    }

    public void closeEvent(Reply reply){
        int capacity;
        try{
            PostDB postDB = new PostDB();
            event.setString(1,reply.getPostId());
            ResultSet result = event.executeQuery();
            if(result.next()){
                capacity = result.getInt(1);
                if(postDB.getAttendeesCount(reply.getPostId())==capacity) {
                    postDB.closePost(reply.getPostId());
                }
            }
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean checkSale(Reply reply){
        double askingprice;
        double minimumraise;
        double highestoffer;
        try{
            PostDB postDB = new PostDB();
            sale.setString(1,reply.getPostId());
            ResultSet result = sale.executeQuery();
            if(result.next()){
                askingprice = result.getDouble(1);
                minimumraise = result.getDouble(2);
                highestoffer = postDB.getHighestOffer(reply.getPostId());
                if(reply.getValue()<highestoffer+minimumraise) {
                    if (highestoffer == 0)
                        throw new InvalidOfferPriceException(String.format("%.2f is less than minimum raise %.2f", reply.getValue(), minimumraise));
                    throw new InvalidOfferPriceException(String.format("%.2f is less than current highest offer %.2f + minimum raise %.2f", reply.getValue(), highestoffer, minimumraise));
                }else if(reply.getValue()>=askingprice)
                    postDB.closePost(reply.getPostId());
                return true;
            }
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (InvalidOfferPriceException e) {
            e.display();
        }
        return false;
    }

    public boolean checkJob(Reply reply){
        double lowestOffer;
        try{
            PostDB postDB = new PostDB();
            lowestOffer = postDB.getLowestOffer(reply.getPostId());
            if(lowestOffer == 0)
                lowestOffer = Double.MAX_VALUE;
            if(reply.getValue()<lowestOffer)
                return true;
            else
                throw new InvalidOfferPriceException(String.format("%.2f is not less than current lowest offer %.2f",reply.getValue(),lowestOffer));
        }catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (InvalidOfferPriceException e) {
            e.display();
        }
        return false;
    }

    public void join(Reply reply,boolean info) {
        try{
            PreparedStatement insert = con.prepareStatement("INSERT INTO REPLY VALUES (?,?,?)");
            insert.setString(1,reply.getPostId());
            insert.setString(2,reply.getResponderId());
            insert.setDouble(3,reply.getValue());
            insert.executeUpdate();
            if(info) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Reply successfully");
                alert.showAndWait();
            }
            if(reply.getPostId().substring(0,3).compareToIgnoreCase("EVE")==0)
                this.closeEvent(reply);
            ((MainWindowController)UniLinkGUI.controllers.get("MAIN")).UpdateView();
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
    }
}
