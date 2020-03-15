/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openbowl.display;

import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import org.openbowl.common.ColorToHex;
import org.openbowl.common.SystemHandler;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class DisplaySystemHandler extends SystemHandler {

    private MainApp app;
    private Timer timer;
    private Preferences prefs;

    public DisplaySystemHandler(MainApp app) {
        prefs = Preferences.userNodeForPackage(this.getClass());
        this.app = app;
        timer = new Timer();
    }

    @Override
    protected void onQuit() {
        timer.schedule(new onQuitTask(), 1000);
    }

    @Override
    protected Map<String, Object> onGet(Map<String, String> parms) {
        Map<String, Object> map = new HashMap<>();
        switch (parms.getOrDefault("get", "none")) {
            case "theme":
                onGetTheme(map);
                break;
            default:
                map.put(SUCCESS, false);
                map.put(ERROR_MSG, "missing or unsupported request");
                break;
        }
        return map;
    }

    @Override
    protected Map<String, Object> onPost(Map<String, String> parms, String body) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> requestBody = new HashMap<>();
        try {
            requestBody = gson.fromJson(body, Map.class);
        } catch (JsonSyntaxException e) {
            map.put(SUCCESS, false);
            map.put(ERROR_MSG, e.getMessage());
            return map;
        }
        try {
            switch (parms.getOrDefault("set", "none")) {
                case "quitProgram":
                    onQuit();
                    map.put(SUCCESS, true);
                    break;
                case "theme":
                    map.putAll(onSetTheme(requestBody));
                    break;
                default:
                    map.put(SUCCESS, false);
                    map.put(ERROR_MSG, "missing or unsupported request");
                    break;
            }
        } catch (ClassCastException e) {
            map.put(SUCCESS, false);
            map.put(ERROR_MSG, "missing or unsupported request");
        }
        return map;
    }

    private void onGetTheme(Map<String, Object> map) {
        map.put(SUCCESS, true);

        String lineColorString = prefs.get(BowlingGameDisplay.SETTING_LINE_COLOR, ColorToHex.colorToHex(BowlingGameDisplay.DEFAULT_LINE_COLOR));
        String textFillString = prefs.get(BowlingGameDisplay.SETTING_TEXT_FILL, ColorToHex.colorToHex(BowlingGameDisplay.DEFAULT_TEXT_FILL));
        String textOutlineString = prefs.get(BowlingGameDisplay.SETTING_TEXT_OUTLINE, ColorToHex.colorToHex(BowlingGameDisplay.DEFAULT_TEXT_OUTLINE));
        String headerFillString = prefs.get(BowlingGameDisplay.SETTING_HEADER_FILL, ColorToHex.colorToHex(BowlingGameDisplay.DEFAULT_HEADER_FILL));
        String scorecardFillString = prefs.get(BowlingGameDisplay.SETTING_SCORECARD_FILL, ColorToHex.colorToHex(BowlingGameDisplay.DEFAULT_SCORECARD_FILL));
        double scorecardAlpha = prefs.getDouble(BowlingGameDisplay.SETTING_SCORECARD_ALPHA, BowlingGameDisplay.DEFAULT_SCORECARD_ALPHA);
        double headerAlpha = prefs.getDouble(BowlingGameDisplay.SETTING_HEADER_ALPHA, BowlingGameDisplay.DEFAULT_HEADER_ALPHA);
        String defaultBackground = getClass().getResource(app.BACKGROUND_VALUE).toExternalForm();
        String backgroundURL = prefs.get(app.BACKGROUND_SETTING, defaultBackground);
        String mediaFolder = prefs.get(app.MEDIA_FOLDER_SETTING, app.MEDIA_FOLDER_VALUE);

        map.put(BowlingGameDisplay.SETTING_LINE_COLOR, lineColorString);
        map.put(BowlingGameDisplay.SETTING_TEXT_FILL, textFillString);
        map.put(BowlingGameDisplay.SETTING_TEXT_OUTLINE, textOutlineString);
        map.put(BowlingGameDisplay.SETTING_HEADER_FILL, headerFillString);
        map.put(BowlingGameDisplay.SETTING_SCORECARD_FILL, scorecardFillString);
        map.put(BowlingGameDisplay.SETTING_SCORECARD_ALPHA, scorecardAlpha);
        map.put(BowlingGameDisplay.SETTING_HEADER_ALPHA, headerAlpha);
        map.put(app.BACKGROUND_SETTING, backgroundURL);
        map.put(app.MEDIA_FOLDER_SETTING, mediaFolder);

    }

    private Map<String, Object> onSetTheme(Map<String, Object> map) {
        Map<String, Object> ret = new HashMap<>();
        try {
            String lineColorString = (String) map.get(BowlingGameDisplay.SETTING_LINE_COLOR);
            String textFillString = (String) map.get(BowlingGameDisplay.SETTING_TEXT_FILL);
            String textOutlineString = (String) map.get(BowlingGameDisplay.SETTING_TEXT_OUTLINE);
            String headerFillString = (String) map.get(BowlingGameDisplay.SETTING_HEADER_FILL);
            String scorecardFillString = (String) map.get(BowlingGameDisplay.SETTING_SCORECARD_FILL);
            double scorecardAlpha = (double) map.get(BowlingGameDisplay.SETTING_SCORECARD_ALPHA);
            double headerAlpha = (double) map.get(BowlingGameDisplay.SETTING_HEADER_ALPHA);
            String backgroundURL = (String) map.get(app.BACKGROUND_SETTING);
            String mediaFolder = (String) map.get(app.MEDIA_FOLDER_SETTING);

            prefs.put(BowlingGameDisplay.SETTING_LINE_COLOR, lineColorString);
            prefs.put(BowlingGameDisplay.SETTING_TEXT_FILL, textFillString);
            prefs.put(BowlingGameDisplay.SETTING_TEXT_OUTLINE, textOutlineString);
            prefs.put(BowlingGameDisplay.SETTING_HEADER_FILL, headerFillString);
            prefs.put(BowlingGameDisplay.SETTING_SCORECARD_FILL, scorecardFillString);

            prefs.putDouble(BowlingGameDisplay.SETTING_SCORECARD_ALPHA, scorecardAlpha);
            prefs.putDouble(BowlingGameDisplay.SETTING_HEADER_ALPHA, headerAlpha);

            prefs.put(app.BACKGROUND_SETTING, backgroundURL);
            prefs.put(app.MEDIA_FOLDER_SETTING, mediaFolder);
            onGetTheme(ret);

        } catch (ClassCastException e) {
            ret.put(SUCCESS, false);
            ret.put(ERROR_MSG, "missing or unsupported request");
        }

        return ret;
    }

    private class onQuitTask extends TimerTask {

        @Override
        public void run() {

            timer.cancel();
            timer = new Timer();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    app.onQuit();
                }
            });
            //timer.schedule(new pinCounterDelayTask(), pinCounterDelay);
        }

    }
}
