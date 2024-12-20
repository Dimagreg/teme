import java.util.Random;

class BlockingBoundedBuffer {
    private final Long[] elements;
    private final int capacity;
    private int in;
    private int out;
    private int count;

    public BlockingBoundedBuffer(int capacity) {
        this.capacity = capacity;
        this.elements = new Long[capacity];
        in = 0;
        out = 0;
        count = 0;
    }

    public synchronized boolean isEmpty() {
        return (count == 0);
    }

    public synchronized boolean isFull() {
        return (count == capacity);
    }

    private void _add(long value) {
        elements[in] = value;
        in = (in + 1) % capacity;
        count++;
    }

    public synchronized void add(long value) {
            while (this.isFull()) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            _add(value);
            this.notifyAll();
    }

    private long _remove() {
        count--;
        long v = elements[out];
        out = (out + 1) % capacity;
        return v;
    }


    public synchronized  long remove() {
            while (this.isEmpty()) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            long number = _remove();
            this.notifyAll();
            return number;
        }
}

/**
 * Producer thread attached to a threadsafe blocking buffer
 */
class Producer extends Thread {
    private final BlockingBoundedBuffer buffer;

    public Producer(BlockingBoundedBuffer buffer) {
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
            //synchronization is handled by the buffer
            buffer.add(number);
        }
    }
}

/**
 * Consumer thread is attached to a threadsafe blocking buffer
 */
class Consumer extends Thread {
    private final int id;
    private final BlockingBoundedBuffer buffer;

    public Consumer(int id, BlockingBoundedBuffer buffer) {
        this.id = id;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        long number;
        while (true) {
            // synchronization is done by buffer
            number = buffer.remove();
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
 * Driver program totest Producer-Consumer scenario
 */
public class BlockingArrayProdCons {
    public static void main(String[] args) {
        BlockingBoundedBuffer buffer = new BlockingBoundedBuffer(10);

        Producer producer = new Producer(buffer);
        producer.start();

        Consumer[] consumers = new Consumer[3];

        for (int i = 0; i < consumers.length; ++i) {
            consumers[i] = new Consumer(i, buffer);
            consumers[i].start();
        }
    }
}
