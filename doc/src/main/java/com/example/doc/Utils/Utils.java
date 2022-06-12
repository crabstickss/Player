package com.javafx.mediaplayer;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

/**
 * Create class Utils for visualization of the interface on the screen/
 */
public class Utils {
    public static void refreshProgressBar(Slider progressBar, Media media, MediaPlayer mediaPlayer, Label timeLabel, Label timeDuration) {
        progressBar.setOnMousePressed(event -> mediaPlayer.seek(Duration.seconds(progressBar.getValue())));
        progressBar.setOnMouseDragged(event -> mediaPlayer.seek(Duration.seconds(progressBar.getValue())));
        mediaPlayer.setOnReady(() -> {
            Duration total = media.getDuration();
            progressBar.setMax(total.toSeconds());
        });

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            progressBar.setValue(newValue.toSeconds());
            String[] musicTime = formatTime(mediaPlayer.getCurrentTime(), mediaPlayer.getTotalDuration()).split("/");
            timeLabel.setText(musicTime[0]);
            timeDuration.setText(musicTime[1]);
        });
    }

    /** Update current song.
     * @param curSongs
     */
    public static void refreshCurrentSongs(ArrayList<File> curSongs) {
        File curList = new File("musicData/currentList.txt");
        curSongs.clear();
        String[] songs;}

    /** Update Playlists.
     * @param playlistsList
     */
    public static void refreshPlaylistsList(ArrayList<String> playlistsList) {
        File curPlaylistsList = new File("musicData/playlists.txt");
        String[] playlists;
        playlistsList.clear();}

    /** Use to describe the form of a text representation.
     * @param elapsed
     * @param duration
     * @return
     */
    public static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int)Math.floor(elapsed.toSeconds());
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedMinutes * 60;}

    /**
     * Check application music-data-folder for multiple users.
     */
    public static void musicDataFolderCheck() {
        File musicData = new File("musicData");}

    /**
     * Check application playlists-folder for multiple users.
     */
    public static void playlistsFolderCheck() {
        File playlists = new File("musicData/playlists");}

    /**
     * Return a dynamically type of the specified list.
     */
    public static void currentListCheck() {
        File file = new File("musicData/currentList.txt");}

    /**
     * Return a dynamically type of the specified list.
     */
    public static void playlistsListCheck() {
        File file = new File("musicData/playlists.txt");}

    /** Change the Playlist  to Current list.
     * @param selectedItem
     * @throws IOException
     */
    public static void fromPlaylistToCurList(String selectedItem) throws IOException {
        File playlistsFile = new File("musicData/playlists/" + selectedItem + ".txt");
        FileWriter curList = new FileWriter("musicData/currentList.txt");
        String playlists;
    }

    /** Create new Playlist.
     * @param name
     * @param songToAdd
     */
    public static void createPlaylist(String name, String songToAdd) {
        File file = new File("musicData/playlists/" + name + ".txt");
        File playlistsList = new File("musicData/playlists.txt");
    }

    /** Add the exit of Playlist.
     * @param name
     * @param songToAdd
     */
    public static void addToExistPlaylist(String name, String songToAdd);
    }

