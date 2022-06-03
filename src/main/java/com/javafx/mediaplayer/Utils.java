package com.javafx.mediaplayer;

import javafx.util.Duration;

public class Utils {
    protected static String formatTime(Duration elapsed, Duration duration) {
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
}
