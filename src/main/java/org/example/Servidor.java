package org.example;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;


public class Servidor implements Runnable {
    private ServerSocket serverSocket;
    private boolean running = true;

    public Servidor(int serverPort) throws IOException {
        this.serverSocket = new ServerSocket(serverPort);
    }

    @Override
    public void run() {
        System.out.println("Servidor iniciado...");
        try {
            while (running) {
                Socket socketToClient = serverSocket.accept();
                DataInputStream fluxIn = new DataInputStream(socketToClient.getInputStream());
                DataOutputStream fluxOut = new DataOutputStream(socketToClient.getOutputStream());

                String data = fluxIn.readUTF();
                System.out.println("Cliente envió: " + data);

                if ("backup".equalsIgnoreCase(data)) {
                    realizarBackup();
                    fluxOut.writeUTF("Backup completado");
                } else if ("exit".equalsIgnoreCase(data)) {
                    fluxOut.writeUTF("Cerrando servidor...");
                    running = false;
                } else {
                    fluxOut.writeUTF("Comando no reconocido");
                }

                socketToClient.close();
            }
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void realizarBackup() {
        String sshCommand = "ssh administrador@40.89.147.152 \"mysqldump -u root --password=1234 --databases adt_grupoA > /home/administrador/backup.sql\"";
        if (ejecutarComando(sshCommand)) {
            System.out.println("Backup realizado correctamente en el servidor remoto.");
        } else {
            System.err.println("Error al realizar el backup.");
        }
        String backupLocalPath = "C:\\Users\\Vicent\\IdeaProjects\\GRUPOA_PSP\\backup\\backup.sql";
        String scpCommand = "scp administrador@40.89.147.152:/home/administrador/backup.sql " + backupLocalPath.replace("\\", "/");
        if (ejecutarComando(scpCommand)) {
            System.out.println("Transferencia de archivos realizado correctamente en el servidor.");
        } else {
            System.err.println("Error al realizar la transferencia de archivos.");
        }
    }

    private boolean ejecutarComando(String comando) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", comando);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Leer y mostrar la salida del comando
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            return exitCode == 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
