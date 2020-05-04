package model.post;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import model.database.PostDB;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;

public abstract class Post implements Serializable{	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Parameters
	private String Id;
	private String Title;
	private String Description;
	private String CreatorId;
	private String Image;
	// open, status = 1; closed, status = 0;
	private String Status;
	private ArrayList<Reply> Replies;
	
	protected Post(String Id, String Title, String Description,String Status, String CreatorId, String Image) {
		this.Id = Id;
		this.Title = Title;
		this.Description = Description;
		this.Status = Status;
		this.CreatorId = CreatorId;
		this.Image = Image;
		this.Replies = new ArrayList<Reply>();
	}
	
	public String getPostId() {
		return Id;
	}
	
	public String getCreatorId() {
		return CreatorId;
	}
	
	public String getStatus() {
		return Status;
	}

 	public ArrayList<Reply> getReply(){
		return Replies;
	}

	public HBox visualize(String User_ID){
		HBox hBox = new HBox();
		hBox.setSpacing(30);
		hBox.prefHeight(300);
		hBox.prefWidth(1000);
		hBox.setPadding(new Insets(10,10,10,10));
		hBox.setAlignment(Pos.CENTER_LEFT);
		StringBuilder ImageURL = new StringBuilder("./images/");
		ImageURL.append(this.Image);
		ImageView imageView = null;

		try {
			FileInputStream input = new FileInputStream(ImageURL.toString());
			imageView = new ImageView(new Image(input));
		} catch (FileNotFoundException e) {
			try {
				imageView = new ImageView(new Image(new FileInputStream("./images/test.png")));
			} catch (FileNotFoundException fileNotFoundException) {
			}
		} finally {
			hBox.getChildren().add(imageView);
			HBox.setHgrow(imageView, Priority.ALWAYS);
		}

		GridPane postDetails = new GridPane();
		postDetails.setPrefWidth(500);
		postDetails.setHgap(20);
		postDetails.setVgap(10);
		postDetails.setAlignment(Pos.CENTER_LEFT);

		ColumnConstraints c1 = new ColumnConstraints();
		c1.setPercentWidth(25);
		ColumnConstraints c2 = new ColumnConstraints();
		c2.setPercentWidth(25);
		ColumnConstraints c3 = new ColumnConstraints();
		c3.setPercentWidth(25);
		ColumnConstraints c4 = new ColumnConstraints();
		c4.setPercentWidth(25);
		postDetails.getColumnConstraints().addAll(c1, c2, c3, c4);

		Label POSTID = new Label("POST ID:");
		POSTID.setStyle("-fx-font-weight: bold");
		Label postID = new Label(this.Id);
		postDetails.add(POSTID,0,0);
		postDetails.add(postID,1,0);
		GridPane.setHalignment(POSTID, HPos.RIGHT);
		GridPane.setHalignment(postID,HPos.LEFT);

		Label TITLE = new Label("TITLE:");
		TITLE.setStyle("-fx-font-weight: bold");
		Label title = new Label(this.Title);
		postDetails.add(TITLE,2,0);
		postDetails.add(title,3,0);
		GridPane.setHalignment(TITLE, HPos.RIGHT);
		GridPane.setHalignment(title,HPos.LEFT);

		Label DESCRIPTION = new Label ("DESCRIPTION:");
		DESCRIPTION.setStyle("-fx-font-weight: bold");
		Label description = new Label(this.Description);
		postDetails.add(DESCRIPTION,2,2);
		postDetails.add(description,3,2);
		GridPane.setHalignment(DESCRIPTION, HPos.RIGHT);
		GridPane.setHalignment(description,HPos.LEFT);

		Label CREATORID = new Label("CREATOR ID:");
		CREATORID.setStyle("-fx-font-weight: bold");
		Label creatorID = new Label(this.CreatorId);
		postDetails.add(CREATORID,0,1);
		postDetails.add(creatorID,1,1);
		GridPane.setHalignment(CREATORID, HPos.RIGHT);
		GridPane.setHalignment(creatorID,HPos.LEFT);

		Label STATUS = new Label("STATUS:");
		STATUS.setStyle("-fx-font-weight: bold");
		Label status = new Label(this.Status);
		postDetails.add(STATUS,0,2);
		postDetails.add(status,1,2);
		GridPane.setHalignment(STATUS, HPos.RIGHT);
		GridPane.setHalignment(status,HPos.LEFT);

		hBox.getChildren().add(postDetails);

		HBox.setHgrow(postDetails, Priority.ALWAYS);

		if(User_ID.compareToIgnoreCase(this.CreatorId)!=0 && this.Status.compareToIgnoreCase("OPEN")==0){
			Button reply = new Button("REPLY");
			reply.setPrefWidth(120);
			reply.setId("REPLY");
			hBox.getChildren().add(reply);
			HBox.setHgrow(reply, Priority.ALWAYS);
		}

		if(User_ID.compareToIgnoreCase(this.CreatorId)==0) {
			Button moredetails = new Button("MORE DETAILS");
			moredetails.setId("");
			moredetails.setPrefWidth(120);
			moredetails.setOnAction(actionEvent -> {
				this.getReplyDetails();
			});
			hBox.getChildren().add(moredetails);
			HBox.setHgrow(moredetails, Priority.ALWAYS);
		}

		return hBox;
	};

	abstract public void handleReply(Reply reply);

	abstract public void getReplyDetails();

	public String getImage() {
		return Image;
	}

	protected void closePost(){
		PostDB postDB = new PostDB();
		postDB.closePost(Id);
	}

	public String getTitle() {
		return Title;
	}

	public String getDescription(){
		return Description;
	}
}
