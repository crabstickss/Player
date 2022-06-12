package com.javafx.mediaplayer;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.*;

import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
    private Button shuffleButton;
    @FXML
    public ListView<String> playlistsListView;
    @FXML
    private ListView<String> songsListView;
    @FXML
    private Label songLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label timeDuration;
    @FXML
    private Slider progressBar;
    @FXML
    private Slider volumeSlider;
    public static ArrayList<File> curSongs;
    public static ArrayList<String> playlistsList;
    public boolean isPlaying;
    public boolean isPaused;
    public Media media;
    public MediaPlayer mediaPlayer;
    public static int songNumber;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        songNumber = 0;
        curSongs = new ArrayList<>();
        playlistsList = new ArrayList<>();
        Utils.musicDataFolderCheck();
        Utils.playlistsFolderCheck();
        Utils.currentListCheck();
        Utils.playlistsListCheck();
        Utils.refreshCurrentSongs(curSongs);
        Utils.refreshPlaylistsList(playlistsList);
        initSongListView();
        initPlaylistsListView();
        initSongListViewSelection();
        initPlaylistsListViewSelection();
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
        createNewSongsList(files, currSongsLength, songs, newSongs);
        addNewSongsToFile(curList, newSongs);
        Utils.refreshCurrentSongs(curSongs);
        for (int i = 0; i < curSongs.size(); i++) {
            String temp = curSongs.get(i).getName();
            fileString.add(temp);
        }
        ObservableList<String> observableList = FXCollections.observableList(fileString);
        songsListView.setItems(observableList);
    }

    @FXML
    private void handleDragOverPlaylists(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);

        }
    }

    @FXML
    private void handleDropPlaylists(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        File playlistsList = new File("musicData/playlists.txt");
        List<String> playlists = new ArrayList<>();
        try {
            playlists.addAll(Arrays.asList(Files.readString(playlistsList.toPath()).split("\n")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (File file : files) {
            if (file.isDirectory()) {
                File newPlayList = new File("musicData/playlists/" + file.getName() + ".txt");
                try {
                    newPlayList.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Files.write(playlistsList.toPath(), file.getName().replace(".txt", "").getBytes(), StandardOpenOption.APPEND);
                    Files.write(playlistsList.toPath(), "\n".getBytes(), StandardOpenOption.APPEND);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                for (File songPath : Objects.requireNonNull(file.listFiles())) {
                    Utils.addToExistPlaylist(file.getName(), songPath.toString());
                }

            } else {
                try {
                    Files.move(file.toPath(), Path.of("musicData/playlists/" + file.getName()), new StandardCopyOption[]{REPLACE_EXISTING});
                    Files.write(playlistsList.toPath(), file.getName().replace(".txt", "").getBytes(), StandardOpenOption.APPEND);
                    Files.write(playlistsList.toPath(), "\n".getBytes(), StandardOpenOption.APPEND);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (playlists.get(0).equals("")) playlists.remove(0);
            playlists.add(file.getName().replace(".txt", ""));
        }
        ObservableList<String> observableList = FXCollections.observableList(playlists);
        playlistsListView.setItems(observableList);
    }
    private void addNewSongsToFile(File curList, String[] newSongs) {
        FileWriter writer;
        try {
            writer = new FileWriter(curList);
            writer.write("");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (String song : newSongs) {
            try {
                Files.write(curList.toPath(), song.getBytes(), StandardOpenOption.APPEND);
                Files.write(curList.toPath(), "\n".getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void createNewSongsList(List<File> files, int currSongsLength, String[] songs, String[] newSongs) {
        for (int i = 0; i < currSongsLength; i++) {
            newSongs[i] = songs[i];
        }
        for (int i = 0; i < files.size(); i++) {
            newSongs[i + currSongsLength] = files.get(i).toString();
        }
    }

    public void playMedia() {
        playButton.setOnMouseClicked(mouseEvent -> {

            mediaPlayer.play();
            Utils.refreshProgressBar(progressBar, media, mediaPlayer, timeLabel, timeDuration);
        });
    }


    public void nextMedia() {
        nextButton.setOnMouseClicked(mouseEvent -> {
            playNextMedia();
            Utils.refreshProgressBar(progressBar, media, mediaPlayer, timeLabel, timeDuration);
        });
    }

    private void playNextMedia() {
        if (isPlaying) mediaPlayer.stop();
        if (songNumber < curSongs.size() - 1) {
            songNumber++;
        } else {
            songNumber = 0;
        }
        playSong();
        songLabel.setText(curSongs.get(songNumber).getName());
    }

    public void pauseMedia() {
        pauseButton.setOnMouseClicked(mouseEvent -> {
            mediaPlayer.pause();
            Utils.refreshProgressBar(progressBar, media, mediaPlayer, timeLabel, timeDuration);
        });
    }

    public void shuffleMedia() {
        shuffleButton.setOnMouseClicked(mouseEvent -> {
            File curList = new File("musicData/currentList.txt");
            List<String> songs = new ArrayList<>();
            List<String> songsList = new ArrayList<>();
            try {
                songs.addAll(Arrays.asList(Files.readString(curList.toPath()).split("\n")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Collections.shuffle(songs);
            try {
                PrintWriter writer = new PrintWriter(curList);
                writer.print("");
                writer.close();
                for (String song : songs) {
                    File temp = new File(song);
                    songsList.add(temp.getName().replace(".txt", ""));
                    Files.write(curList.toPath(), song.getBytes(), StandardOpenOption.APPEND);
                    Files.write(curList.toPath(), "\n".getBytes(), StandardOpenOption.APPEND);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ObservableList<String> observableList = FXCollections.observableList(songsList);
            songsListView.setItems(observableList);
        });
    }

    public void addMediaToPlaylist() {
        addButton.setOnMouseClicked(mouseEvent -> {
            if (songsListView.getSelectionModel().getSelectedItem() != null) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("addPlaylist.fxml"));
                    Scene scene = new Scene(fxmlLoader.load(), 200, 320);
                    Stage stage = new Stage();
                    stage.setTitle("Playlists");
                    stage.setScene(scene);
                    stage.showAndWait();
                    ObservableList<String> observableList = FXCollections.observableList(playlistsList);
                    playlistsListView.setItems(observableList);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void initPlaylistsListView() {
        ObservableList<String> observableList = FXCollections.observableList(playlistsList);
        playlistsListView.setItems(observableList);
    }

    private void initPlaylistsListViewSelection() {
        playlistsListView.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            playlistsListView.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2) {
                    try {
                        Utils.fromPlaylistToCurList(playlistsListView.getSelectionModel().getSelectedItem());
                        Utils.refreshCurrentSongs(curSongs);
                        List<String> fileString = new ArrayList<>();
                        for (int i = 0; i < curSongs.size(); i++) {
                            String temp = curSongs.get(i).getName();
                            fileString.add(temp);
                        }
                        ObservableList<String> observableSongList = FXCollections.observableList(fileString);
                        songsListView.setItems(observableSongList);
                        songNumber = 1;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });
    }

    private void initSongListView() {
        songsListView.setPlaceholder(new Label("Nothing here..."));
    }

    private void initSongListViewSelection() {
        songsListView.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            String selectedSong = songsListView.getSelectionModel().getSelectedItem();
            for (int i = 0; i < curSongs.size(); i++) {
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
                    Utils.refreshProgressBar(progressBar, media, mediaPlayer, timeLabel, timeDuration); } else if (mouseEvent.getClickCount() == 3) {
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
        mediaPlayer.setOnEndOfMedia(() -> playNextMedia());
    }
    public void previousMedia() {
        previousButton.setOnMouseClicked(mouseEvent -> {
            if (isPlaying) mediaPlayer.stop();
            if (progressBar.getValue() < 5) {
                if (songNumber > 0) {
                    songNumber--;
                } else {
                    songNumber = curSongs.size() - 1;
                }
                playSong();
                Utils.refreshProgressBar(progressBar, media, mediaPlayer, timeLabel, timeDuration); } else {
                progressBar.setValue(0);
                mediaPlayer.seek(Duration.seconds(0));
                mediaPlayer.play();
            }
        });
    }

}
