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
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Specific list of Alphabet_char elements
 * @see Alphabet_Char
 * @author MS-1
 */
public class Alphabet_List implements Iterable<Alphabet_Char>{
    private final List<Alphabet_Char> list;

    /*
    
            Constructors
    
    */

    /**
     *  Default constructor, new object of list
     */
    public Alphabet_List() {
        this.list = new ArrayList<>();
    }
    
    /*
    
            Main methods
    
    */

    /**
     * Try add new object into list, throws exception when object is null or
     * object's id is in the list.
     * @param e object to add
     * @return index of added object
     * @throws IllegalAccessException when id of object is in the list
     * @throws NullPointerException when object is null
     */
    public int tryAdd(Alphabet_Char e) throws IllegalAccessException, NullPointerException {
        if (e == null)
            throw new NullPointerException("Empty object of ASCII_Char");
        if (isIdInList(e.getId())) {
            throw new IllegalAccessException("Id " + e.getId() + " is in the list");      
        }
        if (list.isEmpty()) {
            for (int i = 0; i < e.getId(); i++)
                list.add(new Alphabet_Char(i));
            list.add(e);
        }
        int firstId = getFirstId();
        int lastId = getLastId();
        if (e.getId() < firstId) {
            list.add(e);
            for (int i = e.getId() + 1; i < firstId; i++) 
                list.add(new Alphabet_Char(i));
        } else if (e.getId() > lastId) {
            for(int i = lastId + 1; i < e.getId(); i++) {
                list.add(new Alphabet_Char(i));
            }
            list.add(e);
        }
        list.sort((Alphabet_Char o1, Alphabet_Char o2) -> o1.getId() - o2.getId());
        Collections.sort(list);
        return tryFindIndex(e.getId());
    }
    
    /**
     * Removes object with specific index, throws exception when index isn't 
     * on the list. When removed index isn't on the last position
     * object position is reset to default.
     * @param index to remove
     * @throws java.lang.IllegalAccessException when index isn't on the list
     */
    public void remove(int index) throws IllegalAccessException {
        if (index < getSize()) {
            if (list.size() > 0) {
                if (list.get(index).getId() == getLastId()) {
                    removeLast();
                } else {
                    list.set(index, new Alphabet_Char(list.get(index).getId()));
                }
            }
        } else {
            throw new IllegalAccessException("Index " + index + " isn't in the list");
        }
    }
    
    /**
     * Rename position of existing objects in the list, old position is reset to
     * default.
     * @param index of original object, position in the list
     * @param newId of the object, must be unique, otherwise throw exception
     * @return new index in list
     * @throws IllegalAccessException when newId is in the list or index isn't 
     * on the list
     */
    public int renameItemId(int index, int newId) throws IllegalAccessException {
        if (index >= list.size())
            throw new IllegalAccessException("Index " + index + " isn't in the list");
        if (list.get(index).getId() != newId) {
            if (isIdInList(newId)) 
                throw new IllegalAccessException("Id " + newId + " is in the list");
            Alphabet_Char copy = new Alphabet_Char(list.get(index), newId);
            list.set(index, new Alphabet_Char(list.get(index).getId()));
            tryAdd(copy);
        }
        return tryFindIndex(newId);
    }
    
    /**
     * Swap two objects between each other, throw exception when both objects 
     * aren't on the list.
     * @param index0 of first object
     * @param index1 of second object
     */
    public void swapIndexes(int index0, int index1) throws IndexOutOfBoundsException {
        if ((index0 < list.size()) && (index1 < list.size())) {
            if (index0 != index1) {
                Alphabet_Char temp = new Alphabet_Char(list.get(index0), list.get(index1).getId());
                list.set(index0, new Alphabet_Char(list.get(index1), list.get(index0).getId()));
                list.set(index1, temp);  
            }
        } else {
            throw new IndexOutOfBoundsException("Index " + index0 + " or " + index1 + " isn't on the Alphabet_List");
        }
    }
    
    
    
