/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openbowl.display;

import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.openbowl.common.CommonHandler;
import org.openbowl.common.MediaFilters;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class MessageHandler extends CommonHandler {

    private final String DEFAULT_IMAGE = "/org/openbowl/display/images/Default.jpg";

    private final StackPane stack;
    private final Preferences prefs;
    private final Random rand;
    private final ImageView imageView;
    private Timer timer;

    public MessageHandler(StackPane stack) {
        this.stack = stack;
        this.rand = new Random();
        this.prefs = Preferences.userNodeForPackage(this.getClass());
        this.imageView = new ImageView();
        this.timer = new Timer();
    }

    @Override
    protected Map<String, Object> onGet(Map<String, String> parms) {
        Map<String, Object> map = new HashMap<>();
        switch (parms.getOrDefault("get", "none")) {
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
                case "card":
                    if (requestBody.containsKey("type") && requestBody.containsKey("duration")) {
                        int duration = new Double((double) requestBody.get("duration")).intValue();
                        map.putAll(onSetCard((String) requestBody.get("type"), duration));

                    } else {
                        map.put(SUCCESS, false);
                        map.put(ERROR_MSG, "missing parms");
                    }
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

    private Map<String, Object> onSetCard(String cardType, int duration) {
        Map<String, Object> map = new HashMap<>();
        try {
            String mediaPref = prefs.get(MainApp.MEDIA_FOLDER_SETTING, MainApp.MEDIA_FOLDER_VALUE);
            String homeDir = System.getProperty("user.home");
            String mediaDir = homeDir + File.separator + "Media"
                    + File.separator + mediaPref + File.separator
                    + "Cards" + File.separator + cardType + File.separator;
            //System.out.println("media dir: " + mediaDir);
            map.put(SUCCESS, true);
            map.put("path", mediaDir);
            File dir = new File(mediaDir);
            String cardPath = getClass().getResource(DEFAULT_IMAGE).toExternalForm();
            if (dir.exists() && dir.isDirectory()) {
                String[] fileList = dir.list(MediaFilters.createImageFileFilter());
                if (fileList.length > 0) {
                    int i = rand.nextInt() % fileList.length;
                    File m = new File(mediaDir + fileList[i]);
                    if (m.isFile()) {
                        cardPath = new File(mediaDir + fileList[i]).toURI().toString();
                    }
                }
            }
            if (cardType.equals("NONE")) {
                System.out.println("removing card");
                timer.schedule(new RemoveCardTask(), 1);
            } else {
                System.out.println("Showing Card: " + cardPath);
                imageView.setImage(new Image(cardPath));
                showCard(duration);
            }

        } catch (IllegalArgumentException e) {
            map.put(SUCCESS, false);
            map.put(ERROR_MSG, e.toString());
        }

        return map;
    }

    private void showCard(int duration) {
        if (duration >= 0) {
            timer.schedule(new RemoveCardTask(), duration);
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stack.getChildren().add(imageView);

            }
        });
    }

    private class RemoveCardTask extends TimerTask {

        @Override
        public void run() {
            timer.cancel();
            timer = new Timer();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    stack.getChildren().remove(imageView);

                }
            });

        }

    }

}
