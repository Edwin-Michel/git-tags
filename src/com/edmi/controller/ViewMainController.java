package com.edmi.controller;

import com.edmi.util.Title;
import java.net.*;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;

public class ViewMainController extends BaseController implements Initializable, Runnable {
    @FXML
    private TextField txtPort;
    @FXML
    private TextField txtIpAddress;
    @FXML
    private GridPane gridPane;
    @FXML
    private Button btnStart;
    @FXML
    private Button btnStop;

    private boolean statusThread = true;
    private Tile gauge1;
    private Tile gauge2;
    private Tile switch1;
    private Tile switch2;
    private InetAddress address;

    public ViewMainController(ViewFactory viewFactory, String fxmlPath) {
        super(viewFactory, fxmlPath);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initWidgets();
        setDefaultData();

        Thread thread = new Thread(this);
        this.btnStart.setOnAction( actionEvent -> {
            if(txtPort.getText().trim().isEmpty()){
                viewFactory.showAlert(Alert.AlertType.WARNING, Title.HEADER_WA, Title.CONTENT_WA);//1024 - 65535
                return;
            } 
            
            int port = Integer.parseInt(txtPort.getText().trim());
            if(port < 1024 || port > 65535){
                viewFactory.showAlert(Alert.AlertType.ERROR, Title.HEADER_EA, Title.CONTENT_EA);
                return;
            }

            if(!ipAddressIsValid()){
                viewFactory.showAlert(Alert.AlertType.ERROR, Title.HEADER_EIP, Title.CONTENT_EIP);
                return;
            }

            thread.start();
            statusThread = true;
            disabledButtons(true);
            visibleSwitchs(true);
        });
        this.btnStop.setOnAction( actionEvent -> {
            this.statusThread = false;
            if(!thread.isInterrupted()) thread.interrupt();
            visibleSwitchs(false);
            disabledButtons(false);
        });
        
        onlyNumbers();
        disabledButtons(false);
    }

