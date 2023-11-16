import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import models.GsonDataModel;

public class Server {

    private static GsonDataModel sharedDataModel = new GsonDataModel();
    private static boolean isClientReady = false;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8049);
        System.out.println("Server is running...");

        // Create a single-threaded executor for the data sending task
        ExecutorService dataSendingExecutor = Executors.newSingleThreadExecutor();

        // Create a single-threaded executor for logging GsonDataModel
        // ExecutorService loggerExecutor = Executors.newSingleThreadExecutor();

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();

                // Create a single-threaded executor for handling each client connection
                ExecutorService clientHandlerExecutor = Executors.newSingleThreadExecutor();

                clientHandlerExecutor.submit(() -> {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                        System.out.println("Connection Status: " + isClientReady);
                        // Wait for a ready signal from client
                        while (!isClientReady) {
                            String readySignal = in.readLine();
                            isClientReady = readySignal != null && readySignal.trim().equals("ready");
                            System.out.println("Connection Status: " + isClientReady);
                        }

                        String received;
                        while ((received = in.readLine()) != null) {
                            handleClientCommand(received, clientSocket, dataSendingExecutor);
                        }
                    } 
                    catch (SocketException se) {
                        System.out.println("Breaking Client->Comand Connection...");
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    } 
                    finally {
                        try {
                            clientSocket.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // Start a separate thread for continuously sending GsonDataModel to the client
                dataSendingExecutor.submit(() -> {
                    try {
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                        while (!isClientReady) {
                            System.out.println("Waiting for Client to be ready...");
                            Thread.sleep(70);
                        }
                        while (isClientReady) {
                            sendGsonDataModelToClient(sharedDataModel, out);
                            // Adjust the sleep duration based on your requirements
                            Thread.sleep(70);
                        }
                    }
                    catch (SocketException se) {
                        System.out.println("Breaking Command->Client Connection...");
                    }
                    catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                });

                // Start a separate thread for logging GsonDataModel
                // loggerExecutor.submit(() -> {
                //     try {
                //         while (true) {
                //             logGsonDataModel(sharedDataModel);
                //             // Adjust the sleep duration based on your requirements
                //             Thread.sleep(250); // Logging every 0.250 seconds as an example
                //         }
                //     } catch (InterruptedException e) {
                //         e.printStackTrace();
                //     }
                // });
            }
        } 
        finally {
            serverSocket.close();
            // Shutdown the executors when the server exits
            dataSendingExecutor.shutdown();
            // loggerExecutor.shutdown();
        }
    }

    private static void handleClientCommand(String command, Socket clientSocket, ExecutorService dataSendingExecutor) {
        // Implement your command handling logic here
        // Update sharedDataModel accordingly
        // Example:
        if (command.equals("exit")) {
            System.out.print("Client has disconnected...");
            isClientReady = false;
            try {
                clientSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            // dataSendingExecutor.shutdown();
            System.out.println(" Successfully.");
        } 
        else if (command.equals("Brakes: On")) {
            System.out.println("Brakes Engaged!!");
            // out.println("Brakes Engaged!!");
            sharedDataModel.toggleBrakes(true);
        } 
        else if (command.equals("Brakes: Off")) {
            System.out.println("Brakes Disengaged...");
            // out.println("Brakes Disengaged...");
            sharedDataModel.toggleBrakes(false);
        } 
        else if (command.substring(0, 7).equals("Speed: ")) {
            System.out.println("Speed: " + command.substring(7));
            // out.println("Speed Confirmed: " + command.substring(7));
            sharedDataModel.setSpeed(Integer.parseInt(command.substring(7)));
        } 
        else if (command.equals("Accelerate")) {
            System.out.println("Accelerating...");
            sharedDataModel.toggleBrakes(false);
            sharedDataModel.incrementSpeed();
        }
        else if (command.equals("Decelerate")) {
            System.out.println("Decelerating...");
            sharedDataModel.toggleBrakes(false);
            sharedDataModel.decrementSpeed();
        }
        else {
            System.out.println("Invalid Command from Client: " + command);
            // out.println("Not a Valid Command! (" + command + ")");
        }
    }

    private static void sendGsonDataModelToClient(GsonDataModel dataModel, PrintWriter out) {
        // Implement logic to send GsonDataModel to the client
        // Use the provided PrintWriter to send data
        // Example:
        // out.println(dataModel.toJson()); // Assuming toJson() is a method in GsonDataModel
        out.println(dataModel.toJson());
    }

    // private static void logGsonDataModel(GsonDataModel dataModel) {
    //     // Implement logic to log GsonDataModel to a file or another destination
    //     // Example:
    //     System.out.println("Logging GsonDataModel: " + dataModel.toJson());
    // }
}
