package sudokuhw;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 *
 * @author User
 */
public class SudokuInfo {
    final Square[] squares;
    final int emptyCells;
    final double space;
    
    public SudokuInfo(String filePath) throws IOException {
        String cellsStr = new String(Files.readAllBytes(Paths.get(filePath)), Charset.forName("UTF-8"));
        cellsStr = cellsStr.replaceAll("[^.1-9]", "");

        if (cellsStr.length() != 81) {
            throw new IllegalArgumentException("Le celle del sudoku devono essere 81, ma ne sono state ricevute " + cellsStr.length());
        }

        int squaresBitfields[] = new int[9 * 2];
        int[][] cells = new int[9][9];
        int emptyCells = 0;
        Arrays.fill(squaresBitfields, 0);
        // inizializziamo l'arrray delle celle dalla stringa
        for (int i = 0; i < cellsStr.length(); i++) {
            char c = cellsStr.charAt(i);
            int row = i / 9, col = i % 9;
            if (c != '.') {
                if (c < '1' || c > '9') {
                    throw new IllegalArgumentException("I valori ammissibili per le celle del Sudoku sono il punto (.) e i numeri 1-9, ricevuto : '" + c + "'");
                } else {
                    int square = col / 3 + row / 3 * 3;
                    int value = Character.getNumericValue(c) - 1;
                    // bf riga
                    squaresBitfields[square << 1] |= 1 << (value + (row % 3) * 9);
                    // bf colonna
                    squaresBitfields[(square << 1) + 1] |= 1 << (value + (col % 3) * 9);
                    // cell value
                    cells[row][col] = value;
                }
            } else {
                emptyCells++;
                cells[row][col] = 0;
            }
        }
        
        Square[] squares2 = new Square[9];
        for (int i = 0; i < squaresBitfields.length; i += 2)
            squares2[i/2] = new Square(squaresBitfields[i], squaresBitfields[i+1]);
        
        this.squares = squares2;
        this.emptyCells = emptyCells;
        
        // calcola spazio soluzioni
        double spazio = 1;
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if(cells[row][col] != 0)
                    continue;
                int s = 0;
                int square = col / 3 + row / 3 * 3;
                square = this.squares[square].row & this.squares[square].column;
                for (int v = 1; v <= 9; v++) {
                    boolean valid = true;
                    for (int i = 0; i < 9; i++) {
                        if(cells[row][i] == v || cells[i][col] == v || (square >> (v-1)) == 1 || (square >> (v+9-1)) == 1 || (square >> (v+18-1)) == 1 ) {
                            valid = false;
                            break;
                        }
                    }
                    if(valid)
                        s++;
                }
                spazio *= s;
            }
        }
        
        this.space = spazio;
    }
    
    public double getFilledCellsPercentage() {
        return ((int)(((81-emptyCells) / 81.0) * 1000.0))/10.0;
    }
}
