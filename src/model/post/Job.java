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
		postDetails.setHalignment(POSTID,HPos.RIGHT);
		postDetails.setHalignment(postID,HPos.LEFT);

		Label TITLE = new Label("TITLE:");
		TITLE.setStyle("-fx-font-weight: bold");
		Label title = new Label(super.getTitle());
		postDetails.add(TITLE,2,0);
		postDetails.add(title,3,0);
		postDetails.setHalignment(TITLE,HPos.RIGHT);
		postDetails.setHalignment(title,HPos.LEFT);

		Label DESCRIPTION = new Label ("DESCRIPTION:");
		DESCRIPTION.setStyle("-fx-font-weight: bold");
		Label description = new Label(super.getDescription());
		postDetails.add(DESCRIPTION,2,2);
		postDetails.add(description,3,2);
		postDetails.setHalignment(DESCRIPTION,HPos.RIGHT);
		postDetails.setHalignment(description,HPos.LEFT);

		Label CREATORID = new Label("CREATOR ID:");
		CREATORID.setStyle("-fx-font-weight: bold");
		Label creatorID = new Label(super.getCreatorId());
		postDetails.add(CREATORID,0,1);
		postDetails.add(creatorID,1,1);
		postDetails.setHalignment(CREATORID,HPos.RIGHT);
		postDetails.setHalignment(creatorID,HPos.LEFT);

		Label STATUS = new Label("STATUS:");
		STATUS.setStyle("-fx-font-weight: bold");
		Label status = new Label(super.getStatus());
		postDetails.add(STATUS,0,2);
		postDetails.add(status,1,2);
		postDetails.setHalignment(STATUS,HPos.RIGHT);
		postDetails.setHalignment(status,HPos.LEFT);

		Label PROPOSEDPRICE = new Label("PROPOSED PRICE:");
		PROPOSEDPRICE.setStyle("-fx-font-weight: bold");
		Label proposed_price = new Label(String.format("%.2f",this.ProposedPrice));
		postDetails.add(PROPOSEDPRICE,0,3);
		postDetails.add(proposed_price,1,3);
		postDetails.setHalignment(PROPOSEDPRICE,HPos.RIGHT);
		postDetails.setHalignment(proposed_price,HPos.LEFT);

		Label LOWESTOFFER = new Label("LOWEST OFFER:");
		LOWESTOFFER.setStyle("-fx-font-weight: bold");
		Label lowest_offer = new Label(String.format("%.2f",this.LowestOffer));
		postDetails.add(LOWESTOFFER,0,4);
		postDetails.add(lowest_offer,1,4);
		postDetails.setHalignment(LOWESTOFFER,HPos.RIGHT);
		postDetails.setHalignment(lowest_offer,HPos.LEFT);

		hBox.getChildren().add(postDetails);
		Button reply = new Button("REPLY");
		Button moredetails = new Button("MORE DETAILS");
		hBox.getChildren().add(reply);
		hBox.getChildren().add(moredetails);
		return hBox;
	}

	public boolean handleReply(Reply reply) {
		if(reply.getValue() >= LowestOffer) {
			System.out.println("Offer rejected because it's too much");
			return false;
		}
		LowestOffer = reply.getValue();
		super.getReply().add(reply);
		System.out.println("Offer accepted");
		return true;
	}
	
	public String getReplyDetails() {
		StringBuilder details = new StringBuilder();
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


	public double getProposedPrice() {
		return ProposedPrice;
	}

	public double getLowestOffer() {
		return LowestOffer;
	}
}
