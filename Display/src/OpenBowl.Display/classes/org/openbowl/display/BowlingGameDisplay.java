/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openbowl.display;

import java.util.ArrayList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import org.openbowl.common.BowlingGame;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class BowlingGameDisplay extends Region {

    private final String NAME_LABEL = "Name";
    private final String HDCP_LABEL = "HDCP";
    private final String TOTAL_LABEL = "Total";
    private Canvas canvas;
    private double aspect;
    private String laneName;
    private ArrayList<BowlingGame> games;
    private int curentPlayer;

    public BowlingGameDisplay() {
        canvas = new Canvas();
        games = new ArrayList<>();
        getChildren().add(canvas);
        curentPlayer = 0;
        draw();

    }

    public void addPlayer(BowlingGame newPlayer) {
        games.add(newPlayer);
        draw();
    }
    
    public void updatePlater(int playerNumber, BowlingGame game){
        if(playerNumber < games.size()){
            games.get(playerNumber).updateTo(game);
            draw();
        }
    }

    public void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        double xDist = canvas.getWidth() / 44;
        double yDist = canvas.getWidth() / 44;
        double hPercent10 = canvas.getHeight() / 10.0;
        double wPercent05 = canvas.getWidth() / 20.0;
        double hPercent05 = canvas.getHeight() / 20.0;

        drawScoreCardHeader(xDist, yDist);
        for (int i = 0; i < games.size(); i++) {
            drawScoreCard(xDist, (2 * yDist) + (i * 3 * yDist), games.get(i));
        }

        gc.restore();

    }

    private void drawScoreCard(double x, double y, BowlingGame game) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double xDist = canvas.getWidth() / 44.0;
        double yDist = canvas.getWidth() / 44.0;
        double yOffset, xOffset;

        gc.save();
        //gc.strokeLine(x, y, x + (xDist * 42), y);
        //yOffset = y + (xDist);

        //gc.strokeLine(x + (xDist * 9), yOffset, x + (xDist * 42), yOffset);
        //stroke bottom line
        yOffset = y + (yDist * 3);
        gc.strokeLine(x, yOffset, x + (xDist * 42), yOffset);
        //stroke left line
        gc.strokeLine(x, y, x, yOffset);
        //stroke frame lines
        for (int i = 9; i < 43; i += 3) {
            xOffset = x + (i * xDist);
            //System.out.println(i);
            gc.strokeLine(xOffset, y, xOffset, yOffset);
        }
        //stroke handycap / score box outline
        for (int i = 7; i < 40; i += 3) {
            xOffset = x + (xDist * i);
            yOffset = y + yDist;
            gc.strokeLine(xOffset, y, xOffset, yOffset);
            gc.strokeLine(xOffset, yOffset, xOffset + (xDist * 2), yOffset);
        }
        //stroke ball seperator
        for (int i = 11; i < 40; i += 3) {
            xOffset = x + (xDist * i);
            yOffset = y + yDist;
            gc.strokeLine(xOffset, y, xOffset, yOffset);
        }
        //stroke 3rd ball 10th frame
        xOffset = x + (xDist * 36);
        yOffset = y + yDist;
        gc.strokeLine(xOffset, yOffset, xOffset + xDist, yOffset);

        double fontSize = getFontSize(NAME_LABEL, yDist);
        gc.setFont(new Font(gc.getFont().getName(), fontSize * 1.5));
        double fontBuffer = yDist / 10.0;
        double yFont = y + (yDist * 3) - fontBuffer;

        //Stroke Name
        gc.strokeText(game.getPlayerName(), x + fontBuffer, yFont);
        gc.fillText(game.getPlayerName(), x + fontBuffer, yFont);

        gc.setFont(new Font(gc.getFont().getName(), fontSize));
        yFont = y + yDist - (2 * fontBuffer);
        //Stroke HDCP != 0
        if (game.getHandycap() > 0) {
            String hdcp = String.format("%3d", game.getHandycap());
            gc.strokeText(hdcp, x + fontBuffer + (7 * xDist), yFont);
            gc.fillText(hdcp, x + fontBuffer + (7 * xDist), yFont);
        }

        gc.restore();
    }

    private void drawScoreCardHeader(double x, double y) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double xDist = canvas.getWidth() / 44.0;
        double yDist = canvas.getWidth() / 44.0;

        double xOffset, yOffset;
        gc.save();
        gc.strokeLine(x, y, x + (xDist * 42), y);
        yOffset = y + (xDist);
        gc.strokeLine(x, yOffset, x + (xDist * 42), yOffset);
        gc.strokeLine(x, y, x, yOffset);
        for (int i = 9; i < 43; i += 3) {
            xOffset = x + (i * xDist);
            //System.out.println(i);
            gc.strokeLine(xOffset, y, xOffset, yOffset);
        }
        double fontSize = getFontSize(NAME_LABEL, yDist);
        gc.setFont(new Font(gc.getFont().getName(), fontSize));
        double fontBuffer = yDist / 10.0;
        double yFont = y + yDist - (2 * fontBuffer);
        gc.strokeText(NAME_LABEL, x + fontBuffer, yFont);
        gc.fillText(NAME_LABEL, x + fontBuffer, yFont);

        gc.setFont(new Font(gc.getFont().getName(), fontSize * .8));
        gc.strokeText(HDCP_LABEL, x + (7 * xDist), yFont);
        gc.fillText(HDCP_LABEL, x + (7 * xDist), yFont);
        gc.setFont(new Font(gc.getFont().getName(), fontSize));

        for (int i = 1; i < 11; i++) {
            double xLoc = x + (7 * xDist) + (3 * i * xDist);
            xLoc += (i < 10) ? fontBuffer : 0;
            gc.strokeText(Integer.toString(i), xLoc, yFont);
            gc.fillText(Integer.toString(i), xLoc, yFont);
        }

        gc.strokeText(TOTAL_LABEL, x + fontBuffer + (39 * xDist), yFont);
        gc.fillText(TOTAL_LABEL, x + fontBuffer + (39 * xDist), yFont);

        gc.restore();

    }

    private double getFontSize(String text, double maxHeight) {
        double fontSize = 1;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Text nameText = new Text(text);
        nameText.setFont(new Font(gc.getFont().getName(), fontSize));
        while (nameText.getBoundsInLocal().getHeight() < maxHeight) {
            fontSize++;
            nameText.setFont(new Font(gc.getFont().getName(), fontSize));
        }
        return fontSize;
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        aspect = w / h;

        canvas.setHeight(h);
        canvas.setWidth(w);

        draw();
    }
}
