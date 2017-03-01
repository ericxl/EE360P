/*
 * EID's of group members
 * XL5432
 * CSL735
 */
import java.util.concurrent.locks.ReentrantLock;
public class MonitorCyclicBarrier {
    int parties;
    int numLeft;
    final ReentrantLock lock = new ReentrantLock();
	public MonitorCyclicBarrier(int parties) {
        this.numLeft = parties;
        this.parties = parties;
	}
	
    public synchronized int await() throws InterruptedException {
        lock.lock();
        numLeft --;
        int index = numLeft;
        lock.unlock();
        try {
            if (index > 0) {
                wait();
            } else {
                numLeft = parties;
                notifyAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

        }
        return index;
    }
}
