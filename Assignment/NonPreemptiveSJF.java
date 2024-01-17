import java.text.DecimalFormat;
import java.util.Scanner;

public class NonPreemptiveSJF {
    public NonPreemptiveSJF() {
        executeSJFScheduling();
    }

    private void executeSJFScheduling() {
        try (Scanner scanner = new Scanner(System.in)) {
            int numProcesses;
            while (true) {
                System.out.print("Enter the number of processes (3-10): ");
                numProcesses = scanner.nextInt();
                if (numProcesses >= 3 && numProcesses <= 10) {
                    break; // Break the loop if the input is within the valid range
                } else {
                    System.out.println("Number of processes should be between 3 and 10. Please try again.");
                }
            }

            int[] arrivalTime = new int[numProcesses];
            int[] burstTime = new int[numProcesses];
            int[] waitingTime = new int[numProcesses];
            int[] turnaroundTime = new int[numProcesses];
            boolean[] completed = new boolean[numProcesses];

            for (int i = 0; i < numProcesses; i++) {
                System.out.println("Enter details for Process P" + i + ":");
                System.out.print("Arrival Time: ");
                arrivalTime[i] = scanner.nextInt();
                System.out.print("Burst Time: ");
                burstTime[i] = scanner.nextInt();
                completed[i] = false;
            }

            int currentTime = 0;
            int totalTurnaroundTime = 0;
            int totalWaitingTime = 0;

            System.out.println("\nGantt Chart:");

            StringBuilder ganttChart = new StringBuilder("|");
            StringBuilder upperLine = new StringBuilder("-");
            StringBuilder lowerLine = new StringBuilder("-");

            // Define constant width for each time unit in the Gantt chart
            int timeUnitWidth = 5;

            while (true) {
                int shortest = -1;
                int shortestBurst = Integer.MAX_VALUE;

                for (int i = 0; i < numProcesses; i++) {
                    if (!completed[i] && arrivalTime[i] <= currentTime && burstTime[i] < shortestBurst) {
                        shortest = i;
                        shortestBurst = burstTime[i];
                    }
                }

                if (shortest == -1) {
                    boolean allCompleted = true;
                    for (int i = 0; i < numProcesses; i++) {
                        if (!completed[i]) {
                            allCompleted = false;
                            break;
                        }
                    }
                    if (allCompleted) {
                        break;
                    } else {
                        currentTime++;
                        continue;
                    }
                }
                upperLine.append("-".repeat(Math.max(0, timeUnitWidth))).append("------");
                ganttChart.append("P").append(shortest).append(" ".repeat(Math.max(0, timeUnitWidth - 1))).append("|");
                lowerLine.append("-".repeat(Math.max(0, timeUnitWidth))).append("------");

                currentTime += burstTime[shortest];

                turnaroundTime[shortest] = currentTime - arrivalTime[shortest];
                waitingTime[shortest] = turnaroundTime[shortest] - burstTime[shortest];
                totalTurnaroundTime += turnaroundTime[shortest];
                totalWaitingTime += waitingTime[shortest];
                completed[shortest] = true;
            }
            int chartLength = ganttChart.length();
            upperLine.setLength(chartLength);
            lowerLine.setLength(chartLength);
            System.out.println(upperLine);
            System.out.println(ganttChart.toString());
            System.out.println(lowerLine);

           
           
            // System.out.print("0 ");
            // for (int i = 0; i < numProcesses; i++) {
            //     for (int j = 0; j < burstTime[i]; j++) {
            //         System.out.print(" ");
            //     }

            //     int finishTime = arrivalTime[i] + turnaroundTime[i];
            //     System.out.print(finishTime);
            // }

            System.out.println();

            System.out.println("\nProcess Details:");
            System.out.println(
                    "+----------+------------------+------------------+------------------+------------------+------------------+");

            System.out.printf("| %-8s | %-16s | %-16s | %-16s | %-16s | %-16s |\n", "Process", "Arrival Time",
                    "Burst Time", "Finish Time", "Turnaround Time", "Waiting Time");
            System.out.println(
                    "+----------+------------------+------------------+------------------+------------------+------------------+");

            for (int i = 0; i < numProcesses; i++) {
                int finishTime = arrivalTime[i] + turnaroundTime[i];
                System.out.printf("| %-8s | %-16s | %-16s | %-16s | %-16s | %-16s |\n",
                        "P" + i, arrivalTime[i], burstTime[i], finishTime, turnaroundTime[i], waitingTime[i]);
            }

            System.out.println(
                    "+----------+------------------+------------------+------------------+------------------+------------------+");

            double avgTurnaroundTime = (double) totalTurnaroundTime / numProcesses;
            double avgWaitingTime = (double) totalWaitingTime / numProcesses;
            DecimalFormat df = new DecimalFormat("#.##");

            System.out.println("\nTotal Turnaround Time: " + totalTurnaroundTime);
            System.out.println("Average Turnaround Time: " + df.format(avgTurnaroundTime));
            System.out.println("Total Waiting Time: " + totalWaitingTime);
            System.out.println("Average Waiting Time: " + df.format(avgWaitingTime));
        }
    }
}
