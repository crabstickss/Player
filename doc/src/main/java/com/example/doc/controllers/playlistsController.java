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
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 *  * Class PlaylistsController responsible for the part of the GUI that contains a list of playlists and buttons to manage them.
 */
public class playlistController implements Initializable {
    public Button addPlaylistOkButton;
    public TextField playlistTextField;
    public Button addPlaylistCancelButton;
    public ListView<String> addPlaylistListView;
    public ArrayList<String> playlistsList;
    /**
     * Initializes UI elements.
     */
    public void initialize(URL url, ResourceBundle resourceBundle);

    /**
     * Enter a Playlist name.
     */
    public void enterPlaylistName() {
    }

    /**
     * Add the button "Ok" to the Pane.
     */
    public void addPlaylistOk();

    /** Crete a default Playlist.
     * @param song
     */
    private void createDefaultPlaylist(String song);
    /**
     * Add the button "Cancel" to the Pane.
     */
    public void addPlaylistCancel() {
        addPlaylistCancelButton.setOnMouseClicked(mouseEvent -> {
            closePlaylists();
        });
    }

    /**
     * Initialize the view for casting processing.
     */
    private void initPlaylistsListView() {
        ObservableList<String> observableList = FXCollections.observableList(playlistsList);
        addPlaylistListView.setItems(observableList);
    }

    /**
     * Fina a Initialization the view for casting processing.
     */
    private void onSelectionPlaylistsView() {
        addPlaylistListView.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            addPlaylistListView.setOnMouseClicked(mouseEvent ->);

    /**
     * Finish all processes and close PlayList Pane.
     */
    private void closePlaylists();
}
