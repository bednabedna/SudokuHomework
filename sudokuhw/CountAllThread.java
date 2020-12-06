package sudokuhw;



import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

/**
 *
 * @author User
 */
public class CountAllThread extends RecursiveAction {
    ArrayList<Node> others;
    BitfieldSorter sorter;
    long result = -1;

    public CountAllThread(ArrayList<Node> others, BitfieldSorter sorter) {
        this.others = others;
        this.sorter = sorter;
    }
    
    @Override
    protected void compute() {
        result = sorter.CountAllCompatibles(others, true);
    }
    
}
