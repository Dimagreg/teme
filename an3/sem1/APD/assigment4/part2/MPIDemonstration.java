package part2;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MPIDemonstration {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("use : java MPIDemo rank port ports");
            System.exit(1);
        }

        int rank = 0, port = 0;

        try {
            rank = Integer.parseInt(args[0]);
            port = Integer.parseInt(args[1]);
        
        } catch (NumberFormatException ex) {
            ex.printStackTrace();

            System.exit(1);
        }

        List<Integer> portsList = new ArrayList<>();

        for (int i = 2; i < args.length; i++) {
            try {
                portsList.add(Integer.parseInt(args[i]));
            
            } catch (NumberFormatException ex) {
                System.err.println("Invalid port format. " + ex.getLocalizedMessage());

                System.exit(1);
            }
        }

        try {
            MPI.init(rank, port, portsList); // init with blocking -> will not succed if not connected to all ports

            System.out.println("Comm rank: " + MPI.comm_rank());
            System.out.println("Comm size: " + MPI.comm_size());

            // test with 2 processes: 0 and 1
            if (MPI.comm_rank() == 0) {
                MPI.send(1, "Hello from process 0");
                
                System.out.println("Process 0 received: " +  MPI.receive()); // receive with blocking

            } else if (MPI.comm_rank() == 1) {
                System.out.println("Process 1 received: " + MPI.receive()); // receive with blocking

                MPI.send(0, "Hello from process 1");
            }
    
            // Finalize MPI
            MPI.finalizeMPI();
        
        } catch (IOException ex) {
            ex.printStackTrace();
        
        } catch (InterruptedException ex1) {
            ex1.printStackTrace();
        }
    }
}
