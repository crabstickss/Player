package com.javafx.mediaplayer;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

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
        Utils.playlistsFolderCheck();
        Utils.currentListCheck();
        Utils.playlistsListCheck();
        Utils.refreshCurrentSongs(curSongs);
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
        createNewSongsList(files, fileString, newSongsNum, currSongsLength, songs, newSongs);
        addNewSongsToFile(curList, newSongs);
        ObservableList<String> observableList = FXCollections.observableList(fileString);
        songsListView.setItems(observableList);
        Utils.refreshCurrentSongs(curSongs);
    }

    private void addNewSongsToFile(File curList, String[] newSongs) {
        for (String song : newSongs) {
            try {
                Files.write(curList.toPath(), song.getBytes(), StandardOpenOption.APPEND);
                Files.write(curList.toPath(), "\n".getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void createNewSongsList(List<File> files, List<String> fileString, int newSongsNum, int currSongsLength, String[] songs, String[] newSongs) {
        for (int i = 0; i < currSongsLength; i++) {
            newSongs[i] = songs[i];
        }
        for (int i = 0; i < files.size(); i++) {
            newSongs[i + currSongsLength] = files.get(i).toString();
        }
        for (int i = 0; i < newSongsNum; i++) {
            String[] temp = newSongs[i].split("\\\\");
            List<String> al = Arrays.asList(temp);
            fileString.add(al.get(al.size() - 1));
        }
    }


    public void playMedia() {
        playButton.setOnMouseClicked(mouseEvent -> {
            if (isPlaying) mediaPlayer.stop();
            isPlaying = false;
            playSong();
            Utils.refreshProgressBar(progressBar, media, mediaPlayer, timeLabel, timeDuration);
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
            Utils.refreshProgressBar(progressBar, media, mediaPlayer, timeLabel, timeDuration);
        });
    }


    public void pauseMedia() {
        pauseButton.setOnMouseClicked(mouseEvent -> {
            mediaPlayer.pause();
        });
    }

    public void addMediaToPlaylist() {
        addButton.setOnMouseClicked(mouseEvent -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("addPlaylist.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 200, 320);
                Stage stage = new Stage();
                stage.setTitle("Playlists");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
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
                    Utils.refreshProgressBar(progressBar, media, mediaPlayer, timeLabel, timeDuration);
                } else if (mouseEvent.getClickCount() == 3) {
                    if (isPlaying) mediaPlayer.stop();
                    Utils.refreshProgressBar(progressBar, media, mediaPlayer, timeLabel, timeDuration);
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
                Utils.refreshProgressBar(progressBar, media, mediaPlayer, timeLabel, timeDuration);
            } else {
                progressBar.setValue(0);
                mediaPlayer.seek(Duration.seconds(0));
            }
        });
    }
}


