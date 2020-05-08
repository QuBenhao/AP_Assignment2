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

public class Job extends Post {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4L;

	private double ProposedPrice;
	private double LowestOffer;
	
	public Job(String Id, String Title, String Description,String Status, String CreatorId,String Image,double ProposedPrice,double LowestOffer ) {
		super(Id, Title, Description,Status, CreatorId,Image);
		this.ProposedPrice = ProposedPrice;
		this.LowestOffer = LowestOffer;
	}

	@Override
	public HBox visualize(String User_ID) {
		HBox hBox = super.visualize(User_ID);
		hBox.setStyle("-fx-background-color: lightsteelblue");
		GridPane postDetails = null;
		Button reply = null;
		for (Node node: hBox.getChildren()){
			if (node instanceof GridPane)
				postDetails = (GridPane)node;
			if (node instanceof Button && node.getId().compareToIgnoreCase("REPLY")==0)
				reply = (Button) node;
		}

		Label PROPOSEDPRICE = new Label("PROPOSED PRICE:");
		PROPOSEDPRICE.setStyle("-fx-font-weight: bold");
		Label proposed_price = new Label(String.format("%.2f",this.ProposedPrice));
		postDetails.add(PROPOSEDPRICE,0,3);
		postDetails.add(proposed_price,1,3);
		GridPane.setHalignment(PROPOSEDPRICE,HPos.RIGHT);
		GridPane.setHalignment(proposed_price,HPos.LEFT);

		Label LOWESTOFFER = new Label("LOWEST OFFER:");
		LOWESTOFFER.setStyle("-fx-font-weight: bold");
		Label lowest_offer = new Label(String.format("%.2f",this.LowestOffer));
		if(this.LowestOffer==0)
			lowest_offer.setText("NULL");
		postDetails.add(LOWESTOFFER,0,4);
		postDetails.add(lowest_offer,1,4);
		GridPane.setHalignment(LOWESTOFFER,HPos.RIGHT);
		GridPane.setHalignment(lowest_offer,HPos.LEFT);

		if(reply!=null){
			final String[] input = new String[3];
			input[0] = super.getPostId();
			input[1] = User_ID;
			reply.setOnAction(actionEvent -> {
				TextInputDialog textInputDialog = new TextInputDialog();
				textInputDialog.setTitle("REPLY TO JOB");
				textInputDialog.setHeaderText(null);
				textInputDialog.setContentText("ENTER THE VALUE:");
				Optional<String> result = textInputDialog.showAndWait();
				result.ifPresent(value -> {
					input[2] = value;
					try {
						Double.parseDouble(input[2]);
						this.handleReply(new Reply(input[0],input[1],  Double.parseDouble(input[2])));
					}catch (NumberFormatException ex){
						Alert alert = new Alert(Alert.AlertType.ERROR,"Input does not meet the format");
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
			if(replyDB.checkJob(reply)){
				replyDB.join(reply,true);
			}
	}


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
		stage.setTitle("MORE DETAILS FOR JOB POST");
		stage.setScene(main_Scene);
		stage.show();
		UniLinkGUI.stages.put("MOREDETAILS",stage);
		UniLinkGUI.controllers.put("MOREDETAILS",controller);
		UniLinkGUI.stages.get("MAIN").hide();
	}


	public double getProposedPrice() {
		return ProposedPrice;
	}

	public double getLowestOffer() {
		return LowestOffer;
	}
}
