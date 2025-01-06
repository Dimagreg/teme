package part1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Smpd {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Invalid amount of arguments");
            System.exit(1);
        }

        int port = 0;

        try {
            port = Integer.parseInt(args[0]);
        
        } catch (NumberFormatException ex) {
            System.err.println("Invalid port format. " + ex.getLocalizedMessage());

            System.exit(1);
        }

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.printf("Server started. Listening on port %d...\n", port);
            
            Socket socket = serverSocket.accept();
            System.out.println("Client connected!");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            String program = bufferedReader.readLine();

            System.out.println("Message: " + program);

            // split the program into arguments
            String[] programParts = program.split("\\s+");
            ProcessBuilder processBuilder = new ProcessBuilder(programParts);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // read output and send to client via socket
            BufferedReader programOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = programOutput.readLine()) != null) {
                out.println(line);
            }

            process.waitFor(); // wait for process termination
            serverSocket.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
