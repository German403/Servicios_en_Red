package Ejercicios_FTP.Ejercicio_3;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import org.apache.commons.net.ftp.*;

public class ClienteFTPDescarga extends JFrame {
    private FTPClient clienteFTP;
    private DefaultListModel<String> listaModel;
    private JList<String> listaArchivos;
    private JButton btnDescargar, btnSalir;
    private JTextField txtUsuario, txtServidor;
    private JPasswordField txtClave;

    public ClienteFTPDescarga() {
        setTitle("Cliente FTP - Descarga de Archivos");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelSuperior = new JPanel(new GridLayout(3, 2));
        panelSuperior.add(new JLabel("Servidor FTP:"));
        txtServidor = new JTextField("localhost");
        panelSuperior.add(txtServidor);

        panelSuperior.add(new JLabel("Usuario:"));
        txtUsuario = new JTextField();
        panelSuperior.add(txtUsuario);

        panelSuperior.add(new JLabel("Contraseña:"));
        txtClave = new JPasswordField();
        panelSuperior.add(txtClave);

        JButton btnConectar = new JButton("Conectar");
        panelSuperior.add(btnConectar);

        add(panelSuperior, BorderLayout.NORTH);

        listaModel = new DefaultListModel<>();
        listaArchivos = new JList<>(listaModel);
        JScrollPane scrollPane = new JScrollPane(listaArchivos);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel();
        btnDescargar = new JButton("Descargar");
        btnSalir = new JButton("Salir");
        btnDescargar.setEnabled(false);
        panelInferior.add(btnDescargar);
        panelInferior.add(btnSalir);
        add(panelInferior, BorderLayout.SOUTH);

        btnConectar.addActionListener(e -> conectarFTP());
        btnDescargar.addActionListener(e -> descargarArchivo());
        btnSalir.addActionListener(e -> salirAplicacion());

        listaArchivos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listaArchivos.getSelectedIndex() != -1) {
                btnDescargar.setEnabled(true);
            }
        });
    }

    private void conectarFTP() {
        String servidor = txtServidor.getText();
        String usuario = txtUsuario.getText();
        String clave = new String(txtClave.getPassword());

        clienteFTP = new FTPClient();
        try {
            clienteFTP.connect(servidor);
            clienteFTP.enterLocalPassiveMode();

            if (!clienteFTP.login(usuario, clave)) {
                JOptionPane.showMessageDialog(this, "Error de login en " + servidor, "Error", JOptionPane.ERROR_MESSAGE);
                clienteFTP.disconnect();
                return;
            }

            JOptionPane.showMessageDialog(this, "Conectado a " + servidor, "Éxito", JOptionPane.INFORMATION_MESSAGE);

            listaModel.clear();
            FTPFile[] archivos = clienteFTP.listFiles("/");
            for (FTPFile archivo : archivos) {
                if (archivo.isFile()) {
                    listaModel.addElement(archivo.getName());
                }
            }

            if (listaModel.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay archivos disponibles.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error de conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void descargarArchivo() {
        String archivoSeleccionado = listaArchivos.getSelectedValue();
        if (archivoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un archivo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecciona dónde guardar el archivo");
        fileChooser.setSelectedFile(new File(archivoSeleccionado));
        int resultado = fileChooser.showSaveDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoDestino = fileChooser.getSelectedFile();
            try (OutputStream outputStream = new FileOutputStream(archivoDestino)) {
                boolean exito = clienteFTP.retrieveFile(archivoSeleccionado, outputStream);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Archivo descargado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error al descargar el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void salirAplicacion() {
        try {
            if (clienteFTP != null && clienteFTP.isConnected()) {
                clienteFTP.logout();
                clienteFTP.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClienteFTPDescarga().setVisible(true));
    }
}
