/**
 * Copyright (C) 2018 MS-1
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * @author Mikolaj Stankowiak
 * @since 2018-05-18
    
 */
package alphabet_generator;

import java.util.Arrays;
import java.util.Comparator;
import javax.swing.JOptionPane;

/**
 * Object represents 5x8 pixels (5 byte) codes to translate to diodes state in Matrix_Clock.
 * One bit in byte symbolize one diode. Legend of char length:<br>
 * <div style="font-family:monospace;">
 * 5: 0..4 |b&nbsp;&nbsp;&nbsp;&nbsp;| <br>
 * 4: 0..3 |b&nbsp;&nbsp;&nbsp;&nbsp;| <br>
 * 3: 1..3 |&nbsp;b&nbsp;&nbsp;&nbsp;| <br>
 * 2: 1..2 |&nbsp;b&nbsp;&nbsp;&nbsp;| <br>
 * 1: 2..2 |&nbsp;&nbsp;b&nbsp;&nbsp;| <br>
 * </div>
 * b is begin of bytes code.
 * @author Mikolaj Stankowiak
 */
public class Alphabet_Char implements Comparator<Alphabet_Char>,Comparable<Alphabet_Char>,  Cloneable {
    /** horizontal size (X dimension) */
    public static final int MAX_CODES_LENGTH = 5;
    /** separator between data fields in CSV line */
    public static final String SEP = ";";
    /** short char name of object */
    private String sign;
    /** byte codes of pixels*/
    private int[] codes = new int[MAX_CODES_LENGTH]; // byte codes, one byte is Y dim
    /** optional string of description */
    private String description;
    /** unique identifier */
    private int id;
    /** number of bits set to one */
    private int modifiedDots;
    /** horizontal length of char (between 0 and CODES_LEGHT)
     @see CODE_LENGTH*/
    private int length;

    
    /*
     * 
     *      Constructors 
     * 
     */

    /**
     * Default constructor, initialize object with default data and id
     * @param id unique identifier
     */
    public Alphabet_Char(int id) {
        this.id = id;
        this.sign = "_empty";
        this.description = "";
        this.length = 1;
    }
    
    /**
     * Initialize object with default data, id and codes
     * @param id unique identifier
     * @param codes bytes of pixels
     */
    public Alphabet_Char(int id, int[] codes) {
        this(id);
        setCodes(codes); 
    }
    
    /**
     * Copy constructor
     * @param original object to copy
     */
    public Alphabet_Char(Alphabet_Char original) {
        this.sign = original.getSign();
        this.codes = original.getCodes().clone();
        this.description = original.getDescription();
        this.id = original.getId();
        calculateDotsLength();
    }
    
    /**
     * Copy constructor, set new id to copied object
     * @param original object to copy
     * @param newId to copied object
     */
    public Alphabet_Char(Alphabet_Char original, int newId) {
        this(original);
        this.id = newId;
    }
    
    /*
    
            Main methods
    
    */
    /** Calculate modifiedDots and length variables, use after modified codes bytes
     @see modifiedDots
     @see length
     @see codes */
    private void calculateDotsLength() {
        modifiedDots = 0;
        for (int i = 0; i < MAX_CODES_LENGTH; i++) {
            int copy = codes[i];
            for (int j = 0; j < 8; j++) {
                if ((copy % 2) == 1) {
                    modifiedDots++;
                }
                copy >>= 1;
            }
        }
        if (codes[0] > 0) {
            if (codes[4] > 0)
                length = 5;
            else length = 4;
        } else if (codes[1] > 0) {
            if (codes[3] > 0)
                length = 3;
            else length = 2;
        } else length = 1;
    }

    /**
     * NOT USED
     * @param codeId
     * @param bitId
     * @param bitValue
     * @throws ArrayIndexOutOfBoundsException
     */
    private void setBit(int codeId, int bitId, boolean bitValue) throws ArrayIndexOutOfBoundsException {
        if (codeId >= MAX_CODES_LENGTH)
            throw new ArrayIndexOutOfBoundsException("Wrong number of array index named codeId: " + codeId + " required less than " + MAX_CODES_LENGTH);
        if (bitValue) {
            codes[codeId] |= (1 << bitId);
        } else {
            codes[codeId] &= ~(1 << bitId);
        }
        calculateDotsLength();
    }
    
