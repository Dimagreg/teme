package part1;

import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.StringBuilder;
import java.net.Socket;

public class MPIExec {
    public static void main(String[] args) {
        
        if (args.length < 4) {
            System.err.println("for local use: mpiexec -processes N port_1 port_2 .... port_N program.exe");
            System.err.println("for hosts use: mpiexec -hosts N  IPADDRESS_1 IPADDRESS_2 ....  IPADDRESS_N program.exe");
        }

        int NCount = Integer.parseInt(args[1]);
      
        // get ports or hosts
        List<String> portsOrHosts = new ArrayList<>();

        try {
            for (int i = 0; i < NCount; i++) {
                portsOrHosts.add(args[2 + i]);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.err.println("Invalid amount of ports or hosts. " + ex.getLocalizedMessage());
        }

        // get program + arguments
        StringBuilder programs = new StringBuilder();

        for (int i = (2 + NCount); i < args.length; i++) {
            programs.append(args[i]).append(" ");
        } 

        String program = programs.toString().trim();

        System.out.println(program);

        // local use
        if (args[0].equals("-processes")) {

            // start smpds
            System.out.printf("Starting %d SMPD processes...\n", NCount);

            List<Process> processList = new ArrayList<>();

            for (String port : portsOrHosts) {
                try {
                    Process process = new ProcessBuilder("java", "part1.Smpd", port).start();

                    processList.add(process);
                } catch (IOException ex) {
                    System.err.printf("Error starting SMPD on %d. %s", port, ex.getLocalizedMessage());
                }
            }
        }
        // hosts use
        else if (args[0].equals("-hosts")) {
            // noop
        }
        else {
            System.err.println("invalid operation: use -processes or -hosts");

            System.exit(1);
        }

        // use sockets to connect to smpds
        Thread[] threads = new Thread[NCount];

        System.out.println("Starting threads and passing program...");

        for (int i = 0; i < NCount; i++) {
            String host = args[0].equals("-processes") ? "localhost" : portsOrHosts.get(i);
            String port = args[0].equals("-processes") ? portsOrHosts.get(i) : "5010"; // set 5010 as port if using hosts

            threads[i] = new Thread(new SmpdTask(host, port, program));
            threads[i].start();
        }

        System.out.println("Threads started.");

        for (int i = 0; i < NCount; i++) {
            try {
                threads[i].join();
            
            } catch (InterruptedException ex) {
                ex.printStackTrace();

                threads[i].interrupt();
            }
        }

        System.out.println("Done.");
    }
}

class SmpdTask implements Runnable {
    private final String host;
    private final String port;
    private final String program;
    
    public SmpdTask(String host, String port, String program) {
        this.host = host;
        this.port = port;
        this.program = program;
    }

    @Override
    public void run() {
        for (int retries = 1; retries < 3; retries++) { // use retries if fail to connect
            try {
                Socket socket = new Socket(host, Integer.parseInt(port));

                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                printWriter.println(program);

                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println(host + ":" + port + " = " + line);
                }

                socket.close();

                return; // successful return

            } catch (NumberFormatException ex1) {
                System.err.println("Invalid port : " + port);
                
                Thread.currentThread().interrupt();
            
            } catch (IOException ex) {
                System.err.printf("Failed connecting to %s:%s, tries %d/%d. %s\n", host, port, retries, 3, ex.getLocalizedMessage());

                try {
                    Thread.sleep(2000); // wait 2s and try again
                
                } catch (InterruptedException ex1) {
                    ex1.printStackTrace();

                    Thread.currentThread().interrupt();
                }
            }
        }

        System.err.printf("Failed to connect to %s:%s, tries %d/%d\n", host, port, 3, 3);
    }
}