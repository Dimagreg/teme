package sharedcounter1;

class Counter {
    private int value;
    public Counter(int value){
        this.value = value;
    }
    public  void increment(){
        synchronized (this) {
            value++;
        }
        System.out.println("did increment");
    }
    public  void  decrement(){
        synchronized (this) {
            value--;
        }
        System.out.println("did decrement");
    }

    public  int getValue(){
        synchronized (this) {
            return value;
        }
    }
}


class Incrementer implements Runnable {

    private Counter aCounter;
    private int repeats;

    public Incrementer(Counter aCounter, int repeats) {
        this.aCounter = aCounter;
        this.repeats = repeats;
    }

    @Override
    public void run() {
        for (int i=0; i<repeats; i++) {
            aCounter.increment();
        }
    }
}





public class CorrectSharedCounter1 {
    private static final int REPEATS = 5;

    public static void main(String[] args) {

        Counter theCounter = new Counter(0);

        Thread t1 = new Thread(new Incrementer(theCounter, REPEATS));
        Thread t2 = new Thread(new Incrementer(theCounter, REPEATS));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Counter value is "+ theCounter.getValue());
    }
}