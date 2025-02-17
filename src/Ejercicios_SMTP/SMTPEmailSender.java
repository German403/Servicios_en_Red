package Ejercicios_SMTP;

import org.apache.commons.net.smtp.AuthenticatingSMTPClient;
import javax.net.ssl.KeyManager;
import java.io.Writer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SMTPEmailSender {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

            System.out.print("Introduce servidor SMTP . . . . . .: ");
            String servidorSMTP = reader.readLine().trim();

            System.out.print("Necesita negociación TLS (S/N)? .: ");
            boolean usaTLS = reader.readLine().trim().equalsIgnoreCase("S");

            System.out.print("Introduce usuario . . . . . . . . .: ");
            String usuario = reader.readLine().trim();

            System.out.print("Introduce contraseña . . . . . . . : ");
            String contrasena = reader.readLine().trim();

            System.out.print("Introduce puerto . . . . . . . . . : ");
            int puerto = Integer.parseInt(reader.readLine().trim());

            System.out.print("Introduce correo del remitente . . : ");
            String remitente = reader.readLine().trim();

            System.out.print("Introduce correo del destinatario. : ");
            String destinatario = reader.readLine().trim();

            System.out.print("Introduce asunto . . . . . . . . . : ");
            String asunto = reader.readLine().trim();

            System.out.println("Introduce el mensaje. Finalizará cuando se pulse un *:");
            StringBuilder mensaje = new StringBuilder();
            String linea;
            while (!(linea = reader.readLine()).equals("*")) {
                mensaje.append(linea).append("\n");
            }

            if (mensaje.toString().trim().isEmpty()) {
                System.out.println("El mensaje no puede estar vacío.");
                return;
            }




            AuthenticatingSMTPClient clienteSMTP = new AuthenticatingSMTPClient();
            clienteSMTP.connect(servidorSMTP, puerto);
            System.out.println("Conectado al servidor SMTP en el puerto " + puerto);

// Si el usuario eligió usar TLS, ejecutamos STARTTLS
            if (usaTLS) {
                if (clienteSMTP.execTLS()) {
                    System.out.println("STARTTLS activado con éxito.");
                } else {
                    System.out.println("Error al activar STARTTLS.");
                    return; // Si no se puede activar STARTTLS, detenemos la ejecución
                }
            }




            clienteSMTP.connect(servidorSMTP, puerto);
            System.out.println("Conectado al servidor SMTP.");

            if (usaTLS && clienteSMTP.execTLS()) {
                System.out.println("Negociación TLS establecida con éxito.");
            } else if (usaTLS) {
                System.out.println("Fallo en la negociación TLS.");
                return;
            }

            if (!clienteSMTP.auth(AuthenticatingSMTPClient.AUTH_METHOD.LOGIN, usuario, contrasena)) {
                System.out.println("Autenticación fallida. Verifica usuario y contraseña.");
                return;
            }

            clienteSMTP.setSender(remitente);
            clienteSMTP.addRecipient(destinatario);

            Writer escritor = clienteSMTP.sendMessageData();
            if (escritor != null) {
                escritor.write("Subject: " + asunto + "\n");
                escritor.write("To: " + destinatario + "\n\n");
                escritor.write(mensaje.toString());
                escritor.flush();
                escritor.close();
                System.out.println("Correo enviado correctamente.");
            } else {
                System.out.println("Error al enviar el correo.");
            }

            clienteSMTP.logout();
            clienteSMTP.disconnect();
            System.out.println("Conexión cerrada.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
