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
    public static void refreshCurrentSongs(ArrayList<File> curSongs) {
        File curList = new File("musicData/currentList.txt");
        curSongs.clear();
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
    public static void refreshPlaylistsList(ArrayList<String> playlistsList) {
        File curPlaylistsList = new File("musicData/playlists.txt");
        String[] playlists;
        playlistsList.clear();
        try {
            playlists = Files.readString(curPlaylistsList.toPath()).split("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < playlists.length; i++) {
            String temp = playlists[i];
            playlistsList.add(i, temp);
        }
    }
    public static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int)Math.floor(elapsed.toSeconds());
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int)Math.floor(duration.toSeconds());
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationMinutes * 60;
            return String.format("%02d:%02d/%02d:%02d",
            elapsedMinutes, elapsedSeconds,durationMinutes,
            durationSeconds);
        } else {
            return String.format("%02d:%02d",elapsedMinutes,
                    elapsedSeconds);
        }
    }
    public static void musicDataFolderCheck() {
        File musicData = new File("musicData");
        if (!musicData.exists()) {
            musicData.mkdir();
        }
    }
    public static void playlistsFolderCheck() {
        File playlists = new File("musicData/playlists");
        if (!playlists.exists()) {
            playlists.mkdir();
        }
    }
    public static void currentListCheck() {
        File file = new File("musicData/currentList.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
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
    }
    public static void playlistsListCheck() {
        File file = new File("musicData/playlists.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void fromPlaylistToCurList(String selectedItem) throws IOException {
        File playlistsFile = new File("musicData/playlists/" + selectedItem + ".txt");
        FileWriter curList = new FileWriter("musicData/currentList.txt");
        String playlists;
        try {
            playlists = Files.readString(playlistsFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        curList.write(playlists);
        curList.close();
    }
    public static void createPlaylist(String name, String songToAdd) {
        File file = new File("musicData/playlists/" + name + ".txt");
        File playlistsList = new File("musicData/playlists.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Files.write(file.toPath(), songToAdd.getBytes(), StandardOpenOption.APPEND);
            Files.write(file.toPath(), "\n".getBytes(), StandardOpenOption.APPEND);
            Files.write(playlistsList.toPath(), name.getBytes(), StandardOpenOption.APPEND);
            Files.write(playlistsList.toPath(), "\n".getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addToExistPlaylist(String name, String songToAdd) {
        File file = new File("musicData/playlists/" + name + ".txt");
        try {
            Files.write(file.toPath(), songToAdd.getBytes(), StandardOpenOption.APPEND);
            Files.write(file.toPath(), "\n".getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
