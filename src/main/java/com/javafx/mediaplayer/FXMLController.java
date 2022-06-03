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
    public ArrayList<File> curSongs;
    public boolean isPlaying;
    public Media media;
    public MediaPlayer mediaPlayer;
    public int songNumber;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        songNumber = 0;
        curSongs = new ArrayList<>();
        Utils.musicDataFolderCheck();
        Utils.currentListCheck();
        refreshCurrentSongs();
        initSongListView();
        changeVolume();
        playMedia();
        pauseMedia();
        previousMedia();
        nextMedia();
    }

    @FXML
    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }
    @FXML
    private void handleDrop(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        List<String> fileString = new ArrayList<>();
        File curList = new File("musicData/currentList.txt");
        int newSongsNum, currSongsLength; // newSongsNum - amount of songs in new list
        String[] songs;
        try {
            songs = Files.readString(curList.toPath()).split("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (songs[0].equals("")) {
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
            List<String> al = Arrays.asList(temp);
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
        progressBar.setOnMousePressed(event -> mediaPlayer.seek(Duration.seconds(progressBar.getValue())));
        progressBar.setOnMouseDragged(event -> mediaPlayer.seek(Duration.seconds(progressBar.getValue())));
        mediaPlayer.setOnReady(() -> {
            Duration total = media.getDuration();
            progressBar.setMax(total.toSeconds());
        });

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            progressBar.setValue(newValue.toSeconds());
            String[] musicTime = Utils.formatTime(mediaPlayer.getCurrentTime(), mediaPlayer.getTotalDuration()).split("/");
            timeLabel.setText(musicTime[0]);
            timeDuration.setText(musicTime[1]);
        });
    }

    public void playMedia() {
        playButton.setOnMouseClicked(mouseEvent -> {
            if (isPlaying) mediaPlayer.stop();
            isPlaying = false;
            playSong();
            refreshProgressBar();

        });
    }

    public void nextMedia() {
        nextButton.setOnMouseClicked(mouseEvent -> {
            if (isPlaying) mediaPlayer.stop();
            if (songNumber < curSongs.size() - 2) {
                songNumber++;
            } else {
                songNumber = 0;
            }
            playSong();
            songLabel.setText(curSongs.get(songNumber).getName());
            refreshProgressBar();
        });
    }


    public void pauseMedia() {
        pauseButton.setOnMouseClicked(mouseEvent -> {
            mediaPlayer.pause();
        });
    }

    public void addMediaToPlaylist() {
    }

    public void shareMedia() {
    }

    public void choosePlaylist() {
    }

    private void initSongListView() {
        songsListView.setPlaceholder(new Label("Nothing here"));
        songsListView.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            String selectedSong = songsListView.getSelectionModel().getSelectedItem();
            for (int i = 0; i < curSongs.size() - 1; i++) {
                String temp = curSongs.get(i).getName();
                if (temp.equals(selectedSong)) {
                    songNumber = i;
                    break;
                }
            }
            songsListView.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2) {
                    if (isPlaying) mediaPlayer.stop();
                    playSong();
                    refreshProgressBar();
                } else if (mouseEvent.getClickCount() == 3) {
                    if (isPlaying) mediaPlayer.stop();
                    refreshProgressBar();
                }
            });
        });
    }

    private void changeVolume() {
        volumeSlider.valueProperty().addListener((observableValue, number, t1) ->
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01));
    }

    private void playSong() {
        media = new Media(curSongs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songLabel.setText(curSongs.get(songNumber).getName());
        mediaPlayer.play();
        isPlaying = true;
    }

    public void previousMedia() {
        previousButton.setOnMouseClicked(mouseEvent -> {
            if (isPlaying) mediaPlayer.stop();
            if (progressBar.getValue() < 5) {
                if (songNumber > 0) {
                    songNumber--;
                } else {
                    songNumber = curSongs.size() - 2;
                }
                playSong();
                refreshProgressBar();
            }
            else {
                progressBar.setValue(0);
                mediaPlayer.seek(Duration.seconds(0));
            }
        });
    }
}


