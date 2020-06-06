/*
 * Copyright (C) 2020 Open Bowl <http://www.openbowlscoring.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openbowl.client;

import java.util.prefs.Preferences;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.openbowl.common.CommonImages;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class LaneDisplay extends Region {

    private final Paint BACKGROUND_STD = Color.BLACK;
    private final Paint BACKGROUND_HOVER = Color.web("#222222");
    private final Paint BACKGROUND_SELECTED_STD = Color.web("#00aac4");
    private final Paint BACKGROUND_SELECTED_HOVER = Color.web("#04c3e0");

    private final Paint BACKGROUND_ACTIVE_STD = Color.web("#5B8F5A");
    private final Paint BACKGROUND_ACTIVE_HOVER = Color.web("#71B46F");

    private final String VOLT_ICON = "⚡";   // U+26A1 High Voltage
    private final String HEAT_ICON = "🌡";   // U+1F321 Thermometer
    private final String UPDATE_ICON = "⏣"; // U+23E3 Benzene Ring with Circle
    private final String CRASH_ICON = "⊛";  // U+229B circle Asterick
    private final String REBOOT_ICON = "⟲"; // U+27F2 Anticlockwise gap circle arrow

    private Canvas mCanvas;
    private String laneName;
    private Preferences prefs;
    private Paint backgroundColor;
    private Paint textColor;
    private BooleanProperty selectedProperty;

    private BooleanProperty onlineProperty;
    private BooleanProperty updateProperty;
    private BooleanProperty heatProperty;
    private BooleanProperty voltProperty;
    private BooleanProperty rebootProperty;
    private BooleanProperty crashProperty;
    private BooleanProperty gameStatusProperty;
    private CommonImages commonImage;
    private ContextMenu contextMenu;

    public LaneDisplay(String laneName) {
        this.laneName = laneName;
        this.mCanvas = new Canvas();
        getChildren().add(mCanvas);
        this.backgroundColor = Color.BLACK;
        this.textColor = Color.WHITESMOKE;
        this.selectedProperty = new SimpleBooleanProperty(false);
        this.onlineProperty = new SimpleBooleanProperty(false);
        this.updateProperty = new SimpleBooleanProperty(false);
        this.heatProperty = new SimpleBooleanProperty(false);
        this.voltProperty = new SimpleBooleanProperty(false);
        this.rebootProperty = new SimpleBooleanProperty(false);
        this.crashProperty = new SimpleBooleanProperty(false);
        this.gameStatusProperty = new SimpleBooleanProperty(false);

        this.selectedProperty.addListener(notUsed -> draw());
        this.onlineProperty.addListener(notUsed -> draw());
        this.updateProperty.addListener(notUsed -> draw());
        this.heatProperty.addListener(notUsed -> draw());
        this.voltProperty.addListener(notUsed -> draw());
        this.rebootProperty.addListener(notUsed -> draw());
        this.crashProperty.addListener(notUsed -> draw());
        this.gameStatusProperty.addListener(notUsed -> draw());
        commonImage = new CommonImages();
        this.contextMenu = new ContextMenu();

        draw();
        this.hoverProperty().addListener((obs, ob, nb) -> draw());
        this.setOnContextMenuRequested(value -> onContextMenuRequest(value));

    }

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();

        this.setPrefSize(64, 96);

        mCanvas.setHeight(h);
        mCanvas.setWidth(w);

        draw();
    }

    private void draw() {
        //System.out.println(laneName + " draw");
        setBackgroundColor();
        GraphicsContext gc = mCanvas.getGraphicsContext2D();
        gc.save();
        double width = mCanvas.getWidth();
        double height = mCanvas.getHeight();
        double yBlock = height / 8.0;
        double xBlock = width / 5.0;

        double strokeX, strokeY;

        gc.clearRect(0, 0, width, height);

        gc.setFill(backgroundColor);

        gc.fillRoundRect(0, 0, width, height, 15, 15);

        //Stroke Lane #
        Text t = new Text(laneName);
        strokeX = (width - t.getBoundsInLocal().getWidth()) / 2.0;
        gc.setStroke(textColor);
        gc.setFill(textColor);

        gc.strokeText(laneName, strokeX, yBlock);

        if (onlineProperty.get()) {
            gc.drawImage(commonImage.appIcon(), width / 4.0, yBlock * 3.0, width / 2.0, width / 2.0);

        }//End Online
        else {
            gc.setStroke(Color.RED);
            gc.setLineWidth(3);
            gc.strokeLine(0, 0, width, height);
            gc.strokeLine(width, 0, 0, height);
        }//End Offline

        gc.setLineWidth(1);
        //updates
        if (updateProperty.get()) {
            gc.setStroke(Color.WHITE);
            gc.strokeText(UPDATE_ICON, 0, yBlock * 2);
        }
        //reboot
        if (rebootProperty.get()) {
            gc.setStroke(Color.WHITE);
            gc.strokeText(REBOOT_ICON, xBlock * 1, yBlock * 2);
        }
        //volt
        if (voltProperty.get()) {
            gc.setStroke(Color.YELLOW);
            gc.strokeText(VOLT_ICON, xBlock * 2, yBlock * 2);
        }
        //heat
        if (heatProperty.get()) {
            gc.setStroke(Color.RED);
            gc.strokeText(HEAT_ICON, xBlock * 3, yBlock * 2);
        }
        //crash
        if (crashProperty.get()) {
            gc.setStroke(Color.YELLOW);
            gc.strokeText(CRASH_ICON, xBlock * 4, yBlock * 2);
        }
        gc.restore();

    }

    public void update() {
        draw();
    }

    private void setBackgroundColor() {
        if (this.hoverProperty() != null && this.hoverProperty().get()) {
            backgroundColor = selectedProperty.get() ? BACKGROUND_SELECTED_HOVER : BACKGROUND_HOVER;
            backgroundColor = gameStatusProperty.get() ? BACKGROUND_ACTIVE_HOVER : backgroundColor;
        } else {
            backgroundColor = selectedProperty.get() ? BACKGROUND_SELECTED_STD : BACKGROUND_STD;
            backgroundColor = gameStatusProperty.get() ? BACKGROUND_ACTIVE_STD : backgroundColor;
        }
        //draw();
    }

    private void onContextMenuRequest(ContextMenuEvent value) {
        value.consume();
        contextMenu.show(this, value.getScreenX(), value.getScreenY());
    }

    public BooleanProperty selectedProperty() {
        return selectedProperty;
    }

    public BooleanProperty OnlineProperty() {
        return onlineProperty;
    }

    public BooleanProperty UpdateProperty() {
        return updateProperty;
    }

    public BooleanProperty HeatProperty() {
        return heatProperty;
    }

    public BooleanProperty VoltProperty() {
        return voltProperty;
    }

    public BooleanProperty RebootProperty() {
        return rebootProperty;
    }

    public BooleanProperty CrashProperty() {
        return crashProperty;
    }

    public BooleanProperty GameStatusProperty() {
        return gameStatusProperty;
    }

    public String getLaneName() {
        return laneName;
    }

    public void setContextMenu(ContextMenu contextMenu) {
        this.contextMenu = contextMenu;
    }

}
