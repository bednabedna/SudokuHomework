package sudokuhw;




import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 *
 * @author Federico Bednarski 1589903
 */
public class FindParallel extends RecursiveAction {
    
    private final ArrayList<Node> permutations, permutations2;
    private final int skip, threads;
    public ArrayList<Node> result = new ArrayList<>();
    
    public FindParallel(ArrayList<Node> permutations, ArrayList<Node> permutations2, int skip, int threads) {
        this.permutations = permutations;
        this.skip = skip;
        this.threads = threads;
        this.permutations2 = permutations2;
    }
    
    @Override
    protected void compute() {
        for (int i = skip; i < permutations.size(); i += threads) {
            Node col_1 = permutations.get(i);
            
            for (Node col_other_1 : permutations2) {
                if((col_1.value & col_other_1.value) != 0)
                    continue;
                
                ArrayList<Node> lvl_2 = null;

                for (Node col_2 : col_1.children) {
                    for (Node col_other_2 : col_other_1.children) {
                        if ((col_2.value & col_other_2.value) == 0) {

                            ArrayList<Node> lvl_3 = null;

                            for (Node col_3 : col_2.children) {
                                for (Node col_other_3 : col_other_2.children) {

                                    if ((col_3.value & col_other_3.value) == 0) {
                                        if (lvl_3 == null) {
                                            lvl_3 = new ArrayList<>(1);
                                        }
                                        lvl_3.add(new Node(col_3.value | col_other_3.value));
                                    }

                                }
                            }

                            if (lvl_3 != null) {
                                Node n2 = new Node(col_2.value | col_other_2.value);
                                n2.children = lvl_3;
                                if (lvl_2 == null) {
                                    lvl_2 = new ArrayList<>(1);
                                }
                                lvl_2.add(n2);
                            }
                        }
                    }
                }

                if (lvl_2 != null) {
                    Node n1 = new Node(col_1.value | col_other_1.value);
                    n1.children = lvl_2;
                    result.add(n1);
                }
            }
        }
    }
    
    public static ArrayList<Node> Execute(ArrayList<Node> permu, ArrayList<Node> permu2,boolean parallel) {
        int threads = parallel ? ForkJoinPool.commonPool().getParallelism() + 1 : 1;
        
        FindParallel[] tp = new FindParallel[threads];
        for (int i = 0; i < threads-1; i++) {
            tp[i] = new FindParallel(permu, permu2, i, threads);
            tp[i].fork();
        }
        tp[threads-1] = new FindParallel(permu, permu2, threads-1, threads);
        tp[threads-1].compute();
        ArrayList<Node> compatibles = tp[threads-1].result;
        
        for (int i = 0; i < threads-1; i++) {
            FindParallel findParallel = tp[i];
            findParallel.join();
            compatibles.addAll(findParallel.result);
        }
        return compatibles;
    }
}
