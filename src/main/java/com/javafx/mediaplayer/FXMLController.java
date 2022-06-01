package com.javafx.mediaplayer;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;


import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;

public class FXMLController implements Initializable {
    @FXML
    private Button addButton;
    @FXML
    private Button previousButton;
    @FXML
    private Label nameLabel;
    @FXML
    private Button nextButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button playButton;
    @FXML
    private Button playlistButton;
    @FXML
    private ListView<?> playlistsListView;
    @FXML
    private Button shareButton;
    @FXML
    private Button songButton;
    @FXML
    private Label songLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label timeDuration;
    @FXML
    private Slider progressBar;
    @FXML
    private ListView<String> songsListView;
    @FXML
    private Slider volumeSlider;
    @FXML
    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }
    @FXML
    private void handleDrop(DragEvent event) throws FileNotFoundException {
        List<File> files = event.getDragboard().getFiles();
        List<String> fileString = new ArrayList<String>();
        File curList = new File("musicData/currentList.txt");
        String[] songs;
        try {
            songs = Files.readString(curList.toPath()).split("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int newSongsNum, currSongsLength; // newSongsNum - amount of new list
        if (songs[0] == "") {
            newSongsNum = files.size();
            currSongsLength = 0;
        } else {
            newSongsNum = songs.length + files.size();
            currSongsLength = songs.length;
        }
        String[] newSongs = new String[newSongsNum];
        for (int i = 0; i < currSongsLength; i++) {
            newSongs[i] = songs[i];
        }
        for (int i = 0; i < files.size(); i++) {
            newSongs[i + currSongsLength] = files.get(i).toString();
        }
        for (int i = 0; i < newSongsNum; i++){
            String[] temp = newSongs[i].split("\\\\");
            List<String> al = new ArrayList<String>();
            al = Arrays.asList(temp);
            fileString.add(al.get(al.size() - 1));
        }
        for (String song: newSongs) {
            try {
                Files.write(curList.toPath(), song.getBytes(), StandardOpenOption.APPEND);
                Files.write(curList.toPath(), "\n".getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        ObservableList<String> observableList = FXCollections.observableList(fileString);
        songsListView.setItems(observableList);
        refreshCurrentSongs();
    }
    private ArrayList<File> curSongs;
    private boolean isPlaying;
    private Media media;
    private MediaPlayer mediaPlayer;
    private File[] files;
    private File directory;
    private int songNumber;
    private Timer timer;
    private TimerTask task;
    private boolean running;

    public void refreshCurrentSongs() {
        File curList = new File("musicData/currentList.txt");
        String[] songs;
        try {
            songs = Files.readString(curList.toPath()).split("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < songs.length; i++) {
            File temp = new File(songs[i]);
            curSongs.add(i, temp);
        }
    }

    private void refreshProgressBar() {
        progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
            }
        });

        progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                Duration total = media.getDuration();
                progressBar.setMax(total.toSeconds());
            }
        });

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable,
                                Duration oldValue, Duration newValue) {
                progressBar.setValue(newValue.toSeconds());
                String[] musicTime = formatTime(mediaPlayer.getCurrentTime(), mediaPlayer.getTotalDuration()).split("/");
                timeLabel.setText(musicTime[0]);
                timeDuration.setText(musicTime[1]);
            }

            private static String formatTime(Duration elapsed, Duration duration) {
                int intElapsed = (int)Math.floor(elapsed.toSeconds());
                int elapsedHours = intElapsed / (60 * 60);
                if (elapsedHours > 0) {
                    intElapsed -= elapsedHours * 60 * 60;
                }
                int elapsedMinutes = intElapsed / 60;
                int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                        - elapsedMinutes * 60;

                if (duration.greaterThan(Duration.ZERO)) {
                    int intDuration = (int)Math.floor(duration.toSeconds());
                    int durationHours = intDuration / (60 * 60);
                    if (durationHours > 0) {
                        intDuration -= durationHours * 60 * 60;
                    }
                    int durationMinutes = intDuration / 60;
                    int durationSeconds = intDuration - durationHours * 60 * 60 -
                            durationMinutes * 60;
                    if (durationHours > 0) {
                        return String.format("%d:%02d:%02d/%d:%02d:%02d",
                                elapsedHours, elapsedMinutes, elapsedSeconds,
                                durationHours, durationMinutes, durationSeconds);
                    } else {
                        return String.format("%02d:%02d/%02d:%02d",
                                elapsedMinutes, elapsedSeconds,durationMinutes,
                                durationSeconds);
                    }
                } else {
                    if (elapsedHours > 0) {
                        return String.format("%d:%02d:%02d", elapsedHours,
                                elapsedMinutes, elapsedSeconds);
                    } else {
                        return String.format("%02d:%02d",elapsedMinutes,
                                elapsedSeconds);
                    }
                }
            }
        });
    }

    public void playMedia(ActionEvent actionEvent) {
        if (isPlaying) mediaPlayer.stop();
        isPlaying = false;
        media = new Media(curSongs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songLabel.setText(curSongs.get(songNumber).getName());
        mediaPlayer.play();
        isPlaying = true;
        refreshProgressBar();
    }

    public void nextMedia(ActionEvent actionEvent) {
        if (isPlaying) mediaPlayer.stop();
        isPlaying = false;
        if (songNumber < curSongs.size() - 2) {
            songNumber++;
        } else {
            songNumber = 0;
        }
        media = new Media(curSongs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        isPlaying = true;
        songLabel.setText(curSongs.get(songNumber).getName());
        refreshProgressBar();
    }


    public void pauseMedia(ActionEvent actionEvent) {
        mediaPlayer.pause();
    }

    public void addMediaToPlaylist(ActionEvent actionEvent) {
    }

    public void shareMedia(ActionEvent actionEvent) {
    }

    public void chooseSong(ActionEvent actionEvent) {
    }

    public void choosePlaylist(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        songNumber = 0;
        File file = new File("musicData/currentList.txt");
        String[] songs;
        if (!file.exists()) {
            try {
                boolean created = file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                FileWriter fw = new FileWriter(file.toString());
                fw.write("");
                fw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        curSongs = new ArrayList<File>();
        refreshCurrentSongs();
        List<String> songNames = new ArrayList<String>();
        for (File song: curSongs) {
            if (song.exists()) {
                songNames.add(song.getName());
            }
        }
        ObservableList<String> observableList = FXCollections.observableList(songNames);
        if (!curSongs.get(0).exists()) {
            songsListView.setPlaceholder(new Label("Nothing here"));
        } else {
            songsListView.setItems(observableList);
        }
        songsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String selectedSong = songsListView.getSelectionModel().getSelectedItem();
                for (int i = 0; i < curSongs.size() - 1; i++) {
                    String temp = curSongs.get(i).getName();
                    if (temp.equals(selectedSong)) {
                        songNumber = i;
                        break;
                    }
                }
                if (isPlaying) mediaPlayer.stop();
                media = new Media(curSongs.get(songNumber).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                songLabel.setText(curSongs.get(songNumber).getName());
                mediaPlayer.play();
                isPlaying = true;
                refreshProgressBar();
            }
        });
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number,
                                Number t1) {
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            }
        });
    }

    public void previousMedia(ActionEvent actionEvent) {
        if (isPlaying) mediaPlayer.stop();
        isPlaying = false;
        if (progressBar.getValue() < 5) {
            if (songNumber > 0) {
                songNumber--;
            } else {
                songNumber = curSongs.size() - 2;
            }
            media = new Media(curSongs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            songLabel.setText(curSongs.get(songNumber).getName());
            isPlaying = true;
            refreshProgressBar();
        }
        else {
            progressBar.setValue(0);
            mediaPlayer.seek(Duration.seconds(0));
        }
    }
}


