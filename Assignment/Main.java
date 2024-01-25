import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    private static JFrame mainFrame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            mainFrame = new JFrame("Scheduling Algorithms");
            mainFrame.setSize(400, 200);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setLayout(new GridLayout(2, 2, 10, 10));
            mainFrame.getContentPane().setBackground(Color.lightGray);

            JButton btnNonPreemptiveSJF = createButton("Non-Preemptive SJF");
            JButton btnPreemptiveSJF = createButton("Preemptive SJF");
            JButton btnNonPreemptivePriority = createButton("Non-Preemptive Priority");
            JButton btnRoundRobin = createButton("Round Robin");

            mainFrame.add(btnNonPreemptiveSJF);
            mainFrame.add(btnPreemptiveSJF);
            mainFrame.add(btnNonPreemptivePriority);
            mainFrame.add(btnRoundRobin);

            mainFrame.setVisible(true);
        });
    }

    private static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.white);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleButtonClick(text);
            }
        });
        return button;
    }

    private static void handleButtonClick(String algorithm) {
        switch (algorithm) {
            case "Non-Preemptive SJF":
                NonPreemptiveSJFGUI window1 = new NonPreemptiveSJFGUI();
                window1.display1();
                break;
            case "Preemptive SJF":
                PreemptiveSJFGUI window2 = new PreemptiveSJFGUI();
                window2.display2();
                break;
            case "Non-Preemptive Priority":
                NonPreemptivePriorityGUI window3 = new NonPreemptivePriorityGUI();
                window3.display3();
                break;
            case "Round Robin":
                RoundRobinGUI window4 = new RoundRobinGUI();
                window4.display4();
                break;
            default:
                break;
        }
    }

    // Method to display the main homepage
    public static void displayHomepage() {
        mainFrame.setVisible(true);
    }
}
