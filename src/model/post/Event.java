package model.post;

public class Event extends Post {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private String Venue;
	private String Date;
	private int Capacity;
	private int AttendeesCount;
	
	public Event(String Id, String Title, String Description, String CreatorId, String Venue, String Date, int Capacity) {
		super(Id,Title,Description,CreatorId);
		this.Venue = Venue;
		this.Date = Date;
		this.Capacity = Capacity;
		this.AttendeesCount = 0;
	}
	
	public String getPostDetails(){
		String details = super.getPostDetails();
		details += "Venue:		" + Venue + "\n";
		details += "Date:		" + Date + "\n";
		details += "Capacity:	" + Capacity + "\n";
		details += "Attendees:	" + AttendeesCount;
		return details;
	}
	
	@Override
	public boolean handleReply(Reply reply) {
		// the reply is valid, the event is not full and the student id is not yet recorded in that event
		if(AttendeesCount < Capacity && (int)reply.getValue() == 1) {
			for(Reply r: super.getReply()) {
				// already join the event
				if(r.getResponderId().compareTo(reply.getResponderId())==0) {
					System.out.println("Already joined the Event");
					return false;
				}
			}
			// successfully join the event
			super.getReply().add(reply);
			AttendeesCount ++;
			System.out.println("Event registration accepted!");
			if(AttendeesCount == Capacity)
				super.changeStatus();
			return true;
		}
		return false;
	}

	@Override
	public String getReplyDetails() {
		String details = "Attendee list: ";
		if(AttendeesCount == 0)
			details += "Empty";
		else
			for(Reply r: super.getReply()) {
				details += r.getResponderId();
				if(r != super.getReply().get(super.getReply().size()-1))
					details += ",";
			}
		return details;
	}

	@Override
	public void changeStatus() {
		// Creator can change status whenever the event is not full
		if(AttendeesCount < Capacity) {
			super.changeStatus();
			System.out.println("Successfully changed status!");
		}
		else
			System.out.println("Cannot open anymore because the capacity is full");
	}

}
