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
    private final ASCII_List list;
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
        "const uint8_t uiAlphabet[ALPHABET_SIZE][5] PROGMEM = {\n";
    private final String cPart2 = "};\n" +
        "\n" +
        "const uint8_t uiAlLength[ALPHABET_SIZE] PROGMEM = {\n";
    private final String cPart3 = "};";
    
    public AVR_Save(ASCII_List list) {
        this.list = list;
    }
    
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
    public String saveC() throws IOException {
        File f = new File(cName);
        f.getParentFile().mkdirs();
        f.createNewFile();
        try {
            try (PrintWriter writer = new PrintWriter(f)) {
                writer.print(cPart1);
                writer.print(list.getHComment_Codes("\t"));
                writer.print(cPart2);
                writer.print(list.getHLengths("\t"));
                writer.print(cPart3);
                //Desktop dt = Desktop.getDesktop();
                //dt.open(f.getp);
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        return f.getPath();
    }
}
