package model.post;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Event extends Post {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	private String Venue;
	private String Date;
	private int Capacity;
	private int AttendeesCount;
	
	public Event(String Id, String Title, String Description,String Status, String CreatorId,String Image, String Venue, String Date, int Capacity,int AttendeesCount) {
		super(Id,Title,Description,Status,CreatorId,Image);
		this.Venue = Venue;
		this.Date = Date;
		this.Capacity = Capacity;
		this.AttendeesCount = AttendeesCount;
	}

	@Override
	public HBox visualize(String User_ID) {
		HBox hBox = new HBox();
		hBox.setStyle("-fx-background-color: lightblue");
		hBox.setSpacing(30);
		hBox.setAlignment(Pos.CENTER);
		StringBuilder ImageURL = new StringBuilder("./images/");
		ImageURL.append(super.getImage());
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
		}
		GridPane postDetails = new GridPane();
		postDetails.setHgap(20);
		postDetails.setVgap(10);
		postDetails.setAlignment(Pos.CENTER);

		Label POSTID = new Label("POST ID:");
		POSTID.setStyle("-fx-font-weight: bold");
		Label postID = new Label(super.getPostId());
		postDetails.add(POSTID,0,0);
		postDetails.add(postID,1,0);
		postDetails.setHalignment(POSTID, HPos.RIGHT);
		postDetails.setHalignment(postID,HPos.LEFT);

		Label TITLE = new Label("TITLE:");
		TITLE.setStyle("-fx-font-weight: bold");
		Label title = new Label(super.getTitle());
		postDetails.add(TITLE,2,0);
		postDetails.add(title,3,0);
		postDetails.setHalignment(TITLE, HPos.RIGHT);
		postDetails.setHalignment(title,HPos.LEFT);

		Label DESCRIPTION = new Label ("DESCRIPTION:");
		DESCRIPTION.setStyle("-fx-font-weight: bold");
		Label description = new Label(super.getDescription());
		postDetails.add(DESCRIPTION,2,2);
		postDetails.add(description,3,2);
		postDetails.setHalignment(DESCRIPTION, HPos.RIGHT);
		postDetails.setHalignment(description,HPos.LEFT);

		Label CREATORID = new Label("CREATOR ID:");
		CREATORID.setStyle("-fx-font-weight: bold");
		Label creatorID = new Label(super.getCreatorId());
		postDetails.add(CREATORID,0,1);
		postDetails.add(creatorID,1,1);
		postDetails.setHalignment(CREATORID, HPos.RIGHT);
		postDetails.setHalignment(creatorID,HPos.LEFT);

		Label STATUS = new Label("STATUS:");
		STATUS.setStyle("-fx-font-weight: bold");
		Label status = new Label(super.getStatus());
		postDetails.add(STATUS,0,2);
		postDetails.add(status,1,2);
		postDetails.setHalignment(STATUS, HPos.RIGHT);
		postDetails.setHalignment(status,HPos.LEFT);

		Label VENUE = new Label("VENUE:");
		VENUE.setStyle("-fx-font-weight: bold");
		Label venue = new Label(this.Venue);
		postDetails.add(VENUE,2,4);
		postDetails.add(venue,3,4);
		postDetails.setHalignment(VENUE, HPos.RIGHT);
		postDetails.setHalignment(venue,HPos.LEFT);

		Label DATE = new Label("DATE");
		DATE.setStyle("-fx-font-weight: bold");
		Label date = new Label(this.Date);
		postDetails.add(DATE,0,3);
		postDetails.add(date,1,3);
		postDetails.setHalignment(DATE, HPos.RIGHT);
		postDetails.setHalignment(date,HPos.LEFT);

		Label CAPACITY = new Label("CAPACITY:");
		CAPACITY.setStyle("-fx-font-weight: bold");
		Label capacity = new Label(Integer.toString(this.Capacity));
		postDetails.add(CAPACITY,0,4);
		postDetails.add(capacity,1,4);
		postDetails.setHalignment(CAPACITY, HPos.RIGHT);
		postDetails.setHalignment(capacity,HPos.LEFT);

		Label ATTENDEE_COUNT = new Label("ATTENDEE COUNT:");
		ATTENDEE_COUNT.setStyle("-fx-font-weight: bold");
		Label attendee_Count = new Label(Integer.toString(this.AttendeesCount));
		postDetails.add(ATTENDEE_COUNT,0,5);
		postDetails.add(attendee_Count,1,5);
		postDetails.setHalignment(ATTENDEE_COUNT, HPos.RIGHT);
		postDetails.setHalignment(attendee_Count,HPos.LEFT);

		hBox.getChildren().add(postDetails);
		if(User_ID.compareToIgnoreCase(super.getCreatorId())!=0){
			Button reply = new Button("REPLY");
			reply.setOnAction(actionEvent -> {

			});
			hBox.getChildren().add(reply);
		}

		if(User_ID.compareToIgnoreCase(super.getCreatorId())==0) {
			Button moredetails = new Button("MORE DETAILS");
			moredetails.setOnAction(actionEvent -> {

			});
			hBox.getChildren().add(moredetails);
		}
		return hBox;
	}

	@Override
	public boolean handleReply(Reply reply) {
		// the reply is valid, the event is not full and the student id is not yet recorded in that event
		if(AttendeesCount < Capacity && (int)reply.getValue() == 1) {
			for(Reply r: super.getReply()) {
				// already join the event
				if(r.getResponderId().compareTo(reply.getResponderId())==0) {
					System.out.println("Already joined the Event");
					return false;
				}
			}
			// successfully join the event
			super.getReply().add(reply);
			AttendeesCount ++;
			System.out.println("Event registration accepted!");
			if(AttendeesCount == Capacity)
//				super.changeStatus();
			return true;
		}
		return false;
	}

	@Override
	public String getReplyDetails() {
		StringBuilder details = new StringBuilder("Attendee list: ");
		if(AttendeesCount == 0)
			details.append("Empty");
		else
			for(Reply r: super.getReply()) {
				details.append(r.getResponderId());
				if(r != super.getReply().get(super.getReply().size()-1))
					details.append(",");
			}
		return details.toString();
	}

	public String getVenue() {
		return Venue;
	}

	public String getDate() {
		return Date;
	}

	public int getCapacity() {
		return Capacity;
	}

	public int getAttendeesCount() {
		return AttendeesCount;
	}

}
