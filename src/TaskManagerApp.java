import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

public class TaskManagerApp extends JFrame {
    private DefaultTableModel taskTableModel;
    private JTable taskTable;
    private JTextField taskInputField;
    private JComboBox<String> dayComboBox;
    private JComboBox<String> priorityComboBox;
    private JButton addButton;
    private JButton removeButton;
    private JButton loadButton;
    private JButton saveButton; 
    private TableRowSorter<DefaultTableModel> sorter;

    public TaskManagerApp() {
        setTitle("Task Manager");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String[] columnNames = {"曜日", "タスク", "優先度", "完了"};
        taskTableModel = new DefaultTableModel(null, columnNames) {
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        taskTable = new JTable(taskTableModel);
        sorter = new TableRowSorter<>(taskTableModel);
        taskTable.setRowSorter(sorter);
        taskInputField = new JTextField(20);
        dayComboBox = new JComboBox<>(new String[]{"月曜日", "火曜日", "水曜日", "木曜日", "金曜日", "土曜日", "日曜日"});
        priorityComboBox = new JComboBox<>(new String[]{"低", "中", "高"});

        addButton = new JButton("タスク追加");
        removeButton = new JButton("タスク削除");
        loadButton = new JButton("データ読み込み");
        saveButton = new JButton("データ保存"); 

        setLayout(new BorderLayout());

        add(new JScrollPane(taskTable), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.add(dayComboBox);
        inputPanel.add(taskInputField);
        inputPanel.add(priorityComboBox);
        inputPanel.add(addButton);
        inputPanel.add(removeButton);
        inputPanel.add(loadButton);
        inputPanel.add(saveButton);
        add(inputPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addTask();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeTask();
            }
        });

        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadAddressData();
            }
        });

        saveButton.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
                saveAddressData();
            }
        });

        taskTable.getColumnModel().getColumn(3).setCellRenderer(new CheckBoxRenderer());
        taskTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JCheckBox()));

        taskTable.setDefaultEditor(Object.class, null);
    }

    private void addTask() {
        String day = (String) dayComboBox.getSelectedItem();
        String task = taskInputField.getText();
        String priority = (String) priorityComboBox.getSelectedItem();
        boolean completed = false; 

        Object[] rowData = {day, task, priority, completed};
        taskTableModel.addRow(rowData);
        clearInputFields();
    }

    private void removeTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow != -1) {
            taskTableModel.removeRow(selectedRow);
        }
    }

    private void loadAddressData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("task.txt"))) {
            clearTable();
            String line;
            while ((line = reader.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, ",");
                String day = tokenizer.nextToken().trim();
                String task = tokenizer.nextToken().trim();
                String priority = tokenizer.nextToken().trim();
                boolean completed = Boolean.parseBoolean(tokenizer.nextToken().trim());

                Object[] rowData = {day, task, priority, completed};
                taskTableModel.addRow(rowData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAddressData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("task.txt"))) {
            for (int i = 0; i < taskTableModel.getRowCount(); i++) {
                String day = taskTableModel.getValueAt(i, 0).toString();
                String task = taskTableModel.getValueAt(i, 1).toString();
                String priority = taskTableModel.getValueAt(i, 2).toString();
                String completed = taskTableModel.getValueAt(i, 3).toString();

                writer.write(day + "," + task + "," + priority + "," + completed);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearInputFields() {
        taskInputField.setText("");
        dayComboBox.setSelectedIndex(0);
        priorityComboBox.setSelectedIndex(0);
    }

    private void clearTable() {
        int rowCount = taskTableModel.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            taskTableModel.removeRow(i);
        }
    }

    private class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        public CheckBoxRenderer() {
            setHorizontalAlignment(JCheckBox.CENTER);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setSelected((value != null && ((Boolean) value)));
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TaskManagerApp().setVisible(true);
            }
        });
    }
}
