/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabet_generator;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author MS-1
 */
public class Main_Frame extends javax.swing.JFrame {
    private JCheckBox[][] checkBoxes;
    private JLabel[] labels;
    private int bytes[];
    private Alphabet_List list;
    private int selectedRow;
    private final String filename;
    private boolean [][] checkEnable;
    private int actualLength;
    private ItemListener checkListener;
    private boolean enableCheckListener = true;
    private boolean modifiedData = false;
    private final String MODIFIED = "modified";
    private final String NOT_MODIFIED = "not modified";
    TimerTask task;
    Timer timer;
    
    class RemindTask extends TimerTask {
        @Override
        public void run() {
            jLabelDyskietka.setVisible(false);
            timer.cancel(); //Wyłączamy taska
        }
    }

    /**
     * Creates new form Main_Frame
     * @param   filename    path of datafile
     */
    public Main_Frame(String filename) {
        initComponents();
        
        jLabelDyskietka.setVisible(false);
        Filename_Frame.setCenterPosition(this);
        jStatusBar.setVisible(false);
        checkListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent item) {
                if (enableCheckListener) {
                    checkboxClick((JCheckBox)item.getSource()); 
                    modifiedData = true;
                    jStatusBar.setText(MODIFIED);
                }
            }  
        };
        this.filename = filename;
        File f = new File(filename);
        setTitle("Alphabet Generator - " + f.getName());
        actualLength = 0;
        selectedRow = -1;
       
        this.checkBoxes = new JCheckBox[][]{
            { jCheck_00, jCheck_01, jCheck_02, jCheck_03, jCheck_04, jCheck_05, jCheck_06, jCheck_07 },
            { jCheck_10, jCheck_11, jCheck_12, jCheck_13, jCheck_14, jCheck_15, jCheck_16, jCheck_17 },
            { jCheck_20, jCheck_21, jCheck_22, jCheck_23, jCheck_24, jCheck_25, jCheck_26, jCheck_27 },
            { jCheck_30, jCheck_31, jCheck_32, jCheck_33, jCheck_34, jCheck_35, jCheck_36, jCheck_37 },
            { jCheck_40, jCheck_41, jCheck_42, jCheck_43, jCheck_44, jCheck_45, jCheck_46, jCheck_47 }
        };
        this.checkEnable = new boolean[][] {
            { false, false, false, false, false },
            { false, false, true, false, false },
            { false, true, true, false, false },
            { false, true, true, true, false },
            { true, true, true, true, false },
            { true, true, true, true, true }
        };
        
        task = new TimerTask() {
        @Override
            public void run() {
                jLabelDyskietka.setVisible(false);
                timer.cancel();
            }
        };      

        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 8; j++) {
                checkBoxes[i][j].addItemListener(checkListener);
            }
        }
        
        jTableMain.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                //System.out.println("Try to rename from " + selectedRow);
                if (selectedRow != jTableMain.getSelectedRow() && (jTableMain.getSelectedRow() > -1)) {
                    selectedRow = jTableMain.getSelectedRow();
                    //System.out.println("rename to " + selectedRow);
                    trySaveCSV();
                    openSigleItem();
                    
                }
            }
        });
        
        this.bytes = new int[5];
        checkboxClick(jCheck_00);
        jTableMain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        try {
            list = Alphabet_List.loadFromCSV(filename);
        } catch (IOException | IllegalAccessException e) {
            JOptionPane.showMessageDialog(
                    null, 
                    "Error on open Alphabet file",
                    "Error message",
                    JOptionPane.ERROR_MESSAGE);
            setVisible(false);
            dispose();
        }
        refreshList();
        if (jTableMain.getRowCount() > 0) {
            jTableMain.setRowSelectionInterval(0, 0);
            setCheckBoxesColor();
        }

    }
    
    private void trySaveCSV() {
        //System.out.println(modifiedData);
        if (modifiedData == true) {
            try {
            timer = new Timer();
            timer.schedule(new RemindTask(), 1000);
            list.saveToCSV(filename);
            modifiedData = false;
            jStatusBar.setText(NOT_MODIFIED);
            jLabelDyskietka.setVisible(true);
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
    }   
    
    private void openSigleItem() {
        refreshElementInList();
        tryEnableButtons();
        itemToCheckboxes();  
    }
    private void tryEnableButtons() {
        if (jTableMain.getRowCount() > 0) {
            jButtonDelete.setEnabled(true);
            if (selectedRow < jTableMain.getRowCount() - 1) {
                jButtonDown.setEnabled(true);
            } else
                jButtonDown.setEnabled(false);
            if (selectedRow > 0) {
                jButtonUp.setEnabled(true);
            } else
                jButtonUp.setEnabled(false);
        } else {
            jButtonUp.setEnabled(false);
            jButtonDown.setEnabled(false);
            jButtonDelete.setEnabled(false);
        }
    }
    private void setCheckBoxesColor() {
        if (jTableMain.getRowCount() > 0) {
           actualLength = list.get(selectedRow).getLength();
           for(int i = 0; i < 5; i++) {     
                for(int j = 0; j < 8; j++) {
                    if (checkEnable[(actualLength)][i] == true) {
                        if (checkBoxes[i][j].isSelected())
                            checkBoxes[i][j].setBackground(new Color(0, 0, 0));
                        else 
                            checkBoxes[i][j].setBackground(new Color(240, 240, 240));
                    } else 
                        checkBoxes[i][j].setBackground(Color.LIGHT_GRAY);
                }
            } 
        }
    }
    private void setCheckBoxColor(JCheckBox box) {
        //System.out.println("Before: "+ actualLength);
        if (jTableMain.getRowCount() > 0) {
            if (list.get(selectedRow).getLength() != actualLength) {
                actualLength = list.get(selectedRow).getLength();
                setCheckBoxesColor();
            } else {
                
                if (!box.getBackground().equals(Color.LIGHT_GRAY)) {  
                    if (box.isSelected()) {
                        box.setBackground(new Color(0, 0, 0));
                    } else 
                        box.setBackground(new Color(240, 240, 240));
                }
            } 
        } 
    }
    
    private void loadASCIIToList() {
        DefaultTableModel model = (DefaultTableModel)this.jTableMain.getModel();
        deleteAllRows(model);
        
        for (Alphabet_Char item : list) {
            Vector row = new Vector();
            row.add(item.getId());
            row.add(item.getSign());
            row.add(item.getDescription());
            row.add(item.getModifiedDots());
            row.add(item.getLength());
            model.addRow(row);
        }
    }
    
    private void refreshElementInList() {
        Alphabet_Char item = list.get(selectedRow);
        DefaultTableModel model = (DefaultTableModel)this.jTableMain.getModel();
        
        model.setValueAt(item.getId(), selectedRow, 0);
        model.setValueAt(String.valueOf(item.getSign()), selectedRow, 1);
        model.setValueAt(item.getDescription(), selectedRow, 2);
        model.setValueAt(item.getModifiedDots(), selectedRow, 3);    
        model.setValueAt(item.getLength(), selectedRow, 4);
    }

    /**
     * // clear model of fFrame
     * @param model to clear
     */
    public static void deleteAllRows(final DefaultTableModel model) {
        for( int i = model.getRowCount() - 1; i >= 0; i-- ) {
            model.removeRow(i);
        }
    }
    private void checkboxClick(JCheckBox box) {
        generateNumbers();
        numbersToText();
        if (jTableMain.getRowCount() > 0) {
            list.get(selectedRow).setCodes(bytes);
            refreshElementInList();
        }
        setCheckBoxColor(box);
    }
    private void generateNumbers() {
        for(int i = 0; i < 5; i++) {
            bytes[i] = 0;
            for (int j = 0; j < 8; j++) {
                if (checkBoxes[i][j].isSelected())
                    bytes[i] += (1 << j);
            }
        }
    }
    private void numbersToText() {
        String str = "";
        for(int i = 0; i < 5; i++) {
            str += String.format("%3d", bytes[i])+" ";
        }
        jLabelNumbers.setText(str);
    }
    
    private void clearCheckBoxes() {
        enableCheckListener = false;
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 8; j++) {
                checkBoxes[i][j].setSelected(false);

            }
        }
        enableCheckListener = true; 
    }
    
    private void refreshList() {
        loadASCIIToList();
        tryEnableButtons();
    }
    
    private void itemToCheckboxes() {
        bytes = list.get(selectedRow).getCodes();
        enableCheckListener = false;
        for(int i = 0; i < 5; i++) {
            int copy = bytes[i];
            for(int j = 0; j < 8; j++) {
                checkBoxes[i][j].setSelected((copy % 2) == 1);
                copy >>= 1;
            }  
        }
        enableCheckListener = true;
        setCheckBoxesColor();
        numbersToText();
    }
    
    private void addItem(Alphabet_Char newItem) {
        try {
            // repair selectedRow
            //selectedRow = list.tryAdd(new Alphabet_Char(newId));
            int newSelected = list.tryAdd(newItem);
            refreshList();
            jTableMain.setRowSelectionInterval(newSelected, newSelected);
            jTableMain.scrollRectToVisible(new Rectangle(jTableMain.getCellRect(newSelected, 0, true)));
        } catch (IllegalAccessException e) {
            System.out.println(e.toString());
        }   
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTableMain = new javax.swing.JTable();
        jButtonAdd = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jButtonSaveToFile = new javax.swing.JButton();
        jButtonUp = new javax.swing.JButton();
        jButtonDown = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jCheckPanel = new javax.swing.JPanel();
        jCheck_00 = new javax.swing.JCheckBox();
        jCheck_01 = new javax.swing.JCheckBox();
        jCheck_02 = new javax.swing.JCheckBox();
        jCheck_03 = new javax.swing.JCheckBox();
        jCheck_04 = new javax.swing.JCheckBox();
        jCheck_05 = new javax.swing.JCheckBox();
        jCheck_06 = new javax.swing.JCheckBox();
        jCheck_07 = new javax.swing.JCheckBox();
        jCheck_10 = new javax.swing.JCheckBox();
        jCheck_11 = new javax.swing.JCheckBox();
        jCheck_12 = new javax.swing.JCheckBox();
        jCheck_13 = new javax.swing.JCheckBox();
        jCheck_14 = new javax.swing.JCheckBox();
        jCheck_15 = new javax.swing.JCheckBox();
        jCheck_16 = new javax.swing.JCheckBox();
        jCheck_17 = new javax.swing.JCheckBox();
        jCheck_20 = new javax.swing.JCheckBox();
        jCheck_21 = new javax.swing.JCheckBox();
        jCheck_22 = new javax.swing.JCheckBox();
        jCheck_23 = new javax.swing.JCheckBox();
        jCheck_24 = new javax.swing.JCheckBox();
        jCheck_25 = new javax.swing.JCheckBox();
        jCheck_26 = new javax.swing.JCheckBox();
        jCheck_27 = new javax.swing.JCheckBox();
        jCheck_30 = new javax.swing.JCheckBox();
        jCheck_31 = new javax.swing.JCheckBox();
        jCheck_32 = new javax.swing.JCheckBox();
        jCheck_33 = new javax.swing.JCheckBox();
        jCheck_34 = new javax.swing.JCheckBox();
        jCheck_35 = new javax.swing.JCheckBox();
        jCheck_36 = new javax.swing.JCheckBox();
        jCheck_37 = new javax.swing.JCheckBox();
        jCheck_40 = new javax.swing.JCheckBox();
        jCheck_41 = new javax.swing.JCheckBox();
        jCheck_42 = new javax.swing.JCheckBox();
        jCheck_43 = new javax.swing.JCheckBox();
        jCheck_44 = new javax.swing.JCheckBox();
        jCheck_45 = new javax.swing.JCheckBox();
        jCheck_46 = new javax.swing.JCheckBox();
        jCheck_47 = new javax.swing.JCheckBox();
        jButtonBitsDown = new javax.swing.JButton();
        jButtonBitsUp = new javax.swing.JButton();
        jLabelNumbers = new javax.swing.JLabel();
        jButtonClear = new javax.swing.JButton();
        jButtonBytesDown = new javax.swing.JButton();
        jButtonBytesUp = new javax.swing.JButton();
        jButtonAddCopy = new javax.swing.JButton();
        jStatusBar = new javax.swing.JLabel();
        jLabelDyskietka = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jTableMain.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Char", "Description", "Dots", "Length"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableMain.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTableMainPropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(jTableMain);
        if (jTableMain.getColumnModel().getColumnCount() > 0) {
            jTableMain.getColumnModel().getColumn(0).setMinWidth(50);
            jTableMain.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTableMain.getColumnModel().getColumn(0).setMaxWidth(50);
            jTableMain.getColumnModel().getColumn(1).setMinWidth(50);
            jTableMain.getColumnModel().getColumn(1).setPreferredWidth(50);
            jTableMain.getColumnModel().getColumn(1).setMaxWidth(50);
            jTableMain.getColumnModel().getColumn(2).setResizable(false);
            jTableMain.getColumnModel().getColumn(3).setMinWidth(50);
            jTableMain.getColumnModel().getColumn(3).setPreferredWidth(50);
            jTableMain.getColumnModel().getColumn(3).setMaxWidth(50);
            jTableMain.getColumnModel().getColumn(4).setMinWidth(55);
            jTableMain.getColumnModel().getColumn(4).setPreferredWidth(55);
            jTableMain.getColumnModel().getColumn(4).setMaxWidth(55);
        }

        jButtonAdd.setText("Add");
        jButtonAdd.setMaximumSize(new java.awt.Dimension(63, 23));
        jButtonAdd.setMinimumSize(new java.awt.Dimension(63, 23));
        jButtonAdd.setPreferredSize(new java.awt.Dimension(63, 23));
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        jButtonDelete.setText("Delete");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });

        jButtonSaveToFile.setText("Generate");
        jButtonSaveToFile.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButtonSaveToFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveToFileActionPerformed(evt);
            }
        });

        jButtonUp.setText("▲");
        jButtonUp.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButtonUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpActionPerformed(evt);
            }
        });

        jButtonDown.setText("▼");
        jButtonDown.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButtonDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDownActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jCheckPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jCheck_06.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheck_06ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jCheckPanelLayout = new javax.swing.GroupLayout(jCheckPanel);
        jCheckPanel.setLayout(jCheckPanelLayout);
        jCheckPanelLayout.setHorizontalGroup(
            jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jCheckPanelLayout.createSequentialGroup()
                .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jCheckPanelLayout.createSequentialGroup()
                        .addComponent(jCheck_00)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_40))
                    .addGroup(jCheckPanelLayout.createSequentialGroup()
                        .addComponent(jCheck_01)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_41))
                    .addGroup(jCheckPanelLayout.createSequentialGroup()
                        .addComponent(jCheck_02)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_42))
                    .addGroup(jCheckPanelLayout.createSequentialGroup()
                        .addComponent(jCheck_03)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_43))
                    .addGroup(jCheckPanelLayout.createSequentialGroup()
                        .addComponent(jCheck_04)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_24, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_44))
                    .addGroup(jCheckPanelLayout.createSequentialGroup()
                        .addComponent(jCheck_05)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_45))
                    .addGroup(jCheckPanelLayout.createSequentialGroup()
                        .addComponent(jCheck_06)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_46))
                    .addGroup(jCheckPanelLayout.createSequentialGroup()
                        .addComponent(jCheck_07)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheck_47)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jCheckPanelLayout.setVerticalGroup(
            jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jCheckPanelLayout.createSequentialGroup()
                .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jCheckPanelLayout.createSequentialGroup()
                        .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jCheckPanelLayout.createSequentialGroup()
                                .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jCheckPanelLayout.createSequentialGroup()
                                        .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jCheckPanelLayout.createSequentialGroup()
                                                .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addGroup(jCheckPanelLayout.createSequentialGroup()
                                                        .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                            .addGroup(jCheckPanelLayout.createSequentialGroup()
                                                                .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                    .addComponent(jCheck_00)
                                                                    .addComponent(jCheck_10)
                                                                    .addComponent(jCheck_20)
                                                                    .addComponent(jCheck_30)
                                                                    .addComponent(jCheck_40))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                    .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jCheck_01)
                                                                        .addComponent(jCheck_21, javax.swing.GroupLayout.Alignment.TRAILING))
                                                                    .addComponent(jCheck_31)
                                                                    .addComponent(jCheck_41)))
                                                            .addComponent(jCheck_11))
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                            .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(jCheck_02)
                                                                .addComponent(jCheck_22, javax.swing.GroupLayout.Alignment.TRAILING))
                                                            .addComponent(jCheck_32)
                                                            .addComponent(jCheck_42)))
                                                    .addComponent(jCheck_12))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jCheck_03)
                                                        .addComponent(jCheck_23, javax.swing.GroupLayout.Alignment.TRAILING))
                                                    .addComponent(jCheck_33)
                                                    .addComponent(jCheck_43)))
                                            .addComponent(jCheck_13))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(jCheck_04)
                                                .addComponent(jCheck_34)
                                                .addComponent(jCheck_44))
                                            .addComponent(jCheck_24, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jCheck_14))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jCheck_05)
                                        .addComponent(jCheck_25, javax.swing.GroupLayout.Alignment.TRAILING))
                                    .addComponent(jCheck_35)
                                    .addComponent(jCheck_45)))
                            .addComponent(jCheck_15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jCheck_06)
                                .addComponent(jCheck_26, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(jCheck_36)
                            .addComponent(jCheck_46)))
                    .addComponent(jCheck_16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jCheckPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCheck_07)
                    .addComponent(jCheck_17)
                    .addComponent(jCheck_27)
                    .addComponent(jCheck_37)
                    .addComponent(jCheck_47))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButtonBitsDown.setText("▲");
        jButtonBitsDown.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButtonBitsDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBitsDownActionPerformed(evt);
            }
        });

        jButtonBitsUp.setText("▼");
        jButtonBitsUp.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButtonBitsUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBitsUpActionPerformed(evt);
            }
        });

        jLabelNumbers.setFont(new java.awt.Font("Courier New", 1, 9)); // NOI18N
        jLabelNumbers.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelNumbers.setText("jLabel5");

        jButtonClear.setText("Clear");
        jButtonClear.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButtonClear.setMaximumSize(new java.awt.Dimension(63, 23));
        jButtonClear.setMinimumSize(new java.awt.Dimension(63, 23));
        jButtonClear.setPreferredSize(new java.awt.Dimension(63, 23));
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });

        jButtonBytesDown.setText("◀");
        jButtonBytesDown.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButtonBytesDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBytesDownActionPerformed(evt);
            }
        });

        jButtonBytesUp.setText("▶");
        jButtonBytesUp.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButtonBytesUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBytesUpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelNumbers, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButtonBitsDown)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonClear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonBitsUp))
                    .addComponent(jCheckPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonBytesDown)
                    .addComponent(jButtonBytesUp))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jCheckPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addComponent(jButtonBytesDown)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonBytesUp)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelNumbers)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonClear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBitsDown)
                    .addComponent(jButtonBitsUp))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButtonAddCopy.setText("Add copy");
        jButtonAddCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddCopyActionPerformed(evt);
            }
        });

        jStatusBar.setBackground(new java.awt.Color(200, 200, 200));
        jStatusBar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jStatusBar.setLabelFor(this);
        jStatusBar.setText("not modified");
        jStatusBar.setToolTipText("");
        jStatusBar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jStatusBar.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);

        jLabelDyskietka.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelDyskietka.setIcon(new javax.swing.ImageIcon(getClass().getResource("/alphabet_generator/dyskietka_318-127696.jpg"))); // NOI18N
        jLabelDyskietka.setMaximumSize(new java.awt.Dimension(32, 32));
        jLabelDyskietka.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelDyskietka, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jStatusBar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonAddCopy)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonUp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonDown)
                        .addGap(101, 101, 101)
                        .addComponent(jButtonSaveToFile, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButtonUp)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonDown)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButtonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButtonAddCopy)
                                .addComponent(jButtonSaveToFile)
                                .addComponent(jStatusBar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButtonDelete)
                            .addComponent(jLabelDyskietka, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 11, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheck_06ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheck_06ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheck_06ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        trySaveCSV();
    }//GEN-LAST:event_formWindowClosing

    private void jTableMainPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTableMainPropertyChange
        //System.out.println("main property change");
        if (jTableMain.getRowCount() > 0) {
            modifiedData = true;
            jStatusBar.setText(MODIFIED);
            if (list.get(selectedRow).getSign() != ((String)jTableMain.getValueAt(selectedRow, 1))) {
                //System.out.println("different chars");
                list.get(selectedRow).setSign(((String)jTableMain.getValueAt(selectedRow, 1)));
            }
            if (list.get(selectedRow).getDescription() != (String)jTableMain.getValueAt(selectedRow, 2)) {
                //System.out.println("different description");
                list.get(selectedRow).setDescription((String)jTableMain.getValueAt(selectedRow, 2));
            }
            if (list.get(selectedRow).getId() != (int)jTableMain.getValueAt(selectedRow, 0)) {
                int newId = (int)jTableMain.getValueAt(selectedRow, 0);
                int newSelected = 0;
                if (list.isIdInList(newId)) {
                    newSelected = list.tryFindIndex(newId);
                    list.swapIndexes(selectedRow, newSelected);
                } else {
                    try {
                        newSelected = list.renameItemId(selectedRow, newId);
                    } catch (IllegalAccessException e) {
                        newSelected = list.tryFindIndex(newId);
                        System.out.println(e.toString());
                        jTableMain.setRowSelectionInterval(newSelected, newSelected);
                    } 
                }
                trySaveCSV();
                refreshList();
                jTableMain.setRowSelectionInterval(newSelected, newSelected); 
                jTableMain.scrollRectToVisible(new Rectangle(jTableMain.getCellRect(newSelected, 0, true)));
            }
        }
    }//GEN-LAST:event_jTableMainPropertyChange

    private void jButtonBytesDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBytesDownActionPerformed
        if (list.get(selectedRow).getModifiedDots() > 0) {
            modifiedData = true;
            jStatusBar.setText(MODIFIED);
        }
        list.get(selectedRow).shiftBytes(-1);
        openSigleItem();
        
    }//GEN-LAST:event_jButtonBytesDownActionPerformed

    private void jButtonUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpActionPerformed
        if (jButtonUp.isEnabled()) {
            list.swapIndexes(selectedRow, selectedRow - 1);
            // repair selectedRow
            //selectedRow = selectedRow - 1;
            refreshList();
            jTableMain.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
            
        }
    }//GEN-LAST:event_jButtonUpActionPerformed

    private void jButtonDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDownActionPerformed
        if (jButtonDown.isEnabled()) {
            list.swapIndexes(selectedRow, selectedRow + 1);
            // repair selectedRow
            //selectedRow = selectedRow + 1;
            refreshList();
            jTableMain.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
            
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonDownActionPerformed

    private void jButtonSaveToFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveToFileActionPerformed
        trySaveCSV();
        AVR_Save avr = new AVR_Save(list);
        String paths = "";
        try {
            paths = avr.saveHeader()+"\n";
            paths+=avr.saveC();
            JOptionPane.showMessageDialog(null, "Created files:\n"+paths);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null, 
                    "Erron on tryiny create files:\n" + e.toString(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } 
    }//GEN-LAST:event_jButtonSaveToFileActionPerformed

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        //selectedRow = jTableMain.getSelectedRow();
        int newId = list.getNextEmptyId(jTableMain.getSelectedRow());
        addItem(new Alphabet_Char(newId));
        modifiedData = true;
        jStatusBar.setText(MODIFIED);
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        if (jTableMain.getRowCount() > 0) {
            if (JOptionPane.showConfirmDialog(
            this,
            "Are you sure to removing/clearing '" + list.get(selectedRow).getSign()+ "' char?",
            "Question - removing/clearing char",
            JOptionPane.YES_NO_OPTION) == 0) {
                try {
                    list.remove(selectedRow);
                    modifiedData = true;
                    jStatusBar.setText(MODIFIED);
                } catch (IllegalAccessException e) {
                   JOptionPane.showMessageDialog(
                           null, 
                           "Error on removing " + selectedRow + " from list",
                           "Error message",
                           JOptionPane.ERROR_MESSAGE);
                }
                refreshList();
                //System.out.println(selectedRow);
                if (list.getSize() == selectedRow) {
                    jTableMain.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);  
                } else {
                    int newSelected = selectedRow;
                    selectedRow = -1;
                    jTableMain.setRowSelectionInterval(newSelected, newSelected);  
                }

                /*System.out.println(selectedRow + " " + jTableMain.getRowCount());
                if (selectedRow == jTableMain.getRowCount() - 1)  {
                    System.out.println("last");
                    jTableMain.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
                } else 
                jTableMain.setRowSelectionInterval(selectedRow, selectedRow);*/
                
            }
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        if (JOptionPane.showConfirmDialog(
        this,
        "Are you sure to clear content window?",
        "Question - clearing window",
        JOptionPane.YES_NO_OPTION) == 0) {
            clearCheckBoxes();
            actualLength = 0;
            checkboxClick(jCheck_00);
            modifiedData = true;
            jStatusBar.setText(MODIFIED);
        } 
    }//GEN-LAST:event_jButtonClearActionPerformed

    private void jButtonBytesUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBytesUpActionPerformed
        if (list.get(selectedRow).getModifiedDots() > 0) {
            modifiedData = true;
            jStatusBar.setText(MODIFIED);
        }
        list.get(selectedRow).shiftBytes(1);
        openSigleItem();
    }//GEN-LAST:event_jButtonBytesUpActionPerformed

    private void jButtonBitsDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBitsDownActionPerformed
        list.get(selectedRow).shiftBits(-1);
        openSigleItem();
        modifiedData = true;
        jStatusBar.setText(MODIFIED);
    }//GEN-LAST:event_jButtonBitsDownActionPerformed

    private void jButtonBitsUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBitsUpActionPerformed
        list.get(selectedRow).shiftBits(1);
        openSigleItem();
        modifiedData = true;
        jStatusBar.setText(MODIFIED);
    }//GEN-LAST:event_jButtonBitsUpActionPerformed

    private void jButtonAddCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddCopyActionPerformed
        int newId = list.getNextEmptyId(jTableMain.getSelectedRow());
        addItem(new Alphabet_Char(list.get(selectedRow), newId));
        modifiedData = true;
        jStatusBar.setText(MODIFIED);
    }//GEN-LAST:event_jButtonAddCopyActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main_Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main_Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main_Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main_Frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main_Frame("empty.csv").setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonAddCopy;
    private javax.swing.JButton jButtonBitsDown;
    private javax.swing.JButton jButtonBitsUp;
    private javax.swing.JButton jButtonBytesDown;
    private javax.swing.JButton jButtonBytesUp;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonDown;
    private javax.swing.JButton jButtonSaveToFile;
    private javax.swing.JButton jButtonUp;
    private javax.swing.JPanel jCheckPanel;
    private javax.swing.JCheckBox jCheck_00;
    private javax.swing.JCheckBox jCheck_01;
    private javax.swing.JCheckBox jCheck_02;
    private javax.swing.JCheckBox jCheck_03;
    private javax.swing.JCheckBox jCheck_04;
    private javax.swing.JCheckBox jCheck_05;
    private javax.swing.JCheckBox jCheck_06;
    private javax.swing.JCheckBox jCheck_07;
    private javax.swing.JCheckBox jCheck_10;
    private javax.swing.JCheckBox jCheck_11;
    private javax.swing.JCheckBox jCheck_12;
    private javax.swing.JCheckBox jCheck_13;
    private javax.swing.JCheckBox jCheck_14;
    private javax.swing.JCheckBox jCheck_15;
    private javax.swing.JCheckBox jCheck_16;
    private javax.swing.JCheckBox jCheck_17;
    private javax.swing.JCheckBox jCheck_20;
    private javax.swing.JCheckBox jCheck_21;
    private javax.swing.JCheckBox jCheck_22;
    private javax.swing.JCheckBox jCheck_23;
    private javax.swing.JCheckBox jCheck_24;
    private javax.swing.JCheckBox jCheck_25;
    private javax.swing.JCheckBox jCheck_26;
    private javax.swing.JCheckBox jCheck_27;
    private javax.swing.JCheckBox jCheck_30;
    private javax.swing.JCheckBox jCheck_31;
    private javax.swing.JCheckBox jCheck_32;
    private javax.swing.JCheckBox jCheck_33;
    private javax.swing.JCheckBox jCheck_34;
    private javax.swing.JCheckBox jCheck_35;
    private javax.swing.JCheckBox jCheck_36;
    private javax.swing.JCheckBox jCheck_37;
    private javax.swing.JCheckBox jCheck_40;
    private javax.swing.JCheckBox jCheck_41;
    private javax.swing.JCheckBox jCheck_42;
    private javax.swing.JCheckBox jCheck_43;
    private javax.swing.JCheckBox jCheck_44;
    private javax.swing.JCheckBox jCheck_45;
    private javax.swing.JCheckBox jCheck_46;
    private javax.swing.JCheckBox jCheck_47;
    private javax.swing.JLabel jLabelDyskietka;
    private javax.swing.JLabel jLabelNumbers;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jStatusBar;
    private javax.swing.JTable jTableMain;
    // End of variables declaration//GEN-END:variables
}
