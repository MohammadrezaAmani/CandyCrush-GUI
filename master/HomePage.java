
/* 
home page for the user to see 4 buttons to navigate to different pages
*/
/*buton to start new game with candy crush class
button to load game
button to show about page
button to exit the game
*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class HomePage extends JFrame implements ActionListener {
    private JButton start, load, about, exit,Highscores1;
    private JPanel panel;
    private JLabel label;
    private JPanel buttonPanel;
    private JPanel labelPanel;
    static JFrame frame;

    public HomePage() {
        //desighn home page like candy crush with 5 buttons and a label
        frame = new JFrame("Home Page");
        frame.setSize(800, 400);
        //bg color 
        frame.setBackground(Color.PINK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //set location of the frame
        frame.setLocation(284, 160);
        frame.setLayout(new BorderLayout());
        panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        label = new JLabel("Home Page");
        label.setFont(new Font("Serif", Font.BOLD, 40));
        label.setHorizontalAlignment(JLabel.CENTER);
        labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(1, 1));
        labelPanel.add(label);
        panel.add(labelPanel);
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 5));
        start = new JButton("Start");
        start.addActionListener(this);
        load = new JButton("Load");
        load.addActionListener(this);
        about = new JButton("About");
        about.addActionListener(this);
        exit = new JButton("Exit");
        exit.addActionListener(this);
        //add highscores button
        Highscores1 = new JButton("Highscores");
        Highscores1.addActionListener(this);
        //add highscores button
        start.setBackground(Color.red);
        start.setForeground(Color.white);
        start.setFont(new Font("Serif", Font.BOLD, 20));
        load.setBackground(Color.red);
        load.setForeground(Color.white);
        load.setFont(new Font("Serif", Font.BOLD, 20));
        about.setBackground(Color.red);
        about.setForeground(Color.white);
        about.setFont(new Font("Serif", Font.BOLD, 20));
        exit.setBackground(Color.red);
        exit.setForeground(Color.white);
        exit.setFont(new Font("Serif", Font.BOLD, 20));
        Highscores1.setBackground(Color.red);
        Highscores1.setForeground(Color.white);
        Highscores1.setFont(new Font("Serif", Font.BOLD, 20));

        buttonPanel.add(start);
        buttonPanel.add(load);
        buttonPanel.add(about);
        buttonPanel.add(exit);
        //add highscores button
        buttonPanel.add(Highscores1);
        panel.add(buttonPanel);
        frame.add(panel);
        frame.setVisible(true);

        // setSize(500, 500);
        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setLayout(new BorderLayout());
        // panel = new JPanel();
        // panel.setLayout(new GridLayout(2, 1));
        // label = new JLabel("Welcome to Candy Crush");
        // label.setFont(new Font("Serif", Font.BOLD, 40));
        // label.setHorizontalAlignment(JLabel.CENTER);
        // labelPanel = new JPanel();
        // labelPanel.setLayout(new GridLayout(1, 1));
        // labelPanel.add(label);
        // panel.add(labelPanel);
        // buttonPanel = new JPanel();
        // buttonPanel.setLayout(new GridLayout(4, 1));
        // start = new JButton("Start New Game");
        // start.addActionListener(this);
        // load = new JButton("Load Game");
        // load.addActionListener(this);
        // about = new JButton("About");
        // about.addActionListener(this);
        // exit = new JButton("Exit");
        // exit.addActionListener(this);
        // Highscores1 = new JButton("Highscores");
        // //design the buttons to look like candy crush
        // start.setBackground(Color.red);
        // start.setForeground(Color.white);
        // start.setFont(new Font("Serif", Font.BOLD, 20));
        // load.setBackground(Color.red);
        // load.setForeground(Color.white);
        // load.setFont(new Font("Serif", Font.BOLD, 20));
        // about.setBackground(Color.red);
        // about.setForeground(Color.white);
        // about.setFont(new Font("Serif", Font.BOLD, 20));
        // exit.setBackground(Color.red);
        // exit.setForeground(Color.white);
        // exit.setFont(new Font("Serif", Font.BOLD, 20));
        // Highscores1.setBackground(Color.red);
        // Highscores1.setForeground(Color.white);
        // Highscores1.setFont(new Font("Serif", Font.BOLD, 20));
        // Highscores1.addActionListener(this);

        // buttonPanel.add(start);
        // buttonPanel.add(load);
        // buttonPanel.add(about);
        // buttonPanel.add(exit);
        // buttonPanel.add(Highscores1);
        // panel.add(buttonPanel);
        // add(panel, BorderLayout.CENTER);
        // setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == start) {
            new CandyCrush();
            CandyCrush.frame.setVisible(true);
            frame.setVisible(false);
        } else if (e.getSource() == load) {
            CandyCrush game = new CandyCrush();
            try {
                game.LoadGame();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            CandyCrush.frame.setVisible(true);
            frame.setVisible(false);
            
        } else if (e.getSource() == about) {
            AboutPage.showAboutPage();
        } else if (e.getSource() == exit) {
            System.exit(0);
        } else if (e.getSource() == Highscores1) {
            try {
                Highscores.showHighScores();
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

    }
//main method
public static void main(String[] args) {
    HomePage game = new HomePage();
    game.setVisible(true);
}
}