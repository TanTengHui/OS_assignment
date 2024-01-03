import java.util.*;

public class Main {
    private static final String[] MODES = {
        "NonPreemptiveSJF",
        "PreemptiveSJF",
        "NonPreemptivePriority",
        "RoundRobin"
    };

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int type;

        do {
            do {
                System.out.println("Please select mode: ");
                for (int i = 0; i < MODES.length; i++) {
                    System.out.printf("%d. %s\n", i + 1, MODES[i]);
                }
                System.out.print("\nInput (0 to quit) -> ");

                try {
                    type = input.nextInt();
                } catch (Exception e) {
                    System.out.println("\nError: Input is not an integer\n\n");
                    input.nextLine(); // Clear the input buffer
                    continue;
                }

                if (type < 0 || type > MODES.length) System.out.println("\nPlease choose the correct option\n\n");
                else break;
            } while (true);

            switch (type) {
                case 0:
                    System.exit(0);
                case 1:
                    new NonPreemptiveSJF();
                    break;
                case 2:
                    new PreemptiveSJF();
                    break;
                case 3:
                    new NonPreemptivePriority();
                    break;
                case 4:
                    new RoundRobin();
                    break;
                default:
                    System.out.println("\nPlease choose the correct option\n\n");
            }
        } while (type != 0);
    }
}