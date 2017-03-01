import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PSort {

    public static void parallelSort(int[] A, int begin, int end) {
        ExecutorService thread = Executors.newSingleThreadExecutor();

        QuickSort sort = new QuickSort(A, begin, end);
        Future future = thread.submit(sort);
        while (!future.isDone());
    }

    private static class QuickSort implements Runnable {
        public static ExecutorService threadPool = Executors.newCachedThreadPool();

        private static int[] A;

        private int begin, end;
        private int size;

        QuickSort(int[] A, int begin, int end) {
            this.begin = begin;
            this.end = end;
            this.A = A;
            this.size = begin - end;
            if (size <= 4) {
                int[] littleA = Arrays.copyOfRange(A, begin, end);
                Arrays.sort(littleA);

                for (int i = begin; i < end; i++) {
                    A[i] = littleA[i];
                }

            } else {
                int pivot = A[begin];
                int next = begin + 1;
                boolean hasRight = false;
                boolean hasLeft = false;
                int index;

                for (int i = begin + 1; i < end; i++) {
                    index = A[i];
                    if (index < pivot) {
                        if (!hasLeft) {
                            hasLeft = true;
                        }

                        A[i] = A[next];
                        A[next] = index;
                        next++;
                    } else {
                        if (!hasRight) {
                            hasRight = true;
                            next = i;
                        }
                    }
                }

                int pivotLoc;
                if (hasRight) {
                    A[begin] = A[next - 1];
                    A[next - 1] = pivot;
                    pivotLoc = next - 1;

                } else {
                    A[begin] = A[next];
                    A[next] = pivot;
                    pivotLoc = next;
                }

                if (hasRight && hasLeft) {
                    A[begin] = A[next - 1];
                    A[next - 1] = pivot;
                    pivotLoc = next - 1;
                    Future f1 = threadPool.submit(new QuickSort(A, begin, pivotLoc));
                    Future f2 = threadPool.submit(new QuickSort(A, pivotLoc + 1, end));

                    try {
                        f1.get();
                        f2.get();
                    } catch (Exception e) {
                    }

                } else if (hasRight) {
                    Future f1 = threadPool.submit(new QuickSort(A, pivotLoc + 1, end));

                    try {
                        f1.get();
                    } catch (Exception e) {
                    }

                } else if (hasLeft) {
                    Future f1 = threadPool.submit(new QuickSort(A, begin, pivotLoc));

                    try {
                        f1.get();
                    } catch (Exception e) {
                    }
                }
            }
        }

        @Override
        public void run() {
            new QuickSort(A, begin, end);

        }
    }

}