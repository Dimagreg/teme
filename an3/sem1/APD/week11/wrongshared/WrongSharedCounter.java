package wrongshared;
class Counter {
    private int value;
    public Counter(int value){
        this.value = value;
    }
    public void increment(){
        value++;
    }
    public void decrement(){
        value--;
    }
    public int getValue(){
        return value;
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


public class WrongSharedCounter {

    private static int REPEATS=1000000;
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

        System.out.println("Counter value is "+theCounter.getValue());
    }
}