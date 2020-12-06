package sudokuhw;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Federico Bednarski 1589903
 */
public class SudokuSolutionCountCasher {
    public SudokuSolution solutions;
    private long count = -1;

    public SudokuSolutionCountCasher(SudokuSolution solutions) {
        this.solutions = solutions;
    }
    
    public void resetCache() {
        count = -1;
    }
    
    public long count() {
        if(count == -1)
            count = solutions.count();
        return count;
    }
}
