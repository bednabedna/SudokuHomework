package sudokuhw;



import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

/**
 *
 * @author Federico Bednarski 1589903
 */
public class SudokuSolution {
    public int rowBitfieldsCount, columnBitfieldsCount;
    public ArrayList<Node> permutations;
    // genera le permutazioni della prima riga o colonna di un bitfield
    // un bitfield ha 3 righe o 3 colonne
    private int [] GeneratePartialBitfieldPermutations() {
        int [] result = new int[84];
        int count = 0;
        for (int i = 0; i < 7; i++) {
            int mask_1 = 1 << i;
            for (int j = i + 1; j < 8; j++) {
                int mask_2 = 1 << j;
                for (int k = j + 1; k < 9; k++) {
                    result[count] = mask_1 | mask_2 | (1 << k);
                    count++;
                }
            }
        }
        return result;
    }
    // il risultato e' prima riga cosi' com'e' 
    // con seconda riga (a 9 bit dal primo) se non ha valori in comune
    // la terza riga ha una sola scelta (3 valori per 3 posizioni)
    // ed e' quindi composta dai valori non presenti nelle prime 2 (a 18 bit dal primo, fino al 27esimo)
    private ArrayList<Node> GenerateBitfieldPermutations() {
        int [] row_1 = GeneratePartialBitfieldPermutations(); // 84 possibili prime righe
        
        ArrayList<Node> result = new ArrayList<>(1680);
        
        for (int bfr1 : row_1)
            for (int bfr2 : row_1)
                if( (bfr1 & bfr2) == 0 ) {
                    int value = bfr1 | (bfr2 << 9) | ((~(bfr1 | bfr2) & 0b111111111) << 18);
                    result.add(new Node(value));
                }
        
        return result;
    }
    // dato il bitfield di riga genera tutti quell idi colonna legali per lo square
    // eviceversa, dato quello di colonna genera quelli di riga
    private ArrayList<Node> GenerateOppositeBitfieldPermutations(int bitfield) {
        ArrayList<Node> oppositeBitfields = new ArrayList<>(216);
        // ogni riga e' permutata indipendentemente dalle altre in 3 colonne, quindi 6 permutazioni
        // tre righe quindi 6*6*6 = 216
        int[] righe = new int[9];
        int c = 0;
        // estraiamo i bits dalle righe
        for (int i = 0; i < 27; i++) {
            if( (bitfield >> i) % 2 == 1) {
                righe[c] = i % 9;
                c++;
            }      
        }
        
        int[][] perm3 = {{0,1,2},{0,2,1},{1,0,2},{1,2,0},{2,0,1},{2,1,0}};
        
        for (int i = 0; i < 6; i++) {
            int[] p1 = perm3[i];
            
            for (int j = 0; j < 6; j++) {
                int[] p2 = perm3[j];
                
                for (int k = 0; k < 6; k++) {
                    int[] p3 = perm3[k];
                    int col_1 = (1 << righe[p1[0]]) | (1 << righe[p2[0] + 3]) | (1 << righe[p3[0] + 6]);
                    int col_12 = col_1 | (((1 << righe[p1[1]]) | (1 << righe[p2[1] + 3]) | (1 << righe[p3[1] + 6])) << 9);
                    int col_123 = col_12 | (((1 << righe[p1[2]]) | (1 << righe[p2[2] + 3]) | (1 << righe[p3[2] + 6])) << 18);
                    oppositeBitfields.add(new Node(col_123));
                }
            }
        }
        
        return oppositeBitfields;
    }
    
    public SudokuSolution() {
        rowBitfieldsCount = columnBitfieldsCount = 1;
        // generiamo tutte e 1680 le permutazioni del bitfield di riga di un quadrato
        permutations = GenerateBitfieldPermutations();
        // aggiungiamo i nodi con le colonne per ogni riga
        for (Node row : permutations)
            row.children = GenerateOppositeBitfieldPermutations(row.value);
    }
    
    public SudokuSolution(int r, int c) {
        rowBitfieldsCount = r;
        columnBitfieldsCount = c;
    }
    
    public SudokuSolution copy() {
        SudokuSolution s = new SudokuSolution(rowBitfieldsCount, columnBitfieldsCount);
        if(permutations != null) {
            s.permutations = new ArrayList<>(permutations.size());
            for (Node node : permutations)
                s.permutations.add(node.copy());
        }
        return s;
    }
    
    public long count() {
        long count = 0;
        if(permutations != null)
            for (Node permutation : permutations)
                count += permutation.count();
        return count;
    }
    
