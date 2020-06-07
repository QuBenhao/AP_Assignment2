package model.post;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import main.UniLinkGUI;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;

public abstract class Post implements Serializable{
	// Parameters
	private String Id;
	private String Title;
	private String Description;
	private String CreatorId;
	// Image Name
	private String Image;
	// OPEN or CLOSED
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

	public String getImage() {
		return Image;
	}

	public String getTitle() {
		return Title;
	}

	public String getDescription(){
		return Description;
	}

	// Visualize Post as a HBox
	public HBox visualize(String User_ID){
		HBox hBox = new HBox();
		hBox.setSpacing(30);
		hBox.setPrefHeight(200);
		hBox.setPadding(new Insets(10,10,10,10));
		hBox.setAlignment(Pos.CENTER_LEFT);

		// First child: ImageView
		StringBuilder ImageURL = new StringBuilder("./images/");
		ImageURL.append(this.Image);
		ImageView imageView = null;
		try {
			FileInputStream input = new FileInputStream(ImageURL.toString());
			imageView = new ImageView(new Image(input));
		} catch (FileNotFoundException e) {
			try {
				imageView = new ImageView(new Image(new FileInputStream(String.format("./images/%s", UniLinkGUI.Default_ImageName))));
			} catch (FileNotFoundException fileNotFoundException) {
			}
		} finally {
			imageView.setFitHeight(200);
			imageView.setFitWidth(200);
			hBox.getChildren().add(imageView);
			HBox.setHgrow(imageView, Priority.ALWAYS);
		}

		// Second child: GridPane containing all the post details
		GridPane postDetails = new GridPane();
		postDetails.setGridLinesVisible(false);
		GridPane.setVgrow(postDetails,Priority.ALWAYS);
		postDetails.setPrefWidth(500);
		postDetails.setHgap(15);
		postDetails.setVgap(15);
		postDetails.setAlignment(Pos.CENTER_LEFT);

		// ColumnConstraints for the Gridpane
		ColumnConstraints c1 = new ColumnConstraints();
		c1.setPercentWidth(30);
		ColumnConstraints c2 = new ColumnConstraints();
		c2.setPercentWidth(20);
		ColumnConstraints c3 = new ColumnConstraints();
		c3.setPercentWidth(20);
		ColumnConstraints c4 = new ColumnConstraints();
		c4.setPercentWidth(30);
		postDetails.getColumnConstraints().addAll(c1, c2, c3, c4);

		// Post ID
		Label POSTID = new Label("POST ID:");
		POSTID.setStyle("-fx-font-weight: bold");
		Label postID = new Label(this.Id);
		postDetails.add(POSTID,0,0);
		postDetails.add(postID,1,0);
		GridPane.setHalignment(POSTID, HPos.RIGHT);
		GridPane.setHalignment(postID,HPos.LEFT);

		// Title
		Label TITLE = new Label("TITLE:");
		TITLE.setStyle("-fx-font-weight: bold");
		Label title = new Label(this.Title);
		// Wrap Text if it's too long
		title.setWrapText(true);
		title.setMaxWidth(150);
		title.setPrefWidth(150);
		postDetails.add(TITLE,2,0);
		// merge two cells together
		postDetails.add(title,3,0,1,2);
		GridPane.setHalignment(TITLE, HPos.RIGHT);
		GridPane.setHalignment(title,HPos.LEFT);
		GridPane.setValignment(title, VPos.TOP);

		// Description
		Label DESCRIPTION = new Label ("DESCRIPTION:");
		DESCRIPTION.setStyle("-fx-font-weight: bold");
		Label description = new Label(this.Description);
		description.setWrapText(true);
		description.setMaxWidth(150);
		description.setPrefWidth(150);
		postDetails.add(DESCRIPTION,2,2);
		postDetails.add(description,3,2,1,2);
		GridPane.setHalignment(DESCRIPTION, HPos.RIGHT);
		GridPane.setHalignment(description,HPos.LEFT);
		GridPane.setValignment(description, VPos.TOP);

		// Creator ID
		Label CREATORID = new Label("CREATOR ID:");
		CREATORID.setStyle("-fx-font-weight: bold");
		Label creatorID = new Label(this.CreatorId);
		postDetails.add(CREATORID,0,1);
		postDetails.add(creatorID,1,1);
		GridPane.setHalignment(CREATORID, HPos.RIGHT);
		GridPane.setHalignment(creatorID,HPos.LEFT);

		// Status
		Label STATUS = new Label("STATUS:");
		STATUS.setStyle("-fx-font-weight: bold");
		Label status = new Label(this.Status);
		postDetails.add(STATUS,0,2);
		postDetails.add(status,1,2);
		GridPane.setHalignment(STATUS, HPos.RIGHT);
		GridPane.setHalignment(status,HPos.LEFT);

		hBox.getChildren().add(postDetails);
		HBox.setHgrow(postDetails, Priority.ALWAYS);

		// Third child: button
		// If login User is the creator, there will be no Reply button
		if(User_ID.compareToIgnoreCase(this.CreatorId)!=0){
			Button reply = new Button("REPLY");
			reply.setPrefWidth(120);
			reply.setId("REPLY");
			hBox.getChildren().add(reply);
			HBox.setHgrow(reply, Priority.ALWAYS);
			// If post status is 'CLOSED', Reply button is not visible and is disable
			if(this.Status.compareToIgnoreCase("CLOSED")==0) {
				reply.setVisible(false);
				reply.setDisable(true);
			}
		}

		// Only the creator of the post is able to see more details
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

	@Override
	public String toString(){
		StringBuilder s = new StringBuilder("");
		s.append(String.format("PostID: %s\n", this.Id));
		s.append(String.format("Title: %s\n", this.Title));
		s.append(String.format("Description: %s\n", this.Description));
		s.append(String.format("Status: %s\n", this.Status));
		s.append(String.format("CreatorID: %s\n", this.CreatorId));
		s.append(String.format("Image: %s\n", this.Image));
		return s.toString();
	}
}
