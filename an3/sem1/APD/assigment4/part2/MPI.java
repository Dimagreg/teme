package part2;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class MPI {
    private static int rank;
    private static int size;
    private static ServerSocket serverSocket;
    private static Map<Integer, Socket> socketMap = new HashMap<>(); //save process rank with its socket
    private static LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<>(); // use thread-safe queue with blocking on take
    private static Thread serverTaskThread;
    private static List<Thread> clientThreads = new ArrayList<>();
    private static volatile boolean running = true; // use volatile to sync with all threads running to stop if finalize called

    public static int comm_size() {
        return size;
    }

    public static int comm_rank() {
        return rank;
    }

    public static void init(int rank, int port, List<Integer> portsList) throws IOException {
        MPI.rank = rank;
        MPI.size = portsList.size();

        // start server socket
        serverSocket = new ServerSocket(port);
        
        // start listening on port 
        serverTaskThread = new Thread(new ServerTask(serverSocket));
        serverTaskThread.start();

        // start connection with other ports with retries
        for (int i = 0; i < portsList.size(); i++) {
            if (i != rank) {
                int retries = 3;

                boolean connected = false;

                while (retries > 0 && !connected) {
                    try {
                        Socket socket = new Socket("127.0.0.1", portsList.get(i)); // use case for localhost only
                        
                        socketMap.put(i, socket);
                        
                        connected = true;
                        
                        System.out.println("Connected to process " + i + " on port " + portsList.get(i));
                    
                    } catch (IOException e) {
                        retries--;

                        System.err.println("Error connecting to process " + i + " on port " + portsList.get(i) + ": " + e.getMessage());

                        if (retries > 0) {
                            System.out.println("Retrying connection to process " + i + "...");
                            
                            try {
                                Thread.sleep(5000); // 5-second delay before retrying
                            
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }

                        } else {
                            System.err.println("Failed to connect to process " + i);
                        }
                    }
                }
            }
        }

        System.out.println("Process " + rank + " initialized.");
    }

    private static void getClientMessage(Socket clientSocket) { //reads messages received from client and put in queue
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String message;
            
            while ((running && (message = in.readLine()) != null)) { // read message if not finalized
                messageQueue.add(message);
            }

        } catch (IOException e) {
            if (running) {
                System.err.println("Error reading message: " + e.getMessage());
            }
        }
    }

    public static void send(int rank, String message) throws IOException {
        Socket socket = socketMap.get(rank);

        if (socket == null) {
            throw new IOException("No connection to process " + rank);
        }
        
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println(message);
        
        System.out.println("Process " + rank + " sent to " + rank + ": " + message);
    }

    public static String receive() throws InterruptedException {
        String message = messageQueue.take(); //blocking

        return message;
    }

    public static void finalizeMPI() throws IOException {
        running = false;

        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }

        for (Socket socket : socketMap.values()) {
            if (!socket.isClosed()) {
                socket.close();
            }
        }

        // close server thread
        try {
            serverTaskThread.join();
        
        } catch (InterruptedException ex) {
            ex.printStackTrace();

            Thread.currentThread().interrupt();
        }

        // close clients' threads
        for (Thread thread : clientThreads) {
            try {
                thread.join();
            
            } catch (InterruptedException ex) {
                ex.printStackTrace();

                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Process " + rank + " finalized.");
    }

    private static class ServerTask implements Runnable {
        private final ServerSocket serverSocket;

        public ServerTask(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            try {
                while (running) { //while not mpi finalized
                    Socket clientSocket = serverSocket.accept();
                    
                    Thread clientThread = new Thread(() -> getClientMessage(clientSocket));
                    clientThread.start();
                    
                    clientThreads.add(clientThread);
                }
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                    
                    System.err.println("Server error: " + e.getMessage());
                }
            }
        }
    }
}
