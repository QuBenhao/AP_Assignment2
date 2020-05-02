package model.post;

import controller.MoreDetailsController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import main.UniLinkGUI;
import model.database.PostDB;
import model.database.ReplyDB;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
		HBox hBox = new HBox();
		hBox.setStyle("-fx-background-color: lightsteelblue");
		hBox.setSpacing(30);
		hBox.prefHeight(300);
		hBox.prefWidth(1000);
		hBox.setPadding(new Insets(10,10,10,10));
		hBox.setAlignment(Pos.CENTER_LEFT);
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
		Label postID = new Label(super.getPostId());
		postDetails.add(POSTID,0,0);
		postDetails.add(postID,1,0);
		GridPane.setHalignment(POSTID,HPos.RIGHT);
		GridPane.setHalignment(postID,HPos.LEFT);

		Label TITLE = new Label("TITLE:");
		TITLE.setStyle("-fx-font-weight: bold");
		Label title = new Label(super.getTitle());
		postDetails.add(TITLE,2,0);
		postDetails.add(title,3,0);
		GridPane.setHalignment(TITLE,HPos.RIGHT);
		GridPane.setHalignment(title,HPos.LEFT);

		Label DESCRIPTION = new Label ("DESCRIPTION:");
		DESCRIPTION.setStyle("-fx-font-weight: bold");
		Label description = new Label(super.getDescription());
		postDetails.add(DESCRIPTION,2,2);
		postDetails.add(description,3,2);
		GridPane.setHalignment(DESCRIPTION,HPos.RIGHT);
		GridPane.setHalignment(description,HPos.LEFT);

		Label CREATORID = new Label("CREATOR ID:");
		CREATORID.setStyle("-fx-font-weight: bold");
		Label creatorID = new Label(super.getCreatorId());
		postDetails.add(CREATORID,0,1);
		postDetails.add(creatorID,1,1);
		GridPane.setHalignment(CREATORID,HPos.RIGHT);
		GridPane.setHalignment(creatorID,HPos.LEFT);

		Label STATUS = new Label("STATUS:");
		STATUS.setStyle("-fx-font-weight: bold");
		Label status = new Label(super.getStatus());
		postDetails.add(STATUS,0,2);
		postDetails.add(status,1,2);
		GridPane.setHalignment(STATUS,HPos.RIGHT);
		GridPane.setHalignment(status,HPos.LEFT);

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
		postDetails.add(LOWESTOFFER,0,4);
		postDetails.add(lowest_offer,1,4);
		GridPane.setHalignment(LOWESTOFFER,HPos.RIGHT);
		GridPane.setHalignment(lowest_offer,HPos.LEFT);

		hBox.getChildren().add(postDetails);
		HBox.setHgrow(postDetails, Priority.ALWAYS);

		if(User_ID.compareToIgnoreCase(super.getCreatorId())!=0 && super.getStatus().compareToIgnoreCase("OPEN")==0){
			Button reply = new Button("REPLY");
			reply.setPrefWidth(120);
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
			hBox.getChildren().add(reply);
			HBox.setHgrow(reply, Priority.ALWAYS);
		}

		if(User_ID.compareToIgnoreCase(super.getCreatorId())==0) {
			Button moredetails = new Button("MORE DETAILS");
			moredetails.setPrefWidth(120);
			moredetails.setOnAction(actionEvent -> {
				this.getReplyDetails();
			});
			hBox.getChildren().add(moredetails);
			HBox.setHgrow(moredetails, Priority.ALWAYS);
		}
		return hBox;
	}

	@Override
	public void handleReply(Reply reply) {
		ReplyDB replyDB = new ReplyDB();
		if(replyDB.checkExist(reply))
			if(replyDB.checkJob(reply)!=-1){
				double lowestPrice = replyDB.checkJob(reply);
				if(reply.getValue()<lowestPrice){
					replyDB.join(reply);
					PostDB postDB = new PostDB();
					postDB.update(this,reply);
				}
			}
	}


	public void getReplyDetails() {
		ReplyDB replyDB = new ReplyDB();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MoreDetails.fxml"));
		Parent main_Root = null;
		try {
			main_Root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		MoreDetailsController controller = loader.getController();
		controller.setUp(this,"-- Offer History --",replyDB.checkExist(super.getPostId()));

		assert main_Root != null;
		Scene main_Scene = new Scene(main_Root,1200,800);
		Stage stage = new Stage();
		stage.setTitle("MORE DETAILS FOR THE POST");
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
