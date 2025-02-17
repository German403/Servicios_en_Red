package Pruebas;

import java.io.*;
import java.net.*;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;


public class ClienteFTP1 {
    public static void main(String[] args) throws SocketException, IOException {
        FTPClient clienetFTP = new FTPClient();
        String servidorFTP = "ftp.rediris.es";
        System.out.println("Conexion a: " + servidorFTP);

        clienetFTP.connect(servidorFTP);
        clienetFTP.enterLocalPassiveMode();

        System.out.println(clienetFTP.getReplyString());
        int respuesta = clienetFTP.getReplyCode();

        System.out.println("Respuesta: " + respuesta);

        if(!FTPReply.isPositiveCompletion(respuesta)) {
            clienetFTP.disconnect();
            System.out.println("Conexion rechazada: " + respuesta);
            System.exit(0);
        }

        clienetFTP.disconnect();
        System.out.println("Conexion finalizada");
    }
}
