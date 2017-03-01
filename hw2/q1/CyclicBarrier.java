/*
 * EID's of group members
 * XL5432
 * CSL735
 */

import java.util.concurrent.Semaphore; // for implementation using Semaphores
import java.util.concurrent.atomic.AtomicInteger;

public class CyclicBarrier {
    int parties;
    AtomicInteger arrived = new AtomicInteger(0);
    final Semaphore tickets;
    final Semaphore lock;
    public CyclicBarrier(int parties){
        this.tickets = new Semaphore(parties);
        this.parties = parties;
        this.lock = new Semaphore(0);
    }

    public int await() throws InterruptedException {
        tickets.acquire();
        arrived.incrementAndGet();
        int index = parties - arrived.get();
        tickets.release();

        if (arrived.get() < parties) {
            if(lock.availablePermits() != 0){
                lock.acquire();
            }
        } else {
            lock.release(1);
            arrived.set(0);
        }

        return index;
    }

}
