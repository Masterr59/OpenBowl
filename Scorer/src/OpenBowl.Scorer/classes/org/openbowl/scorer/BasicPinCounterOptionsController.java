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
package org.openbowl.scorer;

import javafx.scene.paint.Color;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.prefs.Preferences;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.openbowl.common.BowlingPins;

/**
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class BasicPinCounterOptionsController extends Dialog<Void> implements Initializable {

    @FXML
    private Spinner<String> pinSpinner;

    @FXML
    private Label detectedPinLabel;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Button testButton;

    @FXML
    private Button getImageButton;

    @FXML
    private ImageView cameraView;

    @FXML
    private Canvas overlayCanvas;

    @FXML
    private Slider radiusSlider;

    @FXML
    private Slider levelSlider;

    @FXML
    private TextField radiusTextField;

    @FXML
    private TextField levelTextField;

    @FXML
    private Label xLabel;
    @FXML

    private Label yLabel;

    private final ButtonType okButton;
    private final String name;
    private final Preferences prefs;
    private final BasicPinCounter pinCounter;

    public BasicPinCounterOptionsController(String name, BasicPinCounter pinCounter) throws IOException {
        super();
        this.name = name;
        this.pinCounter = pinCounter;

        prefs = Preferences.userNodeForPackage(this.getClass());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openbowl/scorer/BasicPinCounterOptionsDialog.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        getDialogPane().setContent(root);

        okButton = new ButtonType("Apply", ButtonBar.ButtonData.APPLY);
        ButtonType cancel = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(okButton, cancel);

        getDialogPane().lookupButton(okButton).addEventFilter(ActionEvent.ACTION, eh -> onOK(eh));
    }

    /**
     * Sets the initial pin counter settings
     *
     * @param url
     * @param rb
     *
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ArrayList<String> pins = new ArrayList<>();
        BowlingPins[] allPins = BowlingPins.values();
        for (BowlingPins p : allPins) {
            pins.add(p.toString());
        }
        Collections.sort(pins);

        ObservableList<String> bowlingPins = FXCollections.observableArrayList(pins);
        SpinnerValueFactory<String> pinFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(bowlingPins);

        pinSpinner.setValueFactory(pinFactory);
        pinFactory.valueProperty().addListener((obs, oldValue, newValue) -> onSpinnerChanged(oldValue, newValue));
        //pinSpinner.valueFactoryProperty().addListener((obs, oldValue, newValue) -> onSpinnerChanged(oldValue, newValue));

        cameraView.setPreserveRatio(false);

        onGetImage();

        radiusTextField.textProperty().bind(Bindings.format("%.0f", radiusSlider.valueProperty()));

        levelTextField.textProperty().bind(Bindings.format("%.0f", levelSlider.valueProperty()));

        testButton.setOnAction(notUsed -> countPins());

        getImageButton.setOnAction(notUsed -> onGetImage());

        overlayCanvas.setOnMouseClicked(value -> onClickOverlay(value));

        radiusSlider.valueProperty().addListener(notUsed -> drawOverlay());

        loadValues(pinFactory.getValue());
        countPins();

    }

    private void onOK(ActionEvent eh) {
        saveValues(pinSpinner.getValue());
        loadValues(pinSpinner.getValue());
        eh.consume();
    }

    private void countPins() {
        pinCounter.teardown();
        saveValues(pinSpinner.getValue());
        pinCounter.setup();
        ArrayList<BowlingPins> pins = pinCounter.countPins();
        onGetImage();
        String msg = "";
        for (BowlingPins p : pins) {
            msg += p.toString();
            if (msg.length() > 60 && !msg.contains("\n")) {
                msg += "\n";
            } else {
                msg += ", ";
            }
        }
        if (msg.isBlank()) {
            msg = "No Pins Detected";
        } else if (msg.endsWith(", ")) {
            msg = msg.substring(0, msg.length() - 2);
        }

        detectedPinLabel.setText(msg);
    }

    private void onSpinnerChanged(String oldValue, String newValue) {
        saveValues(oldValue);
        loadValues(newValue);
    }

    private void loadValues(String pinName) {
        int X, Y, R;
        X = prefs.getInt(name + "-" + pinName + "-" + pinCounter.X_SETTING, pinCounter.DEFAULT_X);
        Y = prefs.getInt(name + "-" + pinName + "-" + pinCounter.Y_SETTING, pinCounter.DEFAULT_Y);
        R = prefs.getInt(name + "-" + pinName + "-" + pinCounter.RADIUS_SETTING, pinCounter.DEFAULT_RADIUS);

        xLabel.setText(String.format("%d", X));
        yLabel.setText(String.format("%d", Y));

        radiusSlider.setValue(R);
        levelSlider.setValue(prefs.getInt(name + "-" + pinName + "-" + pinCounter.LEVEL_SETTING, pinCounter.DEFAULT_LEVEL));
        double r, g, b;
        r = prefs.getDouble(name + "-" + pinName + "-" + pinCounter.RED_SETTING, pinCounter.DEFAULT_COLOR.getRed());
        g = prefs.getDouble(name + "-" + pinName + "-" + pinCounter.GREEN_SETTING, pinCounter.DEFAULT_COLOR.getGreen());
        b = prefs.getDouble(name + "-" + pinName + "-" + pinCounter.BLUE_SETTING, pinCounter.DEFAULT_COLOR.getBlue());
        colorPicker.setValue(new Color(r, g, b, 1));

        drawOverlay();

    }

    private void saveValues(String pinName) {
        prefs.putInt(name + "-" + pinName + "-" + pinCounter.X_SETTING, Integer.parseInt(xLabel.getText()));
        prefs.putInt(name + "-" + pinName + "-" + pinCounter.Y_SETTING, Integer.parseInt(yLabel.getText()));
        prefs.putInt(name + "-" + pinName + "-" + pinCounter.RADIUS_SETTING, (int) radiusSlider.getValue());
        prefs.putInt(name + "-" + pinName + "-" + pinCounter.LEVEL_SETTING, (int) levelSlider.getValue());
        prefs.putDouble(name + "-" + pinName + "-" + pinCounter.RED_SETTING, colorPicker.getValue().getRed());
        prefs.putDouble(name + "-" + pinName + "-" + pinCounter.GREEN_SETTING, colorPicker.getValue().getGreen());
        prefs.putDouble(name + "-" + pinName + "-" + pinCounter.BLUE_SETTING, colorPicker.getValue().getBlue());

    }

    private void drawOverlay() {
        int X, Y, R;
        double Xscale, Yscale;
        X = Integer.parseInt(xLabel.getText());
        Y = Integer.parseInt(yLabel.getText());
        R = (int) radiusSlider.getValue();

        Xscale = cameraView.getFitWidth() / cameraView.getImage().getWidth();
        Yscale = cameraView.getFitHeight() / cameraView.getImage().getHeight();

        GraphicsContext gc = overlayCanvas.getGraphicsContext2D();

        gc.save();
        //gc.setFill(Color.BLACK);
        gc.clearRect(0, 0, overlayCanvas.getWidth(), overlayCanvas.getHeight());
        //gc.fillRect(0, 0, overlayCanvas.getWidth(), overlayCanvas.getHeight());
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        //Need to invert Y because the slider goes 0 at the bottom
        //however the canvas has zero at the top

        gc.strokeRect((X - R) * Xscale, (Y - R) * Yscale, (2 * R * Xscale), (2 * R * Yscale));

        gc.restore();
    }

    private void onClickOverlay(MouseEvent value) {
        double X = value.getX();
        double Y = value.getY();
        double Xscale = cameraView.getImage().getWidth() / cameraView.getFitWidth();
        double Yscale = cameraView.getImage().getHeight() / cameraView.getFitHeight();
        xLabel.setText(String.format("%.0f", X * Xscale));
        yLabel.setText(String.format("%.0f", Y * Yscale));
        //System.out.println(value);
        drawOverlay();
    }

    private void onGetImage() {
        cameraView.setImage(SwingFXUtils.toFXImage(pinCounter.getLastCameraImage(), null));
    }

}
