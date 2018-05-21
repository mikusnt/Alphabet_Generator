/*
 * Copyright (C) 2018 MS
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
 */
package alphabet_generator;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 *
 * @author MS
 */
public class AVR_Save {
    private final Alphabet_List list;
    private final String headerName = "AVR_files/alphabet_codes.h";
    private final String header = "/**\n" +
        " * @file alphabet_codes.h\n" +
        " * @author\t\tMikolaj Stankowiak <br>\n" +
        " * \t\t\t\tmik-stan@go2.pl\n" +
        " * 				\n" +
        " * $Created: %s $\n" +
        " * Header file containing byte pixels table (Alphabet) of chars and chars byte length in PROGMEM. <br>\n" +
        " * Created by Alphabet_Generator.\n" + 
        " */\n" +
        "#ifndef SEQ_ALPHABET_CODES_H_\n" +
        "#define SEQ_ALPHABET_CODES_H_\n" +
        "\n" +
        "#include <avr/io.h>\n" +
        "#include <avr/pgmspace.h>\n" +
        "\n" +
        "//! number of chars in alphabet table\n" +
        "#define ALPHABET_SIZE %d\n" +
        "\n" +
        "//! all alphabet chars and special chars\n" +
        "extern const uint8_t uiAlphabet[ALPHABET_SIZE][5] PROGMEM;\n" +
        "//! vertical length of uiAlphabet chars\n" +
        "extern const uint8_t uiAlLength[ALPHABET_SIZE] PROGMEM;\n" +
        "\n" +
        "#endif /* SEQ_ALPHABET_CODES_H_ */";
            private final String cName = "AVR_files/alphabet_codes.c";
            private final String cPart1 = "#include \"alphabet_codes.h\"\n" +
        "\n" +
        "const uint8_t uiAlphabet[ALPHABET_SIZE][%d] PROGMEM = {\n";
    private final String cPart2 = "};\n" +
        "\n" +
        "const uint8_t uiAlLength[ALPHABET_SIZE] PROGMEM = {\n";
    private final String cPart3 = "};";
    
    /**
     *
     * @param list
     */
    public AVR_Save(Alphabet_List list) {
        this.list = list;
    }
    
    /**
     *
     * @return
     * @throws IOException
     */
    public String saveHeader() throws IOException {
        File f = new File(headerName);
        f.getParentFile().mkdirs();
        f.createNewFile();
        try (PrintWriter writer = new PrintWriter(f)) {
            SimpleDateFormat ft = 
                new SimpleDateFormat ("yyyy-MM-dd");
            Date dNow = new Date( );
            writer.print(String.format(header, ft.format(dNow), list.getSize()));
            //Desktop dt = Desktop.getDesktop();
            //dt.open(f.getp);
        }
        return f.getPath();
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public String saveC() throws IOException {
        File f = new File(cName);
        f.getParentFile().mkdirs();
        f.createNewFile();
        try {
            try (PrintWriter writer = new PrintWriter(f)) {
                writer.print(String.format(cPart1, Alphabet_Char.MAX_CODES_LENGTH));
                writer.print(getCCodesTable(list, "\t"));
                writer.print(cPart2);
                writer.print(getCLengthsTable(list, "\t"));
                writer.print(cPart3);
                //Desktop dt = Desktop.getDesktop();
                //dt.open(f.getp);
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return f.getPath();
    }
   
    
    /*
    
            Generating lines from Alphabet_Char
    
    */
    
    /**
     * Generate comment of object to write into C file
     * @param c object whose comment is generated
     * @param prefix string before comment
     * @return comment with description of object
     */
    private String getCLineComment(Alphabet_Char c, String prefix) {
        String descriptionText = "";
        if (c.getDescription().length() > 0) 
            descriptionText = " " + c.getDescription();
        return prefix + "// " + c.getSign() + descriptionText + "| y = " + c.getId();
    }

    /**
     *
     * @param withComma if is true, after close bracket is comma
     * @param prefix string before comment
     * @return one index of two-dim table in C
     */
    private String getCLineCode(Alphabet_Char c, boolean withComma, String prefix) {
        String line;
        line = getCLineComment(c, prefix) + "\n";
        line += prefix + "{ ";
        for(int i = 0; i < Alphabet_Char.MAX_CODES_LENGTH - 1; i++) {
            
            line += String.format("0x%02X, ", c.getCodes()[i] & 0xff);
        }
        line += String.format("0x%02X ", c.getCodes()[Alphabet_Char.MAX_CODES_LENGTH - 1] & 0xff);
        if (withComma == true) 
            line += "},";
        else
            line += "}";
        return line;
    }
    
    /*
    
            Generating values from lines from Alphabet_Char
    
    */
    
    /**
     *
     * @param prefix
     * @return
     */
    private String getCCodesTable(Alphabet_List list, String prefix) {
        String out = "";
            if (list.getSize() > 0) {
            for (int i = 0; i < list.getSize() - 1; i++) {
                out += getCLineCode(list.get(i), true, prefix) + "\n";
            }
            out += getCLineCode(list.get(list.getSize() - 1), false, prefix) + "\n";
        }
        return out;
    }
    
    /**
     *
     * @param prefix
     * @return
     */
    private String getCLengthsTable(Alphabet_List list, String prefix) {
        String out = "";
        if (list.getSize() > 0) {
            for (int i = 0; i < list.getSize() - 1; i++)
                out += prefix + list.get(i).getLength() + ", " + getCLineComment(list.get(i), prefix) + "\n";
            out += prefix + list.get(list.getSize() - 1).getLength() + "  " + getCLineComment(list.get(list.getSize() - 1), prefix) + "\n";
        }
        return out;
    }
}
