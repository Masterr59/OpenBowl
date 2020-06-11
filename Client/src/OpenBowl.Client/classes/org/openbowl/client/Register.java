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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.openbowl.common.AuthorizedUser;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class Register extends Pane implements Initializable {

    private final String NO_RECEIPT_ALERT_TITLE = "Warning";
    private final String NO_RECEIPT_ALERT_HEADER = "Receipt Empty Warning";
    private final String NO_RECEIPT_ALERT_TEXT = "The receipt is empty.";

    private final String NO_LANE_SELECTION_TITLE = "Info";
    private final String NO_LANE_SELECTION_HEADER = "Lane Selection";
    private final String NO_LANE_SELECTION_TEXT = "A product requires a lane selection to continue";

    private final String DB_ERROR = "Database Error!";
    private final String DB_ERROR_SAVE_TRANSACTION = "Error saving transaction to database code: %d";

    public static final int MAX_LINE_LENGTH = 40;
    private final String NEW_TAB_TITLE = "New Tab";
    private final String OPEN_TAB_TITLE = "New Tab";

    @FXML
    Label dateTime;

    @FXML
    Label salesTax;

    @FXML
    Label salesTotal;

    @FXML
    TreeView recieptView;

    @FXML
    VBox vbox;

    private final DatabaseConnector dbConnector;
    private final DoubleProperty numPadProperty;
    private final DoubleProperty taxProperty;
    private final DoubleProperty totalSaleProperty;
    private Timer timer;
    private ClockUpdateTask clockTask;
    private AuthorizedUser user;
    private IntegerProperty minLane, maxLane;
    private boolean allowChangeLane;
    private NumPad numPad;

    public Register(DatabaseConnector db) throws IOException {
        dbConnector = db;
        allowChangeLane = true;
        user = AuthorizedUser.NON_USER;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openbowl/client/Register.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        getChildren().add(root);

        numPad = new NumPad();
        vbox.getChildren().add(numPad);

        numPadProperty = new SimpleDoubleProperty(0.0);
        numPadProperty.bind(numPad.NumPadValueProperty());

        taxProperty = new SimpleDoubleProperty(0.0);
        totalSaleProperty = new SimpleDoubleProperty(0.0);

        salesTax.textProperty().bind(Bindings.format("$%04.2f", taxProperty));
        salesTotal.textProperty().bind(Bindings.format("$%04.2f", totalSaleProperty));

        //Cancel Btn
        numPad.setButtonOnAction(notUsed -> clearRegister(), 1, 3);

        timer = new Timer();
        clockTask = new ClockUpdateTask();
        dateTime.textProperty().bind(clockTask.DateLabelProperty());
        timer.scheduleAtFixedRate(clockTask, 1000, 1000);

        //Special Btn
        numPad.setButtonOnAction(not_used -> onSpecialBtn(), 0, 1);
        clearRegister();

        this.recieptView.getSelectionModel().selectedItemProperty().addListener((obs, oo, no) -> onSelectionChange(oo, no));

        //PayLater Btn
        numPad.setButtonOnAction(notUsed -> saveTab(), 2, 3);
        //FindTab Btn
        numPad.setButtonOnAction(notUsed -> onFindTab(), 3, 3);

        //PayNow Btn
        numPad.setButtonOnAction(notUsed -> onPayNow(), 4, 3);

        minLane = new SimpleIntegerProperty(-1);
        maxLane = new SimpleIntegerProperty(-1);
        minLane.addListener((obs, on, nn) -> onLaneSelectChange());
        maxLane.addListener((obs, on, nn) -> onLaneSelectChange());

        numPad.setOnZeroAction(notUsed -> onDeleteSelected());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void clearRegister() {
        loadRegister(new Receipt());
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
        ProductUseage newUseage = new ProductUseage(Product.TEST_PRODUCT, 1);
        addProductUseageToRegister(newUseage);
        Product packageProduct = new Product(-1, "Pizza & Bowling Package", 29.99, -1, ProductType.TEST_TYPE, TaxType.TAX_META_PACKAGE);
        Product bowlingProduct = new Product(3, "Per Game", 7.99, 1, ProductType.GAME_TYPE, TaxType.TEST_RATE);
        Product pizzaProduct = new Product(-1, "Pizza 1 lg 5 toppings", 19.99, -1, ProductType.FOOD_TYPE, TaxType.TEST_RATE);
        Product discountProduct = new Product(-1, "line discount", -9.99, -1, ProductType.TEST_TYPE, TaxType.TAX_EXEMPT);

        ProductUseage packageUse = new ProductUseage(packageProduct, 0);
        packageUse.setMinLane(0);
        packageUse.setMaxLane(1);

        packageUse.addChildProduct(new ProductUseage(bowlingProduct, 4));
        packageUse.addChildProduct(new ProductUseage(pizzaProduct, 1));
        packageUse.addChildProduct(new ProductUseage(discountProduct, 1));
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
            numPad.setValue(pu.QTYProperty().get());
            numPad.setInitialValue(0.0);
            pu.QTYProperty().bind(numPadProperty);
            this.allowChangeLane = false;
            this.minLane.set(pu.getMinLane());
            this.maxLane.set(pu.getMaxLane());
            this.allowChangeLane = true;
        } else {
            this.minLane.set(-1);
            this.maxLane.set(-1);
        }
        //System.out.println("Old: " + oo + " New: " + no);
    }

    private void onDeleteSelected() {
        if (this.recieptView.getSelectionModel().getSelectedItem() instanceof TreeItem) {
            TreeItem toBeDeleted = (TreeItem) this.recieptView.getSelectionModel().getSelectedItem();
            deleteFromRegister(toBeDeleted, this.recieptView.getRoot());
            //numPad.setValue(0.0);

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
        ProductUseage clone = pu.clone();
        clone.setMinLane(this.minLane.get());
        clone.setMaxLane(this.maxLane.get());
        this.recieptView.getRoot().getChildren().add(clone);
    }

    private void saveTab() {
        if (!this.recieptView.getRoot().getChildren().isEmpty() && user != AuthorizedUser.NON_USER) {
            Integer transID = dbConnector.saveTab(user, (Receipt) this.recieptView.getRoot());
            System.out.printf("Tab saved under tabID %d\n", transID);
            clearRegister();
        }
    }

    public void setUser(AuthorizedUser user) {
        this.user = user;
    }

    private void onFindTab() {
        FindTabDialog dialog = new FindTabDialog(this.dbConnector, this.user);
        dialog.showAndWait();
        if (dialog.isOpenTab()) {
            loadTab(dialog.getTabID());
        }
    }

    private void loadTab(Integer tabID) {
        loadRegister(this.dbConnector.getTab(user, tabID).clone());
    }

    private void loadRegister(Receipt root) {
        numPad.setValue(0.0);
        this.recieptView.setRoot(root);
        this.recieptView.getRoot().expandedProperty().set(true);
        this.recieptView.getRoot().addEventHandler(TreeItem.valueChangedEvent(), notUsed -> updateTotals());
        this.recieptView.getRoot().addEventHandler(TreeItem.childrenModificationEvent(), notUsed -> updateTotals());
        updateTotals();
    }

    public IntegerProperty MinLaneProperty() {
        return minLane;
    }

    public IntegerProperty MaxLaneProperty() {
        return maxLane;
    }

    private void onLaneSelectChange() {
        for (Object o : this.recieptView.getSelectionModel().getSelectedItems()) {
            if (o instanceof ProductUseage && this.allowChangeLane) {
                ProductUseage pu = (ProductUseage) o;
                pu.setMinLane(this.minLane.get());
                pu.setMaxLane(this.maxLane.get());
            }
        }
    }

    private void onPayNow() {
        if (this.recieptView.getRoot().getChildren().size() > 0) {
            if (this.recieptView.getRoot() instanceof Receipt) {
                Receipt root = (Receipt) this.recieptView.getRoot();
                Receipt needLaneSelection = root.requiresLaneSelection();
                if (needLaneSelection == null) {
                    PayNowDialog dialog;
                    try {
                        double total = this.totalSaleProperty.get() + this.taxProperty.get();
                        dialog = new PayNowDialog(total, dbConnector, user);
                        Optional<ButtonType> result = dialog.showAndWait();
                        if (result.get() == ButtonType.OK) {
                            if (this.recieptView.getRoot() instanceof Receipt) {
                                double change = dialog.tenderedProperty().get() - total;
                                Receipt receipt = ((Receipt) this.recieptView.getRoot()).clone();
                                receipt.setAmountDue(total);
                                receipt.setAmountSubTotal(this.totalSaleProperty.get());
                                receipt.setAmountTax(this.taxProperty.get());
                                receipt.setAmountTendered(dialog.tenderedProperty().get());
                                receipt.setPaymentType(dialog.getPaymentType());
                                Integer transactionID = this.dbConnector.saveTransaction(user, receipt.clone());
                                if (transactionID > 0) {
                                    receipt.TransactionProperty().set(transactionID);
                                    clearRegister();
                                    printReceipt(receipt);
                                    if (change > 0 || dialog.getPaymentType() == PaymentType.CASH
                                            || dialog.getPaymentType() == PaymentType.CHECK) {
                                        onOpenCashDraw();
                                    }
                                } else {
                                    Alert alert = new Alert(AlertType.ERROR);
                                    alert.getDialogPane().getStylesheets().add(getClass().getResource("DarkMode.css").toExternalForm());
                                    alert.setTitle(DB_ERROR);
                                    alert.setHeaderText(DB_ERROR);
                                    alert.setContentText(String.format(DB_ERROR_SAVE_TRANSACTION, transactionID.intValue()));

                                    alert.showAndWait();
                                }
                            }
                        }

                    } catch (IOException ex) {
                        System.out.println("Error loading PayNowDialog: " + ex.toString());
                    }

                }// need lane selection 
                else {
                    this.recieptView.getSelectionModel().clearSelection();
                    this.recieptView.getSelectionModel().select(needLaneSelection);
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.getDialogPane().getStylesheets().add(getClass().getResource("DarkMode.css").toExternalForm());
                    alert.setTitle(NO_LANE_SELECTION_TITLE);
                    alert.setHeaderText(NO_LANE_SELECTION_HEADER);
                    alert.setContentText(NO_LANE_SELECTION_TEXT + "\n" + needLaneSelection.toString());

                    alert.showAndWait();

                }
            }

        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("DarkMode.css").toExternalForm());
            alert.setTitle(NO_RECEIPT_ALERT_TITLE);
            alert.setHeaderText(NO_RECEIPT_ALERT_HEADER);
            alert.setContentText(NO_RECEIPT_ALERT_TEXT);

            alert.showAndWait();
        }

    }

    private void printReceipt(Receipt receipt) {
        String msg = "*** TODO print receipt ***";
        System.out.println(msg);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("DarkMode.css").toExternalForm());
        alert.setTitle(msg);
        alert.setContentText(receipt.toString());
        alert.setGraphic(null);
        alert.setHeaderText(null);
        alert.show();
    }

    private void onOpenCashDraw() {
        String msg = "*** TODO trigger open cash drawer ***";
        System.out.println(msg);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("DarkMode.css").toExternalForm());
        alert.setTitle(msg);
        alert.setContentText(msg);
        alert.setGraphic(null);
        alert.setHeaderText(null);
        alert.show();
    }

    

}
