/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

module OpenBowl.Display {
    requires javafx.controls;
    requires javafx.fxml;
    requires OpenBowl.Common;
    requires java.prefs;
    requires java.desktop;
    requires jdk.httpserver;
    //required for Gson
    requires java.sql;
    
    exports org.openbowl.display;
}
