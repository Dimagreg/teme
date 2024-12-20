package sharedcounter2;
class SynchronizedCounter {
    private int value;
    public SynchronizedCounter(int value){
        this.value = value;
    }
    public synchronized void increment(){
        value++;
    }
    public synchronized void  decrement(){
        value--;
    }
    public synchronized int getValue(){
        return value;
    }
}


class Incrementer implements Runnable {

    private SynchronizedCounter aSynchronizedCounter;
    private int repeats;

    public Incrementer(SynchronizedCounter aSynchronizedCounter, int repeats) {
        this.aSynchronizedCounter = aSynchronizedCounter;
        this.repeats = repeats;
    }

    @Override
    public void run() {
        for (int i=0; i<repeats; i++) {
            aSynchronizedCounter.increment();
        }
    }
}


public class CorrectSharedCounter2 {
    private static final int REPEATS = 1000000;

    public static void main(String[] args) {

        SynchronizedCounter theSynchronizedCounter = new SynchronizedCounter(0);

        Thread t1 = new Thread(new Incrementer(theSynchronizedCounter, REPEATS));
        Thread t2 = new Thread(new Incrementer(theSynchronizedCounter, REPEATS));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Counter value is "+ theSynchronizedCounter.getValue());
    }
}