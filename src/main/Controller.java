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
import java.net.URL;
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
    private String path = new File("/Users/velox/Desktop/MusicPlayer/src/main/resources/media/L2-5.mp4").getAbsolutePath();

    private int playStatus = 0;
    private int infoStatus = 0;
    private String songName = "";
    private String songPath = "";
    private MediaList mediaList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // default
            // path = new File("/Users/velox/Desktop/MusicPlayer/src/main/L2-5.mp4").getAbsolutePath();
            me = new Media(new File(path).toURI().toString());
            mp = new MediaPlayer(me);
            //mv.setMediaPlayer(mp);
            mp.setAutoPlay(false);

            // for duck event, bubble is visible when duckClickEvent is called
            bubble.setVisible(false);

            // omit the progressBar feature for now, will implement after
            progress.setVisible(false);
        }
        catch (Exception e) {
            System.out.println("some error occurred");
        }
    }




    public void duckClickEvent(MouseEvent mouseEvent) {
        if (infoStatus == 0) {
        // if the bubble is currently invisible
            bubble.setVisible(true);
            infoStatus = 1;
        }
        else {
            bubble.setVisible(false);
            infoStatus = 0;
        }
    }


    // choose song
    public void menuClickEvent(MouseEvent mouseEvent) {
        try {
            DirectoryChooser direcChooser = new DirectoryChooser();
            direcChooser.setTitle("Open Media File");

            Stage stage = (Stage) background.getScene().getWindow();

            File selectedDirec = direcChooser.showDialog(stage);
            if (selectedDirec != null)
            {
                System.out.println(selectedDirec.getName());
                mediaList = new MediaList(selectedDirec);
            }
            else
            {
                System.out.println("WARNING: no directory selected.");
            }
        }
        catch (Exception e) {
            System.out.println("EXCEPTION THROWN: error occurred");
        }
    }

    // plays the current song
    // if the song is an invalid file type, then albert box appears
    public void playClickAction(MouseEvent mouseEvent) {
        if (playStatus == 1) {
            mp.pause();
            message.setText("Paused: " + songName);
            playStatus = 0;
        }
        else {
            songPath = mediaList.getSongPath();
            songName = mediaList.getSongName();
            if (songPath == "" || songName == "") {
                AlertWindow aw = new AlertWindow();
                aw.display("Error", "No song selected.");
            }
            else {
                me = new Media(new File(songPath).toURI().toString());
                mp = new MediaPlayer(me);
                mp.play();

                message.setText("Playing: " + songName);
                playStatus = 1;
            }
        }
    }

    // goes to the next song in the directory
    // if the current song is the last song in the directory, an albert box appears
    public void nextClickEvent(MouseEvent mouseEvent) {
        mediaList.incCurnInd();
        songPath = mediaList.getSongPath();
        songName = mediaList.getSongName();
        if (songPath == "" || songName == "") {
            AlertWindow aw = new AlertWindow();
            aw.display("Error", "No song to play next.");
        }
        else {
            me = new Media(new File(songPath).toURI().toString());
            mp = new MediaPlayer(me);
            mp.play();

            message.setText("Playing: " + songName);
            playStatus = 1;
        }
    }

    // goes to the previous song in the directory
    // if the current song is the first song, then an albert box appears
    public void previousClickEvent(MouseEvent mouseEvent) {
        mediaList.decCurnInd();
        songPath = mediaList.getSongPath();
        songName = mediaList.getSongName();
        if (songPath == "" || songName == "") {
            AlertWindow aw = new AlertWindow();
            aw.display("Error", "No previous song to be played.");
        }
        else {
            me = new Media(new File(songPath).toURI().toString());
            mp = new MediaPlayer(me);
            mp.play();

            message.setText("Playing: " + songName);
            playStatus = 1;
        }
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
