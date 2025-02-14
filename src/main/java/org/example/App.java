package org.example;

public class App {
    public static void main(String[] args) throws InterruptedException {
        Thread servidor = new Thread(new Servidor(15000,14999),"Servidor");
        Thread client = new Thread(new Client(14999,15000),"Client");

        servidor.start();
        Thread.sleep(2000);
        client.start();

        servidor.join();
        client.join();
    }
}
