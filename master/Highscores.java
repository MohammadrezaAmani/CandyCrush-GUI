
import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

public class Highscores extends JFrame {
    // show a window with the about page
    static JFrame frame;

    public static void showHighScores() throws FileNotFoundException {
        frame = new JFrame("High scores");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        JLabel label = new JLabel("High scores");
        label.setFont(new Font("Serif", Font.BOLD, 40));
        label.setHorizontalAlignment(JLabel.CENTER);
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(1, 1));
        labelPanel.add(label);
        panel.add(labelPanel);
        //open highscore.txt
        File hs = new File("hs");
        String Message = "";
        // show text
        Scanner sc = new Scanner(hs);
        int i = 0;
        while(sc.hasNextLine())
        {
            i += 1;
            Message += sc.nextLine();
            Message += "\n";
            if(i>6)
                break;
        }
        sc.close();

        JTextArea textArea = new JTextArea(Message);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(500, 500));
        JOptionPane.showMessageDialog(null, scrollPane);
    }
}