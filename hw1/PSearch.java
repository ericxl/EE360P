//UT-EID=


import java.util.*;
import java.util.concurrent.*;

public class PSearch implements Callable<Integer>{
    private int[] array;
    private int k;
    private int start;
    private int end;
    
    public PSearch(int[] A, int k, int start, int end) {
        this.array = A;
        this.k = k;
        this.start = start;
        this.end = end;
    }
    
  public static int parallelSearch(int k, int[] A, int numThreads){
      if(A == null || A.length == 0) return -1;
      int threads = numThreads > A.length ? A.length : Math.max(1, numThreads);
      int cap = A.length / threads;
      
      List<PSearch> callables = new ArrayList<PSearch>();
      for (int i = 0; i < threads; i++) {
          int startIndex = i * cap;
          int endIndex = i == threads - 1 ? A.length - 1 : startIndex + cap - 1;
          callables.add(new PSearch(A, k, startIndex, endIndex));
      }
      
      try {
          ExecutorService es = Executors.newCachedThreadPool();
          List<Future<Integer>> results = es.invokeAll(callables);
          es.shutdown();
          
          for (int i = 0; i < results.size(); ++i) {
              int index = results.get(i).get();
              if (index != -1) {
                  return index;
              }
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
      
      return -1;
  }
    
    @Override
    public Integer call(){
        for (int i = this.start; i <= this.end; i++) {
            if (this.array[i] == this.k) {
                return i;
            }
        }
        return -1;
    }
}
