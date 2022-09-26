package Application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class Controller implements Initializable {
    @FXML
    private AnchorPane pane;
    @FXML
    private Label musicLabel;
    @FXML
    private Label timeDisplay;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button prevButton;
    @FXML
    private Button playButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button resetButton;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private Slider volumeSlider;

    private File dirt;
    private File[] files;
    private ArrayList<File> songs;

    private int songNumber;
    private int[] songSpeed = {50, 100, 150, 200};

    private Timer timer;
    private TimerTask task;
    private boolean playing = false;
    private boolean running;

    private Media media;
    private MediaPlayer mediaPlayer;

    private int currentMin;
    private int currentS;
    private int endMin;
    private int endS;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        songs = new ArrayList<File>();
        dirt = new File("music");
        files = dirt.listFiles();
        if(files != null) {
            for(File file : files) {
                songs.add(file);
            }
        }
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        musicLabel.setText(songs.get(songNumber).getName());

        for(int i = 0; i < songSpeed.length; ++i) {
            speedBox.getItems().add(Integer.toString(songSpeed[i]));
        }
        speedBox.setOnAction(this::changeSpeed);

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            }
        });

    }

    public void play() {
        beginTimer();
        playing = true;
        mediaPlayer.play();
    }
    public void pause() {
        stopTimer();
        playing = false;
        mediaPlayer.pause();
    }
    public void next() {
        if(songNumber < songs.size() - 1) {
            songNumber++;
            mediaPlayer.stop();
        }
        else {
            songNumber = 0;
            mediaPlayer.stop();
        }
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        musicLabel.setText(songs.get(songNumber).getName());
        if(playing) play();
        if(running) stopTimer();
    }
    public void reset() {
        mediaPlayer.seek(Duration.seconds(0));
    }
    public void prev() {
        if(songNumber > 0) {
            songNumber--;
            mediaPlayer.stop();
        }
        else {
            songNumber = songs.size() - 1;
            mediaPlayer.stop();
        }
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        musicLabel.setText(songs.get(songNumber).getName());
        if(playing) play();
        if(running) stopTimer();

    }

    public void changeSpeed(ActionEvent event) {
        mediaPlayer.setRate(Integer.parseInt(speedBox.getValue()) * 0.01);
    }
    public void beginTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                currentMin = (int) (current / 60);
                currentS = (int) (current % 60);
                endMin = (int) (end / 60);
                endS = (int) (end % 60);
                progressBar.setProgress(current / end);
                if(current / end == 1) {
                    stopTimer();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 1000, 1000);

    }
    public void stopTimer() {
        running = false;
        timer.cancel();
    }



}