    private void onlyNumbers() {
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            if (change.getText().matches("[0-9]*")) return change;
            return null;
        };
        txtPort.setTextFormatter(new TextFormatter<String>(integerFilter));
    }

    private void disabledButtons(boolean disabled){
        btnStop.setDisable(!disabled);
        txtPort.setDisable(disabled);
        txtIpAddress.setDisable(disabled);
        btnStart.setDisable(disabled);
    }

    private void visibleSwitchs(boolean visible){
        switch1.visibleProperty().setValue(visible);
        switch2.visibleProperty().setValue(visible);
    }

    private void setDefaultData(){
        txtPort.setText("15200");
        txtIpAddress.setText("localhost");
        try {
            this.address = InetAddress.getByName(txtIpAddress.getText().trim());
        } catch (UnknownHostException e) {
            e.printStackTrace(System.out);
        }
        visibleSwitchs(false);
    }

    private boolean ipAddressIsValid(){
        String hostName = txtIpAddress.getText().trim();
        int point = 0;
        for (int i = 0; i < hostName.length(); i++) {
            if(hostName.charAt(i) == '.') point++;
        }
        return point == 3 || hostName.trim().equals("localhost");
    }

    private void initWidgets() {
        switch1 = TileBuilder.create()
                .skinType(Tile.SkinType.SWITCH)
                .backgroundColor(Color.rgb(60, 81, 134))
                .title("Switch1")
                .text("Switch 1")
                .description("OFF")
                .descriptionColor(Color.ORANGE)
                .build();

        switch2 = TileBuilder.create()
                .skinType(Tile.SkinType.SWITCH)
                .backgroundColor(Color.rgb(60, 81, 134))
                .title("Switch 2")
                .text("Switch 2")
                .description("OFF")
                .descriptionColor(Color.ORANGE)
                .build();

        gauge1 = TileBuilder.create()
                .skinType(Tile.SkinType.GAUGE2)
                .title("Temperatura 1")
                //.text("Whatever")
                .unit("\u00B0C")
                .textVisible(true)
                .decimals(2)
                .value(0.0)
                .minValue(0.0)
                .maxValue(100)
                .gradientStops(new Stop(0, Tile.BLUE),
                        new Stop(0.25, Tile.GREEN),
                        new Stop(0.5, Tile.YELLOW),
                        new Stop(0.75, Tile.ORANGE),
                        new Stop(1, Tile.RED))
                .strokeWithGradient(true)
                .animated(true)
                .backgroundColor(Color.rgb(84, 67, 107))
                .trackColor(Tile.TileColor.RED)
                .build();

        gauge2 = TileBuilder.create()
                .skinType(Tile.SkinType.GAUGE2)
                .title("Temperatura 2")
                //.text("Whatever")
                .unit("\u00B0C")
                .textVisible(true)
                .decimals(2)
                .value(0.0)
                .minValue(0.0)
                .maxValue(100)
                .gradientStops(new Stop(0, Tile.BLUE),
                        new Stop(0.25, Tile.GREEN),
                        new Stop(0.5, Tile.YELLOW),
                        new Stop(0.75, Tile.ORANGE),
                        new Stop(1, Tile.RED))
                .strokeWithGradient(true)
                .animated(true)
                .backgroundColor(Color.rgb(84, 67, 107))
                .trackColor(Tile.TileColor.RED)
                .build();

        Tile slider = TileBuilder.create()
                .skinType(Tile.SkinType.SLIDER)
                .title("PWM")
                .text("MOTOR DC")
                //.description("Test")
                .unit("%")
                .barBackgroundColor(Tile.FOREGROUND)
                .build();

        Tile graphic1 = TileBuilder.create()
                .skinType(Tile.SkinType.STOCK)
                .title("Grafica T1")
                .minValue(0)
                .maxValue(100)
                .value(0)
                .averagingPeriod(100)
                .build();

        Tile graphic2 = TileBuilder.create()
                .skinType(Tile.SkinType.STOCK)
                .title("Grafica T2")
                .minValue(0)
                .maxValue(100)
                .value(0)
                .averagingPeriod(100)
                .build();

        switch1.setOnSwitchPressed(e -> {
            System.gc();
            Client client = new Client(Integer.parseInt(txtPort.getText().trim()), address);
            Thread clientThread = new Thread(client);
            if(switch1.getDescription().equals("OFF")){
                switch1.setDescription("ON");
                System.gc();
                client.setData("S1=1");
            } else {
                switch1.setDescription("OFF");
                client.setData("S1=0");
                if(!clientThread.isInterrupted()){
                    clientThread.interrupt();
                }
            }
            clientThread.start();
        });

        switch2.setOnSwitchPressed(e -> {
            System.gc();
            Client client = new Client(Integer.parseInt(txtPort.getText().trim()), address);
            Thread clientThread = new Thread(client);
            if(switch2.getDescription().equals("OFF")){
                switch2.setDescription("ON");
                System.gc();
                client.setData("S2=1");
            } else {
                switch2.setDescription("OFF");
                client.setData("S2=0");
                if(!clientThread.isInterrupted()){
                    clientThread.interrupt();
                }
            }
            clientThread.start();
        });

        gridPane.add(gauge1, 0, 0);
        gridPane.add(gauge2, 1, 0);
        gridPane.add(switch1, 2, 0);
        gridPane.add(graphic1, 0, 1);
        gridPane.add(graphic2, 1, 1);
        gridPane.add(switch2, 2, 1);
        gridPane.add(slider, 0, 2);
        //https://ajaxhispano.com/ask/icono-de-la-aplicacion-javafx-15552/
    }

    @Override
    public void run() {
        final int PORT = Integer.parseInt(txtPort.getText().trim());
        try (DatagramSocket socketUDP = new DatagramSocket(PORT)){
            while (statusThread) {
                byte [] buffer = new byte[1024];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socketUDP.receive(request);
                this.address = request.getAddress();

                String[] data1 = new String(request.getData(), StandardCharsets.UTF_8).split("\\*");
                System.out.println("data1 = " + data1[0]);
                System.out.println("data2 = " + data1[1]);
                //gauge1.setValue(Double.parseDouble(data));
                //gauge2.setValue(Double.parseDouble(data));
//                buffer = "Hello".getBytes();
//                int clientPort = request.getPort();
//                InetAddress clientAddress = request.getAddress();
//                DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
//                socketUDP.send(response);
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
}