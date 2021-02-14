package client;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Client {


    private final int SERVER_PORT = 8082;
    private final String SERVER_ADDRESS = "localhost";
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private JTextArea chatArea;
    private JTextField msgInputField;
    private   DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss");

    public Client() {
        try {
            connection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        prepareGUI();
    }

    public void connection() throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        new Thread(() -> {
            try {
                while (true) {
                    String message = dis.readUTF();
                    if (message.equalsIgnoreCase("/stop")) {
                        System.out.println("Client  stopped by outer command /stop");
                        break;
                    }
                    var time = formatter.format(LocalDateTime.now());
                    chatArea.append("Server [" + time + "] : " + message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public void send() {
        if (msgInputField.getText() != null && !msgInputField.getText().trim().isEmpty()) {
            try {
                var time = formatter.format(LocalDateTime.now());
                dos.writeUTF(msgInputField.getText());
                chatArea.append("Client [" + time + "] : " + msgInputField.getText() + "\n");
                msgInputField.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void prepareGUI() {
        JFrame frame = new JFrame("Chat client");
        frame.setBounds(600, 300, 400, 400);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        BorderLayout borderLayout = new BorderLayout();
        frame.setLayout(borderLayout);
        frame.setResizable(false);
        chatArea = new JTextArea(21, 20);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setBackground(new Color(190, 134, 243));

        msgInputField = new JTextField(29);
        var sendButton = new JButton("send");

        sendButton.addActionListener(e -> {
            send();
        });

        msgInputField.addActionListener(e -> {
            send();
        });

        frame.add(new JScrollPane(chatArea), BorderLayout.NORTH);
        frame.add(msgInputField, BorderLayout.WEST);
        frame.add(sendButton, BorderLayout.EAST);
        frame.setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new Client();
        });
    }
}
