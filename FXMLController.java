package com.javafx.mediaplayer;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.*;

import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.*;
/**
 * Class FXMLController aggregating interface elements.
 */

public class FXMLController implements Initializable {
    private Button addButton;
    private Button previousButton;
    private Button nextButton;
    private Button pauseButton;
    private Button playButton;
    private Button playlistButton;
    private Button shareButton;
    public ListView<String> playlistsListView;
    private ListView<String> songsListView;
    private Label songLabel;
    private Label timeLabel;
    private Label timeDuration;
    private Slider progressBar;
    private Slider volumeSlider;
    public static ArrayList<File> curSongs;
    public static ArrayList<String> playlistsList;
    public boolean isPlaying;
    public Media media;
    public MediaPlayer mediaPlayer;
    public static int songNumber;
    /**
     * Initializes UI elements.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle);
    /**
     * Handling the drag-and-drop event.
     */

    private void handleDragOver(DragEvent event);

    /** Add track on pane with drag-and-drop event.
     * @param event
     */
    private void handleDrop(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        List<String> fileString = new ArrayList<>();
    }

    /**Add playlist on pane with drag-and-drop event.
     * @param event
     */
    private void handleDragOverPlaylists(DragEvent event);

    /** Drag events replace mouse events during drag-and-drop gesture.
     * @param event
     */
    private void handleDropPlaylists(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
            playlists.add(file.getName().replace(".txt", ""));
        ObservableList<String> observableList = FXCollections.observableList(playlists);
        playlistsListView.setItems(observableList);
    }

    /**Adds a track to the file.
     * @param curList
     * @param newSongs
     */
    private void addNewSongsToFile(File curList, String[] newSongs);

    /** Create new SongList.
     * @param files
     * @param currSongsLength
     * @param songs
     * @param newSongs
     */
    private void createNewSongsList(List<File> files, int currSongsLength, String[] songs, String[] newSongs);

    /**
     * Sets the action when the play button is pressed.
     */
    public void playMedia();

    /**
     *  Calls the next method of the player, stops the timer.
     */
    public void nextMedia();

    /**
     *Sets the action whan the nextMedia button is pressed.
     */
    private void playNextMedia() {
        songLabel.setText(curSongs.get(songNumber).getName());
        Utils.refreshProgressBar(progressBar, media, mediaPlayer, timeLabel, timeDuration);
    }

    /**
     *  Sets the action when the pause button is pressed.
     */
    public void pauseMedia()
    /**
     * Add a song to a PlayList.
     */
    public void addMediaToPlaylist()

    /**
      Initialize the view for casting processing.
     */
    private void initPlaylistsListView() {
        ObservableList<String> observableList = FXCollections.observableList(playlistsList);
        playlistsListView.setItems(observableList);}

    /**
     * Highlight the initialization of theviewfor casting processing.
     */
    private void initPlaylistsListViewSelection() {
        playlistsListView.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            playlistsListView.setOnMouseClicked(mouseEvent -> );}

    /** Initialize the view for casting processing. */
    private void initSongListView() {
        songsListView.setPlaceholder(new Label("Nothing here..."));
    }

    /**
     *  Highlight the initialization of theviewfor casting processing.
     */
    private void initSongListViewSelection();

    /**
     * Logic of changing the value of the volume slider.
     */
    private void changeVolume() {
        volumeSlider.valueProperty().addListener((observableValue, number, t1) ->
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01));
    }

    /** Playes playback */
    private void playSong() ;


    /**
     *  Calls the previous method of the player, stops the timer.
     */
    public void previousMedia() ;

}


