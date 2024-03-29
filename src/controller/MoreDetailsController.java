package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import main.UniLinkGUI;
import model.database.PostDB;
import model.exception.InputFormatException;
import model.post.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

public class MoreDetailsController implements Switchable {
    @FXML
    private Button UploadButton;
    @FXML
    private Button SaveButton;
    @FXML
    private Button CloseButton;
    @FXML
    private ListView<Label> ReplyDetails;
    @FXML
    private VBox PostDetails;

    private ImageView imageView;
    private Post post;

    private final ObservableList<Label> replyView = FXCollections.observableArrayList();

    // save all the changes to update later
    private final HashMap<String, String> changes = new HashMap<>();


    @FXML
    public void initialize() {
        ReplyDetails.addEventFilter(MouseEvent.ANY, javafx.event.Event::consume);
    }

    // Load post and reply data to the stage
    public void setUp(Post post, String title, ArrayList<Reply> replies) {
        this.post = post;
        HBox postDetails = post.visualize(post.getCreatorId());
        for (Node node : postDetails.getChildren()) {
            if (node instanceof GridPane)
                ((GridPane) node).setPrefWidth(600);
            if (node instanceof Button) {
                node.setVisible(false);
                node.setDisable(true);
            } else if (node instanceof ImageView)
                imageView = (ImageView) node;
        }
        replyView.add(new Label(title));
        if (replies.isEmpty()) {
            replyView.add(new Label("Empty"));
            if (post instanceof Event) {
                for (Node node : postDetails.getChildren()) {
                    if (node instanceof GridPane) {
                        ArrayList<Node> remove = new ArrayList<>();
                        HashMap<Node, int[]> insert = new HashMap<>();
                        for (Node label : ((GridPane) node).getChildren()) {
                            if (label instanceof Group)
                                continue;
                            if (GridPane.getColumnIndex(label) == 3
                                    || (GridPane.getColumnIndex(label) == 1 && GridPane.getRowIndex(label) == 4)) {
                                TextField temp = new TextField(((Label) label).getText());
                                for (Node LABEL : ((GridPane) node).getChildren()) {
                                    if (LABEL instanceof Group)
                                        continue;
                                    if (GridPane.getColumnIndex(LABEL) == GridPane.getColumnIndex(label) - 1)
                                        if (GridPane.getRowIndex(LABEL).equals(GridPane.getRowIndex(label)))
                                            temp.setId(((Label) LABEL).getText().split(":")[0].replaceAll(" ", "_"));
                                }
                                temp.textProperty().addListener((observable, oldvalue, newvalue) -> {
                                    changes.put(temp.getId(), newvalue);
                                });
                                remove.add(label);
                                int[] position = new int[2];
                                position[0] = GridPane.getColumnIndex(label);
                                position[1] = GridPane.getRowIndex(label);
                                insert.put(temp, position);
                            } else if ((GridPane.getColumnIndex(label) == 1 && GridPane.getRowIndex(label) == 3)
                            ) {
                                DatePicker temp = new DatePicker();
                                LocalDate date = LocalDate.parse(((Label) label).getText());
                                temp.setValue(date);
                                temp.valueProperty().addListener(((observable, oldvalue, newvalue) -> {
                                    changes.put("DATE", newvalue.toString());
                                }));
                                remove.add(label);
                                int[] position = new int[2];
                                position[0] = GridPane.getColumnIndex(label);
                                position[1] = GridPane.getRowIndex(label);
                                insert.put(temp, position);
                            }
                        }
                        if (post.getStatus().compareToIgnoreCase("CLOSED") != 0) {
                            ((GridPane) node).getChildren().removeAll(remove);
                            for (Node key : insert.keySet()) {
                                ((GridPane) node).add(key, insert.get(key)[0], insert.get(key)[1]);
                            }
                        }
                    }
                }
            } else if (post instanceof Sale) {
                for (Node node : postDetails.getChildren()) {
                    if (node instanceof GridPane) {
                        ArrayList<Node> remove = new ArrayList<>();
                        HashMap<TextField, int[]> insert = new HashMap<>();
                        for (Node label : ((GridPane) node).getChildren()) {
                            if (label instanceof Group)
                                continue;
                            if (GridPane.getColumnIndex(label) == 3
                                    || (GridPane.getColumnIndex(label) == 1 && GridPane.getRowIndex(label) == 4)
                                    || (GridPane.getColumnIndex(label) == 1 && GridPane.getRowIndex(label) == 5)) {
                                TextField temp = new TextField(((Label) label).getText());
                                for (Node LABEL : ((GridPane) node).getChildren()) {
                                    if (LABEL instanceof Group)
                                        continue;
                                    if (GridPane.getColumnIndex(LABEL) == GridPane.getColumnIndex(label) - 1)
                                        if (GridPane.getRowIndex(LABEL).equals(GridPane.getRowIndex(label)))
                                            temp.setId(((Label) LABEL).getText().split(":")[0].replaceAll(" ", "_"));
                                }
                                temp.textProperty().addListener((observable, oldvalue, newvalue) -> {
                                    changes.put(temp.getId(), newvalue);
                                });
                                remove.add(label);
                                int[] position = new int[2];
                                position[0] = GridPane.getColumnIndex(label);
                                position[1] = GridPane.getRowIndex(label);
                                insert.put(temp, position);
                            }
                        }
                        if (post.getStatus().compareToIgnoreCase("CLOSED") != 0) {
                            ((GridPane) node).getChildren().removeAll(remove);
                            for (TextField key : insert.keySet()) {
                                ((GridPane) node).add(key, insert.get(key)[0], insert.get(key)[1]);
                            }
                        }
                    }
                }
            } else if (post instanceof Job) {
                for (Node node : postDetails.getChildren()) {
                    if (node instanceof GridPane) {
                        ArrayList<Node> remove = new ArrayList<>();
                        HashMap<TextField, int[]> insert = new HashMap<>();
                        for (Node label : ((GridPane) node).getChildren()) {
                            if (label instanceof Group)
                                continue;
                            if (GridPane.getColumnIndex(label) == 3
                                    || (GridPane.getColumnIndex(label) == 1 && GridPane.getRowIndex(label) == 3)) {
                                TextField temp = new TextField(((Label) label).getText());
                                for (Node LABEL : ((GridPane) node).getChildren()) {
                                    if (LABEL instanceof Group)
                                        continue;
                                    if (GridPane.getColumnIndex(LABEL) == GridPane.getColumnIndex(label) - 1)
                                        if (GridPane.getRowIndex(LABEL).equals(GridPane.getRowIndex(label)))
                                            temp.setId(((Label) LABEL).getText().split(":")[0].replaceAll(" ", "_"));
                                }
                                temp.textProperty().addListener((observable, oldvalue, newvalue) -> {
                                    changes.put(temp.getId(), newvalue);
                                });
                                remove.add(label);
                                int[] position = new int[2];
                                position[0] = GridPane.getColumnIndex(label);
                                position[1] = GridPane.getRowIndex(label);
                                insert.put(temp, position);
                            }
                        }
                        if (post.getStatus().compareToIgnoreCase("CLOSED") != 0) {
                            ((GridPane) node).getChildren().removeAll(remove);
                            for (TextField key : insert.keySet()) {
                                ((GridPane) node).add(key, insert.get(key)[0], insert.get(key)[1]);
                            }
                        }
                    }
                }
            }
        } else {
            UploadButton.setVisible(false);
            UploadButton.setDisable(true);
            SaveButton.setDisable(true);
            SaveButton.setVisible(false);
            for (Reply reply : replies) {
                if (reply.getPostId().substring(0, 3).compareToIgnoreCase("EVE") == 0)
                    replyView.add(new Label(reply.getResponderId()));
                else
                    replyView.add(new Label(String.format("%s: $%.2f", reply.getResponderId(), reply.getValue())));
            }
        }
        if (post.getStatus().compareToIgnoreCase("CLOSED") == 0) {
            UploadButton.setVisible(false);
            UploadButton.setDisable(true);
            SaveButton.setDisable(true);
            SaveButton.setVisible(false);
            CloseButton.setVisible(false);
            CloseButton.setDisable(true);
        }
        PostDetails.getChildren().add(postDetails);
        ObservableList<Node> collection = FXCollections.observableArrayList(PostDetails.getChildren());
        Collections.swap(collection, 0, 1);
        PostDetails.getChildren().setAll(collection);
        ReplyDetails.setItems(replyView);
    }

