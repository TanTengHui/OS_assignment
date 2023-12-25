import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

class Process {
    int arrivalTime;
    int burstTime;
    int completionTime;
    int turnaroundTime;
    int waitingTime;
    int startTime;
    String processName;

    public Process(String processName, int arrivalTime, int burstTime) {
        this.processName = processName;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.completionTime = 0;
        this.turnaroundTime = 0;
        this.waitingTime = 0;
        this.startTime = 0;
    }
}

public class RoundRobin {
    public RoundRobin() {
        executeRoundRobin();
    }

    private void executeRoundRobin() {
        try (Scanner scanner = new Scanner(System.in)) {
            int n, tq;
            float averageWaitingTime;
            int TotalTurnaround = 0;
            float avgTurnaroundTime;
            int TotalWaiting = 0, index;

            while (true) {
                System.out.print("Enter the number of processes (3-10): ");
                n = scanner.nextInt();
                System.out.print("Enter the time quantum: ");
                tq = scanner.nextInt();
                if (n >= 3 && n <= 10) {
                    break;
                } else {
                    System.out.println("Number of processes should be between 3 and 10. Please try again.");
                }
            }
            Process[] p = new Process[50]; // Array of Process objects
            int[] burstArr = new int[50]; // Array to store burst times

            for (int i = 0; i < n; i++) {
                System.out.println("Enter details for Process P" + i + ":");
                System.out.print("Arrival Time" + ": ");
                int arrivalTime = scanner.nextInt();
                System.out.print("Burst Time" + ": ");
                int burstTime = scanner.nextInt();
                p[i] = new Process("P" + i, arrivalTime, burstTime); // Creating Process objects
                burstArr[i] = p[i].burstTime; // Storing burst times
            }

            Queue<Integer> q = new LinkedList<>(); // FIFO queue
            int current_time = 0;
            q.add(0); // Add initial process (assuming ID 0)
            int completed = 0;
            int[] mark = new int[100]; // Array to mark completed processes
            Arrays.fill(mark, 0); // Initialize all elements of the array with 0
            mark[0] = 1; // Mark the initial process as arrived

            StringBuilder ganttChart = new StringBuilder("|");
            StringBuilder upperLine = new StringBuilder("-");
            StringBuilder lowerLine = new StringBuilder("-");
            

            while (completed != n) {
                // Give quantum unit of time to the process that is in the front
                // of the queue and poll this process from the queue.
                index = q.poll();

                if (burstArr[index] == p[index].burstTime) {
                    p[index].startTime = Math.max(current_time, p[index].arrivalTime);
                    current_time = p[index].startTime;
                }

                if (0 < burstArr[index] - tq) {
                    burstArr[index] -= tq;
                    current_time += tq;
                } else {
                    current_time += burstArr[index];
                    p[index].completionTime = current_time;
                    p[index].turnaroundTime = p[index].completionTime - p[index].arrivalTime;
                    p[index].waitingTime = p[index].turnaroundTime - p[index].burstTime;
                    TotalWaiting += p[index].waitingTime;
                    TotalTurnaround += p[index].turnaroundTime;
                    completed++;
                    burstArr[index] = 0;
                }

                // Generate Gantt Chart
                upperLine.append("-".repeat(Math.max(0, 1))).append("------");
                ganttChart.append(" ").append(p[index].processName).append(" |");
                lowerLine.append("-".repeat(Math.max(0, 1))).append("------");
                
                // If some process has arrived when this process was executing,
                // insert them into the queue.
                for (int i = 0; i < n; i++) {
                    if (burstArr[i] > 0 && p[i].arrivalTime <= current_time && mark[i] == 0) {
                        mark[i] = 1;
                        q.add(i);
                    }
                }
                // If the current process has burst time remaining,
                // push the process into the queue again.
                if (0 < burstArr[index])
                    q.add(index);

                // If the queue is empty,
                // pick the first process from the list that is not completed.
                if (q.isEmpty()) {
                    for (int i = 0; i < n; i++) {
                        if (0 < burstArr[i]) {
                            mark[i] = 1;
                            q.add(i);
                            break;
                        }
                    }
                }
            }
            int chartLength = ganttChart.length();
            upperLine.setLength(chartLength);
            lowerLine.setLength(chartLength);
            System.out.println("\nGantt Chart:");
            System.out.println(upperLine);
            System.out.println(ganttChart);
            System.out.println(lowerLine);
            System.out.println();
            System.out.println("\nProcess Details:");
            System.out.println(
                    "+----------+------------------+------------------+------------------+------------------+------------------+");
            System.out.printf("| %-8s | %-16s | %-16s | %-16s | %-16s | %-16s |\n", "Process", "Arrival Time",
                    "Burst Time", "Finish Time", "Turnaround Time", "Waiting Time");
            System.out.println(
                    "+----------+------------------+------------------+------------------+------------------+------------------+");
            for (int i = 0; i < n; i++) {
                System.out.printf(
                        "| %-8s | %-16s | %-16s | %-16s | %-16s | %-16s |\n",
                        i, p[i].arrivalTime, p[i].burstTime, p[i].completionTime,
                        p[i].turnaroundTime, p[i].waitingTime);
            }
            System.out.println(
                    "+----------+------------------+------------------+------------------+------------------+------------------+");
            averageWaitingTime = (float) TotalWaiting / n;
            avgTurnaroundTime = (float) TotalTurnaround / n;
            DecimalFormat df = new DecimalFormat("#.##");
            System.out.println("\nTotal Turnaround Time = " + TotalTurnaround);
            System.out.println("Average Turnaround Time = " + df.format(avgTurnaroundTime));
            System.out.println("Total Waiting Time = " + TotalWaiting);
            System.out.println("Average Waiting Time = " + df.format(averageWaitingTime));
        }
    }

    
}
