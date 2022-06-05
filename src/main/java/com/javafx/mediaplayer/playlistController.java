package com.javafx.mediaplayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class playlistController implements Initializable {
    public Button addPlaylistOkButton;
    public TextField playlistTextField;
    public Button addPlaylistCancelButton;
    public ListView<String> addPlaylistListView;
    public ArrayList<String> playlistsList;
    private boolean isPlaylistSelected = false;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playlistsList = new ArrayList<>();
        Utils.refreshPlaylistsList(playlistsList);
        initPlaylistsListView();
        onSelectionPlaylistsView();
    }
    public void enterPlaylistName() {
    }

    public void addPlaylistOk() {
        addPlaylistOkButton.setOnMouseClicked(mouseEvent -> {
            String songToAdd = FXMLController.curSongs.get(FXMLController.songNumber).toString();
            if (playlistTextField.getText().equals("") && !isPlaylistSelected) {
                createDefaultPlaylist(songToAdd);
            } else if (isPlaylistSelected) {
                Utils.createPlaylist(addPlaylistListView.getSelectionModel().getSelectedItem(), songToAdd);
            } else {
                if (!playlistTextField.getText().equals("")) {
                    Utils.createPlaylist(playlistTextField.getText(), songToAdd);
                }
                else {
                    createDefaultPlaylist(songToAdd);
                }
            }
            closePlaylists();
        });
    }

    private void createDefaultPlaylist(String song) {
        File file = new File("musicData/playlists.txt");
        int number;
        try {
            number = Files.readString(file.toPath()).split("\n").length;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Utils.createPlaylist("Playlist #" + number, song);
    }

    public void addPlaylistCancel() {
        addPlaylistCancelButton.setOnMouseClicked(mouseEvent -> {
            closePlaylists();
        });
    }
    private void initPlaylistsListView() {
        ObservableList<String> observableList = FXCollections.observableList(playlistsList);
        addPlaylistListView.setItems(observableList);
    }
    private void onSelectionPlaylistsView() {
        addPlaylistListView.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            addPlaylistListView.setOnMouseClicked(mouseEvent -> {
                isPlaylistSelected = true;
                if (mouseEvent.getClickCount() == 2) {
                    String selectedPlaylist = addPlaylistListView.getSelectionModel().getSelectedItem();
                    String songToAdd = FXMLController.curSongs.get(FXMLController.songNumber).toString();
                    String playlistPath = "musicData/playlists/" + selectedPlaylist + ".txt";
                    File playlistsFile = new File(playlistPath);
                    try {
                        Files.write(playlistsFile.toPath(), songToAdd.getBytes(), StandardOpenOption.APPEND);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    closePlaylists();
                }
            });
        });
    }
    private void closePlaylists() {
        Stage stage = (Stage) addPlaylistCancelButton.getScene().getWindow();
        stage.close();
    }
}