    public SudokuSolution filterByContent(int bitfieldRow, int bitfieldCol) {
        if(rowBitfieldsCount != 1 || columnBitfieldsCount != 1)
            throw new Error("Questa soluzione non e' un quadrato!");
        
        // tutte le permutazioni sono compatibili (bitfields vuoti)
        if (bitfieldRow + bitfieldCol == 0)
            return copy();
        
        ArrayList<Node> filtered = new ArrayList<>(permutations.size());
        
        for (Node row : permutations) {
            if((row.value & bitfieldRow) == bitfieldRow) {
                Node n = new Node(row.value);
                n.children = new ArrayList<>(row.children.size());
                
                for (Node col : row.children)
                    if((col.value & bitfieldCol) == bitfieldCol)
                        n.children.add(new Node(col.value));
                
                if(n.children.size() > 0)
                    filtered.add(n);
             }
        }
        
        SudokuSolution copy = new SudokuSolution(1, 1);
        copy.permutations = filtered;
        return copy;
    }
    
    public SudokuSolution filterByRow(int bitfieldRow) {
        if(rowBitfieldsCount != 1 || columnBitfieldsCount != 1)
            throw new Error("Questa soluzione non e' un quadrato!");
        
        ArrayList<Node> filtered = new ArrayList<>(permutations.size());
        
        for (Node row : permutations)
            if ((row.value & bitfieldRow) == 0)
                filtered.add(row.copy());
        
        SudokuSolution copy = new SudokuSolution(rowBitfieldsCount, columnBitfieldsCount);
        copy.permutations = filtered;
        
        return copy;
    }
    
    public SudokuSolution filterByColumn(int bitfieldCol) {
        if(rowBitfieldsCount != 1 || columnBitfieldsCount != 1)
            throw new Error("Questa soluzione non e' un quadrato!");
        
        ArrayList<Node> filtered = new ArrayList<>(permutations.size());
        
        for (Node row : permutations) {
            ArrayList<Node> cols = new ArrayList<>(row.children.size());
            
            for (Node col : row.children)
                if ((col.value & bitfieldCol) == 0)
                    cols.add(col.copy());
            
            if(cols.size() > 0) {
                Node n = new Node(row.value);
                n.children = cols;
                filtered.add(n);
            }
        }
        
        SudokuSolution copy = new SudokuSolution(rowBitfieldsCount, columnBitfieldsCount);
        copy.permutations = filtered;
        
        return copy;
    }
    
    public SudokuSolution mergeRows(SudokuSolution other) {
        if(rowBitfieldsCount != 1 || other.rowBitfieldsCount != 1 || columnBitfieldsCount + other.columnBitfieldsCount > 3)
            throw new Error("Servono due quadrati!");
        
        ArrayList<Node> merged = new ArrayList<>();
        
        if(columnBitfieldsCount + other.columnBitfieldsCount == 3) {
            for (Node row : permutations) {
                for (Node otherRow : other.permutations) {
                    if ((row.value & otherRow.value) == 0) {
                        Node n = new Node(0);
                        n.addLeafs(row.children);
                        n.addLeafs(otherRow.children);
                        merged.addAll(n.children);
                    }
                }
            }
            SudokuSolution copy = new SudokuSolution(0, 3);
            copy.permutations = merged;
            return copy;
        } else {
            for (Node row : permutations) {
                for (Node otherRow : other.permutations) {
                    if ((row.value & otherRow.value) == 0) {
                        Node n = new Node(row.value | otherRow.value);
                        n.addLeafs(row.children);
                        n.addLeafs(otherRow.children);
                        merged.add(n);
                    }
                }
            }
            
            SudokuSolution copy = new SudokuSolution(1, columnBitfieldsCount + other.columnBitfieldsCount);
            copy.permutations = merged;
            return copy;
        }
    }
    
    public SudokuSolution mergeColumns(SudokuSolution other, boolean parallel) {
        if(columnBitfieldsCount != 3 || other.columnBitfieldsCount != 3 || rowBitfieldsCount != 0 || other.rowBitfieldsCount != 0 )
            throw new Error("Servono due quadrati!");
        
        SudokuSolution copy = new SudokuSolution(0, 3);
        copy.permutations = FindParallel.Execute(permutations, other.permutations, parallel);
        
        return copy;
    }
    
    public long mergeColumnsCount(SudokuSolution other, boolean parallel) {
        if (columnBitfieldsCount != 3 || other.columnBitfieldsCount != 3 || rowBitfieldsCount != 0 || other.rowBitfieldsCount != 0)
            throw new Error("Servono due quadrati!");
        
        BitfieldSorter other_sorted = new BitfieldSorter(other.permutations);
        
        if(parallel) {
            ParallelCount task = new ParallelCount(permutations, other_sorted);
            ForkJoinPool.commonPool().invoke(task);
            return task.solutions;
        } else {
            return other_sorted.CountAllCompatibles(permutations, false);
        }
    }
        
}
