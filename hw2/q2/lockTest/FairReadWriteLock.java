package lockTest;

/*
 * EID's of group members
 * XL5432
 * CSL735
 */
import java.util.PriorityQueue;
public class FairReadWriteLock {
    final PriorityQueue<Long> readerQueue = new PriorityQueue<>();
    final PriorityQueue<Long> writerQueue = new PriorityQueue<>();
    ThreadLocal<Long> ts = new ThreadLocal<>();

    public synchronized void beginRead(){
        ts.set(new Long(System.currentTimeMillis()));
        readerQueue.add(ts.get());
        while(!writerQueue.isEmpty() && (ts.get() > writerQueue.peek())){
            try{
                this.wait();
            } catch(InterruptedException e){
                e.printStackTrace();
            }
            finally {

            }
        }
    }

    public synchronized void endRead(){
        readerQueue.remove(ts.get());
        notifyAll();
    }

    public synchronized void beginWrite(){
        ts.set(new Long(System.currentTimeMillis()));
        writerQueue.add(ts.get());
        while((!writerQueue.isEmpty() && (ts.get() > writerQueue.peek())) || (!readerQueue.isEmpty() && (ts.get() > readerQueue.peek()))){
            try{
                this.wait();
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
            finally {

            }
        }
    }

    public synchronized void endWrite(){
        writerQueue.remove(ts.get());
        notifyAll();
    }
}
	
