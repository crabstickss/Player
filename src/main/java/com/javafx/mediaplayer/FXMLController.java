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

    private ArrayList<File> curSongs;
    private boolean isPlaying;
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


    public void addMediaToPlaylist() {

    }

    public void playMedia(ActionEvent actionEvent) {
        if (isPlaying) mediaPlayer.stop();
        isPlaying = false;
        media = new Media(curSongs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        songLabel.setText(curSongs.get(songNumber).getName());
        mediaPlayer.play();
        isPlaying = true;
        progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
            }
        });

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<javafx.util.Duration>() {
            @Override
            public void changed(ObservableValue<? extends javafx.util.Duration> observable,
                                javafx.util.Duration oldValue, javafx.util.Duration newValue) {
                progressBar.setValue(newValue.toSeconds());
            }
        });
        progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                javafx.util.Duration total = media.getDuration();
                progressBar.setMax(total.toSeconds());
            }
        });

        progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
            }
        });

        progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
            }
        });

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
        songLabel.setText(curSongs.get(songNumber).getName());

        mediaPlayer.play();
        isPlaying = true;
        progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
            }
        });

        progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                javafx.util.Duration total = media.getDuration();
                progressBar.setMax(total.toSeconds());
            }
        });
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<javafx.util.Duration>() {
            @Override
            public void changed(ObservableValue<? extends javafx.util.Duration> observable,
                                javafx.util.Duration oldValue, javafx.util.Duration newValue) {
                progressBar.setValue(newValue.toSeconds());
            }
        });

        progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
            }
        });

        progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
            }
        });
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
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                FileWriter fw = new FileWriter(file.toString());
                fw.write("");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            songs = Files.readString(file.toPath()).split("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        curSongs = new ArrayList<File>();
        refreshCurrentSongs();
        // directory = new File("src/music");
        // files = directory.listFiles();
        List<String> songNames = new ArrayList<String>();
        if (songs != null) {
            for (String song: songs) {
                String[] temp = song.split("\\\\");
                if (temp.length > 1) {
                    songNames.add(temp[temp.length - 1]);
                }
            }
        }
        // media = new Media(songs.get(songNumber).toURI().toString());
        // mediaPlayer = new MediaPlayer(media);

        //songLabel.setText(songs.get(songNumber).getName());
        ObservableList<String> observableList = FXCollections.observableList(songNames);
        if (songs.length == 1) {
            songsListView.setPlaceholder(new Label("Nothing here"));
        } else {
            songsListView.setItems(observableList);
        }
        songsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                // mediaPlayer.stop();
                // File[] listOfFiles = new File[curSongs.size()];
                // for (int i = 0; i < songs.length; i++) {
                //     listOfFiles[i] = new File(curSongs.get(i).toString());
                // }

                String selectedSong = songsListView.getSelectionModel().getSelectedItem();
                int i;
                for (i = 0; i < curSongs.size() - 1; i++) {
                    String[] temp = curSongs.get(i).toString().split("\\\\");
                    if (temp[temp.length-1].equals(selectedSong)) {
                        break;
                    }
                }
                //media = new Media(listOfFiles[i].toURI().toString());
                // mediaPlayer = new MediaPlayer(media);

                // songLabel.setText(listOfFiles[i].getName());
                // mediaPlayer.play();
                songNumber = i;
                if (isPlaying) mediaPlayer.stop();
                media = new Media(curSongs.get(songNumber).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                songLabel.setText(curSongs.get(songNumber).getName());
                mediaPlayer.play();
                isPlaying = true;
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
            songLabel.setText(curSongs.get(songNumber).getName());

            mediaPlayer.play();
            isPlaying = true;
            progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
                }
            });

            progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
                }
            });

            mediaPlayer.setOnReady(new Runnable() {
                @Override
                public void run() {
                    javafx.util.Duration total = media.getDuration();
                    progressBar.setMax(total.toSeconds());
                }
            });

            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<javafx.util.Duration>() {
                @Override
                public void changed(ObservableValue<? extends javafx.util.Duration> observable,
                                    javafx.util.Duration oldValue, javafx.util.Duration newValue) {
                    progressBar.setValue(newValue.toSeconds());
                }
            });

            progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
                }
            });

            progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
                }
            });
        }
        else {
            progressBar.setValue(0);
            mediaPlayer.seek(Duration.seconds(0));
        }
    }
}


