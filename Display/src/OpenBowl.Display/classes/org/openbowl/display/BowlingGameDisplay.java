/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openbowl.display;

import java.util.ArrayList;
import java.util.prefs.Preferences;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.openbowl.common.Ball;
import org.openbowl.common.BallUtil;
import org.openbowl.common.BowlingFrame;
import org.openbowl.common.BowlingFrame.ScoreType;

import org.openbowl.common.BowlingGame;
import org.openbowl.common.ColorToHex;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class BowlingGameDisplay extends Region {

    public final static Color DEFAULT_LINE_COLOR = Color.WHITE;
    public final static Color DEFAULT_TEXT_OUTLINE = Color.WHITE;
    public final static Color DEFAULT_TEXT_FILL = Color.WHITE;
    public final static Color DEFAULT_HEADER_FILL = Color.BLACK;
    public final static Color DEFAULT_SCORECARD_FILL = Color.BLACK;
    public final static double DEFAULT_HEADER_ALPHA = .5;
    public final static double DEFAULT_SCORECARD_ALPHA = 0.5;

    public final static String SETTING_LINE_COLOR = "Line_Color";
    public final static String SETTING_TEXT_OUTLINE = "Text_Outline";
    public final static String SETTING_TEXT_FILL = "Text_Fill";
    public final static String SETTING_HEADER_FILL = "Header_Fill";
    public final static String SETTING_SCORECARD_FILL = "Scorecard_Fill";
    public final static String SETTING_HEADER_ALPHA = "Header_Alpha";
    public final static String SETTING_SCORECARD_ALPHA = "Scorecard_Alpha";

    private final String NAME_LABEL = "Name";
    private final String HDCP_LABEL = "HDCP";
    private final String TOTAL_LABEL = "Total";
    private Canvas canvas;
    private double aspect;
    private String laneName;
    private ArrayList<BowlingGame> games;
    private int curentPlayer;
    private Preferences prefs;

    private Paint lineColor;
    private Paint headerFill;
    private Paint scorecardFill;
    private Paint textFillColor;
    private Paint textOutlineColor;

    public BowlingGameDisplay() {
        prefs = Preferences.userNodeForPackage(this.getClass());
        loadColors();
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

    public void reset() {
        curentPlayer = 0;
        games.clear();
        draw();
    }

    public int getNumPlayers() {
        return this.games.size();
    }

    /**
     *
     * Updates a player scorecard
     *
     * @param playerNumber The player to be updated
     * @param game The new game scorecard
     */
    public void updatePlater(int playerNumber, BowlingGame game) {
        if (playerNumber < games.size()) {
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

        gc.setFill(scorecardFill);
        gc.fillRect(x, y, x + (xDist * 41), (yDist * 3));

        gc.setStroke(lineColor);
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

        gc.setStroke(textOutlineColor);
        gc.setFill(textOutlineColor);

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
        if (game.getHandicap() > 0) {
            String hdcp = String.format("%3d", game.getHandicap());
            gc.strokeText(hdcp, x + fontBuffer + (7 * xDist), yFont);
            gc.fillText(hdcp, x + fontBuffer + (7 * xDist), yFont);
        }
        //stroke scores
        for (int i = 0; i < game.getFrameIndex(); i++) {
            BowlingFrame cFrame = game.getFrames()[i];
            Ball ballValues[] = cFrame.getBalls();
            ScoreType scoreTypes[] = cFrame.getScoreTypes();

            //frames 1-9 (pins)
            if (i < 9) {
                for (int j = 0; j < 2; j++) {
                    if (scoreTypes[j] != ScoreType.NONE) {
                        String ballString = String.valueOf(BallUtil.toChar(ballValues[j]));
                        gc.strokeText(ballString, x + fontBuffer + ((10 + j + (3 * i)) * xDist), yFont);
                        gc.fillText(ballString, x + fontBuffer + ((10 + j + (3 * i)) * xDist), yFont);
                    }
                }
            } else if (i == 9) {
                for (int j = 0; j < 3; j++) {
                    if (scoreTypes[j] != ScoreType.NONE) {
                        String ballString = String.valueOf(BallUtil.toChar(ballValues[j]));
                        gc.strokeText(ballString, x + fontBuffer + ((36 + j) * xDist), yFont);
                        gc.fillText(ballString, x + fontBuffer + ((36 + j) * xDist), yFont);
                    }
                }
            }

        }
        //Stroke Score
        fontSize = getFontSize(NAME_LABEL, yDist);
        gc.setFont(new Font(gc.getFont().getName(), fontSize * 1.5));
        fontBuffer = yDist / 10.0;
        yFont = y + (yDist * 3) - fontBuffer;
        for (int i = 0; i < game.getFrameIndex(); i++) {
            BowlingFrame cFrame = game.getFrames()[i];
            if (cFrame.getFrameScore() >= 0) {
                gc.strokeText(Integer.toString(cFrame.getFrameScore()), x + fontBuffer + ((9 + (3 * i)) * xDist), yFont);
                gc.fillText(Integer.toString(cFrame.getFrameScore()), x + fontBuffer + ((9 + (3 * i)) * xDist), yFont);
            }
        }
        //Stroke total Score
        gc.strokeText(Integer.toString(game.getGameScore()), x + fontBuffer + (39 * xDist), yFont);
        gc.fillText(Integer.toString(game.getGameScore()), x + fontBuffer + (39 * xDist), yFont);

        gc.restore();
    }

    private void drawScoreCardHeader(double x, double y) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double xDist = canvas.getWidth() / 44.0;
        double yDist = canvas.getWidth() / 44.0;

        double xOffset, yOffset;
        gc.save();

        gc.setFill(headerFill);
        gc.fillRect(x, y, x + (xDist * 41), yDist);

        gc.setStroke(lineColor);
        yOffset = y + (xDist);
        gc.strokeLine(x, y, x + (xDist * 42), y);

        gc.strokeLine(x, yOffset, x + (xDist * 42), yOffset);
        gc.strokeLine(x, y, x, yOffset);
        for (int i = 9; i < 43; i += 3) {
            xOffset = x + (i * xDist);
            gc.strokeLine(xOffset, y, xOffset, yOffset);
        }
        double fontSize = getFontSize(NAME_LABEL, yDist);
        gc.setFont(new Font(gc.getFont().getName(), fontSize));
        double fontBuffer = yDist / 10.0;
        double yFont = y + yDist - (2 * fontBuffer);

        gc.setStroke(textOutlineColor);
        gc.setFill(textFillColor);

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

    public int getCurentPlayer() {
        return curentPlayer;
    }

    public void setCurentPlayer(int curentPlayer) {
        this.curentPlayer = curentPlayer;
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

    public void loadColors() {
        String lineColorString = prefs.get(SETTING_LINE_COLOR, ColorToHex.colorToHex(DEFAULT_LINE_COLOR));
        String textFillString = prefs.get(SETTING_TEXT_FILL, ColorToHex.colorToHex(DEFAULT_TEXT_FILL));
        String textOutlineString = prefs.get(SETTING_TEXT_OUTLINE, ColorToHex.colorToHex(DEFAULT_TEXT_OUTLINE));
        String headerFillString = prefs.get(SETTING_HEADER_FILL, ColorToHex.colorToHex(DEFAULT_HEADER_FILL));
        String scorecardFillString = prefs.get(SETTING_SCORECARD_FILL, ColorToHex.colorToHex(DEFAULT_SCORECARD_FILL));
        double scorecardAlpha = prefs.getDouble(SETTING_SCORECARD_ALPHA, DEFAULT_SCORECARD_ALPHA);
        double headerAlpha = prefs.getDouble(SETTING_HEADER_ALPHA, DEFAULT_HEADER_ALPHA);

        lineColor = Color.web(lineColorString);
        textFillColor = Color.web(textFillString);
        textOutlineColor = Color.web(textOutlineString);
        headerFill = Color.web(headerFillString, headerAlpha);
        scorecardFill = Color.web(scorecardFillString, scorecardAlpha);

    }
}
