package model.post;

import java.util.ArrayList;

public class Sale extends Post {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;
	
	private double AskingPrice;
	private double HighestOffer = 0.0;
	private double MinimumRaise;
	
	public Sale(String Id, String Title, String Description, String CreatorId, double AskingPrice, double MinimumRaise) {
		super(Id, Title, Description, CreatorId);
		this.AskingPrice = AskingPrice;
		this.MinimumRaise = MinimumRaise;
	}

	public String getPostDetails() {
		String details = super.getPostDetails();
		details += "Minimum raise:	" + "$" + MinimumRaise + "\n";
		details += "Highest offer:	";
		if(HighestOffer == 0.0)
			details += "NO OFFER";
		else
			details += "$" + HighestOffer;
		return details;
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
		String details = "";
		details += "Asking price:	" + "$" + AskingPrice + " (NOTE: only visible to the post creator)" + "\n";
		details += "\n";
		ArrayList<Reply> replies = super.getReply();
		if(replies.size()>0) {
			details += "-- Offer History --\n";
			for(int i = replies.size()-1;i >= 0;i--) {
				details += replies.get(i).getResponderId() + ": ";
				details += replies.get(i).getValue();
				if(i!=0)
					details += "\n";
			}
		}
		else
			details += "Offer History: Empty";
		return details;
	}

	@Override
	public void changeStatus() {
		if(HighestOffer < AskingPrice) {
			super.changeStatus();
			System.out.println("Successfully changed status!");
		}
		else
			System.out.println("Cannot open anymore because the highest offer is already greater than Asking Price");
	}

}
