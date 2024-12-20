package prodcons;



import java.util.Random;


/**
 * A simple, bounded buffer that is not thread-safe
 * The buffer does not perform its own synchronisation
 * It works as a circular buffer
 */
class BoundedBuffer {
    private final Long[] elements;
    private final int capacity;
    private int in;
    private int out;
    private int count;

    public BoundedBuffer(int capacity) {
        this.capacity = capacity;
        this.elements = new Long[capacity];
        in = 0;
        out = 0;
        count = 0;
    }

    public boolean isEmpty() {
        return (count == 0);
    }

    public boolean isFull() {
        return (count == capacity);
    }

    public void add(long value) {
        elements[in] = value;
        in = (in + 1) % capacity;
        count++;
    }

    public long remove() {
        count--;
        long v = elements[out];
        out = (out + 1) % capacity;
        return v;
    }
}

/**
 * Producer thread is attached to a buffer of limited capacity.
 * Producer must make sure that the buffer is not full before adding something.
 * If the buffer is full, producer waits.
 */
class Producer extends Thread {
    private final BoundedBuffer buffer;

    public Producer(BoundedBuffer buffer) {
        this.buffer = buffer;
    }

    private long performComputation(long v) {
        long rez = v + 1;
        try {
            // simulate longer work
            sleep(new Random().nextInt(2));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.printf("[Producer] %d\n", rez);
        return rez;
    }

    @Override
    public void run() {
        long number = 0;

        while (true) {
            number = performComputation(number);
            synchronized (buffer) {
                while (buffer.isFull()) { //needs while,not if!!
                    try {
                        buffer.wait(); //producer is blocked
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                buffer.add(number);
                buffer.notifyAll(); // producer notifies blocked consumers
            }
        }
    }
}

/**
 * Consumer thread is attached to a buffer of limited capacity.
 * Consumer must make sure that the buffer is not empty before removing something.
 * If the buffer is empty, consumer waits until an element appears
 */
class Consumer extends Thread {
    private final int id;
    private final BoundedBuffer buffer;

    public Consumer(int id, BoundedBuffer buffer) {
        this.id = id;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        long number;
        while (true) {
            synchronized (buffer) {
                while (buffer.isEmpty()) { // needawhile,not if!!!
                    try {
                        buffer.wait(); // consumer is blocked
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                number = buffer.remove();
                buffer.notifyAll();  // consumer notifies blocked producers
            }
            performLongRunningComputation(number);
        }
    }

    private void performLongRunningComputation(long prime) {
        System.out.printf("[Consumer %d] %d\n", id, prime);

        try {
            // Thread.sleep "simulates" a longer-running computation
            // that uses the number
            sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/**
 * A driver program to test the Producer-Consumer scenario
 * One Producer and 3 Consumers are connected to a bounded buffer
 */
public class ProducerConsumer {
    public static void main(String[] args) {
        BoundedBuffer buffer = new BoundedBuffer(10);

        Producer producer = new Producer(buffer);
        producer.start();

        Consumer[] consumers = new Consumer[3];

        for (int i = 0; i < consumers.length; ++i) {
            consumers[i] = new Consumer(i, buffer);
            consumers[i].start();
        }
    }
}
