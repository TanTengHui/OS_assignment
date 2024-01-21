import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

class Task {
    int processID; // process
    int arrivalTime;
    int burstTime;
    int ct, wt, tat, start_time; // ct = completion time, tat = turn around time, wt = waiting time
    int bt_remaining;

    public Task(int processID, int arrivalTime, int burstTime) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.bt_remaining = burstTime;
    }
}

public class RoundRobin {
    public RoundRobin() {
        executeRoundRobin();
    }

    private void executeRoundRobin() {
        try (Scanner scanner = new Scanner(System.in)) {
            int n, index;

            Queue<Integer> q = new LinkedList<>();
            boolean[] visited = new boolean[100];

            int current_time = 0;
            int completed = 0, tq;
            while (true) {
                System.out.print("Enter the number of processes (3-10): ");
                n = scanner.nextInt();
                if (n >= 3 && n <= 10) {
                    break; // Break the loop if the input is within the valid range
                } else {
                    System.out.println("Number of processes should be between 3 and 10. Please try again.");
                }
            }

            float sum_tat = 0, sum_wt = 0;

            Task[] tasks = new Task[100];

            for (int i = 0; i < n; i++) {
                System.out.println("Enter details for Process P" + i + ":");
                System.out.print("Arrival Time: ");
                int arrivalTime = scanner.nextInt();
                tasks[i] = new Task(i, arrivalTime, 0);
                System.out.print("Burst Time: ");
                tasks[i].burstTime = scanner.nextInt();
                tasks[i].bt_remaining = tasks[i].burstTime;
            }

            System.out.print("\nEnter time quantum: ");
            tq = scanner.nextInt();

            // sort structure on the basis of Arrival time in increasing order
            Arrays.sort(tasks, 0, n, Comparator.comparingInt(p -> p.arrivalTime));

            q.add(0); // add first process to queue
            visited[0] = true; // mark first process as visited
            StringBuilder ganttChart = new StringBuilder("|");
            StringBuilder upperLine = new StringBuilder("-");
            StringBuilder lowerLine = new StringBuilder("-");
            while (completed != n) {
                index = q.poll();

                if (tasks[index].bt_remaining == tasks[index].burstTime) {
                    tasks[index].start_time = Math.max(current_time, tasks[index].arrivalTime);

                    current_time = tasks[index].start_time;

                }

                if (tasks[index].bt_remaining - tq > 0) {
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
                upperLine.append("-".repeat(Math.max(0, 1))).append("------");
                ganttChart.append(" P").append(tasks[index].processID).append(" |");
                lowerLine.append("-".repeat(Math.max(0, 1))).append("------");

                // check which new Processes needs to be pushed to Ready Queue from Input list
                for (int i = 1; i < n; i++) {
                    if (tasks[i].bt_remaining > 0 && tasks[i].arrivalTime <= current_time && !visited[i]) {
                        q.add(i);
                        visited[i] = true;
                    }
                }
                // check if Process on CPU needs to be pushed to Ready Queue
                if (tasks[index].bt_remaining > 0)
                    q.add(index);

                // if queue is empty, just add one process from list, whose remaining burst time
                // > 0
                if (q.isEmpty()) {
                    for (int i = 1; i < n; i++) {
                        if (tasks[i].bt_remaining > 0) {
                            q.add(i);
                            visited[i] = true;
                            break;
                        }
                    }
                }
            } // end of while
            int chartLength = ganttChart.length();
            upperLine.setLength(chartLength);
            lowerLine.setLength(chartLength);

            System.out.println("\nGantt Chart:");
            System.out.println(upperLine);
            System.out.println(ganttChart);
            System.out.println(lowerLine);
            Arrays.sort(tasks, 0, n, Comparator.comparingInt(p -> p.processID)); // sort structure on the basis of
                                                                                 // process ID in increasing order
                                                                                 

            // Output
            System.out.println("\nProcess Details:");
            System.out.println(
                    "+----------+------------------+------------------+------------------+------------------+------------------+");

            System.out.printf("| %-8s | %-16s | %-16s | %-16s | %-16s | %-16s |\n", "Process", "Arrival Time",
                    "Burst Time", "Finish Time", "Turnaround Time", "Waiting Time");
            System.out.println(
                    "+----------+------------------+------------------+------------------+------------------+------------------+");

            for (int i = 0; i < n; i++) {
                int finishTime = tasks[i].arrivalTime + tasks[i].tat;
                System.out.printf("| %-8s | %-16s | %-16s | %-16s | %-16s | %-16s |\n",
                        "P" + tasks[i].processID, tasks[i].arrivalTime, tasks[i].burstTime, finishTime, tasks[i].tat,
                        tasks[i].wt);
            }

            System.out.println(
                    "+----------+------------------+------------------+------------------+------------------+------------------+");

            DecimalFormat df = new DecimalFormat("#.##");

            System.out.println("Total Turnaround Time= " + sum_tat);
            System.out.println("Average Turn Around time= " + df.format(sum_tat / n));
            System.out.println("Total Waiting Time= " + sum_wt);
            System.out.println("Average Waiting Time= " + df.format((float) sum_wt / n));
        }

    }
}
