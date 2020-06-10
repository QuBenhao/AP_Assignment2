package model.post;

import controller.MoreDetailsController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import main.UniLinkGUI;
import model.database.ReplyDB;
import model.exception.InputFormatException;
import model.exception.InvalidOfferPriceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class Job extends Post {

    private final double ProposedPrice;
    private final double LowestOffer;

    public Job(String Id, String Title, String Description, String Status, String CreatorId, String Image, double ProposedPrice, double LowestOffer) {
        super(Id, Title, Description, Status, CreatorId, Image);
        this.ProposedPrice = ProposedPrice;
        this.LowestOffer = LowestOffer;
    }

    // add Job details to Post details
    @Override
    public HBox visualize(String User_ID) {
        HBox hBox = super.visualize(User_ID);
        // different color
        hBox.setStyle("-fx-background-color: lightyellow");
        GridPane postDetails = null;
        Button reply = null;
        for (Node node : hBox.getChildren()) {
            if (node instanceof GridPane)
                postDetails = (GridPane) node;
            if (node instanceof Button && node.getId().compareToIgnoreCase("REPLY") == 0)
                reply = (Button) node;
        }
        assert (postDetails != null);
        // Proposed price
        Label PROPOSEDPRICE = new Label("PROPOSED PRICE:");
        PROPOSEDPRICE.setStyle("-fx-font-weight: bold");
        Label proposed_price = new Label(String.format("$%.2f", this.ProposedPrice));
        postDetails.add(PROPOSEDPRICE, 0, 3);
        postDetails.add(proposed_price, 1, 3);
        GridPane.setHalignment(PROPOSEDPRICE, HPos.RIGHT);
        GridPane.setHalignment(proposed_price, HPos.LEFT);

        // Lowest offer
        Label LOWESTOFFER = new Label("LOWEST OFFER:");
        LOWESTOFFER.setStyle("-fx-font-weight: bold");
        Label lowest_offer = new Label(String.format("$%.2f", this.LowestOffer));
        if (this.LowestOffer == 0)
            lowest_offer.setText("NULL");
        postDetails.add(LOWESTOFFER, 0, 4);
        postDetails.add(lowest_offer, 1, 4);
        GridPane.setHalignment(LOWESTOFFER, HPos.RIGHT);
        GridPane.setHalignment(lowest_offer, HPos.LEFT);

        // Reply to a job
        if (reply != null) {
            final String[] input = new String[3];
            input[0] = super.getPostId();
            input[1] = User_ID;
            reply.setOnAction(actionEvent -> {
                TextInputDialog textInputDialog = new TextInputDialog();
                textInputDialog.setTitle("REPLY TO JOB");
                textInputDialog.setHeaderText(null);
                textInputDialog.setContentText("ENTER THE VALUE:");
                Optional<String> result = textInputDialog.showAndWait();
                result.ifPresent(value -> {
                    input[2] = value;
                    try {
                        Double.parseDouble(input[2]);
                        if (Double.parseDouble(input[2]) >= 0)
                            this.handleReply(new Reply(input[0], input[1], Double.parseDouble(input[2])));
                        else
                            throw new InvalidOfferPriceException("Cannot accept negative offer");
                    } catch (NumberFormatException ex) {
                        try {
                            throw new InputFormatException("Please input a number");
                        } catch (InputFormatException e) {
                            e.display();
                        }
                    } catch (InvalidOfferPriceException e) {
                        e.display();
                    }
                });

            });
        }

        return hBox;
    }

    @Override
    public void handleReply(Reply reply) {
        ReplyDB replyDB = new ReplyDB();
        if (replyDB.checkExist(reply, true))
            if (replyDB.checkJob(reply)) {
                replyDB.join(reply, true);
            }
    }

    public void getReplyDetails() {
        ReplyDB replyDB = new ReplyDB();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(UniLinkGUI.MORE_DETAILS_WINDOW));
        Parent main_Root = null;
        try {
            main_Root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MoreDetailsController controller = loader.getController();
        controller.setUp(this, "-- Offer History --", replyDB.checkExist(super.getPostId()));

        assert main_Root != null;
        Scene main_Scene = new Scene(main_Root);
        Stage stage = new Stage();
        stage.setTitle("MORE DETAILS FOR JOB POST");
        stage.setScene(main_Scene);
        stage.show();
        UniLinkGUI.stages.put("MOREDETAILS", stage);
        UniLinkGUI.controllers.put("MOREDETAILS", controller);
        UniLinkGUI.stages.get("MAIN").hide();
    }

    public double getProposedPrice() {
        return ProposedPrice;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        s.append(String.format("Proposed Price: %.2f\n", this.ProposedPrice));
        s.append("Offer History:\n");
        ReplyDB replyDB = new ReplyDB();
        ArrayList<Reply> replies = replyDB.checkExist(super.getPostId());
        if (replies.isEmpty())
            s.append("Empty\n");
        else {
            for (Reply reply : replies) {
                s.append(String.format("%s offers %.2f\n", reply.getResponderId(), reply.getValue()));
            }
        }
        return s.toString();
    }
}
