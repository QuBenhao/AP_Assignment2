package model.data;

import model.database.PostDB;
import model.database.ReplyDB;
import model.post.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ExportData {
    private PostDB postDB = new PostDB();
    private ReplyDB replyDB = new ReplyDB();
    private HashMap<Post, ArrayList<Reply>> data = new HashMap<>();
    private File file;

    public ExportData(String filepath) {
        file = new File(String.format("%s/export_data.txt",filepath));
        ArrayList<Post> posts = postDB.getPosts("ALL", "ALL", "ALL");
        for(Post post:posts)
            data.put(post,replyDB.checkExist(post.getPostId()));
    }

    public void export() throws IOException {
        FileWriter writer = null;

        writer = new FileWriter(file);
        for (Post post : data.keySet()) {
            writer.write(String.format("PostID: %s\n", post.getPostId()));
            writer.write(String.format("Title: %s\n", post.getTitle()));
            writer.write(String.format("Description: %s\n", post.getDescription()));
            writer.write(String.format("Status: %s\n", post.getStatus()));
            writer.write(String.format("CreatorID: %s\n", post.getCreatorId()));
            writer.write(String.format("Image: %s\n", post.getImage()));
            if (post instanceof Event) {
                writer.write(String.format("Venue: %s\n", ((Event) post).getVenue()));
                writer.write(String.format("Date: %s\n", ((Event) post).getDate()));
                writer.write(String.format("Capacity: %d\n", ((Event) post).getCapacity()));
                writer.write("Attendee List:\n");
                if (data.get(post).isEmpty())
                    writer.write("Empty\n");
                else
                    for (Reply reply : data.get(post))
                        writer.write(String.format("%s\n", reply.getResponderId()));
            } else if (post instanceof Sale) {
                /*
                   	double AskingPrice, double MinimumRaise,double HighestOffer
                */
                writer.write(String.format("Asking Price: %.2f\n", ((Sale) post).getAskingPrice()));
                writer.write(String.format("Minimum Raise: %.2f\n", ((Sale) post).getMinimumRaise()));
                writer.write("Offer History:\n");
                if (data.get(post).isEmpty())
                    writer.write("Empty\n");
                else
                    for (Reply reply : data.get(post))
                        writer.write(String.format("%s offers %.2f\n", reply.getResponderId(), reply.getValue()));
            } else {
                /*
                   	double ProposedPrice,double LowestOffer
                */
                writer.write(String.format("Proposed Price: %.2f\n", ((Job) post).getProposedPrice()));
                writer.write("Offer History:\n");
                if (data.get(post).isEmpty())
                    writer.write("Empty\n");
                else
                    for (Reply reply : data.get(post))
                        writer.write(String.format("%s offers %.2f\n", reply.getResponderId(), reply.getValue()));
            }
            writer.write("\n");
        }
        writer.close();
    }
}
