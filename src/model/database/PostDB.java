package model.database;

import javafx.scene.control.Alert;
import main.UniLinkGUI;
import model.exception.DataBaseFullException;
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
    private PreparedStatement attendsCountStt = null;
    private PreparedStatement highestOfferStt = null;
    private PreparedStatement lowestOfferStt = null;

    public PostDB() {
        con = UniLinkGUI.con;
        SetUpSQL();
    }

    private void SetUpSQL() {
        try {
            // Filter based on type, status and Creator
            // Order by Date DESC so that newest post will be at the front
            search = con.prepareStatement("SELECT * FROM POST WHERE POST_ID LIKE ? AND STATUS LIKE ? AND USER_ID LIKE ? GROUP BY POST_ID ORDER BY DATE DESC");

            // New post
            insert = con.prepareStatement("INSERT INTO POST (POST_ID,TITLE,DESCRIPTION,USER_ID,IMAGE) VALUES(?,?,?,?,?)");
            // New Event
            insertEventStatement = con.prepareStatement("INSERT INTO EVENT (POST_ID,VENUE,DATE,CAPACITY) VALUES(?,?,?,?)");
            // New Sale
            insertSaleStatement = con.prepareStatement("INSERT INTO SALE (POST_ID,ASKING_PRICE,MINIMUM_RAISE) VALUES(?,?,?)");
            // New Job
            insertJobStatement = con.prepareStatement("INSERT INTO JOB (POST_ID,PROPOSED_PRICE) VALUES(?,?)");

            // Delete Post
            deletePostStatement = con.prepareStatement("DELETE FROM POST WHERE POST_ID = ?");
            // Delete Reply
            deleteReplyStatement = con.prepareStatement("DELETE FROM REPLY WHERE POST_ID = ?");

            // Close Post
            status = con.prepareStatement("UPDATE POST SET STATUS = 'CLOSED' WHERE POST_ID = ?");

            // Attendee count from Event's reply
            attendsCountStt = con.prepareStatement("SELECT COUNT(REPLY) FROM REPLY WHERE POST_ID = ?");

            // Highest offer from Sale's reply
            // if null, return 0
            highestOfferStt = con.prepareStatement("SELECT ISNULL(MAX(REPLY),0) FROM REPLY WHERE POST_ID = ?");

            // Lowest offer from Job's reply
            // if null, return 0
            lowestOfferStt = con.prepareStatement("SELECT ISNULL(MIN(REPLY),0) FROM REPLY WHERE POST_ID = ?");
        } catch (SQLException throwables) {
            System.out.println(throwables.toString());
        }
    }

    public int getAttendeesCount(String postID) throws SQLException {
        attendsCountStt.setString(1, postID);
        ResultSet attend = attendsCountStt.executeQuery();
        attend.next();
        return attend.getInt(1);
    }

    public double getHighestOffer(String postID) throws SQLException {
        highestOfferStt.setString(1, postID);
        ResultSet Hoffer = highestOfferStt.executeQuery();
        Hoffer.next();
        return Hoffer.getDouble(1);
    }

    public double getLowestOffer(String postID) throws SQLException {
        lowestOfferStt.setString(1, postID);
        ResultSet Loffer = lowestOfferStt.executeQuery();
        Loffer.next();
        return Loffer.getDouble(1);
    }

    public ArrayList<Post> getPosts(String Type, String Status, String Creator) {
        ArrayList<Post> posts = new ArrayList<>();
        try {
            // Type is described by Post ID,
            // Event: EVE
            // Sale: SAL
            // Job: JOB
            if (Type.compareToIgnoreCase("ALL") == 0)
                search.setString(1, "%");
            else {
                if (Type.compareToIgnoreCase("JOB") == 0) {
                    search.setString(1, Type + "%");
                } else
                    search.setString(1, Type.substring(0, 3) + "%");
            }

            // Status: ALL, OPEN, CLOSED
            if (Status.compareToIgnoreCase("ALL") == 0)
                search.setString(2, "%");
            else
                search.setString(2, Status);

            // Creator: ALL, My Post
            if (Creator.compareToIgnoreCase("ALL") == 0)
                search.setString(3, "%");
            else
                search.setString(3, Creator);

            // Post result
            ResultSet result = search.executeQuery();
            ResultSet resultSet;
            while (result.next()) {
                // SELECT * FROM (14)
                StringBuilder searchPost = new StringBuilder("SELECT * FROM  WHERE POST_ID = ?");
                switch (result.getString(1).substring(0, 3).toUpperCase()) {
                    case "EVE":
                        searchPost.insert(14, "EVENT");
                        PreparedStatement postStatement = con.prepareStatement(searchPost.toString());
                        postStatement.setString(1, result.getString(1));
                        // Event result
                        resultSet = postStatement.executeQuery();
                        if (resultSet.next())
                            posts.add(new Event(result.getString(1),
                                    result.getString(2),
                                    result.getString(3),
                                    result.getString(4),
                                    result.getString(5),
                                    result.getString(7),
                                    resultSet.getString(2),
                                    resultSet.getDate(3).toString(),
                                    resultSet.getInt(4),
                                    this.getAttendeesCount(result.getString(1))));
                        break;
                    case "SAL":
                        searchPost.insert(14, "SALE");
                        postStatement = con.prepareStatement(searchPost.toString());
                        postStatement.setString(1, result.getString(1));
                        // Sale result
                        resultSet = postStatement.executeQuery();
                        if (resultSet.next())
                            posts.add(new Sale(result.getString(1),
                                    result.getString(2),
                                    result.getString(3),
                                    result.getString(4),
                                    result.getString(5),
                                    result.getString(7),
                                    resultSet.getDouble(2),
                                    resultSet.getDouble(3),
                                    this.getHighestOffer(result.getString(1))));
                        break;
                    case "JOB":
                        searchPost.insert(14, "JOB");
                        postStatement = con.prepareStatement(searchPost.toString());
                        postStatement.setString(1, result.getString(1));
                        // Job result
                        resultSet = postStatement.executeQuery();
                        if (resultSet.next())
                            posts.add(new Job(result.getString(1),
                                    result.getString(2),
                                    result.getString(3),
                                    result.getString(4),
                                    result.getString(5),
                                    result.getString(7),
                                    resultSet.getDouble(2),
                                    this.getLowestOffer(result.getString(1))));
                        break;
                    default:
                }
            }
        } catch (SQLException throwables) {
            System.out.println(throwables.toString());
        }
        return posts;
    }

    public void newPost(Post post) {
        if (post == null)
            return;
        try {
            // New Post
            insert.setString(1, post.getPostId());
            insert.setString(2, post.getTitle());
            insert.setString(3, post.getDescription());
            insert.setString(4, post.getCreatorId());
            insert.setString(5, post.getImage());
            int result = insert.executeUpdate();
            // if failed
            if (result != 1) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Create Post failed");
                alert.showAndWait();
            } else {
                // New Event, Sale or Job
                deletePostStatement.setString(1, post.getPostId());
                if (post instanceof Event) {
                    Event event = (Event) post;
                    insertEventStatement.setString(1, event.getPostId());
                    insertEventStatement.setString(2, event.getVenue());
                    insertEventStatement.setDate(3, Date.valueOf(event.getDate()));
                    insertEventStatement.setInt(4, event.getCapacity());
                    result = insertEventStatement.executeUpdate();
                } else if (post instanceof Sale) {
                    Sale sale = (Sale) post;
                    insertSaleStatement.setString(1, sale.getPostId());
                    insertSaleStatement.setDouble(2, sale.getAskingPrice());
                    insertSaleStatement.setDouble(3, sale.getMinimumRaise());
                    result = insertSaleStatement.executeUpdate();
                } else {
                    Job job = (Job) post;
                    insertJobStatement.setString(1, job.getPostId());
                    insertJobStatement.setDouble(2, job.getProposedPrice());
                    result = insertJobStatement.executeUpdate();
                }
                // if insert Event, Sale or Job failed
                if (result != 1) {
                    deletePostStatement.executeUpdate();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Create Post failed");
                    alert.showAndWait();
                } else {
                    // Display the post ID if success
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, String.format("Create Post success with ID %s", post.getPostId()));
                    alert.showAndWait();
                }
            }
        } catch (SQLException throwables) {
            System.out.println(throwables.toString());
        }
    }

    // Generate ID automatically based on Database
    public String ID_Generator(String type) throws DataBaseFullException {
        // Checking exist ID, Start from 001
        String query = "SELECT POST_ID FROM POST WHERE POST_ID LIKE ? GROUP BY POST_ID ORDER BY POST_ID ASC";
        try {
            PreparedStatement searchID = con.prepareStatement(query);
            if (type.compareToIgnoreCase("JOB") == 0) {
                searchID.setString(1, type + "%");
            } else
                searchID.setString(1, type.substring(0, 3) + "%");
            ResultSet result = searchID.executeQuery();
            int i = 1;
            while (result.next()) {
                // i will become an ID not be taken by any posts(No matter a post has been deleted or a new Post)
                if (i != Integer.parseInt(result.getString(1).substring(3)))
                    break;
                i++;
            }

            if (type.compareToIgnoreCase("JOB") != 0)
                type = type.substring(0, 3);
            if (i < 10)
                // 00x
                return String.format("%s00%d", type, i);
            else if (i < 100)
                // 0xx
                return String.format("%s0%d", type, i);
            else if (i < 1000)
                // xxx
                return String.format("%s%d", type, i);
            else
                // run out of ID
                throw new DataBaseFullException("Database full, please contact developer");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void closePost(String postID) {
        try {
            status.setString(1, postID);
            status.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // Update post details
    public void updatePost(String postID, HashMap<String, String> changes) {
        if (changes.isEmpty())
            return;
        try {
            PreparedStatement update;
            // Update all the changes
            for (String key : changes.keySet()) {
                // Update (7) SET (12) = ?
                StringBuilder stringBuilder = new StringBuilder("UPDATE  SET  = ? WHERE POST_ID = ");
                stringBuilder.append(String.format("'%s'", postID));
                stringBuilder.insert(12, key.toUpperCase());
                if (key.compareToIgnoreCase("TITLE") == 0
                        || key.compareToIgnoreCase("DESCRIPTION") == 0
                        || key.compareToIgnoreCase("IMAGE") == 0)
                    stringBuilder.insert(7, "POST");
                else if (key.compareToIgnoreCase("VENUE") == 0)
                    stringBuilder.insert(7, "EVENT");
                else if (key.compareToIgnoreCase("DATE") == 0)
                    stringBuilder.insert(7, "EVENT");
                else if (key.compareToIgnoreCase("CAPACITY") == 0)
                    stringBuilder.insert(7, "EVENT");
                else if (key.compareToIgnoreCase("ASKING_PRICE") == 0
                        || key.compareToIgnoreCase("MINIMUM_RAISE") == 0)
                    stringBuilder.insert(7, "SALE");
                else if (key.compareToIgnoreCase("PROPOSED_PRICE") == 0)
                    stringBuilder.insert(7, "JOB");
                update = con.prepareStatement(stringBuilder.toString());
                if (key.compareToIgnoreCase("TITLE") == 0
                        || key.compareToIgnoreCase("DESCRIPTION") == 0
                        || key.compareToIgnoreCase("IMAGE") == 0
                        || key.compareToIgnoreCase("VENUE") == 0)
                    update.setString(1, changes.get(key));
                else if (key.compareToIgnoreCase("DATE") == 0)
                    update.setDate(1, Date.valueOf(changes.get(key)));
                else if (key.compareToIgnoreCase("CAPACITY") == 0)
                    update.setInt(1, Integer.parseInt(changes.get(key)));
                else if (key.compareToIgnoreCase("ASKING_PRICE") == 0
                        || key.compareToIgnoreCase("MINIMUM_RAISE") == 0
                        || key.compareToIgnoreCase("PROPOSED_PRICE") == 0) {
                    String value = changes.get(key);
                    if(value.charAt(0)=='$'){
                        value = value.substring(1);
                    }
                    update.setDouble(1, Double.parseDouble(value));
                }
                int result = update.executeUpdate();
                if (result != 1) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Update details failed");
                    alert.showAndWait();
                }
            }
        } catch (SQLException throwables) {
            System.out.println(throwables.toString());
        }
    }

    public void deletePost(String postId) {
        try {
            deleteReplyStatement.setString(1, postId);
            deletePostStatement.setString(1, postId);
            StringBuilder stringBuilder = new StringBuilder("DELETE FROM ");
            if (postId.substring(0, 3).compareToIgnoreCase("EVE") == 0)
                stringBuilder.append("EVENT");
            else if (postId.substring(0, 3).compareToIgnoreCase("SAL") == 0)
                stringBuilder.append("SALE");
            else
                stringBuilder.append("JOB");
            stringBuilder.append(" WHERE POST_ID = ?");
            PreparedStatement delete = con.prepareStatement(stringBuilder.toString());
            delete.setString(1, postId);
            deleteReplyStatement.executeUpdate();
            delete.executeUpdate();
            deletePostStatement.executeUpdate();
        } catch (SQLException throwables) {
            System.out.println(throwables.toString());
        }
    }

    // Import file and upload data to database
    public void uploadData(HashMap<Post, ArrayList<Reply>> readData) {
        try {
            PreparedStatement check = con.prepareStatement("SELECT * FROM POST WHERE POST_ID = ?");
            readData.forEach((k, v) -> {
                try {
                    check.setString(1, k.getPostId());
                    if (!check.executeQuery().next())
                        this.newPost(k);
                    for (Reply r : v) {
                        k.handleReply(r);
                    }
                    if (k.getStatus().compareToIgnoreCase("CLOSED") == 0)
                        this.closePost(k.getPostId());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
        } catch (SQLException throwables) {
            System.out.println(throwables.toString());
        }
    }
}
