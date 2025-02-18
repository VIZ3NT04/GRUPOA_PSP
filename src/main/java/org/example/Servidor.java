package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Servidor implements Runnable {
    private int portServidor;
    private int portClient;

    public Servidor(int portServidor, int portClient) {
        this.portServidor = portServidor;
        this.portClient = portClient;
    }

    @Override
    public void run() {
        try {
            byte[] byteARecibir = new byte[1024];
            DatagramPacket paquet = new DatagramPacket(byteARecibir, byteARecibir.length);
            DatagramSocket socketServidor = new DatagramSocket(portClient);
            socketServidor.receive(paquet);
            String mensaje = new String(paquet.getData(), 0, paquet.getLength(), "UTF-8");
            System.out.println("(Servidor) " + mensaje);
            socketServidor.close();

            // PASO 1: Ejecutar mysqldump en la VM de Azure
            String sshCommand = "ssh administrador@40.89.147.152 \"mysqldump -u root --password=1234 --databases adt_grupoA > /home/administrador/backup.sql\"";
            System.out.println("Ejecutando: " + sshCommand);

            if (ejecutarComando(sshCommand)) {
                System.out.println("Backup realizado correctamente en el servidor remoto.");
            } else {
                System.err.println("Error al realizar el backup en el servidor remoto.");
                return;
            }

            // PASO 2: Verificar que el archivo de backup exista en el servidor remoto antes de transferirlo
            String checkBackupCommand = "ssh administrador@40.89.147.152 \"test -f /home/administrador/backup.sql && echo 'Archivo encontrado' || echo 'Archivo no encontrado'\"";
            if (ejecutarComando(checkBackupCommand)) {
                // PASO 2: Transferir el backup a Windows con SCP
                String backupLocalPath = "C:\\Users\\Vicent\\IdeaProjects\\GRUPOA_PSP\\backup\\backup.sql";
                String scpCommand = "scp administrador@40.89.147.152:/home/administrador/backup.sql " + backupLocalPath.replace("\\", "/");
                System.out.println("Ejecutando: " + scpCommand);

                if (ejecutarComando(scpCommand)) {
                    System.out.println("Archivo de backup transferido correctamente a: " + backupLocalPath);
                } else {
                    System.err.println("Error al transferir el archivo de backup.");
                }
            } else {
                System.err.println("No se encontró el archivo de backup en el servidor remoto.");
            }

        } catch (Exception e) {
            e.printStackTrace();
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
