package main;

import com.jfoenix.controls.JFXSlider;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML private JFXSlider slider;
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
    private boolean playing = false;
    private int infoStatus = 0;
    private String songName = "";
    private String songPath = "";
    private MediaList mediaList = null;
    private String path = "/Users/velox/Desktop/MusicPlayer/src/main/resources/media/L2-5.mp4";

    private Duration duration;
    private static final double MIN_CHANGE = 0.5 ;
    private final boolean repeat = false;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private Label playTime;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // for duck event, bubble is visible when duckClickEvent is called
            bubble.setVisible(false);

            me = new Media(new File(path).toURI().toString());
            mp = new MediaPlayer(me);
            mp.setAutoPlay(false);
            duration = mp.getMedia().getDuration();

            mp.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observableValue, Duration duration, Duration t1) {
                    slider.setValue(t1.toSeconds());
                }
            });

            slider.setOnMouseReleased(event -> mp.seek(Duration.seconds(slider.getValue())));
            slider.valueProperty().addListener((observable, oldValue, newValue) -> {
                int seconds = (int) slider.getValue() % 60;
                int minutes = (int) slider.getValue() / 60;
                String time = String.format("%02d:%02d", minutes, seconds);
            });

            slider.setStyle("-jfx-default-track: #659AB1; -jfx-default-thumb: #F2C75C");

            slider.valueProperty().addListener(new InvalidationListener() {
                public void invalidated(Observable ov) {
                    if (slider.isValueChanging()) {
                        // multiply duration by percentage calculated by slider position
                        mp.seek(duration.multiply(slider.getValue() / 100.0));
                    }
                }
            });

            slider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
                if (! isChanging) {
                    mp.seek(Duration.seconds(slider.getValue()));
                }
            });

            slider.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (! slider.isValueChanging()) {
                    double currentTime = mp.getCurrentTime().toSeconds();
                    if (Math.abs(currentTime - newValue.doubleValue()) > MIN_CHANGE) {
                        mp.seek(Duration.seconds(newValue.doubleValue()));
                    }
                }
            });
            mp.play();
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
                songName = mediaList.getSongName();
                songPath = mediaList.getSongPath();
                me = new Media(new File(songPath).toURI().toString());
                mp = new MediaPlayer(me);
                duration = mp.getMedia().getDuration();
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
        if (playing) {
            mp.pause();
            message.setText("Paused: " + songName);
            playing = false;
        }
        else {
            /*
            ***********************************
            * if the current song is at the end
            * ( this is possible if the usr drag the slider to the end )
            * then play next song
            *
            * if the current song is not at the end execute the following command
             */
            playSong(0);
        }
    }

    // goes to the next song in the directory
    // if the current song is the last song in the directory, an albert box appears
    public void nextClickEvent(MouseEvent mouseEvent) {
       playSong(1);
    }

    // goes to the previous song in the directory
    // if the current song is the first song, then an albert box appears
    public void previousClickEvent(MouseEvent mouseEvent) {
        playSong(-1);
    }

    public void playSong(int indChange)
    {
        mp.pause();
        if (mediaList == null)
        {
            AlertWindow aw = new AlertWindow();
            aw.display("Error", "No song selected");
            return;
        }
        mediaList.changeCurnInd(indChange);
        if (indChange != 0) {
            songPath = mediaList.getSongPath();
            songName = mediaList.getSongName();
        }
        if (songPath == "" || songName == "") {
            AlertWindow aw = new AlertWindow();
            if (indChange == -1) {
                aw.display("Error", "No previous song to be played.");
            }
            else if (indChange == 1) {
                aw.display("Error", "No song to be played next.");
            }
            else {
                aw.display("Error", "No song selected");
            }
        }
        else {
            me = new Media(new File(songPath).toURI().toString());
            mp = new MediaPlayer(me);
            mp.setAutoPlay(false);
            mp.play();

            message.setText("Playing: " + songName);
            playing = true;
            slider.valueProperty().addListener(new InvalidationListener() {
                public void invalidated(Observable ov) {
                    if (slider.isValueChanging()) {
                        // multiply duration by percentage calculated by slider position
                        mp.seek(duration.multiply(slider.getValue() / 100.0));
                    }
                }
            });

            slider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
                if (! isChanging) {
                    mp.seek(Duration.seconds(slider.getValue()));
                }
            });
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
