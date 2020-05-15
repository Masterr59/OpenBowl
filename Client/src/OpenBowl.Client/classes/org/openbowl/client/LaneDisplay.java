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
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class LaneDisplay extends Region {

    private final Paint BACKGROUND_STD = Color.BLACK;
    private final Paint BACKGROUND_HOVER = Color.web("#222222");
    private final Paint BACKGROUND_SELECTED_STD = Color.web("#00aac4");
    private final Paint BACKGROUND_SELECTED_HOVER = Color.web("#04c3e0");

    private Canvas mCanvas;
    private String laneName;
    private Preferences prefs;
    private Paint backgroundColor;
    private BooleanProperty selectedProperty;

    public LaneDisplay(String laneName) {
        this.laneName = laneName;
        this.mCanvas = new Canvas();
        getChildren().add(mCanvas);
        backgroundColor = Color.BLACK;
        selectedProperty = new SimpleBooleanProperty(false);
        draw();
        this.hoverProperty().addListener((obs, ob, nb) -> onHoverChange(nb));
        this.setOnMouseClicked(mouseEvent -> onClicked(mouseEvent));
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
        System.out.println();
        GraphicsContext gc = mCanvas.getGraphicsContext2D();
        gc.save();
        gc.clearRect(0, 0, mCanvas.getWidth(), mCanvas.getHeight());

        gc.setFill(backgroundColor);

        gc.fillRect(0, 0, mCanvas.getWidth(), mCanvas.getHeight());

        gc.restore();
    }

    public void update() {
        draw();
    }

    private void onHoverChange(Boolean nb) {
        if (nb) {
            backgroundColor = selectedProperty.get() ? BACKGROUND_SELECTED_HOVER : BACKGROUND_HOVER;
        } else {
            backgroundColor = selectedProperty.get() ? BACKGROUND_SELECTED_STD : BACKGROUND_STD;
        }
        draw();
    }

    private void onClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            mouseEvent.consume();
            System.out.println("Clicked");
            selectedProperty.set(!selectedProperty.get());
            draw();
        }
    }

    private void onContextMenuRequest(ContextMenuEvent value) {
        value.consume();
        System.out.println("rightClicked");
    }

    public BooleanProperty selectedProperty() {
        return selectedProperty;
    }

}
