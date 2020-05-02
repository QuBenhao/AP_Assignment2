package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import main.UniLinkGUI;
import model.database.PostDB;
import model.post.Event;
import model.post.Job;
import model.post.Post;
import model.post.Sale;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class NewPostController implements Switchable{

    @FXML private ImageView PostImage;
    @FXML private GridPane PostInput;

    private String type;
    private String User_ID;
    private String imageName = null;

    @FXML
    public void setUp(String type,String User_ID) {
        this.type = type;
        this.User_ID = User_ID;
        PostInput.setVgap(15);
        PostInput.setHgap(30);

        if(type.compareToIgnoreCase("EVENT")==0) {
            Label TITLE = new Label("TITLE:");
            TextField title = new TextField();
            title.setId("title");
            PostInput.add(TITLE, 0, 0);
            PostInput.add(title, 0, 1);

            Label DESCRIPTION = new Label("DESCRIPTION:");
            TextField description = new TextField();
            description.setId("description");
            PostInput.add(DESCRIPTION, 1, 0);
            PostInput.add(description, 1, 1);

            Label VENUE = new Label("VENUE:");
            TextField venue = new TextField();
            venue.setId("venue");
            PostInput.add(VENUE, 0, 2);
            PostInput.add(venue, 0, 3);

            Label DATE = new Label("DATE:");
            DatePicker date = new DatePicker();
            PostInput.add(DATE, 1, 2);
            PostInput.add(date, 1, 3);

            Label CAPACITY = new Label("CAPACITY:");
            TextField capacity = new TextField();
            capacity.setId("capacity");
            PostInput.add(CAPACITY, 0, 4);
            PostInput.add(capacity, 0, 5);
        }else if(type.compareToIgnoreCase("SALE")==0) {
            Label TITLE = new Label("TITLE:");
            TextField title = new TextField();
            title.setId("title");
            PostInput.add(TITLE, 0, 0);
            PostInput.add(title, 0, 1);

            Label DESCRIPTION = new Label("DESCRIPTION:");
            TextField description = new TextField();
            description.setId("description");
            PostInput.add(DESCRIPTION, 1, 0);
            PostInput.add(description, 1, 1);

            Label ASKINGPRICE = new Label("ASKING PRICE:");
            TextField askingprice = new TextField();
            askingprice.setId("askingprice");
            PostInput.add(ASKINGPRICE, 0, 2);
            PostInput.add(askingprice, 0, 3);

            Label MINIMUMRAISE = new Label("MINIMUM RAISE:");
            TextField minimumraise = new TextField();
            minimumraise.setId("minimumraise");
            PostInput.add(MINIMUMRAISE, 1, 2);
            PostInput.add(minimumraise, 1, 3);
        }else if(type.compareToIgnoreCase("JOB")==0) {
            Label TITLE = new Label("TITLE:");
            TextField title = new TextField();
            title.setId("title");
            PostInput.add(TITLE, 0, 0);
            PostInput.add(title, 0, 1);

            Label DESCRIPTION = new Label("DESCRIPTION:");
            TextField description = new TextField();
            description.setId("description");
            PostInput.add(DESCRIPTION, 0, 2);
            PostInput.add(description, 0, 3);

            Label PROPOSEDPRICE = new Label("PROPOSED PRICE:");
            TextField proposedprice = new TextField();
            proposedprice.setId("proposedprice");
            PostInput.add(PROPOSEDPRICE, 0, 4);
            PostInput.add(proposedprice, 0, 5);
        }

    }

    @FXML
    public void UploadImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Post image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files","*.png", "*.jpg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(UniLinkGUI.stages.get("EVENT"));
        if(selectedFile!=null){
            String imagepath = selectedFile.getAbsolutePath();
            String[] path = imagepath.split("/");
            imageName = path[path.length-1];
            try {
                PostImage.setImage(new Image(new FileInputStream(String.format("./images/%s",imageName))));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR,"Invalid file");
            alert.showAndWait();
        }
    }

    @FXML
    public void Submit(ActionEvent actionEvent) {
        HashMap<String,Object> input = new HashMap<>();
        PostDB postDB = new PostDB();
        Post post = null;
        try{
            for(Node node: PostInput.getChildren()){
                if(node instanceof TextField){
                    if(((TextField)node).getText().isEmpty())
                        throw new Exception();
                    if(node.getId().compareTo("capacity")==0)
                        Integer.valueOf(((TextField) node).getText());
                    else if(node.getId().compareTo("askingprice")==0)
                        Double.valueOf(((TextField) node).getText());
                    else if(node.getId().compareTo("minimumraise")==0)
                        Double.valueOf(((TextField) node).getText());
                    else if(node.getId().compareTo("proposedprice")==0)
                        Double.valueOf(((TextField) node).getText());
                    input.put(node.getId(),((TextField)node).getText());
                } else if (node instanceof DatePicker) {
                    input.put("date",((DatePicker)node).getValue());
                }
            }
            if(type.compareToIgnoreCase("EVENT")==0){
                /*
                	public Event(String Id, String Title, String Description,
                	String Status, String CreatorId,String Image, String Venue,
                	String Date, int Capacity,int AttendeesCount)
                 */
                post = new Event(
                        postDB.ID_Generator("EVENT"),
                        input.get("title").toString(),
                        input.get("description").toString(),
                        "OPEN",
                        User_ID,
                        imageName,
                        input.get("venue").toString(),
                        input.get("date").toString(),
                        Integer.parseInt(input.get("capacity").toString()),
                        0
                );
            } else if(type.compareToIgnoreCase("SALE")==0){
                /*
                	public Sale(String Id, String Title, String Description,
                	String Status, String CreatorId,String Image,
                	double AskingPrice, double MinimumRaise,double HighestOffer)
                 */
                post = new Sale(
                        postDB.ID_Generator("SALE"),
                        input.get("title").toString(),
                        input.get("description").toString(),
                        "OPEN",
                        User_ID,
                        imageName,
                        Double.parseDouble(input.get("askingprice").toString()),
                        Double.parseDouble(input.get("minimumraise").toString()),
                        0
                );
            } else if(type.compareToIgnoreCase("JOB")==0){
                /*
                	public Job(String Id, String Title, String Description,String Status, String CreatorId,String Image,double ProposedPrice,double LowestOffer )
                 */
                post = new Job(
                        postDB.ID_Generator("JOB"),
                        input.get("title").toString(),
                        input.get("description").toString(),
                        "OPEN",
                        User_ID,
                        imageName,
                        Double.parseDouble(input.get("proposedprice").toString()),
                        0
                );
            }
            postDB.newPost(post);
            switchStage();
        }catch (NumberFormatException numberFormatException){
            Alert alert = new Alert(Alert.AlertType.ERROR,String.format("Input with wrong Type!%s",numberFormatException.getMessage()));
            alert.showAndWait();
        } catch (Exception ex){
            Alert alert = new Alert(Alert.AlertType.ERROR,"Cannot sumbit when some textfields are left blank");
            alert.showAndWait();
        }
    }

    public void BackToMainWindow(ActionEvent actionEvent) {
        switchStage();
    }

    @Override
    public void switchStage() {
        ((MainWindowController)UniLinkGUI.controllers.get("MAIN")).UpdateView();
        UniLinkGUI.stages.get("MAIN").show();
        UniLinkGUI.stages.get(type.toUpperCase()).close();
        UniLinkGUI.stages.remove(type.toUpperCase());
        UniLinkGUI.controllers.remove(type.toUpperCase());
    }
}
