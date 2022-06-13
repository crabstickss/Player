package com.javafx.tests;

import com.javafx.mediaplayer.Utils.Utils;
import javafx.util.Duration;
import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {
    public UtilsTest() {
    }

    @Test
    public void formatTime() {
        Duration duration = new Duration(20000.0D);
        Duration elapsed = new Duration(5000.0D);
        String expected = "00:05/00:20";
        String actual = Utils.formatTime(elapsed, duration);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void formatTimeDurationZero() {
        Duration duration = new Duration(0.0D);
        Duration elapsed = new Duration(2000.0D);
        String expected = "00:02";
        String actual = Utils.formatTime(elapsed, duration);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void currentListCheck() {
    }


}
