import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("Select an option:");
            System.out.println("1. Non-Preemptive Shortest Job First (SJF)");
            System.out.println("2. Preemptive Shortest Job First (SJF)");
            System.out.println("3. Non-Preemptive Priority");
            System.out.println("4. Round Robin");
            System.out.println("0. Exit");
            System.out.println();
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 0:
                    exit = true;
                    System.out.println("Exiting the program...");
                    break;
                case 1:
                    new NonPreemptiveSJF();
                    
                case 2:
                    new PreemptiveSJF();
                    
                case 3:
                    new NonPreemptivePriority();
                    
                case 4:
                    new RoundRobin();
                    
                default:
                    System.out.println("Invalid choice! Please enter a valid option.");
            }
        }

        scanner.close();
    }
}
