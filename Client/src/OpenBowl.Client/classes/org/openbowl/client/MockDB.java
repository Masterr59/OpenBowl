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
import java.util.Random;
import javafx.scene.control.TreeItem;
import org.openbowl.common.AuthorizedUser;
import org.openbowl.common.SystemStatus;
import org.openbowl.common.UserRole;
import org.openbowl.common.WebFunctions;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class MockDB implements DatabaseConnector {

    private final String ADMIN = "Admin";
    private final String MANAGER = "Manager";
    private final String DESK = "Desk";
    private final String RESTAURANT = "Restaurant";
    private final String NONE = "None";
    private final String DEFAULT_TOKEN = "yZ9Ut95MG3xdf5gc6WgT";
    private final String GET_LANE_STATUS_PATH = "system/?get=status";

    private final String DEPT_REST = "Restaurant";
    private final String DEPT_BAR = "Bar";
    private final String DEPT_PRO = "Pro Shop";

    private final String[] DEPTS = {DatabaseConnector.GAME_DEPARTMENT_NAME, DEPT_REST, DEPT_BAR, DEPT_PRO};

    private final Random rand;
    private final Gson gson;

    private Map<Integer, TreeItem> tabs;

    public MockDB() {
        rand = new Random();
        gson = new Gson();
        tabs = new HashMap<>();
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
                Response = WebFunctions.doHttpGetRequest("127.0.0.1", GET_LANE_STATUS_PATH, DEFAULT_TOKEN);
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
                String Response = WebFunctions.doHttpGetRequest("127.0.0.1", GET_LANE_STATUS_PATH, DEFAULT_TOKEN);
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
        ProductType typeGame = new ProductType(0, "Game");
        ProductType typeFood = new ProductType(1, "Food / Drink");
        ProductType typeRetail = new ProductType(2, "Retail");
        ProductType typeOther = new ProductType(3, "Other");
        ProductType typeDiscount = new ProductType(4, "Discount");

        Product item;
        ProductUseage pkg;
        Product laneRental = new Product(2, "Lane Rental", 19.99, SD, typeFood, TaxType.TEST_RATE);
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
    public Integer saveTab(AuthorizedUser user, TreeItem root) {
        if (user.isAuthorized(UserRole.TRANSACTION_ADD)) {
            int i = rand.nextInt(Integer.MAX_VALUE);
            tabs.put(i, root);
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
    public TreeItem getTab(AuthorizedUser user, Integer tabid) {
        TreeItem item = new TreeItem("NONE");
        if (user.isAuthorized(UserRole.TRANSACTION_ADD) && tabs.containsKey(tabid)) {
            item = tabs.get(tabid);
        }
        return item;
    }

    @Override
    public Integer saveTransaction(AuthorizedUser user, TreeItem root) {
        int transactionID = -1;
        if (user.isAuthorized(UserRole.TRANSACTION_ADD)) {
            if (root instanceof ProductUseage) {
                ProductUseage pu = (ProductUseage) root;
                if (pu.getTransaction_ID() > 0) {
                    transactionID = pu.getTransaction_ID();
                } else {
                    transactionID = rand.nextInt(Integer.MAX_VALUE);
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

}
