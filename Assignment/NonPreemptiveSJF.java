import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Arrays;

class Process {
    int pid; // Process ID
    int at; // Arrival Time
    int bt; // Burst Time
    int ct, wt, tat, start_time; // Completion Time, Waiting Time, Turn Around Time, start time
}

public class NonPreemptiveSJFGUI {
    private JFrame frame;
    private JTextField numProcessesField;
    private JButton btnRun;
    private JButton btnHome;
    private JTextArea ganttChartArea;
    private JTable processTable;
    private DefaultTableModel tableModel;
    private JLabel lblTotalTurnaround;
    private JLabel lblAvgTurnaround;
    private JLabel lblTotalWaiting;
    private JLabel lblAvgWaiting;

    public NonPreemptiveSJFGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblNumProcesses = new JLabel("Number of Processes:");
        lblNumProcesses.setBounds(10, 10, 150, 20);
        frame.getContentPane().add(lblNumProcesses);

        numProcessesField = new JTextField();
        numProcessesField.setBounds(170, 10, 50, 20);
        frame.getContentPane().add(numProcessesField);

        btnRun = new JButton("Run Non-Preemptive SJF");
        btnRun.setBounds(10, 40, 200, 30);
        frame.getContentPane().add(btnRun);

        ganttChartArea = new JTextArea();
        ganttChartArea.setEditable(false);
        JScrollPane ganttScrollPane = new JScrollPane(ganttChartArea);
        ganttScrollPane.setBounds(10, 80, 760, 100);
        frame.getContentPane().add(ganttScrollPane);

        lblTotalTurnaround = new JLabel("Total Turnaround Time:");
        lblTotalTurnaround.setBounds(10, 190, 200, 20);
        frame.getContentPane().add(lblTotalTurnaround);

        lblAvgTurnaround = new JLabel("Average Turnaround Time:");
        lblAvgTurnaround.setBounds(10, 220, 200, 20);
        frame.getContentPane().add(lblAvgTurnaround);

        lblTotalWaiting = new JLabel("Total Waiting Time:");
        lblTotalWaiting.setBounds(10, 250, 200, 20);
        frame.getContentPane().add(lblTotalWaiting);

        lblAvgWaiting = new JLabel("Average Waiting Time:");
        lblAvgWaiting.setBounds(10, 280, 200, 20);
        frame.getContentPane().add(lblAvgWaiting);

        JScrollPane processScrollPane = new JScrollPane();
        processScrollPane.setBounds(10, 310, 760, 240);
        frame.getContentPane().add(processScrollPane);

        String[] columnNames = { "Process", "Arrival Time", "Burst Time", "Finish Time", "Turnaround Time",
                "Waiting Time" };
        tableModel = new DefaultTableModel(columnNames, 0);
        processTable = new JTable(tableModel);
        processScrollPane.setViewportView(processTable);

        btnRun.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executeNonPreemptiveSJF();
            }
        });
        btnHome = new JButton("Return to Homepage");
        btnHome.setBounds(10, 540, 200, 30);
        frame.getContentPane().add(btnHome);

        btnHome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToHomepage();
            }
        });
    }

    private void executeNonPreemptiveSJF() {
        try {
            int n;
            n = Integer.parseInt(numProcessesField.getText());
            if (n < 3 || n > 10) {
                JOptionPane.showMessageDialog(frame,
                        "Number of processes should be between 3 and 10. Please try again.");
                return;
            }

            float sum_tat = 0, sum_wt = 0;
            Process[] ps = new Process[n];

            for (int i = 0; i < n; i++) {
                ps[i] = new Process();
                ps[i].pid = i;
                ps[i].at = Integer.parseInt(JOptionPane.showInputDialog("Enter arrival time for Process P" + i));
                ps[i].bt = Integer.parseInt(JOptionPane.showInputDialog("Enter burst time for Process P" + i));
            }

            Arrays.sort(ps, (p1, p2) -> Integer.compare(p1.at, p2.at));

            StringBuilder ganttChart = new StringBuilder("|");
            StringBuilder upperLine = new StringBuilder("-");
            StringBuilder lowerLine = new StringBuilder("-");
            int timeUnitWidth = 5;

            int current_time = 0, completed = 0;

            while (completed != n) {
                int min_index = -1;
                int minimum = Integer.MAX_VALUE;

                for (int i = 0; i < n; i++) {
                    if (ps[i].at <= current_time && ps[i].bt < minimum && ps[i].ct == 0) {// Process is available at current time
                        minimum = ps[i].bt; // Find the process with minimum burst time
                        min_index = i; // Store the index of the process with minimum burst time
                    }
                }

                if (min_index == -1) {// No process is available at current time
                    current_time++;
                } else {
                    
                    upperLine.append("-".repeat(Math.max(0, ps[min_index].bt))).append("------");
                    ganttChart.append(String.format(" %-" + timeUnitWidth + "s |", "P" + ps[min_index].pid));
                    lowerLine.append("-".repeat(Math.max(0, ps[min_index].bt))).append("------");
                    ps[min_index].start_time = current_time;
                    ps[min_index].ct = ps[min_index].start_time + ps[min_index].bt;
                    ps[min_index].tat = ps[min_index].ct - ps[min_index].at;
                    ps[min_index].wt = ps[min_index].tat - ps[min_index].bt;

                    sum_tat += ps[min_index].tat;
                    sum_wt += ps[min_index].wt;

                    completed++;
                    current_time = ps[min_index].ct;
                }
            }

            int chartLength = ganttChart.length();
            upperLine.setLength(chartLength);
            lowerLine.setLength(chartLength);

            ganttChartArea.setText("\nGantt Chart:\n");
            ganttChartArea.append(upperLine + "\n");
            ganttChartArea.append(ganttChart + "\n");
            ganttChartArea.append(lowerLine + "\n");

            Arrays.sort(ps, (p1, p2) -> Integer.compare(p1.pid, p2.pid)); // Sort by process ID

            tableModel.setRowCount(0);
            for (int i = 0; i < n; i++) {
                tableModel.addRow(new Object[] {
                        "P" + ps[i].pid, ps[i].at, ps[i].bt, ps[i].ct, ps[i].tat, ps[i].wt
                });
            }

            DecimalFormat df = new DecimalFormat("#.##");
            lblTotalTurnaround.setText("Total Turnaround Time: " + sum_tat);
            lblAvgTurnaround.setText("Average Turnaround Time: " + df.format(sum_tat / n));
            lblTotalWaiting.setText("Total Waiting Time: " + sum_wt);
            lblAvgWaiting.setText("Average Waiting Time: " + df.format((float) sum_wt / n));

            JOptionPane.showMessageDialog(frame, "Non-Preemptive SJF Scheduling Completed!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void display1() {
        frame.setVisible(true);
    }

    private void returnToHomepage() {
        frame.dispose(); // Close the current frame
        Main.displayHomepage(); // Call a method in Main class to display the homepage
    }

}
