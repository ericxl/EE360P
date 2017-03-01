/*
 * EID's of group members
 * XL5432
 * CSL735
 */
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Garden {
    private static final int MAX_NONSEEDED = 4;
    private static final int MAX_NONFILLED = 8;

    private final ReentrantLock seedLock;
    private final ReentrantLock shovelLock;

    private final Condition holeReadyToSeed;
    private final Condition holeReadyToFill;
    private final Condition readyToDig;

    private final AtomicInteger numHolesDug;
    private final AtomicInteger numHolesSeeded;
    private final AtomicInteger numHolesFilled;
    
  public Garden() {
      seedLock = new ReentrantLock();
      shovelLock = new ReentrantLock();

      holeReadyToSeed = seedLock.newCondition();
      holeReadyToFill = shovelLock.newCondition();
      readyToDig = shovelLock.newCondition();

      numHolesDug = new AtomicInteger(1);
      numHolesSeeded = new AtomicInteger(1);
      numHolesFilled = new AtomicInteger(1);
  };
  public void startDigging() throws InterruptedException {
      shovelLock.lock();
      while(numHolesDug.get() - numHolesFilled.get() >= MAX_NONFILLED || numHolesDug.get() - numHolesSeeded.get() >= MAX_NONSEEDED){
          readyToDig.await();
      }
  };

  public void doneDigging() {
      int dug = numHolesDug.incrementAndGet();
      int seeded = numHolesSeeded.get();

      shovelLock.unlock();

      if (dug > seeded) {
          seedLock.lock();
          try {
              holeReadyToSeed.signal();
          }finally {
              seedLock.unlock();
          }
      }
  };

  public void startSeeding() throws InterruptedException{
      seedLock.lock();
      while (numHolesDug.get() <= numHolesSeeded.get()){
          holeReadyToSeed.await();
      }
  };

  public void doneSeeding() {
      int seeded = numHolesSeeded.incrementAndGet();
      int filled = numHolesFilled.get();
      int dug = numHolesDug.get();

      seedLock.unlock();
      shovelLock.lock();

      try {
          if (seeded > filled) {
              holeReadyToFill.signal();
          }

          if (dug - seeded < MAX_NONSEEDED) {
              readyToDig.signal();
          }
      }finally {
          shovelLock.unlock();
      }
  };
  public void startFilling() throws InterruptedException{
      shovelLock.lock();

      while (numHolesSeeded.get() <= numHolesFilled.get()){
          holeReadyToFill.await();
      }
  };

  public void doneFilling() {
      numHolesFilled.incrementAndGet();
      readyToDig.signal();
      shovelLock.unlock();
  };

   public int totalDug() {  return numHolesDug.get(); };
   public int totalSeeded() { return numHolesSeeded.get();  };
   public int totalFilled() {  return numHolesFilled.get(); };
}
