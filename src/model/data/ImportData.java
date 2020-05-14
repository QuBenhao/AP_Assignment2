package model.data;

import model.post.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ImportData {
    private File file;
    public ImportData(File file){
        this.file = file;
    }

    public HashMap<Post, ArrayList<Reply>> readData() throws Exception {
        Scanner input = null;
        String line = null;
        HashMap<Post, ArrayList<Reply>> data = new HashMap<>();

        input = new Scanner(new FileInputStream(file));

        while(input.hasNextLine()){
            ArrayList<String> temp = new ArrayList<>();

            // Moving to next post
            line = input.nextLine();

            // Load each post data
            while(line != "" && line.length()!=0 && input.hasNextLine()) {
                // "%s: %s\n"
                String[] spl = line.split("\n")[0].split(": ");
                // spl[spl.length-1] = 'Important data'
                temp.add(spl[spl.length - 1]);
                line = input.nextLine();
            }

            // Recognize each type by line number
            ArrayList<Reply> reply = new ArrayList<>();
            if(temp.get(0).contains("EVE")){
                if(temp.get(10).compareToIgnoreCase("Empty")!=0)
                    for(int i=10;i<temp.size();i++)
                        reply.add(new Reply(temp.get(0),temp.get(i),1));
                data.put(new Event(temp.get(0),temp.get(1),temp.get(2),temp.get(3),temp.get(4),temp.get(5),temp.get(6),
                                    temp.get(7),Integer.parseInt(temp.get(8)),reply.size())
                            , reply);
            } else if(temp.get(0).contains("SAL")){
                double highestoffer = 0;
                if(temp.get(9).compareToIgnoreCase("Empty")!=0)
                    for(int i=9;i<temp.size();i++){
                        if(highestoffer < Double.parseDouble(temp.get(i).split(" offers ")[1]))
                            highestoffer = Double.parseDouble(temp.get(i).split(" offers ")[1]);
                        reply.add(new Reply(temp.get(0),temp.get(i).split(" offers ")[0],Double.parseDouble(temp.get(i).split(" offers ")[1])));
                    }
                data.put(new Sale(temp.get(0),temp.get(1),temp.get(2),temp.get(3),temp.get(4),temp.get(5),Double.parseDouble(temp.get(6)),
                                Double.parseDouble(temp.get(7)),highestoffer)
                        , reply);
            } else{
                double lowestoffer = 0;
                if(temp.get(8).compareToIgnoreCase("Empty")!=0) {
                    lowestoffer = Double.MAX_VALUE;
                    for (int i = 8; i < temp.size(); i++) {
                        if(lowestoffer > Double.parseDouble(temp.get(i).split(" offers ")[1]))
                            lowestoffer = Double.parseDouble(temp.get(i).split(" offers ")[1]);
                        reply.add(new Reply(temp.get(0), temp.get(i).split(" offers ")[0], Double.parseDouble(temp.get(i).split(" offers ")[1])));
                    }
                }
                data.put(new Job(temp.get(0),temp.get(1),temp.get(2),temp.get(3),temp.get(4),temp.get(5),Double.parseDouble(temp.get(6)),
                                lowestoffer)
                        , reply);
            }
        }

        return data;
    }
}
