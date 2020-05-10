/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

module OpenBowl.Client {
    requires javafx.controls;
    requires OpenBowl.Common;
    requires java.prefs;
    requires javafx.web;
    requires javafx.fxml;
    
    opens org.openbowl.client to javafx.fxml;
    exports org.openbowl.client;
}
