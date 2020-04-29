package model.post;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Post implements Serializable{	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Parameters
	private String Id;
	private String Title;
	private String Description;
	private String CreatorId;
	// open, status = 1; closed, status = 0;
	private byte Status = 1;
	private ArrayList<Reply> Replies;
	
	protected Post(String Id, String Title, String Description, String CreatorId) {
		this.Id = Id;
		this.Title = Title;
		this.Description = Description;
		this.CreatorId = CreatorId;
		this.Replies = new ArrayList<Reply>();
	}
	
	public String getPostId() {
		return Id;
	}
	
	public String getCreatorId() {
		return CreatorId;
	}
	
	public byte getStatus() {
		return Status;
	}
		
	public void changeStatus() {
		// 1 -> 0, 0 -> 1
		Status = (byte) (1 - Status);
	}

	public ArrayList<Reply> getReply(){
		return Replies;
	}
	
	public String getPostDetails() {
		String detail = "";
		detail += "ID:		" + this.Id + "\n";
		detail += "Title:		" + this.Title + "\n";
		detail += "Description:	" + this.Description + "\n";
		detail += "Creator ID:	" + this.CreatorId + "\n";
		detail += "Status:		";
		if(this.Status == 1)
			detail += "OPEN";
		else
			detail += "CLOSED";
		detail += "\n";
		return detail;
	}
	
	public abstract boolean handleReply(Reply reply);
	public abstract String getReplyDetails();
	
}
