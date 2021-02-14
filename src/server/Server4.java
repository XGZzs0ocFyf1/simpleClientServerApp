package server;

import java.io.*;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server4 {

    private static final int PORT = 8082;


    private static void startServer() {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {


            System.out.printf("Server started on port %s\n", PORT);

            var socket = serverSocket.accept();

            System.out.printf("Client connected to port %s\n", PORT);
            var dis = new DataInputStream(socket.getInputStream());
            var dos = new DataOutputStream(socket.getOutputStream());

            //this thread handles messages from client
            Thread clientMessagePrinterThread = new Thread(() -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss");
                System.out.println("Thread for handling client messages started.");
                try {
                    while (true) {
                        var message = dis.readUTF();
                        if (message.equalsIgnoreCase("/stop")) {
                            System.out.println("Thread stopped by outer command /stop");
                            break;
                        }
                        var time = formatter.format(LocalDateTime.now());
                        System.out.printf("Client [%s] : %s \n", time, message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            });



            //this thread handles server messages for client
            Thread serverMessageInputThread = new Thread(() -> {
                System.out.println("Server message input thread started.");
                try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))){
                    while (true) {
                        var message = br.readLine();
                        dos.writeUTF(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

            clientMessagePrinterThread.start();
            serverMessageInputThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        startServer();
    }
}
