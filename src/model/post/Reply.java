package model.post;

import java.io.Serializable;

public class Reply implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5L;
	
	private String PostId;
	// Value has to be greater than 0
	private double Value;
	private String ResponderId;
	
	public Reply(String PostId, double Value, String ResponderId) {
		this.PostId = PostId;
		this.Value = Value;
		this.ResponderId = ResponderId;
	}
	
	public String getPostId() {
		return PostId;
	}
	
	public double getValue() {
		return Value;
	}
	
	public String getResponderId() {
		return ResponderId;
	}
}
