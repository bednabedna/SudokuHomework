package sudokuhw;



import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 *
 * @author Federico Bednarski 1589903
 */
public class ParallelCount extends RecursiveAction {
    private final ArrayList<Node> permutations;
    private final BitfieldSorter sorted;
    long solutions;

    public ParallelCount(ArrayList<Node> permu, BitfieldSorter sorted) {
        this.permutations = permu;
        this.sorted = sorted;
    }

    @Override
    protected void compute() {
        int threads = ForkJoinPool.commonPool().getParallelism() + 1;

        ParallelCountThread[] tp = new ParallelCountThread[threads];
        for (int i = 0; i < threads-1; i++) {
            tp[i] = new ParallelCountThread(permutations, sorted, i, threads);
            tp[i].fork();
        }
        
        tp[threads-1] = new ParallelCountThread(permutations, sorted, threads-1, threads);
        tp[threads-1].compute();
        solutions = tp[threads-1].result;
        
        for (int i = 0; i < threads-1; i++) {
            ParallelCountThread countParallel = tp[i];
            countParallel.join();
            solutions += countParallel.result;
        }
    }
    
    
    
}
