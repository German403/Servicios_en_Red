package Ejercicios_FTP.Ejercicio_2;

import java.io.*;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.net.ftp.*;

public class ClienteFTPUpload {
    public static void main(String[] args) {
        String servidorFTP = "localhost"; // Servidor FTP local
        String usuario = "usuario1"; // Usuario FTP (ajústalo según tu configuración)
        String clave = "usuario1"; // Contraseña FTP

        FTPClient clienteFTP = new FTPClient();

        try {
            // Conectar al servidor
            System.out.println("Conectando a " + servidorFTP + "...");
            clienteFTP.connect(servidorFTP);
            clienteFTP.enterLocalPassiveMode();

            if (!clienteFTP.login(usuario, clave)) {
                System.out.println("Error de login en " + servidorFTP);
                clienteFTP.disconnect();
                return;
            }

            System.out.println("Conectado a " + servidorFTP);

            // Abrir JFileChooser para seleccionar archivo
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Selecciona un archivo para subir");

            int resultado = fileChooser.showOpenDialog(null);
            if (resultado != JFileChooser.APPROVE_OPTION) {
                System.out.println("No se seleccionó ningún archivo.");
                clienteFTP.logout();
                clienteFTP.disconnect();
                return;
            }

            File archivo = fileChooser.getSelectedFile();
            System.out.println("Archivo seleccionado: " + archivo.getAbsolutePath());

            // Establecer el tipo de archivo (modo binario)
            clienteFTP.setFileType(FTP.BINARY_FILE_TYPE);

            // Subir archivo
            try (InputStream inputStream = new FileInputStream(archivo)) {
                boolean exito = clienteFTP.storeFile(archivo.getName(), inputStream);
                if (exito) {
                    System.out.println("Archivo subido correctamente.");
                } else {
                    System.out.println("Error al subir el archivo.");
                }
            }

            // Listar archivos en el directorio raíz para verificar la subida
            System.out.println("Contenido del directorio raíz después de la subida:");
            FTPFile[] archivos = clienteFTP.listFiles("/");
            for (FTPFile file : archivos) {
                System.out.println(" - " + file.getName() + (file.isDirectory() ? " [DIR]" : " [Archivo]"));
            }

            // Cerrar sesión y desconectar
            clienteFTP.logout();
            clienteFTP.disconnect();
            System.out.println("Desconectado del servidor FTP.");

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
