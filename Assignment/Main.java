import java.util.*;
public class Main {
    public static void main(String[] args) {
       
         Scanner input = new Scanner(System.in);
        int type = 0;
         do {
            System.out.print("Please select mode: \n" +
                                "1. NonPreemptiveSJF\n" +
                                "2. PreemptiveSJF\n" +
                                "3. NonPreemptivePriority\n" +
                                "4. RoundRobin\n\n" +
                                "Input (0 to quit) -> ");

            try {
                type = input.nextInt();
            } catch (Exception e) {
                System.out.println("\nError: Input is not an integer\n\n");
            }
             if (type == 0) System.exit(0);
            else if (!(type == 1 || type == 2 || type ==3 || type == 4)) System.out.println("\nPlease choose the correct option\n\n");
            else break;
        } while (true);

        if (type == 1) new NonPreemptiveSJF();
        else if (type == 2) new PreemptiveSJF();
        else if (type == 3) new NonPreemptivePriority();
        else if (type == 4) new RoundRobin();

        input.close();
    }
}