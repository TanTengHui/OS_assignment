import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

class Task {
    int processID;
    int arrivalTime;
    int burstTime;
    int ct, wt, tat, start_time;
    int bt_remaining;

    public Task(int processID, int arrivalTime, int burstTime) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.bt_remaining = burstTime;
    }
}

public class RoundRobinGUI {
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

    public RoundRobinGUI() {
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

        btnRun = new JButton("Run Round Robin");
        btnRun.setBounds(10, 40, 150, 30);
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
                executeRoundRobin();
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

    private void executeRoundRobin() {
        try {
            int n, index;
            Queue<Integer> q = new LinkedList<>();// queue for ready processes
            boolean[] visited = new boolean[100];// array to keep track of visited processes
            int current_time = 0, completed = 0, tq;
            DecimalFormat df = new DecimalFormat("#.##");

            n = Integer.parseInt(numProcessesField.getText());
            if (n < 3 || n > 10) {
                JOptionPane.showMessageDialog(frame,
                        "Number of processes should be between 3 and 10. Please try again.");
                return; // exit the method if the validation fails
            }
            float sum_tat = 0, sum_wt = 0;
            Task[] tasks = new Task[100];

            for (int i = 0; i < n; i++) {
                tasks[i] = new Task(i,
                        Integer.parseInt(JOptionPane.showInputDialog("Enter arrival time for Process P" + i)),
                        0);
                tasks[i].burstTime = Integer
                        .parseInt(JOptionPane.showInputDialog("Enter burst time for Process P" + i));
                tasks[i].bt_remaining = tasks[i].burstTime;
            }

            tq = Integer.parseInt(JOptionPane.showInputDialog("Enter time quantum:"));

            Arrays.sort(tasks, 0, n, Comparator.comparingInt(p -> p.arrivalTime));

            q.add(0);
            visited[0] = true;

            StringBuilder ganttChart = new StringBuilder("|");
            StringBuilder upperLine = new StringBuilder("-");
            StringBuilder lowerLine = new StringBuilder("-");
            int timeUnitWidth = 5;
            while (completed != n) {
                index = q.poll();

                if (tasks[index].bt_remaining == tasks[index].burstTime) { // first time execution
                    tasks[index].start_time = Math.max(current_time, tasks[index].arrivalTime);
                    current_time = tasks[index].start_time;
                }
                
                if (tasks[index].bt_remaining - tq > 0) { // process is not completed
                    tasks[index].bt_remaining -= tq;
                    current_time += tq;
                } else {
                    current_time += tasks[index].bt_remaining;
                    tasks[index].bt_remaining = 0;
                    completed++;

                    tasks[index].ct = current_time;
                    tasks[index].tat = tasks[index].ct - tasks[index].arrivalTime;
                    tasks[index].wt = tasks[index].tat - tasks[index].burstTime;

                    sum_tat += tasks[index].tat;
                    sum_wt += tasks[index].wt;
                }

                upperLine.append("-".repeat(Math.max(0, tasks[index].burstTime))).append("------");
                ganttChart.append(String.format(" %-" + timeUnitWidth + "s |", "P" + tasks[index].processID));
                lowerLine.append("-".repeat(Math.max(0, tasks[index].burstTime))).append("------");

                for (int i = 1; i < n; i++) {// add processes to queue
                    if (tasks[i].bt_remaining > 0 && tasks[i].arrivalTime <= current_time && !visited[i]) {
                        q.add(i);
                        visited[i] = true;
                    }
                }
                if (tasks[index].bt_remaining > 0)// add the current process to the end of the queue
                    q.add(index);

                if (q.isEmpty()) {  // if queue is empty, find the next process to execute
                    for (int i = 1; i < n; i++) {
                        if (tasks[i].bt_remaining > 0) {
                            q.add(i);
                            visited[i] = true;
                            break;
                        }
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

            Arrays.sort(tasks, 0, n, Comparator.comparingInt(p -> p.processID)); // sort by process ID

            tableModel.setRowCount(0);
            for (int i = 0; i < n; i++) {
                int finishTime = tasks[i].arrivalTime + tasks[i].tat;
                tableModel.addRow(new Object[] {
                        "P" + tasks[i].processID, tasks[i].arrivalTime, tasks[i].burstTime, finishTime, tasks[i].tat,
                        tasks[i].wt
                });
            }

            lblTotalTurnaround.setText("Total Turnaround Time: " + sum_tat);
            lblAvgTurnaround.setText("Average Turnaround Time: " + df.format(sum_tat / n));
            lblTotalWaiting.setText("Total Waiting Time: " + sum_wt);
            lblAvgWaiting.setText("Average Waiting Time: " + df.format((float) sum_wt / n));

            JOptionPane.showMessageDialog(frame, "Round Robin Scheduling Completed!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void display4() {
        frame.setVisible(true);
    }

    private void returnToHomepage() {
        frame.dispose(); // Close the current frame
        Main.displayHomepage(); // Call a method in Main class to display the homepage
    }

}
