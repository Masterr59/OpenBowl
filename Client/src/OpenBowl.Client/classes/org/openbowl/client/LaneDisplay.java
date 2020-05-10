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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class LaneDisplay extends Region {

    private Canvas mCanvas;
    private String laneName;
    private Preferences prefs;

    public LaneDisplay(String laneName) {
        this.laneName = laneName;
        this.mCanvas = new Canvas();
        getChildren().add(mCanvas);
        draw();
        
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

        gc.setFill(Color.BLACK);
        
        gc.fillRect(0, 0, mCanvas.getWidth(), mCanvas.getHeight());
        
        gc.restore();
    }

    public void update(){
        draw();
    }
}
