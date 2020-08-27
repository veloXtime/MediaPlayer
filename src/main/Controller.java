package main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;



public class Controller implements Initializable {

    @FXML private Pane infoPane;
    @FXML private AnchorPane background;
    @FXML private ImageView last;
    @FXML private ImageView next;
    @FXML private ImageView play;
    @FXML private ImageView close;
    @FXML private ImageView menu;
    @FXML private ImageView rubberDuck;
    @FXML private ImageView bubble;
    @FXML private ProgressBar progress;
    @FXML private Label message;
    @FXML private Label info;
    @FXML private MediaView mv;

    private MediaPlayer mp;
    private Media me;
    private String path = new File("/Users/velox/Desktop/MusicPlayer/src/main/L2-5.mp4").getAbsolutePath();
    private int playStatus = 0;
    private String song = "hello";
    private int infoStatus = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // default
            // path = new File("/Users/velox/Desktop/MusicPlayer/src/main/L2-5.mp4").getAbsolutePath();
            me = new Media(new File(path).toURI().toString());
            mp = new MediaPlayer(me);
            //mv.setMediaPlayer(mp);
            mp.setAutoPlay(false);

            bubble.setVisible(false);

            System.out.println(path);
        }
        catch (Exception e) {
            System.out.println("error occured");
        }
    }

    public void duckClickEvent(MouseEvent mouseEvent) {
        if (infoStatus == 1) {
            bubble.setVisible(false);
            infoStatus = 0;
            System.out.println("hello world");
        }
        else {
            info.setText("");
            infoStatus = 1;
            bubble.setVisible(true);
            System.out.println("hello world");
        }
    }


    public void playClickAction(MouseEvent mouseEvent) {
        if (playStatus == 1) {
            mp.pause();
            playStatus = 0;
            message.setText("Paused: " + song);
        }
        else {
            me = new Media(new File(path).toURI().toString());
            mp = new MediaPlayer(me);
            mp.setAutoPlay(false);

            // ----------------------------
            System.out.println(path);
            // ----------------------------

            mp.play();
            playStatus = 1;
            message.setText("Playing: " + song);
        }
    }

    // choose song
    public void menuClickEvent(MouseEvent mouseEvent) {
        DirectoryChooser direcChooser = new DirectoryChooser();
        direcChooser.setTitle("Open Media File");
        Stage stage = (Stage) background.getScene().getWindow();
        File selectedDirectory = direcChooser.showDialog(stage);
        System.out.println(selectedDirectory.getName());

        String direcPath = "~/Desktop/MusicPlayer/src/main/";
        if (selectedDirectory != null) {
            direcPath = selectedDirectory.getAbsolutePath();
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(selectedDirectory);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("ALL MEDIA", "*.*"),
                new FileChooser.ExtensionFilter("MP4", "*.mp4"),
                new FileChooser.ExtensionFilter("MP3", "*.mp3"),
                new FileChooser.ExtensionFilter("WAV", "*.wav")
        );
        //File file = fileChooser.showOpenDialog(stage);
        /*
        if (file != null) {
            song = file.getName();
            path = file.getPath();
        }
        */
    }

    // confirm exit
    public void closeClickEvent(MouseEvent mouseEvent) {
        ConfirmWindow cw = new ConfirmWindow();
        boolean choice = cw.display("Confirm Exit", "Do you want to exit?");
        if (choice == true) {
            Stage stage = (Stage) background.getScene().getWindow();
            stage.close();
        }
    }

    // move the window
    private double xChange = 0;
    private double yChange = 0;

    public void windowDragEvent(MouseEvent mouseEvent) {
        Stage stage = (Stage) background.getScene().getWindow();
        stage.setX(mouseEvent.getScreenX() - xChange);
        stage.setY(mouseEvent.getScreenY() - yChange);
    }

    public void windowPressEvent(MouseEvent mouseEvent) {
        xChange = mouseEvent.getSceneX();
        yChange = mouseEvent.getSceneY();
    }
}
