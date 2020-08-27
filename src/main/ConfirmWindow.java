package main;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmWindow implements EventHandler<ActionEvent> {

    private boolean choice = false;
    private Button yesChoice = new Button("yes");
    private Button noChoice = new Button("no");
    private Stage confirmWindow;

    public boolean display(String title, String message) {
        confirmWindow = new Stage();
        confirmWindow.initModality(Modality.APPLICATION_MODAL);
        confirmWindow.setTitle(title);
        confirmWindow.setMinWidth(250);

        Label label = new Label();
        label.setText(message);

        yesChoice.setOnAction(this);
        noChoice.setOnAction(this);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, yesChoice, noChoice);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        confirmWindow.setScene(scene);
        confirmWindow.showAndWait();
        return choice;
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        if (actionEvent.getSource() == yesChoice)
        {
            choice = true;
            confirmWindow.close();
        }
        else if (actionEvent.getSource() == noChoice)
        {
            choice = false;
            confirmWindow.close();
        }
    }
}
