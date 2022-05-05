package com.edmi.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import javafx.scene.control.Alert;

public class ViewFactory {

    public void showMainWindow(){
        BaseController baseController = new ViewMainController(this, "com/edmi/view/ViewMain.fxml");
        this.initStage(baseController);
    }
    
    public void showAlert(Alert.AlertType type, String header, String content){
        Alert alert = new Alert(type);
        alert.setResizable(false);
        alert.setTitle("Mensaje del Sistema");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void initStage(BaseController controller){
        try {
            URL fxml = getClass().getClassLoader().getResource(controller.getFxmlPath());
            FXMLLoader fxmlLoader = new FXMLLoader(fxml);
            fxmlLoader.setController(controller);
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Title Main");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
    }

    public void closeWindow(Stage stage){
        stage.close();
    }
}