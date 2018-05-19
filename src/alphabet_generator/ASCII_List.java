/*
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
 */
package alphabet_generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author MS-1
 */
public class ASCII_List implements Iterable<ASCII_Char>{
    private final List<ASCII_Char> list;

    /*
    
            Constructors
    
    */
    public ASCII_List() {
        this.list = new ArrayList<>();
    }
    
    /*
    
            Main methods
    
    */
    public boolean isIdInList(int newId) {
        for (ASCII_Char item : list) {
            if (newId == item.getId())
                return true;
        }
        return false;
    }
    
    public int renameItemId(int index, int newId) {
        if (list.get(index).getId() != newId) {
            try {
                if (isIdInList(newId)) 
                    throw new IllegalAccessException("Id " + newId + " is in the list");
                ASCII_Char copy = new ASCII_Char(list.get(index), newId);
                list.remove(index);
                tryAdd(copy);
            } catch (IllegalAccessException e) {
                System.out.println(e.toString());
            }
        }
        return tryFindId(newId);
    }
    
    public void swapIndexes(int index0, int index1) {
        try {
        if ((index0 < list.size()) && (index1 < list.size())) {
            ASCII_Char temp = new ASCII_Char(list.get(index0), list.get(index1).getId());
            list.set(index0, new ASCII_Char(list.get(index1), list.get(index0).getId()));
            list.set(index1, temp);  
        } else {
            throw new IndexOutOfBoundsException();
        }
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.toString());
        }
    }
    
    public int tryAdd(ASCII_Char e) throws IllegalAccessException, NullPointerException {
        if (e == null)
            throw new NullPointerException("Empty object of ASCII_Char");
        if (isIdInList(e.getId()))
                throw new IllegalAccessException("Id " + e.getId() + " is in the list");
        if (list.isEmpty())
            list.add(e);
        int firstId = getFirstId();
        int lastId = getLastId();
        if (e.getId() < firstId) {
            list.add(e);
            for (int i = e.getId() + 1; i < firstId; i++) 
                list.add(new ASCII_Char(i));
        } else if (e.getId() > lastId) {
            for(int i = lastId + 1; i < e.getId(); i++) {
                list.add(new ASCII_Char(i));
            }
            list.add(e);
        }
        list.sort((ASCII_Char o1, ASCII_Char o2) -> o1.getId() - o2.getId());
        Collections.sort(list);
        return tryFindId(e.getId());
    }
    
    private int tryFindId(int id) throws ArrayIndexOutOfBoundsException {
        for(int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == id)
                return i;
        }
        throw new ArrayIndexOutOfBoundsException("Can't find object in list");
    }
    
    private void removeLast() {
        if (list.size() > 0) {
            list.remove(list.size()-1);
        }
    }
    
    private void removeFirst() {
        if (list.size() > 0)
            list.remove(0);
    }
    
    public void remove(int index) {
        if (list.size() > 0) {
            if (list.get(index).getId() == getLastId()) {
                removeLast();
            } else if (list.get(index).getId() == getFirstId()) {
                removeFirst();

            } else {
                list.set(index, new ASCII_Char(list.get(index).getId()));
            }
        }
    }
    
    @Override
    public String toString() {
        String out = "";
        for (ASCII_Char item : list) {
            out += item.toCSVLine();
        }
        return out;
    }
    
    @Override
    public Iterator<ASCII_Char> iterator() {
        return list.iterator();
    }
    
    /*
    
            CSV
    
    */
    public void saveToCSV(String filename){
        File f = new File(filename);
        try (PrintWriter file = new PrintWriter(f)) {
            file.print(this.toString());
        } catch (Exception e) {
            System.out.println(filename + " not found");       
        }
    }
    
    public static ASCII_List readFromCSV(String filename) {
        ASCII_List newList = new ASCII_List();
        try {
            FileReader reader = new FileReader(filename);
            try (BufferedReader file = new BufferedReader(reader)) {
                String str;
                while ((str = file.readLine()) != null) {
                    newList.tryAdd(ASCII_Char.fromCSVLine(str));
                }
            }
        } catch (IOException e) {
            System.out.println(filename + " IO exception");
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ASCII_List.class.getName()).log(Level.SEVERE, null, ex);
        }
        return newList;
    }
    
    public static boolean verifyCSV(String filename) {
        ASCII_List newList = new ASCII_List();
        try {
            FileReader reader = new FileReader(filename);
            try (BufferedReader file = new BufferedReader(reader)) {
                String str;
                str = file.readLine(); 
                if (str == null)
                    return true;
                newList.tryAdd(ASCII_Char.fromCSVLine(str));
            }
        } catch (IOException | IllegalAccessException | NullPointerException e) {
            System.out.println(e.toString());
            return false;
        }
        return true;
    }
    
    public String getHComment_Codes(String prefix) {
        String out = "";
            if (list.size() > 0) {
            for (int i = 0; i < list.size() - 1; i++) {
                out += list.get(i).getHComment_Code(true, prefix) + "\n";
            }
            out += list.get(list.size() - 1).getHComment_Code(false, prefix) + "\n";
        }
        return out;
    }
    
    public String getHLengths(String prefix) {
        String out = "";
        if (list.size() > 0) {
            for (int i = 0; i < list.size() - 1; i++)
                out += prefix + list.get(i).getLength() + ", " + list.get(i).getHLineComment("") + "\n";
            out += prefix + list.get(list.size() - 1).getLength() + "  " + list.get(list.size() - 1).getHLineComment("") + "\n";
        }
        return out;
    }
    
   /*
    
            Getters, setters
    
    */ 
    public int getNextEmptyId(int index) {
        if (list.isEmpty())
            return 0;
        int id = list.get(index).getId();
        for(int i = index + 1; i < list.size(); i++) {
            if (list.get(i).getId() > (id + 1))
                return id + 1;
            id = list.get(i).getId();
        }
        return id + 1;
    }
    
    private int getFirstId() {
        return list.get(0).getId();
    }
    
    private int getLastId() {
        return list.get(list.size()-1).getId();
    }
    
    public ASCII_Char get(int index) {
        return list.get(index);
    }
    
    private void set(int index, ASCII_Char value) {
        list.set(index, value);
    }
    
    public int getSize() {
        return list.size();
    } 
}
