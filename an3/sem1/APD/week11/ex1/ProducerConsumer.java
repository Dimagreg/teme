package ex1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

class Producer extends Thread {
    private final BlockingQueue<String> buffer;
    private final int producerId;

    public Producer(BlockingQueue<String> buffer, int producerId) {
        this.buffer = buffer;
        this.producerId = producerId;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int randomValue = ThreadLocalRandom.current().nextInt(1, 101);
                String item = "Value: " + randomValue + " from Producer: " + producerId;
                buffer.put(item); // Put the item in the buffer
                System.out.println("Producer " + producerId + " produced: " + item);
                Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Producer " + producerId + " interrupted.");
        }
    }
}

// Consumer thread
class Consumer extends Thread {
    private final BlockingQueue<String> buffer;
    private final int consumerId;

    public Consumer(BlockingQueue<String> buffer, int consumerId) {
        this.buffer = buffer;
        this.consumerId = consumerId;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String item = buffer.take();
                System.out.println("Consumer " + consumerId + " consumed: " + item);
                Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Consumer " + consumerId + " interrupted.");
        }
    }
}

public class ProducerConsumer {
    public static void main(String[] args) {
        BlockingQueue<String> buffer = new LinkedBlockingQueue<>(5);

        Thread producer1 = new Thread(new Producer(buffer, 1));
        Thread producer2 = new Thread(new Producer(buffer, 2));

        Thread consumer1 = new Thread(new Consumer(buffer, 1));
        Thread consumer2 = new Thread(new Consumer(buffer, 2));

        producer1.start();
        producer2.start();
        consumer1.start();
        consumer2.start();

        try {
            producer1.join();
            producer2.join();
            consumer1.join();
            consumer2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Main thread interrupted.");
        }
    }
}
