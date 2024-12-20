/**
 * Greetings is a demo of creating a number of NTHREADS threads
 * and waiting for their completion
 * The odd threads are Hello threads, the even threads are Ciao threads
 */
package hello;

class Ciao implements Runnable {
    private final int externalID;

    public Ciao(int externalID) {
        this.externalID = externalID;
    }

    @Override
    public void run() {
        System.out.printf("Thread %s  with externalID=%d says Ciao!\n", Thread.currentThread().getName(), externalID);
    }
}

class Hello implements Runnable {
    private final int externalID;

    public Hello(int externalID) {
        this.externalID = externalID;
    }

    @Override
    public void run() {
        System.out.printf("Thread %s  with externalID=%d says Hello!\n", Thread.currentThread().getName(), externalID);
    }
}


public class Greetings {
    private static final int NTHREADS = 10;

    public static void main(String[] args) {
        Thread[] threads = new Thread[NTHREADS];
        for (int i = 0; i < NTHREADS; ++i) {
            if (i % 2 == 0) threads[i] = new Thread(new Ciao(i));
            else threads[i] = new Thread(new Hello(i));
        }
        System.out.println("Thread objects have been created but not yet started");

        for (int i = 0; i < NTHREADS; ++i) {
            threads[i].start();
        }

        System.out.println("Wait to join threads ...");
        for (int i = 0; i < NTHREADS; ++i) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("All threads joined!");
    }
}
