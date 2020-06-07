package model.post;

import controller.MoreDetailsController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import main.UniLinkGUI;
import model.database.ReplyDB;
import model.exception.InputFormatException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class Sale extends Post {

	private double AskingPrice;
	private double HighestOffer;
	private double MinimumRaise;
	
	public Sale(String Id, String Title, String Description,String Status, String CreatorId,String Image, double AskingPrice, double MinimumRaise,double HighestOffer) {
		super(Id, Title, Description, Status, CreatorId, Image);
		this.AskingPrice = AskingPrice;
		this.HighestOffer = HighestOffer;
		this.MinimumRaise = MinimumRaise;
	}

	public double getAskingPrice() {
		return AskingPrice;
	}

	public double getHighestOffer() {
		return HighestOffer;
	}

	public double getMinimumRaise() {
		return MinimumRaise;
	}

	// add Sale details to Post details
	@Override
	public HBox visualize(String User_ID) {
		HBox hBox = super.visualize(User_ID);
		// different color
		hBox.setStyle("-fx-background-color: lightpink");
		GridPane postDetails = null;
		Button reply = null;
		for (Node node: hBox.getChildren()){
			if (node instanceof GridPane)
				postDetails = (GridPane)node;
			if (node instanceof Button && node.getId().compareToIgnoreCase("REPLY")==0)
				reply = (Button) node;
		}

		// Highest offer
		Label HIGHESTOFFER = new Label("HIGHEST OFFER:");
		HIGHESTOFFER.setStyle("-fx-font-weight: bold");
		Label highestOffer = new Label(String.format("$%.2f",this.HighestOffer));
		if(this.HighestOffer==0)
			highestOffer.setText("NULL");
		postDetails.add(HIGHESTOFFER,0,3);
		postDetails.add(highestOffer,1,3);
		GridPane.setHalignment(HIGHESTOFFER, HPos.RIGHT);
		GridPane.setHalignment(highestOffer,HPos.LEFT);

		// Minimum raise
		Label MINIMUMRAISE = new Label("MINIMUM RAISE:");
		MINIMUMRAISE.setStyle("-fx-font-weight: bold");
		Label minimumRaise = new Label(String.format("$%.2f",this.MinimumRaise));
		postDetails.add(MINIMUMRAISE,0,4);
		postDetails.add(minimumRaise,1,4);
		GridPane.setHalignment(MINIMUMRAISE, HPos.RIGHT);
		GridPane.setHalignment(minimumRaise,HPos.LEFT);

		// Only the creator can see Asking price
		if(User_ID.compareToIgnoreCase(super.getCreatorId())==0){
			Label ASKINGPRICE = new Label("ASKING PRICE:");
			Label askingprice = new Label(String.format("$%.2f",this.AskingPrice));
			postDetails.add(ASKINGPRICE,0,5);
			postDetails.add(askingprice,1,5);
			GridPane.setHalignment(ASKINGPRICE, HPos.RIGHT);
			GridPane.setHalignment(askingprice,HPos.LEFT);
		}

		// Reply to a Sale
		if(reply!=null) {
			final String[] input = new String[3];
			input[0] = super.getPostId();
			input[1] = User_ID;
			reply.setOnAction(actionEvent -> {
				TextInputDialog textInputDialog = new TextInputDialog();
				textInputDialog.setTitle("REPLY TO SALE");
				textInputDialog.setHeaderText(null);
				textInputDialog.setContentText("ENTER THE VALUE:");
				Optional<String> result = textInputDialog.showAndWait();
				result.ifPresent(value -> {
					input[2] = value;
					try {
						Double.parseDouble(input[2]);
						this.handleReply(new Reply(input[0], input[1], Double.parseDouble(input[2])));
					} catch (NumberFormatException ex) {
						try{
							throw new InputFormatException("Please input a number");
						}catch (InputFormatException e){
							e.display();
						}
					}
				});
			});
		}

		return hBox;
	}

	@Override
	public void handleReply(Reply reply) {
		ReplyDB replyDB = new ReplyDB();
		if(replyDB.checkExist(reply,true)) {
			if(replyDB.checkSale(reply))
				replyDB.join(reply,true);
		}
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
		controller.setUp(this,"-- Offer History --",replyDB.checkExist(super.getPostId()));

		assert main_Root != null;
		Scene main_Scene = new Scene(main_Root);
		Stage stage = new Stage();
		stage.setTitle("MORE DETAILS FOR SALE POST");
		stage.setScene(main_Scene);
		stage.show();
		UniLinkGUI.stages.put("MOREDETAILS",stage);
		UniLinkGUI.controllers.put("MOREDETAILS",controller);
		UniLinkGUI.stages.get("MAIN").hide();
	}

	@Override
	public String toString(){
		StringBuilder s = new StringBuilder(super.toString());
		s.append(String.format("Asking Price: %.2f\n", this.AskingPrice));
		s.append(String.format("Minimum Raise: %.2f\n", this.MinimumRaise));
		s.append("Offer History:\n");
		ReplyDB replyDB = new ReplyDB();
		ArrayList<Reply> replies = replyDB.checkExist(super.getPostId());
		if (replies.isEmpty())
			s.append("Empty\n");
		else {
			for (Reply reply : replies) {
				s.append(String.format("%s offers %.2f\n", reply.getResponderId(), reply.getValue()));
			}
		}
		return s.toString();
	}
}
