import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

class Task {
    int arrivalTime;
    int burstTime;
    int completionTime;
    int turnaroundTime;
    int waitingTime;
    int startTime;
    String taskName;

    public Task(String taskName, int arrivalTime, int burstTime) {
        this.taskName = taskName;
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
            Task[] t = new Task[50]; // Array of Task objects
            int[] burstArr = new int[50]; // Array to store burst times

            for (int i = 0; i < n; i++) {
                System.out.println("Enter details for Process P" + i + ":");
                System.out.print("Arrival Time" + ": ");
                int arrivalTime = scanner.nextInt();
                System.out.print("Burst Time" + ": ");
                int burstTime = scanner.nextInt();
                t[i] = new Task("P" + i, arrivalTime, burstTime); // Creating Task objects
                burstArr[i] = t[i].burstTime; // Storing burst times
            }

            Queue<Integer> q = new LinkedList<>(); // FIFO queue
            int current_time = 0;
           // q.add(0); // Add initial task (assuming ID 0)
            int completed = 0;
            int[] mark = new int[100]; // Array to mark completed tasks
            Arrays.fill(mark, 0); // Initialize all elements of the array with 0
            //mark[0] = 1; // Mark the initial task as arrived

            StringBuilder ganttChart = new StringBuilder("|");
            StringBuilder upperLine = new StringBuilder("-");
            StringBuilder lowerLine = new StringBuilder("-");
            boolean hasArrivedZero = false;
            for (int i = 0; i < n; i++) {
                if (burstArr[i] > 0 && t[i].arrivalTime == 0) {
                    mark[i] = 1;
                    q.add(i);
                    hasArrivedZero = true;
                }
            }

            while (completed != n) {
                // Give quantum unit of time to the task that is in the front
                // of the queue and poll this task from the queue.
                 if (hasArrivedZero) {
                    hasArrivedZero = false;
                    continue;}
                index = q.poll();

                if (burstArr[index] == t[index].burstTime) {
                    t[index].startTime = Math.max(current_time, t[index].arrivalTime);
                    current_time = t[index].startTime;
                }

                if (0 < burstArr[index] - tq) {
                    burstArr[index] -= tq;
                    current_time += tq;
                } else {
                    current_time += burstArr[index];
                    t[index].completionTime = current_time;
                    t[index].turnaroundTime = t[index].completionTime - t[index].arrivalTime;
                    t[index].waitingTime = t[index].turnaroundTime - t[index].burstTime;
                    TotalWaiting += t[index].waitingTime;
                    TotalTurnaround += t[index].turnaroundTime;
                    completed++;
                    burstArr[index] = 0;
                }

                // Generate Gantt Chart
                upperLine.append("-".repeat(Math.max(0, 1))).append("------");
                ganttChart.append(" ").append(t[index].taskName).append(" |");
                lowerLine.append("-".repeat(Math.max(0, 1))).append("------");
                
                // If some task has arrived when this task was executing,
                // insert them into the queue based on their arrival time.
                for (int i = 0; i < n; i++) {
                    if (burstArr[i] > 0 && t[i].arrivalTime <= current_time && mark[i] == 0) {
                        mark[i] = 1;
                        q.add(i);
                    }
                }
                // If the current task has burst time remaining,
                // push the task into the queue again.
                if (0 < burstArr[index]){
                    q.add(index);
                }
                   
                // If the queue is empty,
                // pick the first task from the list that is not completed.
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
            System.out.println("\nTask Details:");
            System.out.println(
                    "+----------+------------------+------------------+------------------+------------------+------------------+");
            System.out.printf("| %-8s | %-16s | %-16s | %-16s | %-16s | %-16s |\n", "Process", "Arrival Time",
                    "Burst Time", "Finish Time", "Turnaround Time", "Waiting Time");
            System.out.println(
                    "+----------+------------------+------------------+------------------+------------------+------------------+");
            for (int i = 0; i < n; i++) {
                System.out.printf(
                        "| %-8s | %-16s | %-16s | %-16s | %-16s | %-16s |\n",
                        t[i].taskName, t[i].arrivalTime, t[i].burstTime, t[i].completionTime,
                        t[i].turnaroundTime, t[i].waitingTime);
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
