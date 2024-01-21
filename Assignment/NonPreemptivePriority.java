import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class Process {
    String processName;
    int arrivalTime;
    int burstTime;
    int priority;

    public Process(String processName, int arrivalTime, int burstTime, int priority) {
        this.processName = processName;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
    }

   
}

public class NonPreemptivePriority {
    public NonPreemptivePriority() {
        executeNonPriority();
    }

    private void executeNonPriority() {
        try (Scanner scanner = new Scanner(System.in)) {
            int numProcesses;
            while (true) {
                System.out.print("Enter the number of processes (3-10): ");
                numProcesses = scanner.nextInt();

                if (numProcesses >= 3 && numProcesses <= 10) {
                    break;
                } else {
                    System.out.println("Number of processes should be between 3 and 10. Please try again.");
                }
            }

            List<Process> processes = new ArrayList<>();

            for (int i = 0; i < numProcesses; i++) {
                System.out.println("Enter details for Process P" + i + ":");
                System.out.print("Arrival Time: ");
                int arrivalTime = scanner.nextInt();
                System.out.print("Burst Time: ");
                int burstTime = scanner.nextInt();
                System.out.print("Priority: ");
                int priority = scanner.nextInt();

                processes.add(new Process("P" + i, arrivalTime, burstTime, priority));
            }

            processes.sort((p1, p2) -> {
                if (p1.arrivalTime == p2.arrivalTime) {
                    return Integer.compare(p2.priority, p1.priority); // Higher priority first
                }
                return Integer.compare(p1.arrivalTime, p2.arrivalTime); // Lower arrival time first
            }); // Sort processes by process name

            int currentTime = 0;
            int totalTurnaroundTime = 0;
            int totalWaitingTime = 0;

            List<Process> readyQueue = new ArrayList<>();
            List<String[]> tableData = new ArrayList<>();
            StringBuilder ganttChart = new StringBuilder("|");
            StringBuilder upperLine = new StringBuilder("-");
            StringBuilder lowerLine = new StringBuilder("-");

            while (!processes.isEmpty() || !readyQueue.isEmpty()) {
                while (!processes.isEmpty() && processes.get(0).arrivalTime <= currentTime) {
                    readyQueue.add(processes.remove(0));
                    readyQueue.sort(Comparator.comparing(p -> p.priority)); // Sort ready queue by priority
                }

                if (!readyQueue.isEmpty()) {
                    Process currentProcess = readyQueue.remove(0);
                    int finishTime = currentTime + currentProcess.burstTime;
                    int turnaroundTime = finishTime - currentProcess.arrivalTime;
                    int waitingTime = turnaroundTime - currentProcess.burstTime;

                    totalTurnaroundTime += turnaroundTime;
                    totalWaitingTime += waitingTime;

                    String[] rowData = {
                            currentProcess.processName,
                            String.valueOf(currentProcess.arrivalTime),
                            String.valueOf(currentProcess.burstTime),
                            String.valueOf(finishTime),
                            String.valueOf(currentProcess.priority),
                            String.valueOf(turnaroundTime),
                            String.valueOf(waitingTime)
                    };
                    tableData.add(rowData);
                    int ganttChartWidth = 5; // constant size for the Gantt chart

                    upperLine.append("-".repeat(Math.max(0, currentProcess.burstTime))).append("-----");
                    ganttChart.append(String.format(" %-" + ganttChartWidth + "s |", currentProcess.processName));
                    lowerLine.append("-".repeat(Math.max(0, currentProcess.burstTime))).append("-----");
                    
                    currentTime = finishTime;
                } else {
                    currentTime++; // Idle CPU when no processes are in the ready queue
                }
            }

            int chartLength = ganttChart.length();
            upperLine.setLength(chartLength);
            lowerLine.setLength(chartLength);

            // Display Gantt chart
            System.out.println("\nGantt Chart:");
            System.out.println(upperLine);
            System.out.println(ganttChart.toString());
            System.out.println(lowerLine);
            
            
            
            
            // Display process details table
            System.out.println("\nProcess Details:");
            System.out.println("+----------+------------------+------------------+------------------+------------------+------------------+------------------+");
            System.out.printf("| %-8s | %-16s | %-16s | %-16s | %-16s | %-16s | %-16s |\n", "Process", "Arrival Time", "Burst Time", "Finish Time", "Priority", "Turnaround Time", "Waiting Time");
            System.out.println("+----------+------------------+------------------+------------------+------------------+------------------+------------------+");
            tableData.sort(Comparator.comparing(row -> Integer.parseInt(row[0].substring(1))));

            for (String[] row : tableData) {
                System.out.printf("| %-8s | %-16s | %-16s | %-16s | %-16s | %-16s | %-16s |\n", row[0], row[1], row[2], row[3], row[4], row[5], row[6]);
            }
            System.out.println("+----------+------------------+------------------+------------------+------------------+------------------+------------------+");

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