    @FXML
    public void UploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Post image");
        fileChooser.setInitialDirectory(new File("./images/"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(UniLinkGUI.stages.get("EVENT"));
        if (selectedFile != null) {
            String imagepath = selectedFile.getAbsolutePath();
            String[] path = imagepath.split("/");
            String imageName = path[path.length - 1];
            try {
                imageView.setImage(new Image(new FileInputStream(String.format("./images/%s", imageName))));
                changes.put("IMAGE", imageName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid file");
            alert.showAndWait();
        }
    }

    @FXML
    public void ClosePost() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Warning !");
        alert.setContentText("Are you sure you want to close this post ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                PostDB postDB = new PostDB();
                postDB.closePost(post.getPostId());
                switchStage();
            }
        }
    }

    @FXML
    public void DeletePost() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Warning !");
        alert.setContentText("Are you sure you want to delete this post ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                PostDB postDB = new PostDB();
                postDB.deletePost(post.getPostId());
                switchStage();
            }
        }
    }

    // Validate user input before saving form
    @FXML
    public void Save() {
        try {
            for (Node root : PostDetails.getChildren()) {
                if (root instanceof HBox) {
                    for (Node parent : ((HBox) root).getChildren()) {
                        if (parent instanceof GridPane) {
                            for (Node node : ((GridPane) parent).getChildren()) {
                                if (node instanceof TextField) {
                                    if (((TextField) node).getText().isEmpty())
                                        throw new Exception();
                                    if (node.getId().compareToIgnoreCase("capacity") == 0) {
                                        if (Integer.parseInt(((TextField) node).getText()) <= 0)
                                            throw new InputFormatException("Please Enter a positive integer for capacity");
                                    } else if (node.getId().compareToIgnoreCase("asking_price") == 0
                                            || node.getId().compareToIgnoreCase("minimum_raise") == 0
                                            || node.getId().compareToIgnoreCase("proposed_price") == 0) {
                                        String value = ((TextField) node).getText();
                                        if(value.charAt(0)=='$'){
                                            value = value.substring(1);
                                        }
                                        if (Double.parseDouble(value) <= 0)
                                            throw new InputFormatException("Please Enter a positive number for askingprice");
                                    }
                                }
                            }
                        }
                    }
                }
            }
            PostDB postDB = new PostDB();
            postDB.updatePost(post.getPostId(), changes);
            switchStage();
        } catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, String.format("Input with wrong Type!%s", ex.getMessage()));
            alert.showAndWait();
        } catch (InputFormatException inputFormatException) {
            inputFormatException.display();
        } catch (Exception e) {
            try {
                throw new InputFormatException("Cannot save when some textfields are left blank");
            } catch (InputFormatException ex) {
                ex.display();
            }
        }
    }

    @FXML
    public void BackToMainWindow() {
        switchStage();
    }

    // Back to main stage
    @Override
    public void switchStage() {
        ((MainWindowController) UniLinkGUI.controllers.get("MAIN")).UpdateView();
        UniLinkGUI.stages.get("MAIN").show();
        UniLinkGUI.stages.get("MOREDETAILS").close();
        UniLinkGUI.stages.remove("MOREDETAILS");
        UniLinkGUI.controllers.remove("MOREDETAILS");
    }
}
