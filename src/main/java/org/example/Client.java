package org.example;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client implements Runnable{
    private int portClient;
    private int portServidor;

    public Client(int portClient, int portServidor) {
        this.portClient = portClient;
        this.portServidor = portServidor;
    }

    @Override
    public void run() {
        try {
                Scanner sc = new Scanner(System.in);
                System.out.println("(Client) Cadena a enviar: ");
                String texto = sc.nextLine();
                DatagramPacket paquet = new DatagramPacket(texto.getBytes("UTF-8"),texto.length(), InetAddress.getByName("127.0.0.1"),portClient);
                DatagramSocket socket = new DatagramSocket();
                socket.send(paquet);
                socket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
