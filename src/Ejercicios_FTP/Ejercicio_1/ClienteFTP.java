package Ejercicios_FTP.Ejercicio_1;
import java.io.IOException;
import org.apache.commons.net.ftp.*;

public class ClienteFTP {
    public static void main(String[] args) {
        // Lista de servidores FTP anónimos
        String[] servidores = {
                "ftp.rediris.es",
                "ftp.freebsd.org",
                "ftp.cs.princeton.edu",
                "ftp.uvigo.es",
                "ftp.ujaen.es",
                "ftp.aytolacoruna.es"
        };

        for (String servidor : servidores) {
            conectarYListar(servidor);
        }
    }

    public static void conectarYListar(String servidorFTP) {
        FTPClient clienteFTP = new FTPClient();
        String usuario = "anonymous";
        String clave = "anonymous";

        try {
            System.out.println("\nConectando a: " + servidorFTP + "...");
            clienteFTP.connect(servidorFTP);
            clienteFTP.enterLocalPassiveMode(); // Modo pasivo para evitar problemas con firewalls

            // Comprobar conexión
            int respuesta = clienteFTP.getReplyCode();
            if (!FTPReply.isPositiveCompletion(respuesta)) {
                System.out.println("No se pudo conectar a " + servidorFTP);
                clienteFTP.disconnect();
                return;
            }

            // Iniciar sesión anónima
            if (!clienteFTP.login(usuario, clave)) {
                System.out.println("No se pudo iniciar sesión en " + servidorFTP);
                clienteFTP.disconnect();
                return;
            }

            System.out.println("Login exitoso en: " + servidorFTP);
            System.out.println("Directorio actual: " + clienteFTP.printWorkingDirectory());

            // Obtener lista de directorios en la raíz
            FTPFile[] archivos = clienteFTP.listFiles("/");
            System.out.println("Contenido del directorio raíz:");

            for (FTPFile archivo : archivos) {
                if (archivo.isDirectory()) {
                    System.out.println("[DIR] " + archivo.getName());

                    // Cambiar al directorio e imprimir su contenido
                    if (clienteFTP.changeWorkingDirectory("/" + archivo.getName())) {
                        FTPFile[] contenido = clienteFTP.listFiles();
                        for (FTPFile item : contenido) {
                            System.out.println("  - " + item.getName() + (item.isDirectory() ? " [DIR]" : ""));
                        }
                        clienteFTP.changeToParentDirectory(); // Volver al directorio raíz
                    }
                } else {
                    System.out.println("  " + archivo.getName() + " [Archivo]");
                }
            }

            clienteFTP.logout();
            clienteFTP.disconnect();
            System.out.println("Desconectado de " + servidorFTP);

        } catch (IOException e) {
            System.out.println("Error al conectar con " + servidorFTP + ": " + e.getMessage());
        }
    }
}
