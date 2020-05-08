package model.post;

import controller.MoreDetailsController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import main.UniLinkGUI;
import model.database.ReplyDB;

import java.io.IOException;
import java.util.Optional;

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
		HBox hBox = super.visualize(User_ID);
		hBox.setStyle("-fx-background-color: lightblue");

		GridPane postDetails = null;
		Button reply = null;
		for (Node node: hBox.getChildren()) {
			if (node instanceof GridPane)
				postDetails = (GridPane) node;
			if (node instanceof Button && node.getId().compareToIgnoreCase("REPLY")==0)
				reply = (Button) node;
		}

		Label VENUE = new Label("VENUE:");
		VENUE.setStyle("-fx-font-weight: bold");
		Label venue = new Label(this.Venue);
		postDetails.add(VENUE,2,4);
		postDetails.add(venue,3,4);
		GridPane.setHalignment(VENUE, HPos.RIGHT);
		GridPane.setHalignment(venue,HPos.LEFT);

		Label DATE = new Label("DATE:");
		DATE.setStyle("-fx-font-weight: bold");
		Label date = new Label(this.Date);
		postDetails.add(DATE,0,3);
		postDetails.add(date,1,3);
		GridPane.setHalignment(DATE, HPos.RIGHT);
		GridPane.setHalignment(date,HPos.LEFT);

		Label CAPACITY = new Label("CAPACITY:");
		CAPACITY.setStyle("-fx-font-weight: bold");
		Label capacity = new Label(Integer.toString(this.Capacity));
		postDetails.add(CAPACITY,0,4);
		postDetails.add(capacity,1,4);
		GridPane.setHalignment(CAPACITY, HPos.RIGHT);
		GridPane.setHalignment(capacity,HPos.LEFT);

		Label ATTENDEE_COUNT = new Label("ATTENDEE COUNT:");
		ATTENDEE_COUNT.setStyle("-fx-font-weight: bold");
		Label attendee_Count = new Label(Integer.toString(this.AttendeesCount));
		postDetails.add(ATTENDEE_COUNT,0,5);
		postDetails.add(attendee_Count,1,5);
		GridPane.setHalignment(ATTENDEE_COUNT, HPos.RIGHT);
		GridPane.setHalignment(attendee_Count,HPos.LEFT);

		if(reply!=null) {
			final String[] input = new String[3];
			input[0] = super.getPostId();
			input[1] = User_ID;
			reply.setOnAction(actionEvent -> {
				TextInputDialog textInputDialog = new TextInputDialog();
				textInputDialog.setTitle("REPLY TO EVENT");
				textInputDialog.setHeaderText(null);
				textInputDialog.setContentText("ENTER 1 TO JOIN:");
				Optional<String> result = textInputDialog.showAndWait();
				result.ifPresent(value -> {
					input[2] = value;
					try {
						if (Integer.parseInt(input[2]) == 1)
							this.handleReply(new Reply(input[0], input[1], Integer.parseInt(input[2])));
					} catch (NumberFormatException ex) {
						Alert alert = new Alert(Alert.AlertType.ERROR, "Input does not meet the format");
						alert.showAndWait();
					}
				});

			});
		}

		return hBox;
	}

	@Override
	public void handleReply(Reply reply) {
		ReplyDB replyDB = new ReplyDB();
		if(replyDB.checkExist(reply,true))
			replyDB.join(reply,true);
	}

	@Override
	public void getReplyDetails() {
		ReplyDB replyDB = new ReplyDB();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(UniLinkGUI.MORE_DETAILS_WINDOW));
		Parent main_Root = null;
		try {
			main_Root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		MoreDetailsController controller = loader.getController();
		controller.setUp(this,"Attendees List:",replyDB.checkExist(super.getPostId()));
		assert main_Root != null;
		Scene main_Scene = new Scene(main_Root);
		Stage stage = new Stage();
		stage.setTitle("MORE DETAILS FOR EVENT POST");
		stage.setScene(main_Scene);
		stage.show();
		UniLinkGUI.stages.put("MOREDETAILS",stage);
		UniLinkGUI.controllers.put("MOREDETAILS",controller);
		UniLinkGUI.stages.get("MAIN").hide();
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