    /**
     * SHL and SHR operiations in all single bytes in codes independently. 
     * Positive value is SHL, negative is SHR.
     * @see codes
     * @param shiftValue direction and shift value
     */
    public void shiftBits(int shiftValue) {
        if (shiftValue > 0) {
            for (int i = 0; i < MAX_CODES_LENGTH; i++) {
                codes[i] <<= Math.abs(shiftValue);
            }
        } else {
            for (int i = 0; i < MAX_CODES_LENGTH; i++) {
                codes[i] >>= Math.abs(shiftValue);
            } 
        }
        calculateDotsLength();
    }
    
    /**
     * SHL and SHR operiations in codes table. One index is like one bit in original shift operations. 
     * Positive value is SHL, negative is SHR.
     * @param shiftValue direction and shift value
     */
    public void shiftBytes(int shiftValue) {
       if (shiftValue > 0) {
            for (int i = MAX_CODES_LENGTH - 1; i >= shiftValue; i--) {
                codes[i] = codes[i - shiftValue];
            }
            for (int i = 0; i < shiftValue; i++) {
                codes[i] = 0;
            }
        } else {
            shiftValue = Math.abs(shiftValue);
            for (int i = 0; i < MAX_CODES_LENGTH - shiftValue; i++) {
                codes[i] = codes[i + shiftValue];
            }
            for (int i = MAX_CODES_LENGTH - shiftValue; i < MAX_CODES_LENGTH; i++) {
                codes[i] = 0;
            } 
        } 
        calculateDotsLength();
    }
    
    /*
     * 
     *      CSV
     * 
     */

    /**
     * Generate CSV line, between data is separator
     * @see SEP
     * @return line ready to save to CSV file
     */
    public String toCSVLine() {
        return String.valueOf(id) + SEP + String.valueOf(sign) + SEP + description.replace(SEP, "_") + SEP + Arrays.toString(codes).replace(" ", "").replace("[", "").replace("]", "").replace(",", SEP) + "\n";
    }
    
    /**
     * Decode CSV line to object
     * @param line from CSV file
     * @return object from file
     */
    public static Alphabet_Char fromCSVLine(String line) {
        
        String[] tokens = line.split(Alphabet_Char.SEP);
        try {

            Alphabet_Char out = new Alphabet_Char(Integer.parseInt(tokens[0]));
            int delta = 0;
            if (tokens.length > 8) {
                delta = tokens.length - 8;
                String chars = "";
                for (int i = 0; i < delta; i++)
                    chars += SEP;
                out.setSign(chars);
            } else {
                out.setSign(tokens[1+delta]);
            }
            out.setDescription(tokens[2+delta]);
            int[] bytes = new int[MAX_CODES_LENGTH];
            for (int i = 0; i < MAX_CODES_LENGTH; i++) {
                bytes[i] = Integer.parseInt(tokens[3+i+delta]);
            }
            out.setCodes(bytes);
            //System.out.print(out.getHLine(false));
            return out;
        } catch (NumberFormatException e) {
            System.out.println("Error in parsing " + line + " to ASCII_Char object");
            JOptionPane.showMessageDialog(
                    null, 
                    "Error in parsing " + line + " to ASCII_Char object",
                    "Error message",
                    JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    /*
    
            Compare, clone, toString
    
    */
    @Override
    public int compareTo(Alphabet_Char e) {
        return id - e.getId();
    }
    
    @Override
    public int compare(Alphabet_Char one, Alphabet_Char two) {
        return one.compareTo(two);
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    @Override
    public String toString() {
        
        return "Alphabet_Char[Id: " + id + ", Char: " + sign + ", description: " 
                + description + ", codes: " + Arrays.toString(codes) 
                + ", modified dots: " + modifiedDots + ", length: " + length + "]";
    }
    
    
    /*
    
            Getters, setters
    
    */
    /**
     * @return the sign
     */
    public String getSign() {
        return sign;
    }

    /**
     * @param sign the sign to set
     */
    public void setSign(String sign) {
        this.sign = sign;
    }

    /**
     * @return the codes
     */
    public int[] getCodes() {
        return codes;
    }

    /**
     * @param codes the codes to set
     */
    public final void setCodes(int[] codes) throws ArrayIndexOutOfBoundsException {
        if (codes.length == MAX_CODES_LENGTH)
            this.codes = codes;
        else throw new ArrayIndexOutOfBoundsException("Wrong number of array named codes: " + codes.length + " required: "+MAX_CODES_LENGTH);
        calculateDotsLength();
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the modifiedDots
     */
    public int getModifiedDots() {
        return modifiedDots;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @return the empty
     */
    public boolean isEmpty() {
        if (modifiedDots > 0) {
            return false;
        } else 
            return true;
    }
    

}
