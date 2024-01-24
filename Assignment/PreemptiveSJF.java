import java.text.DecimalFormat;
import java.util.Scanner;

class Progress {
    int pid; // Process ID
    int at; // Arrival Time
    int bt; // Burst Time
    int ct, wt, tat, start_time; // Completion Time, Waiting Time, Turn Around Time, start time
}

public class PreemptiveSJF {
    public PreemptiveSJF() {
        executePSJF();
    }

    private void executePSJF() {
        try (Scanner scanner = new Scanner(System.in)) {
            int n;
            float[] bt_remaining;
            boolean[] is_completed;

            int current_time = 0;
            int completed = 0;
            float sum_tat = 0, sum_wt = 0;

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

            Progress[] pg = new Progress[n];
            bt_remaining = new float[n];
            is_completed = new boolean[n];
            for (int i = 0; i < n; i++) {
                pg[i] = new Progress();
                pg[i].pid = i;
                System.out.print("\nEnter Progress " + i + " Arrival Time: ");
                pg[i].at = scanner.nextInt();
                System.out.print("\nEnter Progress " + i + " Burst Time: ");
                pg[i].bt = scanner.nextInt();
                bt_remaining[i] = pg[i].bt;
                is_completed[i] = false;
            }
            // Variables for Gantt chart
            StringBuilder ganttChart = new StringBuilder("|");
            StringBuilder upperLine = new StringBuilder("-");
            StringBuilder lowerLine = new StringBuilder("-");
            int timeUnitWidth = 2; // Constant width for each time unit in the Gantt chart
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
                    upperLine.append("-".repeat(Math.max(0, timeUnitWidth))).append("------");
                    ganttChart.append("P").append(pg[min_index].pid)
                            .append(" ".repeat(Math.max(0, timeUnitWidth - 1))).append("|");
                    lowerLine.append("-".repeat(Math.max(0, timeUnitWidth))).append("------");

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

            // Calculate Length of Process completion cycle
            max_completion_time = Integer.MIN_VALUE;
            min_arrival_time = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                max_completion_time = Math.max(max_completion_time, pg[i].ct);
                min_arrival_time = Math.min(min_arrival_time, pg[i].at);
            }
            int chartLength = ganttChart.length();
            upperLine.setLength(chartLength);
            lowerLine.setLength(chartLength);

            System.out.println("\nGantt Chart:");
            System.out.println(upperLine);
            System.out.println(ganttChart);
            System.out.println(lowerLine);

            System.out.println("\nProcess Details:");
            System.out.println(
                    "+----------+------------------+------------------+------------------+------------------+------------------+");

            System.out.printf("| %-8s | %-16s | %-16s | %-16s | %-16s | %-16s |\n", "Process", "Arrival Time",
                    "Burst Time", "Finish Time", "Turnaround Time", "Waiting Time");
            System.out.println(
                    "+----------+------------------+------------------+------------------+------------------+------------------+");

            for (int i = 0; i < n; i++) {
                System.out.printf("| %-8s | %-16s | %-16s | %-16s | %-16s | %-16s |\n",
                        "P" + pg[i].pid, pg[i].at, pg[i].bt, pg[i].ct, pg[i].tat, pg[i].wt);
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
