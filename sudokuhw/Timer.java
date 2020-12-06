package sudokuhw;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import static sudokuhw.Utils.FormatTime;

/**
 *
 * @author Federico Bednarski 1589903
 */
public class Timer {

   private long start;

    public Timer() {
        start = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return FormatTime(millis());
    }

    public long restart() {
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        start = end;
        return elapsed;
    }
    
    public long millis() {
        return System.currentTimeMillis() - start;
    }
}
