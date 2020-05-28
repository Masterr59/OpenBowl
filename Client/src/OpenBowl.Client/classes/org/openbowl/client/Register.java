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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import org.openbowl.common.AuthorizedUser;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class Register extends Pane implements Initializable {

    public static final int MAX_LINE_LENGTH = 40;
    private final String NEW_TAB_TITLE = "New Tab";
    private final String OPEN_TAB_TITLE = "New Tab";

    private enum NumPadKeys {
        KEY_0,
        KEY_1,
        KEY_2,
        KEY_3,
        KEY_4,
        KEY_5,
        KEY_6,
        KEY_7,
        KEY_8,
        KEY_9,
        KEY_DOT,
        KEY_BACKSPACE,
    }

    @FXML
    Label dateTime;

    @FXML
    TreeView recieptView;

    @FXML
    Label salesTax;

    @FXML
    Label salesTotal;

    @FXML
    Button adjustBtn;

    @FXML
    Button specialBtn;

    @FXML
    Button noSaleBtn;

    @FXML
    Button num0;
    @FXML
    Button num1;
    @FXML
    Button num2;
    @FXML
    Button num3;
    @FXML
    Button num4;
    @FXML
    Button num5;
    @FXML
    Button num6;
    @FXML
    Button num7;
    @FXML
    Button num8;
    @FXML
    Button num9;

    @FXML
    Button eraseBtn;

    @FXML
    Button numDot;

    @FXML
    Button cancelBtn;

    @FXML
    Button payLaterBtn;

    @FXML
    Button findTabBtn;

    @FXML
    Button payNowbtn;

    private DatabaseConnector dbConnector;
    private DoubleProperty numPadProperty;
    private DoubleProperty taxProperty;
    private DoubleProperty totalSaleProperty;
    private String numPadStringValue;
    private Timer timer;
    private ClockUpdateTask clockTask;
    private AuthorizedUser user;

    public Register(DatabaseConnector db) throws IOException {
        dbConnector = db;
        user = AuthorizedUser.NON_USER;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openbowl/client/Register.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        getChildren().add(root);
        numPadStringValue = "";
        numPadProperty = new SimpleDoubleProperty(0.0);

        taxProperty = new SimpleDoubleProperty(0.0);
        totalSaleProperty = new SimpleDoubleProperty(0.0);

        num0.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_0));
        num1.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_1));
        num2.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_2));
        num3.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_3));
        num4.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_4));
        num5.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_5));
        num6.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_6));
        num7.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_7));
        num8.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_8));
        num9.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_9));
        numDot.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_DOT));
        eraseBtn.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_BACKSPACE));

        salesTax.textProperty().bind(Bindings.format("$%04.2f", taxProperty));
        salesTotal.textProperty().bind(Bindings.format("$%04.2f", totalSaleProperty));

        cancelBtn.setOnAction(notUsed -> clearRegister(NEW_TAB_TITLE));

        timer = new Timer();
        clockTask = new ClockUpdateTask();
        dateTime.textProperty().bind(clockTask.DateLabelProperty());
        timer.scheduleAtFixedRate(clockTask, 1000, 1000);

        specialBtn.setOnAction(not_used -> onSpecialBtn());
        this.recieptView.setRoot(new TreeItem<String>(NEW_TAB_TITLE));
        this.recieptView.getRoot().expandedProperty().set(true);
        this.recieptView.getRoot().addEventHandler(TreeItem.valueChangedEvent(), notUsed -> updateTotals());
        this.recieptView.getRoot().addEventHandler(TreeItem.childrenModificationEvent(), notUsed -> updateTotals());
        this.recieptView.getSelectionModel().selectedItemProperty().addListener((obs, oo, no) -> onSelectionChange(oo, no));

        payLaterBtn.setOnAction(notUsed -> saveTab());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    private void onNumPadType(NumPadKeys keystroke) {
        switch (keystroke) {
            case KEY_0:
                numPadStringValue += "0";
                break;
            case KEY_1:
                numPadStringValue += "1";
                break;
            case KEY_2:
                numPadStringValue += "2";
                break;
            case KEY_3:
                numPadStringValue += "3";
                break;
            case KEY_4:
                numPadStringValue += "4";
                break;
            case KEY_5:
                numPadStringValue += "5";
                break;
            case KEY_6:
                numPadStringValue += "6";
                break;
            case KEY_7:
                numPadStringValue += "7";
                break;
            case KEY_8:
                numPadStringValue += "8";
                break;
            case KEY_9:
                numPadStringValue += "9";
                break;
            case KEY_DOT:
                if (!numPadStringValue.contains(".")) {
                    numPadStringValue += ".";
                }

                break;
            case KEY_BACKSPACE:
                if (!numPadStringValue.isBlank()) {
                    numPadStringValue = numPadStringValue.substring(0, numPadStringValue.length() - 1);
                }
                break;
        }
        if (!numPadStringValue.isBlank()) {
            numPadProperty.set(Double.parseDouble(numPadStringValue));
        } else {
            numPadProperty.set(0.0);
        }
        if (numPadStringValue.equals("0")) {
            onDeleteSelected();
        }
    }

    public void clearRegister(String title) {
        numPadProperty.set(0.0);
        numPadStringValue = "";

        recieptView.getRoot().getChildren().clear();
        recieptView.getRoot().setValue(title);
        recieptView.getRoot().expandedProperty().set(true);

    }

    public void killTasks() {
        System.out.println("Register - killing clock task");
        clockTask.cancel();
        clockTask = null;
        timer.cancel();
        timer.purge();
        timer = null;
    }

    private void onSpecialBtn() {
        ProductUseage newUseage = new ProductUseage(Product.TEST_PRODUCT, 1, 1);
        addProductUseageToRegister(newUseage);

        Product packageProduct = new Product(-1, "Pizza & Bowling Package", 29.99, -1, ProductType.TEST_TYPE, TaxType.TAX_META_PACKAGE);
        Product bowlingProduct = new Product(-1, "Bowling lane 1 hrs", 19.99, -1, ProductType.TEST_TYPE, TaxType.TEST_RATE);
        Product pizzaProduct = new Product(-1, "Pizza 1 lg 5 toppings", 19.99, -1, ProductType.TEST_TYPE, TaxType.TEST_RATE);
        Product discountProduct = new Product(-1, "line discount", -9.99, -1, ProductType.TEST_TYPE, TaxType.TAX_EXEMPT);

        ProductUseage packageUse = new ProductUseage(packageProduct, 0, 1);

        packageUse.addChildProduct(new ProductUseage(bowlingProduct, 1, 1));
        packageUse.addChildProduct(new ProductUseage(pizzaProduct, 1, 1));
        packageUse.addChildProduct(new ProductUseage(discountProduct, 1, 1));
        packageUse.QTYProperty().set(1);

        addProductUseageToRegister(packageUse);
    }

    private void updateTotals() {
        updateSaleTotal();
        updateTaxTotal();
    }

    private void updateSaleTotal() {
        double saleTotal = 0.0;
        for (Object ob : this.recieptView.getRoot().getChildren()) {
            if (ob instanceof ProductUseage) {
                ProductUseage child = (ProductUseage) ob;
                saleTotal += getSaleTotal(child);
            }
        }
        this.totalSaleProperty.set(saleTotal);

    }

    private double getSaleTotal(TreeItem root) {
        double sale = 0.0;

        if (!root.getChildren().isEmpty()) {
            double childTotal = 0.0;
            for (Object ob : root.getChildren()) {
                if (ob instanceof ProductUseage) {
                    ProductUseage child = (ProductUseage) ob;
                    childTotal += getSaleTotal(child);
                }
            }
            if (root instanceof ProductUseage) {
                ProductUseage pu = (ProductUseage) root;
                pu.getProduct_ID().Product_PriceProperty().set(childTotal);
            }
        }
        if (root instanceof ProductUseage) {
            ProductUseage child = (ProductUseage) root;
            sale += child.getProduct_ID().Product_PriceProperty().get() * child.QTYProperty().doubleValue();
        }
        return sale;
    }

    private void updateTaxTotal() {
        double taxTotal = 0.0;
        for (Object ob : this.recieptView.getRoot().getChildren()) {
            if (ob instanceof ProductUseage) {
                ProductUseage child = (ProductUseage) ob;
                taxTotal += getTaxTotal(child);
            }
        }
        this.taxProperty.set(taxTotal);
    }

    private double getTaxTotal(TreeItem root) {
        double tax = 0.0;
        if (root instanceof ProductUseage) {
            ProductUseage child = (ProductUseage) root;
            tax += child.getProduct_ID().Product_PriceProperty().get()
                    * child.QTYProperty().get()
                    * child.getProduct_ID().getTax_Type().getRate();

            for (Object ob : child.getChildren()) {
                if (ob instanceof ProductUseage) {
                    tax += getTaxTotal((ProductUseage) ob);
                }
            }
        }

        return tax;
    }

    private void onSelectionChange(Object oo, Object no) {
        ProductUseage pu;
        if (oo instanceof ProductUseage) {
            pu = (ProductUseage) oo;
            pu.QTYProperty().unbind();
        }
        if (no instanceof ProductUseage) {
            pu = (ProductUseage) no;
            numPadProperty.set(pu.QTYProperty().doubleValue());
            numPadStringValue = "";
            pu.QTYProperty().bind(numPadProperty);
        }
        //System.out.println("Old: " + oo + " New: " + no);
    }

    private void onDeleteSelected() {
        if (this.recieptView.getSelectionModel().getSelectedItem() instanceof TreeItem) {
            TreeItem toBeDeleted = (TreeItem) this.recieptView.getSelectionModel().getSelectedItem();
            deleteFromRegister(toBeDeleted, this.recieptView.getRoot());
            numPadStringValue = "";

        }
    }

    private void deleteFromRegister(TreeItem delete, TreeItem root) {
        if (root.getChildren().contains(delete)) {
            root.getChildren().remove(delete);
        } else {
            for (Object o : root.getChildren()) {
                if (o instanceof TreeItem) {
                    TreeItem childRoot = (TreeItem) o;
                    deleteFromRegister(delete, childRoot);
                }
            }
        }
    }

    public void addProductUseageToRegister(ProductUseage pu) {
        this.recieptView.getRoot().getChildren().add(pu.clone());
    }

    private void saveTab() {
        if (!this.recieptView.getRoot().getChildren().isEmpty() && user != AuthorizedUser.NON_USER) {

            Integer transID = dbConnector.saveTab(user, this.recieptView.getRoot());
            System.out.printf("Tab saved under tabID %d\n", transID);
            clearRegister(NEW_TAB_TITLE);
        }
    }

    public void setUser(AuthorizedUser user) {
        this.user = user;
    }

}
