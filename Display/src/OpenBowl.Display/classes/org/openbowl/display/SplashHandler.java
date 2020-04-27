/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openbowl.display;

import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import org.openbowl.common.BowlingSplash;
import org.openbowl.common.CommonHandler;
import org.openbowl.common.MediaFilters;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class SplashHandler extends CommonHandler {

    private final String DEFAULT_SPLASH = "/org/openbowl/display/images/Splash_Default.mp4";
    private final String PI_DEFAULT_SPLASH = "/home/pi/Media/Default/Videos/default_animation.mp4";

    private Media media;
    private MediaPlayer mediaPlayer;
    private final MediaView mediaView;
    private final StackPane stack;
    private final Preferences prefs;
    private final BooleanProperty onScreen;
    private final Random rand;
    private String SplashPath;
    private int ScreenNumber;

    public SplashHandler(StackPane stack, int screen) {
        this.stack = stack;
        this.ScreenNumber = screen;
        rand = new Random();
        prefs = Preferences.userNodeForPackage(this.getClass());

        mediaView = new MediaView();
        onScreen = new SimpleBooleanProperty(false);
        onScreen.addListener(notUsed -> onScreenChanged());
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
                case "splash":
                    if (requestBody.containsKey("type")) {
                        map.putAll(onSetSplash((String) requestBody.get("type")));

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

    private Map<String, Object> onSetSplash(String typeString) {
        Map<String, Object> map = new HashMap<>();
        try {
            String mediaPref = prefs.get(MainApp.MEDIA_FOLDER_SETTING, MainApp.MEDIA_FOLDER_VALUE);
            BowlingSplash splashType = BowlingSplash.valueOf(typeString);
            String homeDir = System.getProperty("user.home");
            String mediaDir = homeDir + File.separator + "Media"
                    + File.separator + mediaPref + File.separator
                    + "Videos" + File.separator + splashType.toString() + File.separator;
            //System.out.println("media dir: " + mediaDir);
            map.put(SUCCESS, true);
            map.put("path", mediaDir);
            File dir = new File(mediaDir);
            SplashPath = getClass().getResource(DEFAULT_SPLASH).toExternalForm();
            if (dir.exists() && dir.isDirectory()) {
                String[] fileList = dir.list(MediaFilters.createVideoFileFilter());
                if (fileList.length > 0) {
                    int i = rand.nextInt() % fileList.length;
                    File m = new File(mediaDir + fileList[i]);
                    if (m.isFile()) {
                        //System.out.println(mediaDir + fileList[i]);
                        SplashPath = new File(mediaDir + fileList[i]).toURI().toString();
                    }
                }
            }
            media = new Media(SplashPath);
            System.out.printf("Screen %d - Playing Media: %s\n", ScreenNumber, SplashPath);
            showSplash();

        } catch (IllegalArgumentException e) {
            map.put(SUCCESS, false);
            map.put(ERROR_MSG, e.toString());
        }

        return map;
    }

    private void showSplash() {
        onScreen.setValue(Boolean.TRUE);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (!isPi()) {
                    mediaPlayer = new MediaPlayer(media);
                    mediaView.setMediaPlayer(mediaPlayer);
                    mediaView.setPreserveRatio(false);
                    mediaView.fitHeightProperty().bind(stack.heightProperty());
                    mediaView.fitWidthProperty().bind(stack.widthProperty());
                    mediaPlayer.setOnEndOfMedia(new Runnable() {
                        @Override
                        public void run() {
                            onScreen.setValue(Boolean.FALSE);
                        }
                    });
                    stack.getChildren().add(mediaView);
                    mediaPlayer.play();
                } else {
                    SplashPath = SplashPath.startsWith("jar")? PI_DEFAULT_SPLASH : SplashPath.substring(5);
                    Bounds b = stack.localToScene(stack.getBoundsInLocal());
                    String windowLocation = Double.toString(b.getMinX());
                    windowLocation += "," + Double.toString(b.getMinY());
                    double width = b.getMaxX() - b.getMinX();
                    double height = b.getMaxY() - b.getMinY();
                    windowLocation += "," + Double.toString(width);
                    windowLocation += "," + Double.toString(height);
                    
                    String command = "omxplayer --no-keys --layer 99 --win " + windowLocation + " " + SplashPath;
                    
                    try {
                        
                        Process p = Runtime.getRuntime().exec(command);
                        p.getOutputStream().close();
                        
                        int exitValue = p.waitFor();
                        if (exitValue != 0) {
                            System.out.println("omxplayer Abnormal process termination");
                            System.out.println("Command: " + command);
                            System.out.println(new String(p.getErrorStream().readAllBytes()));
                        }
                    } catch (InterruptedException ex) {
                        System.out.println("omxplayer process inturpted");
                    } catch (IOException ex) {
                        System.out.println("omxplayer io exception: " + ex.toString());
                    }
                }
            }

        });

    }

    private void onScreenChanged() {
        if (!onScreen.get()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    stack.getChildren().remove(mediaView);
                }
            });
        }
    }

    private boolean isPi() {
        if (System.getProperty("os.name").equals("Linux")) {
            if (System.getProperty("os.arch").equals("x86") || System.getProperty("os.arch").equals("amd64")) {
                return false;
            }
            return true;
        }
        return false;
    }

}
