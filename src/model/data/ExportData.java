package model.data;

import model.database.PostDB;
import model.post.Post;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ExportData {
    private final ArrayList<Post> data;
    private final File file;

    // Constructor: Load Post data
    public ExportData(String filepath) {
        file = new File(String.format("%s/export_data.txt", filepath));
        PostDB postDB = new PostDB();
        data = postDB.getPosts("ALL", "ALL", "ALL");
    }

    public void export() throws IOException {
        FileWriter writer = null;
        writer = new FileWriter(file);
        for (Post post : data) {
            writer.write(post.toString());
            writer.write("\n");
        }
        writer.close();
    }
}
