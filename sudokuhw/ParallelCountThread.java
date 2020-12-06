package sudokuhw;




import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

/**
 *
 * @author Federico Bednarski 1589903
 */
public class ParallelCountThread extends RecursiveAction {
    
    private final ArrayList<Node> permutations;
    private final int skip, threads;
    private final BitfieldSorter sorted;
    public long result = 0;
    
    public ParallelCountThread(ArrayList<Node> permu, BitfieldSorter sorted, int skip, int threads) {
        this.permutations = permu;
        this.skip = skip;
        this.threads = threads;
        this.sorted = sorted;
    }
    
    @Override
    protected void compute() {
        result = sorted.CountAllCompatibles(permutations, skip, threads);
    }
}
