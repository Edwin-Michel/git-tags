package com.edmi.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Client implements Runnable{
    private final int port;
    private final InetAddress address;
    private String data;

    public Client(int port, InetAddress address){
        this.port = port;
        this.address = address;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public void run() {
        byte[] buffer;
        try {
            try (DatagramSocket socket = new DatagramSocket()) {
                buffer = data.getBytes(StandardCharsets.UTF_8);
                
                DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, port);
                socket.send(request);
                
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);
            }
        } catch (UnknownHostException ex) {
            ex.printStackTrace(System.out);
        } catch (SocketException ex) {
            ex.printStackTrace(System.out);
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
    }
}