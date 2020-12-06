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
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Federico Bednarski 1589903
 */
public class Utils {
    static public int SquareToBitfieldRow(int[] square) {
        if (square.length != 9) {
            throw new Error("Square deve essere un array di 9 elementi");
        }

        int bitfieldRiga = 0;
        short usedValues = 0;

        for (int i = 0; i < 9; i++) {
            int v = square[i], r = i / 3;
            if (v < 0 || v > 8) {
                throw new Error("i valori devono essere tra 0 e 8");
            }
            if ((usedValues & (1 << v)) != 0) {
                throw new Error("Il; valore e' stato gia' usato!");
            }
            usedValues |= 1 << v;
            bitfieldRiga |= 1 << (r * 9 + v);
        }

        return bitfieldRiga;
    }

    static public int SquareToBitfieldColumn(int[] square) {
        if (square.length != 9) {
            throw new Error("Square deve essere un array di 9 elementi");
        }

        int bitfieldColonna = 0;
        short usedValues = 0;

        for (int i = 0; i < 9; i++) {
            int v = square[i], c = i % 3;
            if (v < 0 || v > 8) {
                throw new Error("i valori devono essere tra 0 e 8");
            }
            if ((usedValues & (1 << v)) != 0) {
                throw new Error("Il; valore e' stato gia' usato!");
            }
            usedValues |= 1 << v;
            bitfieldColonna |= 1 << (c * 9 + v);
        }

        return bitfieldColonna;
    }
    
    static public Square[] Rotate(Square[] original) {
        if(original.length != 9)
            throw new Error("input dovrebbe essere lungo 9");
        Square[] rotated = new Square[9];
        for (int i = 0; i < 9; i++) {
            int row = i / 3, col = i % 3;
            int r = col*3 + row;
            Square s = original[i];
            rotated[r] = new Square(s.column, s.row);
        }
        return rotated;
    }
    
    static public String SquareToString(int bitfieldRiga, int bitfieldColonna) {
        return SquareToString(bitfieldRiga, bitfieldColonna, '\n');
    }

    static public String SquareToString(int bitfieldRiga, int bitfieldColonna, char divider) {
        String description = "";
        for (int i = 0; i < 9; i++) {
            int r = i / 3, c = i % 3;
            if (i % 3 == 0 && i != 0) {
                description += divider;
            }
            // il risultato avra' un solo bit a 1 in i-esima posizione
            // i + 1 e' ugale al numero in quella posizione
            int bitfieldPosizione = ((bitfieldRiga >> (r * 9)) & (bitfieldColonna >> (c * 9))) & 0b111111111;
            // troviamo il valore
            if (bitfieldPosizione == 0) {
                description += "*";
                continue;
            }
            for (int b = 0; b < 9; b++) {
                bitfieldPosizione >>= 1;
                if (bitfieldPosizione == 0) {
                    description += b;
                    break;
                }
            }
        }
        return description;
    }

    static String BitfieldToString(int bitfield) {
        String description = "";
        for (int i = 0; i < 9 * 3; i++, bitfield >>= 1) {
            description += (bitfield % 2);
        }
        return description;
    }

    static String BitfieldToNumericString(int bitfield) {
        String description = "";
        for (int i = 0; i < 9 * 3; i++, bitfield >>= 1) {
            if (i % 9 == 0 && i != 0) {
                description += '|';
            }
            if (bitfield % 2 == 1) {
                description += i % 9;
            }
        }
        return description;
    }

    static public String SquareToString(int[] squares, int i) {
        return SquareToString(squares[i << 1], squares[(i << 1) + 1]);
    }
    
    static public String FormatTime(long time) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
        long minutes = 0;

        while (seconds >= 60) {
            seconds -= 60;
            minutes++;
        }

        String stringTime = "";

        if (minutes > 0) {
            stringTime += minutes + " minute" + (minutes == 1 ? "" : "s");
        }

        if (seconds > 0 || minutes == 0) {
            if (stringTime.length() > 0) {
                stringTime += " and ";
            }
            stringTime += seconds + " second" + (seconds == 1 ? "" : "s");
        }
        return stringTime;
    }
}
