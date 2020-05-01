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
import java.util.ArrayList;

public class Sale extends Post {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;
	
	private double AskingPrice;
	private double HighestOffer;
	private double MinimumRaise;
	
	public Sale(String Id, String Title, String Description,String Status, String CreatorId,String Image, double AskingPrice, double MinimumRaise,double HighestOffer) {
		super(Id, Title, Description, Status, CreatorId, Image);
		this.AskingPrice = AskingPrice;
		this.HighestOffer = HighestOffer;
		this.MinimumRaise = MinimumRaise;
	}

	@Override
	public HBox visualize(String User_ID) {
		HBox hBox = new HBox();
		hBox.setStyle("-fx-background-color: lightskyblue");
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

		Label HIGHESTOFFER = new Label("HIGHEST OFFER:");
		HIGHESTOFFER.setStyle("-fx-font-weight: bold");
		Label highestOffer = new Label(String.format("%.2f",this.HighestOffer));
		postDetails.add(HIGHESTOFFER,0,3);
		postDetails.add(highestOffer,1,3);
		postDetails.setHalignment(HIGHESTOFFER, HPos.RIGHT);
		postDetails.setHalignment(highestOffer,HPos.LEFT);

		Label MINIMUMRAISE = new Label("MINIMUM RAISE:");
		MINIMUMRAISE.setStyle("-fx-font-weight: bold");
		Label minimumRaise = new Label(String.format("%.2f",this.MinimumRaise));
		postDetails.add(MINIMUMRAISE,0,4);
		postDetails.add(minimumRaise,1,4);
		postDetails.setHalignment(MINIMUMRAISE, HPos.RIGHT);
		postDetails.setHalignment(minimumRaise,HPos.LEFT);

		if(User_ID.compareToIgnoreCase(super.getCreatorId())==0){
			Label ASKINGPRICE = new Label("ASKING PRICE:");
			Label askingprice = new Label(String.format("%.2f",this.AskingPrice));
			postDetails.add(ASKINGPRICE,0,5);
			postDetails.add(askingprice,1,5);
			postDetails.setHalignment(ASKINGPRICE, HPos.RIGHT);
			postDetails.setHalignment(askingprice,HPos.LEFT);
		}

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
		if(reply.getValue() >= HighestOffer + MinimumRaise){
			super.getReply().add(reply);
			System.out.println("Offer accepted");
			HighestOffer = reply.getValue();
			if(reply.getValue() >= AskingPrice) {
				super.changeStatus();
			}
			return true;
		}
		else
			System.out.println("Rejected because it's not enough");
		return false;
	}

	@Override
	public String getReplyDetails() {
		StringBuilder details = new StringBuilder("Asking price:	");
		details.append("$");
		details.append(AskingPrice);
		details.append(" (NOTE: only visible to the post creator)");
		details.append("\n\n");
		ArrayList<Reply> replies = super.getReply();
		if(replies.size()>0) {
			details.append("-- Offer History --\n");
			for(int i = replies.size()-1;i >= 0;i--) {
				details.append(replies.get(i).getResponderId());
				details.append(": ");
				details.append(replies.get(i).getValue());
				if(i!=0)
					details.append("\n");
			}
		}
		else
			details.append("Offer History: Empty");
		return details.toString();
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

}
