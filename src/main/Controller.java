package main;

import com.jfoenix.controls.JFXSlider;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
import java.util.Timer;
import java.util.TimerTask;

public class Controller implements Initializable {

    @FXML private ImageView pause;
    @FXML private Label totalTime;
    @FXML private Label curnTime;
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

    private MediaPlayer mp;
    private Media me;
    private boolean playing = false;
    private boolean displaying = false;
    private String songName = "";
    private String songPath = "";
    private MediaList mediaList = null;

    private Duration duration;
    private Label playTime;
    private Timer timer;
    private TimerTask timerTask;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            bubble.setVisible(false);
            pause.setVisible(false);
        }
        catch (Exception e) {
            System.out.println("error occurred");
        }
    }

    public void setListeners() {
        slider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (slider.isValueChanging()) {
                    mp.seek(duration.multiply(slider.getValue() / 100.0));
                }
            }
        });

        mp.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration duration, Duration t1) {
                if (!slider.isValueChanging())
                    slider.setValue((double)(t1.toSeconds()) / (double) (mp.getTotalDuration().toSeconds()) * 100.0);
                totalTime.setText(formatTime(mp.getTotalDuration().toSeconds()));
                curnTime.setText(formatTime(t1.toSeconds()));
            }
        });

        slider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (! isChanging) {
                mp.seek(Duration.seconds(slider.getValue() / 100 * mp.getTotalDuration().toSeconds()));
            }
        });
    }

    private void sliderClock(boolean state) {
        if (state) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    javafx.application.Platform.runLater(() -> {
                        slider.setValue(slider.getValue() + 1);
                        int seconds = (int) slider.getValue() % 60;
                        int minutes = (int) slider.getValue() / 60;
                        String time = String.format("%02d:%02d", minutes, seconds);
                    });
                }
            };
            timer.scheduleAtFixedRate(timerTask, 0, 1000);
        } else {
            if (timer != null) {
                timerTask.cancel();
                timer.cancel();
                timer.purge();
            }
        }
    }

    public String formatTime(double seconds) {
        int hours = (int) (seconds / 60 / 60);
        int mins = (int) (seconds / 60 % 60);
        int secs = (int) (seconds % 60);
        String result = formatInt(mins) + ":" + formatInt(secs);
        if (hours > 0) result = formatInt(hours) + ":" + result;
        return result;
    }

    public String formatInt(int num) {
        return String.format("%2s", num).replace(' ', '0');
    }



    public void duckClickEvent(MouseEvent mouseEvent) {
        bubble.setVisible(!displaying);
        displaying = !displaying;
    }

    public void changePlayButton(boolean status) {
            pause.setVisible(status);
            play.setVisible(!status);
    }


    // import songs by clicking on menu icon
    public void menuClickEvent(MouseEvent mouseEvent) {
        try {
            DirectoryChooser direcChooser = new DirectoryChooser();
            direcChooser.setTitle("Open Media File");
            Stage stage = (Stage) background.getScene().getWindow();
            File selectedDirec = direcChooser.showDialog(stage);
            if (selectedDirec != null) {
                mediaList = new MediaList(selectedDirec);
                playSong(0);
                changePlayButton(true);
            }
            else {
                System.out.println("WARNING: no directory selected.");
            }
        }
        catch (Exception e) {
            System.out.println("EXCEPTION THROWN: error occurred");
        }
    }

    public void playNullError(int indChange) {
        if (songPath.equals("") || songName.equals("")) {
            String message;
            switch (indChange) {
                case -1:
                    message = "No previous song to be played.";
                    break;
                case 1:
                    message = "No song to be played next.";
                    break;
                case 0:
                    message = "No song selected.";
                    break;
                default:
                    message = "";
                    break;
            }
            AlertWindow aw = new AlertWindow();
            aw.display("Error", message);
        }
    }

    public void playSong(int indChange) {
        if (mediaList == null) {
            AlertWindow aw = new AlertWindow();
            aw.display("Error", "No song selected");
            return;
        }
        //  if (mp != null) { mp.stop(); }

        mediaList.changeCurnInd(indChange);

        songName = mediaList.getSongName();
        songPath = mediaList.getSongPath();

        if (songPath.equals("") || songName.equals("")) {
            playNullError(indChange);
            mediaList.changeCurnInd(-1 * indChange);
            return;
        }
        else if (indChange != 0 || mp == null) {
            if (mp != null) { mp.stop(); }
            me = new Media(songPath);
            mp = new MediaPlayer(me);
            duration = mp.getMedia().getDuration();
            // System.out.println(duration);
            setListeners();
        }
        message.setText("Playing: " + songName);
        // sliderClock(true);
        mp.play();
        playing = true;
    }

    public void playClickAction(MouseEvent mouseEvent) {
        if (playing) {
            mp.pause();
            changePlayButton(false);
            message.setText("Paused: " + songName);
            playing = false;
            // sliderClock(false);
        }
        else {
            changePlayButton(true);
            playSong(0);
        }
    }

    public void nextClickEvent(MouseEvent mouseEvent) {
        changePlayButton(true);
        playSong(1);
    }

    public void previousClickEvent(MouseEvent mouseEvent) {
        changePlayButton(true);
        playSong(-1);
    }




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
