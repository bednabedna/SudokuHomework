package sudokuhw;



import java.io.IOException;
/**
 *
 * @author Federico Bednarski 1589903
 */
public class SudokuHW {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(args.length < 1) {
            System.err.println("Errore, manca il path per il file con il sudoku!");
            System.err.println("Usage: java -cp . sudokuhw/SudokuHW path/to/sudoku");
            return;
        }
            
        CountSolutions(args[0]);
        //TestSuite1();
        //Test6("C:/Users/User/Documents/NetBeansProjects/SudokuHW/resources/test2/", "test2_f.txt");
    }
    
    static final public String BASE_URL = "C:/Users/User/Documents/NetBeansProjects/SudokuHW/resources/";
    
    
    static public SudokuSolution[] FindInitialProblemSpace(Square[] startingSquares, SudokuSolution squaresPermutations) {
        SudokuSolution[] squares = new SudokuSolution[9];

        for (int i = 0; i < 9; i++) {
            squares[i] = squaresPermutations.filterByContent(startingSquares[i].row, startingSquares[i].column);
        }

        // sfoltiamo i quadrati in base ai vicini precompilati
        for (int i = 0; i < 9; i++) {
            int row = i / 3, col = i % 3;
            // vicino 1 di riga
            int a = ((col + 1) % 3) + row * 3;
            squares[i] = squares[i].filterByRow(startingSquares[a].row);
            // vicino 2 di riga
            int b = ((col + 2) % 3) + row * 3;
            squares[i] = squares[i].filterByRow(startingSquares[b].row);
            // vicino 1 di colonna
            int c = ((row + 1) % 3) * 3 + col;
            squares[i] = squares[i].filterByColumn(startingSquares[c].column);
            // vicino 2 di colonna
            int d = ((row + 2) % 3) * 3 + col;
            squares[i] = squares[i].filterByColumn(startingSquares[d].column);
        }
        
        return squares;
    }
    
    static public SudokuSolutionCountCasher[] FindAndSortRows(SudokuSolution[] squares) {
        

        SudokuSolutionCountCasher row_1 = new SudokuSolutionCountCasher(squares[0].mergeRows(squares[1]).mergeRows(squares[2]));
        SudokuSolutionCountCasher row_2 = new SudokuSolutionCountCasher(squares[3].mergeRows(squares[4]).mergeRows(squares[5]));
        SudokuSolutionCountCasher row_3 = new SudokuSolutionCountCasher(squares[6].mergeRows(squares[7]).mergeRows(squares[8]));
        
        row_1.count();
        row_2.count();
        row_3.count();
        
        SudokuSolutionCountCasher[] rows = new SudokuSolutionCountCasher[3];
        
        if(row_1.count() < row_2.count()) {
            rows[0] = row_1;
            rows[1] = row_2;
        } else {
            rows[0] = row_2;
            rows[1] = row_1;
        }
        
        if(row_3.count() < rows[1].count()) {
            if(row_3.count() < rows[0].count()) {
                rows = new SudokuSolutionCountCasher[] { row_3, rows[0], rows[1] };
            } else {
                rows = new SudokuSolutionCountCasher[] { rows[0], row_3, rows[1] };
            }
        } else {
            rows[2] = row_3;
        }
        
        return rows;
    }
    
    static public void Test5(String basePath, String path) throws IOException {
       
        boolean parallel = false;
        int times = 5;
        Timer timer = new Timer(), totalTimer = new Timer();
        
        
        long fase_1 = 0, fase_2 = 0, fase_3 = 0, fase_4 = 0, time = 0, solutions = 0;
        for (int i = 0; i < times; i++) {
            totalTimer.restart();
            timer.restart();
            SudokuInfo sudokuInfo = new SudokuInfo(basePath + path);
            SudokuSolution SQUARES_PERMUTATIONS = new SudokuSolution();
            SudokuSolution[] initialSpace = FindInitialProblemSpace(sudokuInfo.squares, SQUARES_PERMUTATIONS);
            fase_1 += timer.restart();
            SudokuSolutionCountCasher[] rows1 = FindAndSortRows(initialSpace);
            fase_2 += timer.restart();
            SudokuSolution rows1_12 = rows1[0].solutions.mergeColumns(rows1[1].solutions, parallel);
            fase_3 += timer.restart();
            solutions = rows1_12.mergeColumnsCount(rows1[2].solutions, parallel);
            fase_4 += timer.millis();
            time += totalTimer.millis();
        }
        
        System.out.println("   <tr>\n      <th>" + path.substring(6) + "</th>");
        System.out.println("      <td>" + solutions + "</td>");
        System.out.println("      <td>" + fase_1/times + " ms</td>");
        System.out.println("      <td>" + fase_2/times + " ms</td>");
        System.out.println("      <td>" + fase_3/times + " ms</td>");
        System.out.println("      <td>" + fase_4/times + " ms</td>");
        System.out.println("      <td>" + time/times + " ms</td>\n   </tr>");

        //System.out.println(path + " found " + solutions + " solutions in " + time + " ms, space is " + sudokuInfo.space + ", grid is " + sudokuInfo.getFilledCellsPercentage() + "% filled");
    }
    
    static public double format(double input) {
        return ((double)((int)(input * 100)))/100;
    }
    
    static public void Test7(String basePath, String path) throws IOException {
       
        int times = 1;
        Timer timer = new Timer();
        
        
        long fase_3_para = 0, fase_3_serial = 0, fase_4_para = 0, fase_4_serial = 0, solutions = 0;
        for (int i = 0; i < times; i++) {
            SudokuInfo sudokuInfo = new SudokuInfo(basePath + path);
            SudokuSolution SQUARES_PERMUTATIONS = new SudokuSolution();
            SudokuSolution[] initialSpace = FindInitialProblemSpace(sudokuInfo.squares, SQUARES_PERMUTATIONS);
            SudokuSolutionCountCasher[] rows1 = FindAndSortRows(initialSpace);
            timer.restart();
            SudokuSolution rows1_12 = rows1[0].solutions.mergeColumns(rows1[1].solutions, true);
            fase_3_para += timer.restart();
            solutions = rows1_12.mergeColumnsCount(rows1[2].solutions, true);
            fase_4_para += timer.millis();
        }
        
        for (int i = 0; i < times; i++) {
            SudokuInfo sudokuInfo = new SudokuInfo(basePath + path);
            SudokuSolution SQUARES_PERMUTATIONS = new SudokuSolution();
            SudokuSolution[] initialSpace = FindInitialProblemSpace(sudokuInfo.squares, SQUARES_PERMUTATIONS);
            SudokuSolutionCountCasher[] rows1 = FindAndSortRows(initialSpace);
            timer.restart();
            SudokuSolution rows1_12 = rows1[0].solutions.mergeColumns(rows1[1].solutions, false);
            fase_3_serial += timer.restart();
            solutions = rows1_12.mergeColumnsCount(rows1[2].solutions, false);
            fase_4_serial += timer.millis();
        }
        
        System.out.println("   <tr>\n      <th>" + path.substring(6) + "</th>");
        System.out.println("      <td>" + solutions + "</td>");
        System.out.println("      <td>" + fase_3_serial/times + " ms</td>");
        System.out.println("      <td>" + fase_3_para/times + " ms</td>");
        System.out.println("      <td>" + format(fase_3_serial/(double)fase_3_para) + "</td>");
        System.out.println("      <td>" + fase_4_serial/times + " ms</td>");
        System.out.println("      <td>" + fase_4_para/times + " ms</td>");
        System.out.println("      <td>" + format(fase_4_serial/(double)fase_4_para) + "</td>\n   </tr>");

        //System.out.println(path + " found " + solutions + " solutions in " + time + " ms, space is " + sudokuInfo.space + ", grid is " + sudokuInfo.getFilledCellsPercentage() + "% filled");
    }
    
    static public void Test6(String basePath, String path) throws IOException {
        int times = 1;
        long serialTime = 0, solutions = 0;
        for (int threads = 1; threads <= 2; threads++) {
            Timer timer = new Timer();
            for (int i = 0; i < times; i++) {
                SudokuInfo sudokuInfo = new SudokuInfo(basePath + path);

                SudokuSolution SQUARES_PERMUTATIONS = new SudokuSolution();

                SudokuSolution[] initialSpace = FindInitialProblemSpace(sudokuInfo.squares, SQUARES_PERMUTATIONS);

                SudokuSolutionCountCasher[] rows1 = FindAndSortRows(initialSpace);
                
                SudokuSolution rows1_12 = rows1[0].solutions.mergeColumns(rows1[1].solutions, threads != 1);
                
                solutions = rows1_12.mergeColumnsCount(rows1[2].solutions, threads != 1);
            }
            long time = timer.millis()/times;
            if(threads == 1){
                serialTime = time;
            } else {
                System.out.println("   <tr>\n      <th>" + path.substring(6) + "</th>");
                System.out.println("      <td>" + solutions + "</td>");
                System.out.println("      <td>" + serialTime + " ms</td>");
                System.out.println("      <td>" + time + " ms</td>");
                System.out.println("      <td>" + format(serialTime/(double)time) + "</td>\n   </tr>");
            }
        }
    }
    
    static public void TestSuite1() throws IOException {
        char letters[] = {'a', 'b', 'c', 'd', 'e'};

        for (char letter : letters) {
            Test7(BASE_URL, "test1/test1_" + letter + ".txt");
        }

        for (char letter : letters) {
            Test7(BASE_URL, "test2/test2_" + letter + ".txt");
        }
    }
    
    static public void CountSolutions(String path) {
        SudokuInfo sudokuInfo;
        
        try {
            sudokuInfo = new SudokuInfo(path);
        } catch (IOException ex) {
            System.err.println("Il file non esiste!");
            return;
        }
        

        SudokuSolution SQUARES_PERMUTATIONS = new SudokuSolution();

        SudokuSolution[] initialSpace = FindInitialProblemSpace(sudokuInfo.squares, SQUARES_PERMUTATIONS);

        SudokuSolutionCountCasher[] rows1 = FindAndSortRows(initialSpace);

        SudokuSolution rows1_12 = rows1[0].solutions.mergeColumns(rows1[1].solutions, true);

        long solutions = rows1_12.mergeColumnsCount(rows1[2].solutions, true);
        System.out.println("spazio delle soluzioni = " + sudokuInfo.space + "\nfattore di riempimento = " + sudokuInfo.getFilledCellsPercentage() + "%\nsoluzioni = " + solutions);
    }
    
}
