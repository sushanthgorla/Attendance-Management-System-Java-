package A;

import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import javax.swing.*;

public class AttendanceManagement {

    JFrame frame;
    ArrayList<String> students = new ArrayList<>();
    ArrayList<JCheckBox> checkBoxes;
    File attendanceFile = new File("attendance_records.txt");

    AttendanceManagement() {
        frame = new JFrame("Attendance Management System");
        frame.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Attendance Management System", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Student");
        JButton markBtn = new JButton("Mark Attendance");
        JButton percentBtn = new JButton("Show Attendance %");

        buttonPanel.add(addBtn);
        buttonPanel.add(markBtn);
        buttonPanel.add(percentBtn);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        JTextArea info = new JTextArea(
                "Welcome!\n\n" +
                "1) Click 'Add Student' to add student names.\n" +
                "2) Click 'Mark Attendance' to mark attendance.\n" +
                "3) Click 'Show Attendance %' to view attendance percentage."
        );
        info.setEditable(false);
        info.setFont(new Font("Arial", Font.PLAIN, 14));
        frame.add(info, BorderLayout.CENTER);

        addBtn.addActionListener(e -> addStudent());
        markBtn.addActionListener(e -> markAttendance());
        percentBtn.addActionListener(e -> showPercentages());

        frame.setSize(500, 350);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    void addStudent() {
        String name = JOptionPane.showInputDialog(frame, "Enter student name:");
        if (name != null && !name.trim().isEmpty()) {
            students.add(name.trim());
            JOptionPane.showMessageDialog(frame, "Student added successfully!");
        }
    }

    void markAttendance() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No students added yet!");
            return;
        }

        String date = JOptionPane.showInputDialog(
                frame,
                "Enter Date (YYYY-MM-DD):",
                LocalDate.now().toString()
        );

        if (date == null || date.trim().isEmpty()) return;

        JFrame markFrame = new JFrame("Mark Attendance - " + date);
        markFrame.setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel(new GridLayout(students.size(), 1));
        checkBoxes = new ArrayList<>();

        for (String s : students) {
            JCheckBox cb = new JCheckBox(s);
            checkBoxes.add(cb);
            panel.add(cb);
        }

        JButton saveBtn = new JButton("Save Attendance");
        saveBtn.addActionListener(e -> saveAttendance(date, markFrame));

        markFrame.add(new JScrollPane(panel), BorderLayout.CENTER);
        markFrame.add(saveBtn, BorderLayout.SOUTH);

        markFrame.setSize(300, 300);
        markFrame.setLocationRelativeTo(frame);
        markFrame.setVisible(true);
    }

    void saveAttendance(String date, JFrame markFrame) {
        try (FileWriter writer = new FileWriter(attendanceFile, true)) {
            writer.write("Date: " + date + "\n");
            for (JCheckBox cb : checkBoxes) {
                writer.write(cb.getText() + " - " +
                        (cb.isSelected() ? "Present" : "Absent") + "\n");
            }
            writer.write("------------------------------------\n");
            JOptionPane.showMessageDialog(frame, "Attendance saved!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        markFrame.dispose();
    }

    void showPercentages() {
        if (!attendanceFile.exists()) {
            JOptionPane.showMessageDialog(frame, "No attendance records found!");
            return;
        }

        Map<String, Integer> totalDays = new HashMap<>();
        Map<String, Integer> presentDays = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(attendanceFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.contains(" - ")) {
                    String[] parts = line.split(" - ");
                    String name = parts[0].trim();
                    String status = parts[1].trim();

                    totalDays.put(name, totalDays.getOrDefault(name, 0) + 1);
                    if (status.equalsIgnoreCase("Present")) {
                        presentDays.put(name, presentDays.getOrDefault(name, 0) + 1);
                    }
                }
            }
            
            StringBuilder sb = new StringBuilder("Attendance Percentage:\n\n");
            for (String s : students) {
                int total = totalDays.getOrDefault(s, 0);
                int present = presentDays.getOrDefault(s, 0);
                double percent = (total == 0) ? 0 : (present * 100.0 / total);
                sb.append(s).append(": ")
                  .append(String.format("%.2f", percent))
                  .append("%\n");
            }

            JOptionPane.showMessageDialog(frame, sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new AttendanceManagement();
    }
}