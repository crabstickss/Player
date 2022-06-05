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
    }
    public void enterPlaylistName() {
    }

    public void addPlaylistOk() {
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
    private void closePlaylists() {
        Stage stage = (Stage) addPlaylistCancelButton.getScene().getWindow();
        stage.close();
    }
}