    /**
     * Try find the specific on the list
     * @param id to find
     * @return index on the list of the finded object
     */
    public int tryFindIndex(int id) throws ArrayIndexOutOfBoundsException {
        for(int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == id)
                return i;
        }
        throw new ArrayIndexOutOfBoundsException("Id " + id + " isn't on the Alphabet_List");
    }
    
    /**
     * Test to find element with specific id in list
     * @param newId of the finding item
     * @return true if id is in the list, false otherwise
     */
    public boolean isIdInList(int newId) {
        for (Alphabet_Char item : list) {
            if (newId == item.getId())
                return true;
        }
        return false;
    }
    
    /** Removes last element on the list */
    private void removeLast() {
        if (list.size() > 0) {
            list.remove(list.size()-1);
        }
    }
    
    /** Removes first element on the list*/
    private void removeFirst() {
        if (list.size() > 0)
            list.remove(0);
    }
    
    
    
    @Override
    public String toString() {
        String out = "";
        for (Alphabet_Char item : list) {
            out += item.toString();
        }
        return out;
    }
    
    @Override
    public Iterator<Alphabet_Char> iterator() {
        return list.iterator();
    }
    
    /*
    
            CSV
    
    */

    /** Generates CSV text from all elements of the list 
     *  @return data from list in CSV format
     */
    public String toCSV() {
        String out = "";
        for (Alphabet_Char item : list) {
            out += item.toCSVLine();
        }
        return out;
    }
    
    /**
     * Save list to CSV file with specific name, throws exception when IO error
     * was occured.
     * @param filename of the CSV file
     * @throws java.io.IOException when IO error
     */
    public void saveToCSV(String filename) throws IOException {
        try (BufferedWriter file = Files.newBufferedWriter(Paths.get(filename), Charset.forName("windows-1250"))) {
                file.write(this.toCSV());
        } 
    }
    
    /**
     * Constructor of object from CSV file. Throws exception when IO error or
     * data are't correct.
     * @param filename of the CSV file
     * @return list object
     * @throws java.io.IOException when IO error
     * @throws java.lang.IllegalAccessException when are two or more the same 
     * id in file
     */
    public static Alphabet_List loadFromCSV(String filename) throws IOException, IllegalAccessException {
        Alphabet_List newList = new Alphabet_List();
        try (BufferedReader file = Files.newBufferedReader(Paths.get(filename), Charset.forName("windows-1250"))) {
            String str;
            while ((str = file.readLine()) != null) {
                newList.tryAdd(Alphabet_Char.fromCSVLine(str));
            }
        }
        return newList;
    }
    
    /**
     * Verification of CSV file, all records in file are readed.
     * @param filename of CSV file to test
     * @return true when file is correct
     */
    public static boolean verifyCSV(String filename) {
        Alphabet_List newList = new Alphabet_List();
        try {
            FileReader reader = new FileReader(filename);
            try (BufferedReader file = new BufferedReader(reader)) {
                String str;
                while ((str = file.readLine()) != null) {
                    newList.tryAdd(Alphabet_Char.fromCSVLine(str));
                }
            }
        } catch (IOException | IllegalAccessException | NullPointerException e) {
            return false;
        }
        return true;
    }
    
   /*
    
            Getters, setters
    
    */ 

    /**
     * Find first id which isn't on the list and is further on the list from 
     * index position
     * @param index position in list
     * @return new empty id further id of index on the list
     */
    public int getNextEmptyId(int index) {
        if (list.isEmpty())
            return 0;
        // list must be sorted and continuos
        else return list.get(list.size() - 1).getId() + 1;
    }
    
    private int getFirstId() throws NullPointerException {
        if (list.size() > 0)
            return list.get(0).getId();
        else throw new NullPointerException("Alphabet_List is null");
    }
    
    private int getLastId() throws NullPointerException  {
        if (list.size() > 0)
            return list.get(list.size()-1).getId();
        else throw new NullPointerException("Alphabet_List is null");
    }
    
    /**
     *
     * @param index position of object
     * @return object on the specific position
     */
    public Alphabet_Char get(int index) {
        return list.get(index);
    }
    
    /**
     *
     * @return size of the list
     */
    public int getSize() {
        return list.size();
    } 
}
