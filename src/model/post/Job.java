package model.post;

import java.util.ArrayList;

public class Job extends Post {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4L;
	
	private double ProposedPrice;
	private double LowestOffer = Double.MAX_VALUE;
	
	public Job(String Id, String Title, String Description, String CreatorId, double ProposedPrice) {
		super(Id, Title, Description, CreatorId);
		this.ProposedPrice = ProposedPrice;
	}
	
	public String getPostDetails() {
		String details = super.getPostDetails();
		details += "Proposed price:	" + "$" + ProposedPrice + "\n";
		if(super.getReply().size()!=0)
			details += "Lowest offer:	" + "$" + LowestOffer;
		else
			details += "Lowest offer:	" + "NO OFFER";
		return details;
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
		String details = "";
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
}
