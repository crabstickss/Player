package com.javafx.tests;

import com.javafx.mediaplayer.Utils;
import javafx.util.Duration;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class UtilsTest {
    public UtilsTest() {
    }

    /**
     * Testing elapsed time and duration
     */
    @Test
    public void formatTime() {
        Duration duration = new Duration(20000.0D);
        Duration elapsed = new Duration(5000.0D);
        String expected = "00:05/00:20";
        String actual = Utils.formatTime(elapsed, duration);
        Assert.assertEquals(expected, actual);
    }


    /**
     * Testing the case when duration = 0
     */
    @Test
    public void formatTimeDurationZero() {
        Duration duration = new Duration(0.0D);
        Duration elapsed = new Duration(2000.0D);
        String expected = "00:02";
        String actual = Utils.formatTime(elapsed, duration);
        Assert.assertEquals(expected, actual);
    }

    /**
     * Testing if file musicData created
     */
    @Test
    public void musicDataFolderCheck() {
        Utils.musicDataFolderCheck();
        Assert.assertTrue(new File("musicData").exists());
    }

    /**
     * Testing if folder musicData/playlists is created
     */
    @Test
    public void playlistsFolderCheck() {
        Utils.musicDataFolderCheck();
        Assert.assertTrue(new File("musicData/playlists").exists());
    }

    /**
     * Testing if file musicData/currentList.txt is created
     * +
     * if it is empty or null
     */
    @Test
    public void currentListCheck() throws IOException {
        Utils.currentListCheck();
        Assert.assertTrue(new File("musicData/currentList.txt").exists());
        BufferedReader reader = new BufferedReader(new FileReader("musicData/currentList.txt"));
        String text = reader.readLine();
        Assert.assertTrue(text == null || text.isEmpty());
    }

    /**
     * Testing if file musicData/playlists.txt is created
     */
    @Test
    public void playlistsListCheck() {
        Utils.playlistsListCheck();
        Assert.assertTrue(new File("musicData/playlists.txt").exists());
    }

    /**
     * Testing if method createPlaylist works correctly by creating new playlist file
     */
    @Test
    public void createPlaylist() {
        Utils.createPlaylist("pop", "медуза");
        File file = new File("musicData/playlists/pop.txt");
        Assert.assertTrue(file.exists());
        file.delete();
    }
    /**
     * Testing if method addToExistPlaylist works correctly by adding to playlist new song
     */
    @Test
    public void addToExistPlaylist() throws IOException {
        Utils.createPlaylist("pop", "медуза");
        Utils.addToExistPlaylist("pop", "елочка");
        File file = new File("musicData/playlists/pop.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String first = reader.readLine();
        String second = reader.readLine();
        Assert.assertEquals("медуза", first);
        Assert.assertEquals("елочка", second);
        file.delete();
    }
}
