package org.example;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Servidor implements Runnable{
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
            DatagramPacket paquet = new DatagramPacket(byteARecibir,byteARecibir.length);
            DatagramSocket socketServidor = new DatagramSocket(portClient);
            socketServidor.receive(paquet);
            String mensaje = new String(paquet.getData(),0,paquet.getLength(),"UTF-8");
            System.out.println("(Servidor) " + mensaje);
            socketServidor.close();

            /* *** Procces Builder *** */


            /*

            ProcessBuilder pb = new ProcessBuilder(
                "ssh", "usuari@servidor-remot",
                "mysqldump -u root probagrupoa > /tmp/backup.sql"
            );

            Process process = pb.start();
            process.waitFor();
             */
            ProcessBuilder pb = new ProcessBuilder(
                    "C:\\xampp\\mysql\\bin\\mysqldump.exe",  // Cambia según tu ruta
                    "-h", "localhost",
                    "-u", "root",
                    "--protocol=tcp",
                    "probagrupoa"
            );


            // Redirigir la salida a un archivo en una ruta relativa (carpeta local 'backup')
            File backupFile = new File("backup/backup.sql");
            backupFile.getParentFile().mkdirs(); // Crear directorios si no existen
            pb.redirectOutput(backupFile);

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Backup realizado correctamente en: " + backupFile.getAbsolutePath());
            } else {
                System.err.println("Error al realizar el backup. Código de salida: " + exitCode);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
