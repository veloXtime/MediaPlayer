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

public class AlertWindow implements EventHandler<ActionEvent>{

        private Stage alertWindow;

        public void display(String title, String message) {
            alertWindow = new Stage();
            alertWindow.initModality(Modality.APPLICATION_MODAL);
            alertWindow.setTitle(title);
            alertWindow.setMinWidth(250);

            Label label = new Label();
            label.setText(message);

            VBox layout = new VBox(10);
            layout.getChildren().addAll(label);
            layout.setAlignment(Pos.CENTER);
            Scene scene = new Scene(layout);
            alertWindow.setScene(scene);
            alertWindow.showAndWait();
            return;
        }

    @Override
    public void handle(ActionEvent actionEvent) {
            return ;
    }

 //           if (actionEvent.getSource() == yesChoice)
 //           {
 //               choice = true;
 //               confirmWindow.close();
 //           }
 //           else if (actionEvent.getSource() == noChoice)
 //           {
 //               choice = false;
 //               confirmWindow.close();
 //           }
 //       }

}
