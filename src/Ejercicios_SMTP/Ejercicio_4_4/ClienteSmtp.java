package Ejercicios_SMTP.Ejercicio_4_4;

import java.io.Writer;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;

import org.apache.commons.net.smtp.AuthenticatingSMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.commons.net.smtp.SimpleSMTPHeader;

public class ClienteSmtp {
    public static void main(String[] args) throws 
    NoSuchAlgorithmException, 
    UnrecoverableKeyException,
    KeyStoreException, 
    InvalidKeyException,
    InvalidKeySpecException{
        
        //Declaramos el Scanner
        Scanner sc = new Scanner(System.in);

        //se crea cliente SMTP seguro
        AuthenticatingSMTPClient authenticatingSMTPClient = new AuthenticatingSMTPClient();

        //datos del usuario y del servidor
        //Servidor SMTP
        System.out.print("Introduce servidor SMTP...........: ");
        String server = sc.nextLine();
        //TLS
        System.out.print("Necesita negociación TLS(S/N)?....: ");
        String tls = sc.nextLine();
        //Usuario
        System.out.print("Introduce usuario.................: ");
        String username = sc.nextLine();
        //Contraseña
        System.out.print("Introduce Contraseña..............: ");
        String passwd = sc.nextLine();
        //Puerto
        System.out.print("Introduce puerto..................: ");
        int puerto = sc.nextInt();
        sc.nextLine(); //Limpiamos el buffer
        //Correo del remitente
        System.out.print("Introduce correo del remitente....: ");
        String cRemitente = sc.nextLine();
        //Correo destinatario
        System.out.print("Introduce correo del destinatario.: ");
        String cDestinatario = sc.nextLine();
        //Asunto
        System.out.print("Introduce asunto..................: ");
        String asunto = sc.nextLine();
        // Mensaje: Acumularemos todas las líneas hasta que el usuario ingrese *
        StringBuilder mensaje = new StringBuilder();
        System.out.println("Introduce el mensaje. Finalizará cuando se pulse un *");
        String linea;
        while (true) {
            linea = sc.nextLine();
            if (linea.equals("*")) {
                break; // Salir del bucle cuando el usuario ingresa *
            }
            mensaje.append(linea).append("\n"); // Añadimos la línea al mensaje
        }

        try {
            int respuesta;

            //creacion de la clave para establecer un canal seguro
            KeyManagerFactory keyManagerFactory = 
            KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

            keyManagerFactory.init(null, null);
            KeyManager KeyManager = keyManagerFactory.getKeyManagers()[0];
            
            //Conexion al servidor SMTP
            authenticatingSMTPClient.connect(server, puerto);
            System.out.println("1 -"+ authenticatingSMTPClient.getReplyString());

            //Se establece la clave para la comunicación segura
            authenticatingSMTPClient.setKeyManager(KeyManager);

            respuesta = authenticatingSMTPClient.getReplyCode();
            
            //Encaso de que la key no sea buena se corta la conexion
            if (!SMTPReply.isPositiveCompletion(respuesta)) {
                authenticatingSMTPClient.disconnect();
                System.err.println("Conexion rechazada.");
                System.exit(1);
            }

            //Se envia el comando EHLO
            authenticatingSMTPClient.ehlo(server); //necesario
            System.out.println("2 - "+authenticatingSMTPClient.getReplyString());

            //En caso de querer TLS
            if (tls.toLowerCase().equals("s")) {
                
                //Necesita Negociación TLS - Modo no implicito
                //Se ejecuta el comanto Strarttls y se comprueba que es true
                if (authenticatingSMTPClient.execTLS()) {
                    System.out.println("3 - " + authenticatingSMTPClient.getReplyStrings());

                    //Se realiza la autenticacion con el servidor
                    if (authenticatingSMTPClient.auth(AuthenticatingSMTPClient.AUTH_METHOD.LOGIN, username, passwd)) {
                        //Cabecera del mensaje
                        SimpleSMTPHeader simpleSMTPHeader = new SimpleSMTPHeader(cRemitente,cDestinatario ,asunto);

                        //el nombre de usuario y el email de origen coinciden
                        authenticatingSMTPClient.setSender(cRemitente);
                        authenticatingSMTPClient.addRecipient(cDestinatario);
                        System.out.println("5 -"+ authenticatingSMTPClient.getReplyString());

                        //Se envia Data
                        Writer writer = authenticatingSMTPClient.sendMessageData();
                        //En caso de que data sea null
                        if (writer == null) {//fallo
                            System.out.println("Fallo al enviar data");
                        }

                        writer.write(simpleSMTPHeader.toString()); //Cabecera
                        writer.write(mensaje.toString()); //mensaje
                        writer.close();
                        System.out.println("6 -"+ authenticatingSMTPClient.completePendingCommand());
                        
                        //Comprobamos si el mensaje se ha enviado con exito
                        boolean exito = authenticatingSMTPClient.completePendingCommand();
                        if (!exito) { //fallo
                            System.out.println("Fallo al enviar");
                        }
                        else{
                            System.out.println("Mensaje enviado con exito");
                        } 
                    }
                    else{
                        System.out.println("Usuario no autenticado");
                    }
                }
                else{
                    System.out.println("Fallo al ejecutar Starttls");
                }
            }
            else{
                if (authenticatingSMTPClient.auth(AuthenticatingSMTPClient.AUTH_METHOD.LOGIN, username, passwd)) {
                    //Cabecera del mensaje
                    SimpleSMTPHeader simpleSMTPHeader = new SimpleSMTPHeader(cRemitente,cDestinatario ,asunto);

                    //el nombre de usuario y el email de origen coinciden
                    authenticatingSMTPClient.setSender(cRemitente);
                    authenticatingSMTPClient.addRecipient(cDestinatario);
                    System.out.println("5 -"+ authenticatingSMTPClient.getReplyString());

                    //Se envia Data
                    Writer writer = authenticatingSMTPClient.sendMessageData();
                    //En caso de que data sea null
                    if (writer == null) {//fallo
                        System.out.println("Fallo al enviar data");
                    }

                    writer.write(simpleSMTPHeader.toString()); //Cabecera
                    writer.write(mensaje.toString()); //mensaje
                    writer.close();
                    System.out.println("6 -"+ authenticatingSMTPClient.completePendingCommand());
                    
                    //Comprobamos si el mensaje se ha enviado con exito
                    boolean exito = authenticatingSMTPClient.completePendingCommand();
                    if (!exito) { //fallo
                        System.out.println("Fallo al enviar");
                    }
                    else{
                        System.out.println("Mensaje enviado con exito");
                    } 
                }
            }

        } catch (Exception e) {
           System.err.println(e);
        }
        sc.close();
    }
}
