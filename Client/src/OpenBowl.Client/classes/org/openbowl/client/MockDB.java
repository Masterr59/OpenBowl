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

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import org.openbowl.common.AuthorizedUser;
import org.openbowl.common.SimplePlayer;
import org.openbowl.common.SystemStatus;
import org.openbowl.common.UserRole;
import org.openbowl.common.WebFunctions;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class MockDB extends DatabaseConnector {

    private final String ADMIN = "Admin";
    private final String MANAGER = "Manager";
    private final String DESK = "Desk";
    private final String RESTAURANT = "Restaurant";
    private final String NONE = "None";
    private final String DEFAULT_TOKEN = "yZ9Ut95MG3xdf5gc6WgT";
    private final String GET_LANE_STATUS_PATH = "system/?get=status";

    private final String ACTIVATION_ERROR_TITLE = "Error";
    private final String ACTIVATION_ERROR_HEADER = "Unsuported Game Type";
    private final String ACTIVATION_ERROR_TEXT = "Game Type %s is not recognized";

    private final String ACTIVATION_TITLE = "Information";
    private final String ACTIVATION_HEADER = "Lane Activation Information";

    private final String LANE_GAME_ACTIVATION_PATH = "game/%s/?set=newSession";

    private final String DEPT_REST = "Restaurant";
    private final String DEPT_BAR = "Bar";
    private final String DEPT_PRO = "Pro Shop";

    private final String[] DEPTS = {DatabaseConnector.GAME_DEPARTMENT_NAME, DEPT_REST, DEPT_BAR, DEPT_PRO};

    private final String PREF_SCORER_IP = "ScorerIP";
    private final String DEFAULT_SCORER_IP = "127.0.0.1";

    private final Random rand;
    private final Gson gson;
    private Preferences mPrefs;

    private Map<Integer, Receipt> tabs;

    public MockDB() {
        rand = new Random();
        gson = new Gson();
        tabs = new HashMap<>();
        mPrefs = Preferences.userNodeForPackage(this.getClass());
    }

    @Override
    public AuthorizedUser login(String UserName, String Password) {
        ArrayList<UserRole> roles = new ArrayList<>();
        AuthorizedUser ret = AuthorizedUser.NON_USER;
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.YEAR, 10);
        switch (UserName) {
            case ADMIN:
                for (UserRole ur : UserRole.values()) {
                    if (ur != UserRole.NONE) {
                        roles.add(ur);
                    }
                }
                ret = new AuthorizedUser(DEFAULT_TOKEN, exp, 99, UserName, roles);
                break;
            case MANAGER:
                roles.add(UserRole.GENERATE_REPORTS);
                roles.add(UserRole.TRANSACTION_ADD);
                roles.add(UserRole.TRANSACTION_DELETE);
                roles.add(UserRole.GAME_ADMIN);
                ret = new AuthorizedUser(DEFAULT_TOKEN, exp, 99, UserName, roles);
                break;
            case DESK:
                roles.add(UserRole.TRANSACTION_ADD);
                roles.add(UserRole.GAME_ADMIN);
                ret = new AuthorizedUser(DEFAULT_TOKEN, exp, 99, UserName, roles);
                break;
            case RESTAURANT:
                roles.add(UserRole.TRANSACTION_ADD);
                ret = new AuthorizedUser(DEFAULT_TOKEN, exp, 99, UserName, roles);
                break;
            case NONE:
                roles.add(UserRole.NONE);
                ret = new AuthorizedUser(DEFAULT_TOKEN, exp, 99, UserName, roles);
                break;
        }

        return ret;

    }

    @Override
    public String updateUserPassword(AuthorizedUser user, String oldPassword, String newPassword) {
        String ret = "MockDB Connector: updating password for user %s ... random %s";
        String status = rand.nextBoolean() ? "Success" : "Failure";

        return String.format(ret, user.getUsername(), status);
    }

    @Override
    public int getNumLanes(AuthorizedUser user) {
        if (user.isAuthorized(UserRole.GAME_ADMIN)) {
            return 4;
        }
        return 0;
    }

    @Override
    public boolean isLaneOnline(int lane) {
        if (lane < 2) {
            String Response = "";
            try {
                String ip = mPrefs.get(PREF_SCORER_IP, DEFAULT_SCORER_IP);
                Response = WebFunctions.doHttpGetRequest(ip, GET_LANE_STATUS_PATH, DEFAULT_TOKEN);
                Map<String, ArrayList<String>> status = gson.fromJson(Response, Map.class);
                if (status.containsKey("status")) {
                    ArrayList<SystemStatus> laneStatus = new ArrayList<>();
                    for (String s : status.get("status")) {
                        laneStatus.add(SystemStatus.valueOf(s));
                    }

                    return laneStatus.contains(SystemStatus.ONLINE);
                }
                return false;
            } catch (IOException | InterruptedException ex) {
                System.out.println("Error isLaneOnline - " + ex.toString());
                System.out.println(Response);
                return false;
            } catch (IllegalStateException ex) {
                System.out.println("Error isLaneOnline - " + ex.toString());
                System.out.println(Response);
                return false;
            }

        } else if (lane == 2) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ArrayList<SystemStatus> getLaneStatus(int lane) {
        ArrayList<SystemStatus> status = new ArrayList<>();
        if (lane < 2) {
            try {
                String ip = mPrefs.get(PREF_SCORER_IP, DEFAULT_SCORER_IP);
                String Response = WebFunctions.doHttpGetRequest(ip, GET_LANE_STATUS_PATH, DEFAULT_TOKEN);
                Map<String, ArrayList<String>> statusMap = gson.fromJson(Response, Map.class);
                if (statusMap.containsKey("status")) {

                    ArrayList<SystemStatus> laneStatus = new ArrayList<>();
                    for (String s : statusMap.get("status")) {
                        laneStatus.add(SystemStatus.valueOf(s));
                    }
                    return laneStatus;
                }

            } catch (IOException | InterruptedException ex) {
                System.out.println("Error getting lane status - " + ex.toString());
            }
        } else if (lane == 2) {
            status.add(SystemStatus.ONLINE);
            status.add(SystemStatus.CRASH_DETECTED);
            status.add(SystemStatus.ERROR);
            status.add(SystemStatus.OVERHEAT);
            status.add(SystemStatus.REBOOT_REQUIRED);
            status.add(SystemStatus.UNDERVOLT);
            status.add(SystemStatus.UPDATE_AVAILABLE);
        } else {
            status.add(SystemStatus.OFFLINE);
        }
        return status;
    }

    @Override
    public Map<Integer, String> getDepartments(AuthorizedUser user) {
        Map<Integer, String> map = new HashMap<>();
        if (user.isAuthorized(UserRole.TRANSACTION_ADD)) {
            for (int i = 0; i < DEPTS.length; i++) {
                map.put(i, DEPTS[i]);
            }
        }
        return map;
    }

    @Override
    public Map<Integer, String> getSubDepartments(AuthorizedUser user, Integer DepartmentID) {
        Map<Integer, String> map = new HashMap<>();
        if (user.isAuthorized(UserRole.TRANSACTION_ADD)) {
            switch (DepartmentID) {
                case 0: //Game
                    map.put(0, "League");
                    map.put(1, "Normal Play");
                    map.put(2, "Packages");
                    map.put(9, "Other");
                    break;
                case 1: //Rest
                    map.put(3, "Breakfast");
                    map.put(4, "Lunch");
                    map.put(5, "Dinner");
                    map.put(6, "Drinks");
                    map.put(7, "Specials");
                    map.put(9, "Other");
                    break;
                case 2://Bar
                    map.put(6, "Drinks");
                    map.put(7, "Adult Beverages");
                    map.put(9, "Other");
                    break;
                case 3://Pro
                    map.put(8, "Pro Shop");
                    map.put(9, "Other");
                    break;

            }
        }
        return map;
    }

    @Override
    public ArrayList<ProductUseage> getProducts(AuthorizedUser user, Integer SD) {
        ProductType typeGame = ProductType.GAME_TYPE;
        ProductType typeFood = ProductType.FOOD_TYPE;
        ProductType typeRetail = new ProductType(2, "Retail");
        ProductType typeOther = new ProductType(3, "Other");
        ProductType typeDiscount = new ProductType(4, "Discount");

        Product item;
        ProductUseage pkg;
        Product laneRental = new Product(2, "Lane Rental", 19.99, SD, typeGame, TaxType.TEST_RATE);
        Product smallPizza = new Product(13, "Small Pizza", 7.99, SD, typeFood, TaxType.TEST_RATE);
        Product medPizza = new Product(14, "Medium Pizza", 12.99, SD, typeFood, TaxType.TEST_RATE);
        Product largePizza = new Product(15, "Large Pizza", 19.99, SD, typeFood, TaxType.TEST_RATE);
        Product pitcherSoda = new Product(21, "Pitcher of Soda", 5.99, SD, typeFood, TaxType.TEST_RATE);
        Product discount999 = new Product(30, "Line Discount", -9.99, SD, typeDiscount, TaxType.TAX_EXEMPT);
        Product discount1997 = new Product(31, "Line Discount", -19.97, SD, typeDiscount, TaxType.TAX_EXEMPT);

        ArrayList<ProductUseage> productList = new ArrayList<>();
        if (user.isAuthorized(UserRole.TRANSACTION_ADD)) {
            switch (SD) {
                case 0: // league
                    item = new Product(0, "League Play Per Hour", 9.99, SD, typeGame, TaxType.TEST_RATE);
                    productList.add(new ProductUseage(item, 1));
                    item = new Product(1, "League Play Per Game", 9.99, SD, typeGame, TaxType.TEST_RATE);
                    productList.add(new ProductUseage(item, 1));
                    break;
                case 1: // normal game
                    productList.add(new ProductUseage(laneRental, 1));
                    item = new Product(3, "Per Game", 7.99, SD, typeGame, TaxType.TEST_RATE);
                    productList.add(new ProductUseage(item, 1));
                    item = new Product(4, "Per Frame", 0.99, SD, typeGame, TaxType.TEST_RATE);
                    productList.add(new ProductUseage(item, 1));
                    break;
                case 2: // Packages
                    item = new Product(5, "Family Package", 29.99, SD, typeGame, TaxType.TAX_META_PACKAGE);
                    pkg = new ProductUseage(item, 1);
                    pkg.addChildProduct(new ProductUseage(laneRental, 1));
                    pkg.addChildProduct(new ProductUseage(largePizza, 1));
                    pkg.addChildProduct(new ProductUseage(discount999, 1));
                    productList.add(pkg);

                    item = new Product(6, "Party Package", 45.99, SD, typeGame, TaxType.TAX_META_PACKAGE);
                    pkg = new ProductUseage(item, 1);
                    pkg.addChildProduct(new ProductUseage(laneRental, 1));
                    pkg.addChildProduct(new ProductUseage(largePizza, 2));
                    pkg.addChildProduct(new ProductUseage(pitcherSoda, 1));
                    pkg.addChildProduct(new ProductUseage(discount1997, 1));
                    productList.add(pkg);
                    break;
                case 3: // Breakfast
                    item = new Product(7, "Eggs (2)", 1.99, SD, typeFood, TaxType.TEST_RATE);
                    productList.add(new ProductUseage(item, 1));
                    item = new Product(8, "Bacon (2)", 1.99, SD, typeFood, TaxType.TEST_RATE);
                    productList.add(new ProductUseage(item, 1));
                    item = new Product(9, "Sausage (2)", 1.99, SD, typeFood, TaxType.TEST_RATE);
                    productList.add(new ProductUseage(item, 1));
                    item = new Product(10, "Hashbrowns", 0.99, SD, typeFood, TaxType.TEST_RATE);
                    productList.add(new ProductUseage(item, 1));
                    item = new Product(11, "Biscuits & Gravy", 2.99, SD, typeFood, TaxType.TEST_RATE);
                    productList.add(new ProductUseage(item, 1));
                    break;
                case 4: // Lunch
                    item = new Product(12, "HotDog", 1.99, SD, typeFood, TaxType.TEST_RATE);
                    productList.add(new ProductUseage(item, 1));
                    productList.add(new ProductUseage(smallPizza, 1));
                    productList.add(new ProductUseage(medPizza, 1));
                    productList.add(new ProductUseage(largePizza, 1));
                    break;
                case 5: // Dinner
                    item = new Product(17, "Hot Wings", 13.99, SD, typeFood, TaxType.TEST_RATE);
                    productList.add(new ProductUseage(item, 1));
                    productList.add(new ProductUseage(smallPizza, 1));
                    productList.add(new ProductUseage(medPizza, 1));
                    productList.add(new ProductUseage(largePizza, 1));
                    break;
                case 6: //Drinks
                    item = new Product(18, "Small Soda", 0.99, SD, typeFood, TaxType.TEST_RATE);
                    productList.add(new ProductUseage(item, 1));
                    item = new Product(19, "Medium Soda", 1.99, SD, typeFood, TaxType.TEST_RATE);
                    productList.add(new ProductUseage(item, 1));
                    item = new Product(20, "Large Soda", 2.25, SD, typeFood, TaxType.TEST_RATE);
                    productList.add(new ProductUseage(item, 1));
                    productList.add(new ProductUseage(pitcherSoda, 1));
                    break;
                case 7: // Audult Beverages
                    item = new Product(22, "Beer", 3.99, SD, typeFood, TaxType.TEST_RATE);
                    productList.add(new ProductUseage(item, 1));
                    item = new Product(23, "Wine", 3.99, SD, typeFood, TaxType.TEST_RATE);
                    productList.add(new ProductUseage(item, 1));
                    item = new Product(24, "Whiskey", 4.99, SD, typeFood, TaxType.TEST_RATE);
                    productList.add(new ProductUseage(item, 1));
                    break;
                case 8: // Pro Shop
                    item = new Product(25, "Bowling Ball", 19.99, SD, typeRetail, TaxType.TEST_RATE);
                    productList.add(new ProductUseage(item, 1));
                    item = new Product(26, "Ball Polish", 4.99, SD, typeRetail, TaxType.TEST_RATE);
                    productList.add(new ProductUseage(item, 1));
                    item = new Product(27, "Team Shirt", 19.99, SD, typeRetail, TaxType.TEST_RATE);
                    productList.add(new ProductUseage(item, 1));
                    break;
                case 9: // Other;
                    item = new Product(28, "Gift Card Sale", 1.00, SD, typeOther, TaxType.TAX_EXEMPT);
                    productList.add(new ProductUseage(item, 1));
                    item = new Product(29, "Military Discount", -17.76, SD, typeDiscount, TaxType.TAX_EXEMPT);
                    productList.add(new ProductUseage(item, 1));
                    productList.add(new ProductUseage(discount999, 1));
                    break;

            }

        }

        return productList;
    }

    @Override
    public Integer saveTab(AuthorizedUser user, Receipt root) {
        if (user.isAuthorized(UserRole.TRANSACTION_ADD)) {
            int i = rand.nextInt(Integer.MAX_VALUE);
            root.TransactionProperty().set(i);
            tabs.put(i, root.clone());
            return i;
        }
        return -1;
    }

    @Override
    public ArrayList<Integer> findTabs(AuthorizedUser user) {
        ArrayList<Integer> list = new ArrayList<>();
        if (user.isAuthorized(UserRole.TRANSACTION_ADD)) {
            list.addAll(tabs.keySet());
        }
        return list;
    }

    @Override
    public Receipt getTab(AuthorizedUser user, Integer tabid) {
        Receipt item;
        if (user.isAuthorized(UserRole.TRANSACTION_ADD) && tabs.containsKey(tabid)) {
            item = tabs.get(tabid);
            //System.out.printf("Loading tabID: %d\n%s", tabid, item);
        } else {
            item = new Receipt();
        }
        return item;
    }

    @Override
    public Integer saveTransaction(AuthorizedUser user, Receipt root) {
        int transactionID = -1;
        if (user.isAuthorized(UserRole.TRANSACTION_ADD)) {
            if (root.TransactionProperty().get() > 0) {
                transactionID = root.TransactionProperty().get();
            } else {
                transactionID = rand.nextInt(Integer.MAX_VALUE);
                root.TransactionProperty().set(transactionID);
            }
            for (Object o : root.getChildren()) {
                if (o instanceof ProductUseage) {
                    ProductUseage pu = (ProductUseage) o;
                    checkForLaneActivations(pu);
                }
            }

        }

        return transactionID;
    }

    @Override
    public boolean removeTab(AuthorizedUser user, Integer tabid) {
        boolean success = false;
        if (user.isAuthorized(UserRole.TRANSACTION_ADD)) {
            if (tabs.containsKey(tabid)) {
                tabs.remove(tabid);
                success = true;
            }
        }
        return success;
    }

    @Override
    public ArrayList<PaymentType> getPaymentTypes(AuthorizedUser user) {
        ArrayList<PaymentType> list = new ArrayList<>();
        if (user.isAuthorized(UserRole.TRANSACTION_ADD)) {
            list.add(PaymentType.CASH);
            list.add(PaymentType.CHECK);
            list.add(PaymentType.CREDIT_DEBIT);
        }
        return list;
    }

    private void checkForLaneActivations(ProductUseage root) {
        if (root.getProduct_ID().getProduct_type() == ProductType.GAME_TYPE) {
            int laneMin = root.getMinLane();
            int laneMax = root.getMaxLane();
            int numLanes = laneMax - laneMin + 1;
            numLanes = (laneMax == -1) ? 1 : numLanes;
            int qty = root.QTYProperty().get();
            int perLane[] = new int[numLanes];
            for (int i = 0; i < perLane.length; i++) {
                perLane[i] = 0;
            }
            int pos = 0;
            while (qty > 0) {
                perLane[pos]++;
                pos++;
                pos = pos % numLanes;
                qty--;
            }
            String gameType = root.getProduct_ID().getProduct_Name();
            if (gameType.equals("Per Game")) {
                String msg = "Sending lane activations to:\n";
                for (int i = 0; i < perLane.length; i++) {
                    int laneID = laneMin + i;
                    int numGames = perLane[i];
                    Platform.runLater(() -> {
                        activateGamesOnLane(laneID, numGames);
                    });
                    msg += String.format("%d Games => Lane %d \n", perLane[i], laneID);
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.getDialogPane().getStylesheets().add(getClass().getResource("DarkMode.css").toExternalForm());
                alert.setTitle(ACTIVATION_TITLE);
                alert.setHeaderText(ACTIVATION_HEADER);
                alert.setContentText(msg);

                alert.show();

            } else {

                showAlert(ACTIVATION_ERROR_TITLE, String.format(ACTIVATION_ERROR_TEXT, gameType));

            }
        }
        for (Object o : root.getChildren()) {
            if (o instanceof ProductUseage) {
                ProductUseage pu = (ProductUseage) o;
                checkForLaneActivations(pu);
            }
        }
    }

    private void activateGamesOnLane(int laneID, int games) {
        if (laneID == 0 || laneID == 1) {
            String laneSide = (laneID == 0) ? "odd" : "even";
            String uuid = UUID.randomUUID().toString();
            String postData = String.format("{\"type\": \"numbered\", \"games\": %d, \"UUID\": %s}", games, uuid);
            try {
                String ip = mPrefs.get(PREF_SCORER_IP, DEFAULT_SCORER_IP);
                String Response = WebFunctions.doHttpPostRequest(ip, String.format(LANE_GAME_ACTIVATION_PATH, laneSide), postData, DEFAULT_TOKEN);
                Map<String, Object> statusMap = gson.fromJson(Response, Map.class);
                if (statusMap.containsKey("success")) {
                    if (statusMap.get("success") instanceof Boolean) {
                        System.out.println("Lane Activation Status: " + (Boolean) statusMap.get("success"));
                        if (this.onLaneActivated != null) {
                            ActionEvent ae = new ActionEvent(laneID, null);
                            this.onLaneActivated.handle(ae);
                        }
                    }
                }

            } catch (IOException | InterruptedException ex) {
                System.out.println("Error getting lane status - " + ex.toString());
            } catch (Exception ex) {
                showAlert("Lane Activation Error", ex.toString());
            }
        }
    }

    @Override
    public void configurationDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("DarkMode.css").toExternalForm());
        alert.setTitle("MockDB Config");
        alert.setGraphic(null);
        alert.setHeaderText("Scorer IP");
        String ip = mPrefs.get(PREF_SCORER_IP, DEFAULT_SCORER_IP);
        TextField ft = new TextField(ip);
        alert.getDialogPane().setContent(ft);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            mPrefs.put(PREF_SCORER_IP, ft.textProperty().get());

        }
    }

    @Override
    public void cycleLane(AuthorizedUser user, int laneID) {
        if (user.isAuthorized(UserRole.GAME_ADMIN) && laneID < 2) {
            Platform.runLater(() -> {
                onCycleLane(laneID);
            });
        }
    }

    private void onCycleLane(int laneID) {
        if (laneID == 0 || laneID == 1) {
            String laneSide = (laneID == 0) ? "odd" : "even";
            String postData = "{}";
            String laneCommand = "lane/%s/?set=pinSetterCycleNoScore";
            String Response = "";
            try {
                String ip = mPrefs.get(PREF_SCORER_IP, DEFAULT_SCORER_IP);
                Response = WebFunctions.doHttpPostRequest(ip, String.format(laneCommand, laneSide), postData, DEFAULT_TOKEN);
                Map<String, Object> statusMap = gson.fromJson(Response, Map.class);
                if (statusMap.containsKey("success")) {
                    if (statusMap.get("success") instanceof Boolean) {
                        System.out.println("Lane cycle status: " + (Boolean) statusMap.get("success"));

                    }
                }
            } catch (Exception ex) {
                showAlert("Lane Cycle Error", ex.toString() + "\n" + Response);
            }
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("DarkMode.css").toExternalForm());
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(msg);

        alert.show();
    }

    @Override
    public void pauseResumeAbortSession(AuthorizedUser user, String type, int laneID) {
        if (user.isAuthorized(UserRole.GAME_ADMIN) && laneID < 2) {
            Platform.runLater(() -> {
                onPauseResumeAbortSession(type, laneID);
            });
        }
    }

    private void onPauseResumeAbortSession(String type, int laneID) {
        if (laneID == 0 || laneID == 1) {
            String laneSide = (laneID == 0) ? "odd" : "even";
            String postData = String.format("{\"UUID\": %s}", getCurrentSession(laneID));
            String laneCommand = "game/%s/?set=" + type;
            String Response = "";
            try {
                String ip = mPrefs.get(PREF_SCORER_IP, DEFAULT_SCORER_IP);
                Response = WebFunctions.doHttpPostRequest(ip, String.format(laneCommand, laneSide), postData, DEFAULT_TOKEN);
                Map<String, Object> statusMap = gson.fromJson(Response, Map.class);
                if (statusMap.containsKey("success")) {
                    if (statusMap.get("success") instanceof Boolean) {
                        System.out.println("Lane cycle status: " + (Boolean) statusMap.get("success"));

                    }
                }
            } catch (Exception ex) {
                showAlert("Lane " + type + " session error", ex.toString() + "\n" + Response);
            }
        }
    }

    @Override
    public String getCurrentSession(int laneID) {
        if (laneID == 0 || laneID == 1) {
            String laneSide = (laneID == 0) ? "odd" : "even";
            String laneCommand = "game/%s/?get=currentSession";
            String Response = "";
            try {
                String ip = mPrefs.get(PREF_SCORER_IP, DEFAULT_SCORER_IP);
                Response = WebFunctions.doHttpGetRequest(ip, String.format(laneCommand, laneSide), DEFAULT_TOKEN);
                Map<String, String> status = gson.fromJson(Response, Map.class);
                if (status.containsKey("UUID")) {
                    return status.get("UUID");
                }

            } catch (Exception ex) {
                showAlert("Lane get session error", ex.toString() + "\n" + Response);
            }

        }
        return "";
    }

    @Override
    public ArrayList<SimplePlayer> getPlayers(AuthorizedUser user) {
        ArrayList<SimplePlayer> list = new ArrayList<>();
        list.add(new SimplePlayer("Fred F."));
        list.add(new SimplePlayer("Barney R."));
        list.add(new SimplePlayer("Mr. Slate"));
        list.add(new SimplePlayer("GR8 Gazoo"));

        return list;
    }

    @Override
    public void addPlayer(AuthorizedUser user, int laneID, SimplePlayer player) {
        if (user.isAuthorized(UserRole.GAME_ADMIN) && laneID < 2) {
            Platform.runLater(() -> {
                onAddPlayer(player, laneID);
            });
        }
    }

    private void onAddPlayer(SimplePlayer player, int laneID) {
        if (laneID == 0 || laneID == 1) {
            String laneSide = (laneID == 0) ? "odd" : "even";
            Map<String, Object> map = new HashMap<>();
            map.put("UUID", getCurrentSession(laneID));
            map.put("playerName", player.getName());
            map.put("playerUUID", player.getUuid());
            map.put("playerHDCP", player.getHdcp());
            String postData = gson.toJson(map);
            String laneCommand = "game/%s/?set=newPlayer";
            String Response = "";
            try {
                String ip = mPrefs.get(PREF_SCORER_IP, DEFAULT_SCORER_IP);
                Response = WebFunctions.doHttpPostRequest(ip, String.format(laneCommand, laneSide), postData, DEFAULT_TOKEN);
                Map<String, Object> statusMap = gson.fromJson(Response, Map.class);
                if (statusMap.containsKey("success")) {
                    if (statusMap.get("success") instanceof Boolean) {
                        System.out.println("Lane cycle status: " + (Boolean) statusMap.get("success"));

                    }
                }
            } catch (Exception ex) {
                showAlert("Lane add player " + player.toString() + " error", ex.toString() + "\n" + Response);
            }
        }
    }

    @Override
    public Map<String, Object> getLaneConfig(AuthorizedUser user, int laneID, String type) {
        Map<String, Object> map = new HashMap<>();
        if (user.isAuthorized(UserRole.MANAGE_SCORER)) {
            if (laneID == 0 || laneID == 1) {
                String laneSide = (laneID == 0) ? "odd" : "even";
                String laneCommand = "game/%s/?get=" + type;
                String Response = "";
                try {
                    String ip = mPrefs.get(PREF_SCORER_IP, DEFAULT_SCORER_IP);
                    Response = WebFunctions.doHttpGetRequest(ip, String.format(laneCommand, laneSide), DEFAULT_TOKEN);
                    Map<String, String> status = gson.fromJson(Response, Map.class);
                    if (status.containsKey("CurentConfig")) {
                        map.putAll(gson.fromJson(status.get("CurentConfig"), Map.class));
                    }

                } catch (Exception ex) {
                    showAlert("Lane get session error", ex.toString() + "\n" + Response);
                }

            }
        }
        return map;
    }

    @Override
    public void setLaneConfig(AuthorizedUser user, int laneID, String type, Map<String, Object> config) {

        if ((laneID == 0 || laneID == 1) && user.isAuthorized(UserRole.MANAGE_SCORER)) {
            String laneSide = (laneID == 0) ? "odd" : "even";
            String laneCommand = "lane/%s/?set=" + type;
            String postData = gson.toJson(config);
            String Response = "";
            try {
                String ip = mPrefs.get(PREF_SCORER_IP, DEFAULT_SCORER_IP);
                Response = WebFunctions.doHttpPostRequest(ip, String.format(laneCommand, laneSide), postData, DEFAULT_TOKEN);
                Map<String, Object> statusMap = gson.fromJson(Response, Map.class);
                if (statusMap.containsKey("success")) {

                }
            } catch (Exception ex) {
                showAlert("Set lane Config Error", ex.toString() + "\n" + Response);
            }
        }
    }

    @Override
    public byte[] getLastImage(AuthorizedUser user, int laneID) {
        if (user.isAuthorized(UserRole.MANAGE_SCORER)) {
            if (laneID == 0 || laneID == 1) {
                String laneSide = (laneID == 0) ? "odd" : "even";
                String laneCommand = "lane/%s/?get=lastImage";
                String Response = "";
                try {
                    String ip = mPrefs.get(PREF_SCORER_IP, DEFAULT_SCORER_IP);
                    Response = WebFunctions.doHttpGetRequest(ip, String.format(laneCommand, laneSide), DEFAULT_TOKEN);
                    Map<String, String> status = gson.fromJson(Response, Map.class);
                    if (status.containsKey("file")) {
                        return status.get("file").getBytes();
                    }

                } catch (Exception ex) {
                    showAlert("Lane get image error", ex.toString());
                }

            }
        }
        return new String("Error Retreaving image").getBytes();
    }

}
