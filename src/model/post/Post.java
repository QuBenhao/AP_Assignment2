package model.post;

import javafx.scene.layout.HBox;

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
	private String Image;
	// open, status = 1; closed, status = 0;
	private String Status;
	private ArrayList<Reply> Replies;
	
	protected Post(String Id, String Title, String Description,String Status, String CreatorId, String Image) {
		this.Id = Id;
		this.Title = Title;
		this.Description = Description;
		this.Status = Status;
		this.CreatorId = CreatorId;
		this.Image = Image;
		this.Replies = new ArrayList<Reply>();
	}
	
	public String getPostId() {
		return Id;
	}
	
	public String getCreatorId() {
		return CreatorId;
	}
	
	public String getStatus() {
		return Status;
	}

 	public ArrayList<Reply> getReply(){
		return Replies;
	}

	abstract public HBox visualize(String User_ID);

	abstract public boolean handleReply(Reply reply);

	abstract public String getReplyDetails();

	public String getImage() {
		return Image;
	}

	protected void changeStatus(){
		this.Status = "CLOSED";
	}

	public String getTitle() {
		return Title;
	}

	public String getDescription(){
		return Description;
	}
}
