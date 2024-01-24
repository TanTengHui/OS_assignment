import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Scanner;

class Process {
    int pid; // Process ID
    int at; // Arrival Time
    int bt; // Burst Time
    int ct, wt, tat, start_time; // Completion Time, Waiting Time, Turn Around Time, start time
}

public class NonPreemptiveSJF {
    public NonPreemptiveSJF() {
        executeSJF();
    }

    private void executeSJF() {
        try (Scanner scanner = new Scanner(System.in)) {
            int n;
            boolean[] is_completed;
            int current_time = 0;
            int completed = 0;

            int max_completion_time, min_arrival_time;

            while (true) {
                System.out.print("Enter total number of processes (3-10): ");
                n = scanner.nextInt();
                if (n >= 3 && n <= 10) {
                    break; // Break the loop if the input is within the valid range
                } else {
                    System.out.println("Number of processes should be between 3 and 10. Please try again.");
                }
            }
            is_completed = new boolean[n];
            Arrays.fill(is_completed, false);

            Process[] ps = new Process[n];
            for (int i = 0; i < n; i++) {
                ps[i] = new Process();
                ps[i].pid = i;
                System.out.print("\nEnter Process " + i + " Arrival Time: ");
                ps[i].at = scanner.nextInt();
                System.out.print("\nEnter Process " + i + " Burst Time: ");
                ps[i].bt = scanner.nextInt();
            }

            // Variables for Gantt chart
            StringBuilder ganttChart = new StringBuilder("|");
            StringBuilder upperLine = new StringBuilder("-");
            StringBuilder lowerLine = new StringBuilder("-");
            int timeUnitWidth = 2; // Constant width for each time unit in the Gantt chart

            int sum_tat = 0, sum_wt = 0;
            while (completed != n) {
                int min_index = -1;
                int minimum = Integer.MAX_VALUE;

                for (int i = 0; i < n; i++) {
                    if (ps[i].at <= current_time && !is_completed[i]) {
                        if (ps[i].bt < minimum) {
                            minimum = ps[i].bt;
                            min_index = i;
                        }
                        if (ps[i].bt == minimum) {
                            if (ps[i].at < ps[min_index].at) {
                                minimum = ps[i].bt;
                                min_index = i;
                            }
                        }
                    }
                }

                if (min_index == -1) {
                    current_time++;
                } else {

                    upperLine.append("-".repeat(Math.max(0, timeUnitWidth))).append("------");
                    ganttChart.append("P").append(ps[min_index].pid)
                            .append(" ".repeat(Math.max(0, timeUnitWidth - 1))).append("|");
                    lowerLine.append("-".repeat(Math.max(0, timeUnitWidth))).append("------");
                    ps[min_index].start_time = current_time;
                    ps[min_index].ct = ps[min_index].start_time + ps[min_index].bt;
                    ps[min_index].tat = ps[min_index].ct - ps[min_index].at;
                    ps[min_index].wt = ps[min_index].tat - ps[min_index].bt;

                    sum_tat += ps[min_index].tat;
                    sum_wt += ps[min_index].wt;

                    completed++;
                    is_completed[min_index] = true;
                    current_time = ps[min_index].ct;

                }
            }

            max_completion_time = Integer.MIN_VALUE;
            min_arrival_time = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                max_completion_time = Math.max(max_completion_time, ps[i].ct);
                min_arrival_time = Math.min(min_arrival_time, ps[i].at);
            }
            int chartLength = ganttChart.length();
            upperLine.setLength(chartLength);
            lowerLine.setLength(chartLength);

            System.out.println("\nGantt Chart:");
            System.out.println(upperLine);
            System.out.println(ganttChart);
            System.out.println(lowerLine);

            // Output the Process Details table
            System.out.println("\nProcess Details:");
            System.out.println(
                    "+----------+------------------+------------------+------------------+------------------+------------------+");

            System.out.printf("| %-8s | %-16s | %-16s | %-16s | %-16s | %-16s |\n", "Process", "Arrival Time",
                    "Burst Time", "Finish Time", "Turnaround Time", "Waiting Time");
            System.out.println(
                    "+----------+------------------+------------------+------------------+------------------+------------------+");

            for (int i = 0; i < n; i++) {
                System.out.printf("| %-8s | %-16s | %-16s | %-16s | %-16s | %-16s |\n",
                        "P" + ps[i].pid, ps[i].at, ps[i].bt, ps[i].ct, ps[i].tat, ps[i].wt);
            }

            System.out.println(
                    "+----------+------------------+------------------+------------------+------------------+------------------+");

            DecimalFormat df = new DecimalFormat("#.##");

            System.out.println("\nTotal Turnaround Time= " + sum_tat);
            System.out.println("Average Turnaround time= " + df.format((float) sum_tat / n));
            System.out.println("Total Waiting Time= " + sum_wt);
            System.out.println("Average Waiting Time= " + df.format((float) sum_wt / n));

        }
        ;

    }
}
