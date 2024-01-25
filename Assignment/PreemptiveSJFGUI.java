import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

class Progress {
    int pid; // Process ID
    int at; // Arrival Time
    int bt; // Burst Time
    int ct, wt, tat, start_time; // Completion Time, Waiting Time, Turn Around Time, start time
}

public class PreemptiveSJFGUI {
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

    public PreemptiveSJFGUI() {
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

        btnRun = new JButton("Run Preemptive SJF");
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
                executePreemptiveSJF();
            }
        });

        // Create a new button for returning to homepage
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

    private void executePreemptiveSJF() {
        try {
            int n;
            n = Integer.parseInt(numProcessesField.getText());
            if (n < 3 || n > 10) {
                JOptionPane.showMessageDialog(frame,
                        "Number of processes should be between 3 and 10. Please try again.");
                return;
            }

            float[] bt_remaining = new float[n];
            boolean[] is_completed = new boolean[n];
            Progress[] pg = new Progress[n];

            for (int i = 0; i < n; i++) {
                pg[i] = new Progress();
                pg[i].pid = i;
                pg[i].at = Integer.parseInt(JOptionPane.showInputDialog("Enter arrival time for Process P" + i));
                pg[i].bt = Integer.parseInt(JOptionPane.showInputDialog("Enter burst time for Process P" + i));
                bt_remaining[i] = pg[i].bt;
                is_completed[i] = false;
            }

            StringBuilder ganttChart = new StringBuilder("|");
            StringBuilder upperLine = new StringBuilder("-");
            StringBuilder lowerLine = new StringBuilder("-");
            int timeUnitWidth = 6;

            int current_time = 0, completed = 0;
            float sum_tat = 0, sum_wt = 0;

            while (completed != n) {
                int min_index = -1;
                int minimum = Integer.MAX_VALUE;

                for (int i = 0; i < n; i++) {
                    if (pg[i].at <= current_time && !is_completed[i]) {
                        if (bt_remaining[i] < minimum) {
                            minimum = (int) bt_remaining[i];
                            min_index = i;
                        }
                        if (bt_remaining[i] == minimum) {
                            if (pg[i].at < pg[min_index].at) {
                                minimum = (int) bt_remaining[i];
                                min_index = i;
                            }
                        }
                    }
                }

                if (min_index == -1) {
                    current_time++;
                } else {
                    if (bt_remaining[min_index] == pg[min_index].bt) {
                        pg[min_index].start_time = current_time;
                    }
                    upperLine.append("-".repeat(Math.max(0, pg[min_index].bt))).append("------");
                    ganttChart.append(String.format(" %-" + timeUnitWidth + "s |", "P" + pg[min_index].pid));
                    lowerLine.append("-".repeat(Math.max(0, pg[min_index].bt))).append("------");

                    bt_remaining[min_index] -= 1;
                    current_time++;

                    if (bt_remaining[min_index] == 0) {
                        pg[min_index].ct = current_time;
                        pg[min_index].tat = pg[min_index].ct - pg[min_index].at;
                        pg[min_index].wt = pg[min_index].tat - pg[min_index].bt;

                        sum_tat += pg[min_index].tat;
                        sum_wt += pg[min_index].wt;

                        completed++;
                        is_completed[min_index] = true;
                    }
                }
            }

            int chartLength = ganttChart.length();
            upperLine.setLength(chartLength);
            lowerLine.setLength(chartLength);

            ganttChartArea.setText("\nGantt Chart:\n");
            ganttChartArea.append(upperLine + "\n");
            ganttChartArea.append(ganttChart + "\n");
            ganttChartArea.append(lowerLine + "\n");

            tableModel.setRowCount(0);
            for (int i = 0; i < n; i++) {
                tableModel.addRow(new Object[] {
                        "P" + pg[i].pid, pg[i].at, pg[i].bt, pg[i].ct, pg[i].tat, pg[i].wt
                });
            }

            DecimalFormat df = new DecimalFormat("#.##");
            lblTotalTurnaround.setText("Total Turnaround Time: " + sum_tat);
            lblAvgTurnaround.setText("Average Turnaround Time: " + df.format(sum_tat / n));
            lblTotalWaiting.setText("Total Waiting Time: " + sum_wt);
            lblAvgWaiting.setText("Average Waiting Time: " + df.format((float) sum_wt / n));

            JOptionPane.showMessageDialog(frame, "Preemptive SJF Scheduling Completed!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void display2() {
        frame.setVisible(true);
    }

    private void returnToHomepage() {
        frame.dispose(); // Close the current frame
        Main.displayHomepage(); // Call a method in Main class to display the homepage
    }

}