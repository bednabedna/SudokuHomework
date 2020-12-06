package sudokuhw;



import java.util.ArrayList;

/**
 *
 * @author Federico Bednarski 1589903
 */
public class BitfieldSorter {
    public static final int MAX_BITS = 27;
    private ArrayList<Node> values = null;
    private int bit;
    private BitfieldSorter withoutBit = null, withBit = null;
    
    public BitfieldSorter(ArrayList<Node> values) {
        this(values, 0);
    }
    
    public BitfieldSorter(ArrayList<Node> values, int bit) {
        ArrayList<Node> valuesWithBit = null, valuesWithoutBit = null;
        this.bit = bit;
        
        if(values.size() > 1)
            while(this.bit < MAX_BITS) {

                valuesWithBit = new ArrayList<>();
                valuesWithoutBit = new ArrayList<>();

                for (Node node : values) {
                    if( (node.value >> this.bit) % 2 == 1)
                        valuesWithBit.add(node);
                    else
                        valuesWithoutBit.add(node);
                }

                if(valuesWithBit.isEmpty() || valuesWithoutBit.isEmpty())
                    this.bit++;
                else
                    break;
            }
        else
            this.bit = MAX_BITS;
        
        if(this.bit >= MAX_BITS) {
            this.values = values;
            return;
        }
        
        withBit = new BitfieldSorter(valuesWithBit, this.bit+1);
        withoutBit = new BitfieldSorter(valuesWithoutBit, this.bit+1);
    }
    
    public long CountAllCompatibles(ArrayList<Node> others, int skip, int step) {
        
        if(isLeaf()) {
            return this.CountAllCompatibles(others, false);
        } else {
            ArrayList<Node> nodesWithoutBit = new ArrayList<>(), myNodes = new ArrayList<>();
            for (int i = skip; i < others.size(); i += step) {
                Node other = others.get(i);
                myNodes.add(other);
                if( (other.value >> bit) % 2 == 0)
                    nodesWithoutBit.add(other);
            }
            CountAllThread task = new CountAllThread(myNodes, withoutBit);
            task.fork();
            long res = withBit.CountAllCompatibles(nodesWithoutBit, true);

            task.join();
            return res + task.result;
        }
        
    }
    
    public long CountAllCompatibles(ArrayList<Node> others, boolean dividiEtImpera) {
        
        if(isLeaf()) {
            long count = 0;
            
            for (Node col_1 : others) {
                if ((col_1.value & values.get(0).value) != 0)
                    // il primo bitfield di col_other_1 e' sempre uguale
                    continue;
                for (Node col_other_1 : values) {
                    for (Node col_2 : col_1.children) {
                        for (Node col_other_2 : col_other_1.children) {
                            if ((col_2.value & col_other_2.value) == 0) {

                                for (Node col_3 : col_2.children) {
                                    for (Node col_other_3 : col_other_2.children) {

                                        if ((col_3.value & col_other_3.value) == 0) {
                                            count++;
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
            return count;
            
        } else {
            ArrayList<Node> nodesWithoutBit = new ArrayList<>();
            for (Node other : others) {
                if( (other.value >> bit) % 2 == 0)
                    nodesWithoutBit.add(other);
            }
            if(!dividiEtImpera)
                return withBit.CountAllCompatibles(nodesWithoutBit, false) + withoutBit.CountAllCompatibles(others, false);
            
            CountAllThread task = new CountAllThread(others, withoutBit);
            task.fork();
            long res = withBit.CountAllCompatibles(nodesWithoutBit, true);
            task.join();
            return res + task.result;
        }
        
    }
    
    boolean isLeaf() {
        return withBit == null;
    }
    
}
