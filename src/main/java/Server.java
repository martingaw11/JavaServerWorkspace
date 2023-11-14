import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import models.GsonDataModel;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8049);
        System.out.println("Server is running...");
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    String received;
                    while ((received = in.readLine()) != null) {
                        if (received.equals("exit")) {
                            System.out.println("Client has left the chat.");
                            out.println("Exit Successful.");
                            break;
                        }
                        else if (received.equals("Brakes: On")) {
                            System.out.println("Brakes Engaged!!");
                            out.println("Brakes Engaged!!");
                        }
                        else if (received.equals("Brakes: Off")) {
                            System.out.println("Brakes Disengaged...");
                            out.println("Brakes Disengaged...");
                        }
                        else if (received.substring(0, 7).equals("Speed: ")) {
                            System.out.println("Speed: " + received.substring(7));
                            out.println("Speed confirmed: " + received.substring(7));
                        }
                        else {
                            System.out.println("Message from client: " + received);
                            out.println("Not a Valid Command! (" + received + ")");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    clientSocket.close();
                }
            }
        } finally {
            serverSocket.close();
        }
    }
}
