module TCPcom {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires java.desktop;
    requires eu.hansolo.tilesfx;
    requires com.google.gson;

    opens com.edmi.view;
    opens com.edmi.controller;
    opens com.edmi.controller.json;
    opens com.edmi.app;
}