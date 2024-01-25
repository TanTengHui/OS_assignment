import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class Procedure {
    String processName;
    int arrivalTime;
    int burstTime;
    int priority;

    public Procedure(String processName, int arrivalTime, int burstTime, int priority) {
        this.processName = processName;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
    }

    // Getter methods
    public String getProcessName() {
        return processName;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getPriority() {
        return priority;
    }
}

public class NonPreemptivePriorityGUI {
    private JFrame frame;
    private JTextField numProcessesField;
    private JButton btnRun;
    private JButton btnHome;
    private JTable processTable;
    private DefaultTableModel tableModel;
    private JTextArea ganttChartArea;
    private JLabel lblTotalTurnaround;
    private JLabel lblAvgTurnaround;
    private JLabel lblTotalWaiting;
    private JLabel lblAvgWaiting;

    public NonPreemptivePriorityGUI() {
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

        btnRun = new JButton("Run Non-Preemptive Priority Scheduling");
        btnRun.setBounds(10, 40, 300, 30);
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

        String[] columnNames = { "Process", "Arrival Time", "Burst Time", "Finish Time", "Priority", "Turnaround Time",
                "Waiting Time" };
        tableModel = new DefaultTableModel(columnNames, 0);
        processTable = new JTable(tableModel);
        processScrollPane.setViewportView(processTable);

        btnRun.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executeNonPriority();
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

    private void executeNonPriority() {
        try {
            int n;
            n = Integer.parseInt(numProcessesField.getText());
            if (n < 3 || n > 10) {
                JOptionPane.showMessageDialog(frame,
                        "Number of processes should be between 3 and 10. Please try again.");
                return;
            }

            List<Procedure> pd = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                int arrivalTime = Integer.parseInt(
                        JOptionPane.showInputDialog(frame, "Enter arrival time for Process P" + i));
                int burstTime = Integer.parseInt(
                        JOptionPane.showInputDialog(frame, "Enter burst time for Process P" + i));
                int priority = Integer.parseInt(
                        JOptionPane.showInputDialog(frame, "Enter priority for Process P" + i));

                pd.add(new Procedure("P" + i, arrivalTime, burstTime, priority));
            }

            pd.sort(Comparator.comparing(Procedure::getArrivalTime).thenComparing(Procedure::getPriority));

            int currentTime = 0;
            int totalTurnaroundTime = 0;
            int totalWaitingTime = 0;

            List<Procedure> readyQueue = new ArrayList<>();
            List<String[]> tableData = new ArrayList<>();
            StringBuilder ganttChart = new StringBuilder("|");
            StringBuilder upperLine = new StringBuilder("-");
            StringBuilder lowerLine = new StringBuilder("-");

            while (!pd.isEmpty() || !readyQueue.isEmpty()) {
                while (!pd.isEmpty() && pd.get(0).getArrivalTime() <= currentTime) {
                    readyQueue.add(pd.remove(0));
                    readyQueue.sort(Comparator.comparing(Procedure::getPriority)); // Sort ready queue by priority
                }

                if (!readyQueue.isEmpty()) {
                    Procedure currentProcedure = readyQueue.remove(0);
                    int finishTime = currentTime + currentProcedure.getBurstTime();
                    int turnaroundTime = finishTime - currentProcedure.getArrivalTime();
                    int waitingTime = turnaroundTime - currentProcedure.getBurstTime();

                    totalTurnaroundTime += turnaroundTime;
                    totalWaitingTime += waitingTime;

                    String[] rowData = {
                            currentProcedure.getProcessName(),
                            String.valueOf(currentProcedure.getArrivalTime()),
                            String.valueOf(currentProcedure.getBurstTime()),
                            String.valueOf(finishTime),
                            String.valueOf(currentProcedure.getPriority()),
                            String.valueOf(turnaroundTime),
                            String.valueOf(waitingTime)
                    };
                    tableData.add(rowData);

                    int ganttChartWidth = 5; // constant size for the Gantt chart

                    upperLine.append("-".repeat(Math.max(0, currentProcedure.getBurstTime()))).append("-----");
                    ganttChart
                            .append(String.format(" %-" + ganttChartWidth + "s |", currentProcedure.getProcessName()));
                    lowerLine.append("-".repeat(Math.max(0, currentProcedure.getBurstTime()))).append("-----");

                    currentTime = finishTime;
                } else {
                    currentTime++; // Idle CPU when no procedures are in the ready queue
                }
            }

            int chartLength = ganttChart.length();
            upperLine.setLength(chartLength);
            lowerLine.setLength(chartLength);

            // Display Gantt chart
            ganttChartArea.setText("\nGantt Chart:\n");
            ganttChartArea.append(upperLine + "\n");
            ganttChartArea.append(ganttChart.toString() + "\n");
            ganttChartArea.append(lowerLine + "\n");

            // Display procedure details table
            tableModel.setRowCount(0);
            tableData.sort(Comparator.comparing(row -> Integer.parseInt(row[0].substring(1))));
            for (String[] row : tableData) {
                tableModel.addRow(row);
            }

            double avgTurnaroundTime = (double) totalTurnaroundTime / n;
            double avgWaitingTime = (double) totalWaitingTime / n;

            DecimalFormat df = new DecimalFormat("#.##");

            lblTotalTurnaround.setText("Total Turnaround Time: " + totalTurnaroundTime);
            lblAvgTurnaround.setText("Average Turnaround Time: " + df.format(avgTurnaroundTime));
            lblTotalWaiting.setText("Total Waiting Time: " + totalWaitingTime);
            lblAvgWaiting.setText("Average Waiting Time: " + df.format(avgWaitingTime));
            JOptionPane.showMessageDialog(frame, "Non-Preemptive SJF Scheduling Completed!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void display3() {
        frame.setVisible(true);
    }

    private void returnToHomepage() {
        frame.dispose(); // Close the current frame
        Main.displayHomepage(); // Call a method in Main class to display the homepage
    }

}
