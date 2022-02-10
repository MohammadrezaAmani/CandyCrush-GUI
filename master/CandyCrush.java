/*
 * NAME OF CLASS: CandyCrush
 * AUTHOR: MOHAMMADREZA AMANI
 * DATE: JUN/2022
 * PURPOSE: To create a GAMEBOARD and a LOGIC for the game.
 * VERSION: 1.0
 * DETAILS: This class is the PRIMARY class for the game.
 * LOGIC:
 * 1. Create a GAMEBOARD.
 * - The GAMEBOARD is a 2D array of BUTTONS.
 * -each button is a candy.
 * -candies have a name in ij foramt that related to thei index in the array.
 * -candies have a color that is related to the name.
 * -candies have a Text that is related to the TYPE.
 * - The LOGIC of changing and destoying and filling the candies is in the
 * GAMEBOARD.
 * logic of changing:
 * "if they are CHANGEABLE then change their color and text but their name is constant."
 * logic of destroying:
 * "after checking if they are CHANGEABLE, we check their neighbors with CHANGELIST function and if they have the same color then we destroy them."
 * logic of filling:
 * "check if there is a empty button and if there is then fill it with upper candy and destroy the upper candy."
 * 2- after creating the GAMEBOARD we create a JFRAME and add the GAMEBOARD to
 * it and after that we add logic of the game to the JFRAME we have to
 * initialize the check for ending functions:
 * win function:
 * "if the high score is more than 1500 then we win."
 * lose function:
 * "if there is no more candies to move then we lose."
 * check for ending function:
 * "use CHANGEABLE function for all candies and if there is no more candies to move then we lose."
 * FUNCTIONS:
 * PUBLIC CandyCrush:
 * "constructor of the class."
 * PRIVATE VOID ACTIONLISTENER:
 * "this function is the action listener for the buttons."
 * PUBLIC VOID NEWGAME:
 * "start newgame randomly."
 * PUBLIC INT GETI:
 * "return the i of the button."
 * PUBLIC INT GETJ:
 * "return the j of the button."
 * PUBLIC VOID ACTIONPERFORMED:
 * "this function is the action performed for the buttons."
 * 
 */
 // -------------import libraries-----------------
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

//--------------writing the class -----------------
public class CandyCrush implements ActionListener, KeyListener {
    static JFrame frame;
    final String RC = "\u229B"; // for bombs
    final String SC = "\u2299"; // for simple candy
    final String LC = "\u211A"; // for coulmn bomb candy
    final String LR = "\u2296"; // for row bomb candy
    final String empty = " ";
    final Color blue = Color.blue; // for blue color of font of candy
    final Color red = Color.red; // for red color of font of candy
    final Color green = Color.green; // for green color of font of candy
    final Color yellow = Color.yellow; // for yellow color of font of candy
    // navy blue = rgb(0,0,128)
    final Color bg = new Color(0, 0, 120); // for background color of game
    final Color bg2 = new Color(0, 0, 120);
    final Color fg = Color.white; // fg for none-candies
    final Color selected = Color.CYAN; // for selected candy
    final Color destoyed = Color.black; // for destroyed candy
    final Color hint = Color.gray; // for hint
    MenuBar menuBar; // for menu bar
    Menu fileMenu, helpMenu; // for menu
    MenuItem newGame, LoadGame, SaveGame, Help, About, Exit; // for menu items
    JPanel gamePanel, scorePanel, infoPanel, buttonPanel; // for panels
    JLabel scoreLabel, infoLabel; // for labels
    JButton backButton; // for back button
    JButton lastClicked; // for last clicked button
    JButton emptyButton; // for empty button
    JButton[][] button; // for buttons
    JButton RCButton, SCButton, LCButton, LRButton; // for buttons
    int score; // for saving score
    int lci, lcj; // for saving last clicked index
    int hi, hj, hx, hy; // for saving hint
    ArrayList<JButton> destroy_list; // for saving destroyed candies
    ArrayList<JButton> dest; // for saving destroyed candies of row
    ArrayList<JButton> dest2; // for saving destroyed candies of column
    ArrayList<JButton> dest3; // for saving destroyed candies of RB

    public CandyCrush() { // constructor
        frame = new JFrame("Candy Crush");
        frame.setLocation(360, 0);
        frame.setSize(720, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        // setResizable(false);
        // setLocationRelativeTo(null);
        menuBar = new MenuBar();
        fileMenu = new Menu("File");
        helpMenu = new Menu("Help");
        newGame = new MenuItem("New Game");
        LoadGame = new MenuItem("Load Game");
        SaveGame = new MenuItem("Save Game");
        Help = new MenuItem("Help");
        About = new MenuItem("About");
        Exit = new MenuItem("Exit");
        fileMenu.add(newGame);
        fileMenu.add(LoadGame);
        fileMenu.add(SaveGame);
        fileMenu.add(Exit);
        helpMenu.add(Help);
        helpMenu.add(About);
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        frame.setMenuBar(menuBar);
        RCButton = new JButton(RC);
        RCButton.setBackground(bg);
        RCButton.setText(RC);
        SCButton = new JButton(SC);
        SCButton.setBackground(bg);
        SCButton.setText(SC);
        LCButton = new JButton(LC);
        LCButton.setBackground(bg);
        LCButton.setText(LC);
        LRButton = new JButton(LR);
        LRButton.setBackground(bg);
        LRButton.setText(LR);
        gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(10, 10));
        gamePanel.setBackground(bg);
        gamePanel.setBorder(BorderFactory.createLineBorder(Color.black));
        scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(1, 2));
        scorePanel.setBackground(bg);
        scorePanel.setBorder(BorderFactory.createLineBorder(Color.black));
        infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(1, 2));
        infoPanel.setBackground(bg);
        infoPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.setBackground(bg);
        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setForeground(fg);
        infoLabel = new JLabel("");
        infoLabel.setForeground(fg);
        backButton = new JButton("Back");
        backButton.setBackground(bg);
        backButton.setForeground(fg);
        backButton.setBorder(BorderFactory.createLineBorder(Color.black));
        scorePanel.add(scoreLabel);
        scorePanel.add(infoLabel);
        infoPanel.add(backButton);
        buttonPanel.add(scorePanel);
        buttonPanel.add(infoPanel);
        frame.add(gamePanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        newGame();
        frame.setVisible(true);
        addActionListener(this);
        lastClicked.setForeground(bg);
        dest = new ArrayList<JButton>(); // for saving destroyed candies of row
        dest2 = new ArrayList<JButton>(); // for saving destroyed candies of column
        dest3 = new ArrayList<JButton>(); // for saving destroyed candies of RB
    }

    private void addActionListener(CandyCrush candyCrush) { // for adding action listener
        newGame.addActionListener(candyCrush);
        LoadGame.addActionListener(candyCrush);
        SaveGame.addActionListener(candyCrush);
        Help.addActionListener(candyCrush);
        About.addActionListener(candyCrush);
        Exit.addActionListener(candyCrush);
        backButton.addActionListener(candyCrush);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                button[i][j].addActionListener(candyCrush);
            }
        }
    }

    public void newGame() {
        score = 0;
        lastClicked = new JButton();
        lastClicked.setName("null");
        scoreLabel.setText("Score: " + score);
        button = new JButton[10][10];
        String[] Candies = { SC };
        Color[] Colors = { blue, red, green, yellow };
        for (int i = 0; i < 10; i++) {
            Random rand = new Random();
            Random rand1 = new Random();
            for (int j = 0; j < 10; j++) {
                int r = rand.nextInt(4);
                int r1 = rand1.nextInt(Candies.length);
                button[i][j] = new JButton(Candies[r1]);
                button[i][j].setBackground(bg2);
                button[i][j].setForeground(Colors[r]);
                button[i][j].setBorder(BorderFactory.createEmptyBorder());
                button[i][j].setName("" + i + j);
                // font
                Font font = new Font("Arial", Font.BOLD, 40);
                button[i][j].setFont(font);
                gamePanel.add(button[i][j]);
            }
        }
        destroy_list = new ArrayList<JButton>();
    }

    int getI(JButton b) {
        return Integer.parseInt(b.getName().substring(0, 1));
    }

    int getJ(JButton b) {
        return Integer.parseInt(b.getName().substring(1, 2));
    }

    // action listener
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newGame) {
            newGame();
        } else if (e.getSource() == LoadGame) {
            try {
                LoadGame();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } else if (e.getSource() == SaveGame) {
            SaveGame();
        } else if (e.getSource() == Help) {
            Help_me();
        } else if (e.getSource() == About) {
            About();
        } else if (e.getSource() == Exit) {
            System.exit(0);
        } else if (e.getSource() == backButton) {
            backButton();
        } else {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (e.getSource() == button[i][j]) {
                        win();
                        gameOver();
                        if (!lastClicked.getName().equals("null")) {
                            if (!lastClicked.getName().equals(button[i][j].getName())) {
                                change(lci, lcj, i, j);

                                if (destroy_list.size() > 0) {
                                    if (!destroy_list.contains(button[i][j])) {
                                        destroy_list.add(button[i][j]);
                                    }
                                    if (changeable(i, j, lci, lcj)) {
                                        if (!destroy_list.contains(button[lci][lcj])) {
                                            destroy_list.add(button[lci][lcj]);
                                        }
                                    }
                                    destroy();
                                    scoreLabel.setText("Score: " + score);

                                }
                                while (isEmpty()) {
                                    fill();
                                }
                                destroy_list.clear();
                            }
                            deselect(lci, lcj);
                        } else {
                            select(i, j);
                        }
                    }
                }
            }
        }
    }

    public void destroy() {
        // System.out.println(destroy_list.size());
        // //print everything in the list
        // for (int i = 0; i < destroy_list.size(); i++) {
        // System.out.println(destroy_list.get(i).getText());
        // }
        // print everything in the list
        // for i in the destroy list if in the dest dest2 dest3 remove
        for (int i = 0; i < dest.size(); i++) {
            if (destroy_list.contains(dest.get(i))) {
                destroy_list.remove(destroy_list.indexOf(dest.get(i)));
                i--;
            }
        }
        for (int i = 0; i < dest2.size(); i++) {
            if (destroy_list.contains(dest2.get(i))) {
                destroy_list.remove(destroy_list.indexOf(dest2.get(i)));
                i--;
            }
        }
        for (int i = 0; i < dest3.size(); i++) {
            if (destroy_list.contains(dest3.get(i))) {
                destroy_list.remove(destroy_list.indexOf(dest3.get(i)));
                i--;
            }
        }
        // // for i in dest3 if dest2 and dest contains it remove ftom dest and dest2
        for (int i = 0; i < dest3.size(); i++) {
            if (dest2.contains(dest3.get(i))) {
                dest2.remove(dest2.indexOf(dest3.get(i)));
                i--;
            }
            if (dest.contains(dest3.get(i))) {
                dest.remove(dest.indexOf(dest3.get(i)));
                i--;
            }
       }
        while (dest.size() > 0) {
            try {
                System.out.println("dest: " + dest.get(0).getName());
                randomCandy(getI(dest.get(0)), getJ(dest.get(0)), LR);
                dest.remove(0);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        while (dest2.size() > 0) {
            try {
                System.out.println("dest2: " + dest2.get(0).getName());
                randomCandy(getI(dest2.get(0)), getJ(dest2.get(0)), LC);
                dest2.remove(0);

            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        while (dest3.size() > 0) {
            try {
            System.out.println("dest3: " + dest3.get(0).getName());
            randomCandy(getI(dest3.get(0)), getJ(dest3.get(0)), RC);
            dest3.remove(0);

            } catch (Exception e) {

            }
        }
        for (int i = 0; i < destroy_list.size(); i++) {
            System.out.println(destroy_list.get(i).getText());
        }
        while (destroy_list.size() > 0) {
            if (destroy_list.get(0).getText().equals(SCButton.getText())) {
                score += 5;
                button[getI(destroy_list.get(0))][getJ(destroy_list.get(0))].setText(" ");
                button[getI(destroy_list.get(0))][getJ(destroy_list.get(0))].setForeground(bg);
                destroy_list.remove(0);
                scoreLabel.setText("Score: " + score);
            } else if (destroy_list.get(0).getText().equals(RCButton.getText())) {
                button[getI(destroy_list.get(0))][getJ(destroy_list.get(0))].setText(SC);
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        try {
                            destroy_list.add(button[getI(destroy_list.get(0)) + i][getJ(destroy_list.get(0)) + j]);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                }
                destroy_list.remove(0);
                score += 15;
                scoreLabel.setText("Score: " + score);
                destroy();
            } else if (destroy_list.get(0).getText().equals(LCButton.getText())) {
                button[getI(destroy_list.get(0))][getJ(destroy_list.get(0))].setText(SC);
                for (int i = 0; i < 10; i++) {
                    try {
                        destroy_list.add(button[i][getJ(destroy_list.get(0))]);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
                score += 10;
                scoreLabel.setText("Score: " + score);
                destroy_list.remove(0);
                System.out.println("destroy");
                destroy();
            } else if (destroy_list.get(0).getText().equals(LRButton.getText())) {
                button[getI(destroy_list.get(0))][getJ(destroy_list.get(0))].setText(SC);
                for (int j = 0; j < 10; j++) {
                    try {
                        destroy_list.add(button[getI(destroy_list.get(0))][j]);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
                destroy_list.remove(0);
                score += 10;
                scoreLabel.setText("Score: " + score);
                destroy();
            } else {
                scoreLabel.setText("Score: " + score);
                destroy_list.remove(0);
            }
        }
    }

    // about method show a dialog box with information about the game
    public void About() {
        JOptionPane.showMessageDialog(null, "Candy Crush\n\n" + "Version: 1.0\n" + "Author: Mohammadreza Amani\n"
                + "Date: 20/1/2022\n" + "Email: More.Amani@yahoo.com");
    }

    public void uh() {
        if (hi == -12) {
            return;
        }
        try {
            button[hi][hj].setBackground(bg2);
            button[hx][hy].setBackground(bg2);
        } catch (Exception e) {
            System.out.println("");
        }
        hi = -12;
        hj = -12;
        hx = -12;
        hy = -12;

    }

    public void Help_me() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                try {
                    if (changeable(i, j, i + 1, j)) {
                        button[i][j].setBackground(hint);
                        button[i + 1][j].setBackground(hint);
                        hi = i;
                        hj = j;
                        hx = i + 1;
                        hy = j;
                        return;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    if (changeable(i + 1, j, i, j)) {
                        button[i][j].setBackground(hint);
                        button[i + 1][j].setBackground(hint);
                        hi = i + 1;
                        hj = j;
                        hx = i;
                        hy = j;
                        return;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    if (changeable(i, j + 1, i, j)) {
                        button[i][j].setBackground(hint);
                        button[i][j + 1].setBackground(hint);
                        hi = i;
                        hj = j + 1;
                        hx = i;
                        hy = j;
                        return;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    if (changeable(i, j, i, j + 1)) {
                        button[i][j].setBackground(hint);
                        button[i][j + 1].setBackground(hint);
                        hi = i;
                        hj = j;
                        hx = i;
                        hy = j + 1;
                        return;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }
    }

    // game over
    public void gameOver() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                try {
                    if (changeable(i, j, i + 1, j)) {

                        return;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    if (changeable(i + 1, j, i, j)) {
                        return;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    if (changeable(i, j + 1, i, j)) {
                        return;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    if (changeable(i, j, i, j + 1)) {
                        return;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }

        JOptionPane.showMessageDialog(null, "Game Over");

    }

    public void backButton() {
        new HomePage();
        ((Window) CandyCrush.frame).setVisible(false);
    }

    public void SaveGame() {
        String text = "";
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (button[i][j].getText().equals(RC)) {
                    text += "RC";
                    if (button[i][j].getForeground().equals(blue)) {
                        text += "R";
                    } else if (button[i][j].getForeground().equals(red)) {
                        text += "C";
                    } else if (button[i][j].getForeground().equals(green)) {
                        text += "L";
                    } else if (button[i][j].getForeground().equals(yellow)) {
                        text += "Y";
                    }
                    if (j != 9) {
                        text += ",";
                    }
                } else if (button[i][j].getText().equals(LC)) {
                    text += "LC";
                    if (button[i][j].getForeground().equals(blue)) {
                        text += "R";
                    } else if (button[i][j].getForeground().equals(red)) {
                        text += "C";
                    } else if (button[i][j].getForeground().equals(green)) {
                        text += "L";
                    } else if (button[i][j].getForeground().equals(yellow)) {
                        text += "Y";
                    }
                    if (j != 9) {
                        text += ",";
                    }
                } else if (button[i][j].getText().equals(LR)) {
                    text += "LR";
                    if (button[i][j].getForeground().equals(blue)) {
                        text += "R";
                    } else if (button[i][j].getForeground().equals(red)) {
                        text += "C";
                    } else if (button[i][j].getForeground().equals(green)) {
                        text += "L";
                    } else if (button[i][j].getForeground().equals(yellow)) {
                        text += "Y";
                    }
                    if (j != 9) {
                        text += ",";
                    }
                } else if (button[i][j].getText().equals(SC)) {
                    text += "SC";
                    if (button[i][j].getForeground().equals(blue)) {
                        text += "R";
                    } else if (button[i][j].getForeground().equals(red)) {
                        text += "C";
                    } else if (button[i][j].getForeground().equals(green)) {
                        text += "L";
                    } else if (button[i][j].getForeground().equals(yellow)) {
                        text += "Y";
                    }
                    if (j != 9) {
                        text += ",";
                    }
                }

            }
            text += "\n";
        }
        try {
            FileWriter fw = new FileWriter("CandyCrush.csv");
            // show dialog box
            JOptionPane.showMessageDialog(null, "Game Saved");
            fw.write(text);
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void LoadGame() throws IOException {
        // create a file chooser object and save content in to text variable
        String text = "";
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        System.out.println(result);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println(selectedFile.getAbsolutePath());
            try {
                // every thing in the file is read and stored in the text variable
                String path = selectedFile.getAbsolutePath();
                Scanner sc = new Scanner(new File(path));
                System.out.println(text);
                while (sc.hasNextLine()) {
                    text += sc.nextLine();
                    text += "\n";
                    System.out.println(text);
                }
                // this get no line found error

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println(text);
        // split the text into lines
        Scanner input = new Scanner(text);
        for (int i = 0; i < 10; i++) {
            if (input.hasNextLine()) {
                String line = input.nextLine();
                System.out.println(line);
                for (int j = 0; j < 10; j++) {
                    String next = line.split(",")[j];
                    System.out.println(next);
                    String no = next.substring(0, 2);
                    String color = next.substring(2, 3);
                    System.out.println(no + " " + color);
                    if (no.equals("RC")) {
                        button[i][j].setText(RC);
                    } else if (no.equals("LC")) {
                        button[i][j].setText(LC);
                    } else if (no.equals("LR")) {
                        button[i][j].setText(LR);
                    } else if (no.equals("SC")) {
                        button[i][j].setText(SC);
                    }
                    if (color.equals("R")) {
                        button[i][j].setForeground(red);
                    } else if (color.equals("C")) {
                        button[i][j].setForeground(blue);
                    } else if (color.equals("G")) {
                        button[i][j].setForeground(green);
                    } else if (color.equals("Y")) {
                        button[i][j].setForeground(yellow);
                    }
                    button[i][j].setBackground(bg);
                    button[i][j].setName("" + i + j);
                }
            } else {
                break;
            }
        }
        input.close();
    }

    public void select(int i, int j) {
        lci = i;
        lcj = j;
        lastClicked.setName(button[i][j].getName());
        try {
            button[i][j].setBackground(selected);
        } catch (Exception act) {
        }
    }

    public void deselect(int i, int j) {
        try {
            button[i][j].setBackground(bg);
            lastClicked.setName("null");
        } catch (Exception e) {
            // TODO: handle exception
            lastClicked.setName("null");
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            new Menu();
            frame.dispose();
        }
    }

    public void win() {
        if (score >= 1500) {
            JOptionPane.showMessageDialog(null, "You Win");
            new Menu();
            frame.dispose();
        }
    }

    public void saveScoreAtHS() throws IOException {
        Scanner sc = new Scanner(new File("hs"));
        int[] hs = new int[5];
        for (int i = 0; i < 5; i++) {
            hs[i] = sc.nextInt();
        }
        // for i in hs if score > i then insert score at i
        for (int i = 0; i < 5; i++) {
            if (score > hs[i]) {
                for (int j = 4; j > i; j--) {
                    hs[j] = hs[j - 1];
                }
                hs[i] = score;
                break;
            }
        }
        sc.close();
        PrintStream ps = new PrintStream(new File("hs"));
        for (int i = 0; i < 5; i++) {
            ps.println(hs[i]);

        }
    }

    public boolean changeable(int i, int j, int x, int y) {
        if (Math.abs(i - x) + Math.abs(j - y) > 1 || Math.abs(i - x) + Math.abs(j - y) == 0) {
            return false;
        }
        if (same(i, j, x, y))
            return false;
        int count = 0;
        if (i == x) {
            int k = y - j;

            for (int l = 1; l < 3; l++) {
                try {
                    if (same(i, j, x, y + k * l)) {
                        count++;
                        if (count > 1) {
                            return true;
                        }
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    if (count > 1) {
                        return true;
                    }
                }
            }
            count = 0;
            for (int l = 1; l < 3; l++) {
                try {
                    if (same(i, j, i + l, y)) {
                        count++;
                        if (count > 1) {
                            return true;
                        }
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    if (count > 1) {
                        return true;
                    } else {
                        break;
                    }
                }
            }

            for (int l = 1; l < 3; l++) {
                try {
                    if (same(i, j, i - l, y)) {
                        count++;
                        if (count > 1) {
                            return true;
                        }
                    } else {
                        count = 0;
                        break;
                    }
                } catch (Exception e) {
                    if (count > 1) {
                        return true;
                    } else {
                        count = 0;
                    }
                }
            }
        }
        count = 0;
        if (j == y) {
            int k = x - i;

            for (int l = 1; l < 3; l++) {
                try {
                    if (same(i, j, x + k * l, y)) {
                        count++;
                        if (count > 1) {
                            return true;
                        }
                    } else {
                        count = 0;
                        break;
                    }
                } catch (Exception e) {
                    if (count > 1) {
                        return true;
                    } else {
                        count = 0;
                        break;
                    }
                }
            }
            count = 0;
            for (int l = 1; l < 3; l++) {
                try {
                    if (same(i, j, x, y + l)) {
                        count++;
                        if (count > 1) {
                            return true;
                        }
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    if (count > 1) {
                        return true;
                    } else {
                        break;
                    }
                }
            }
            for (int l = 1; l < 3; l++) {
                try {
                    if (same(i, j, x, y - l)) {
                        count++;
                        if (count > 1) {
                            return true;
                        }
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    if (count > 1) {
                        return true;
                    } else {
                        break;
                    }
                }

            }
        }
        return false;
    }

    public void addToDestroyList(ArrayList<String> a) {
        for (int i = 0; i < a.size(); i++) {
            int b = a.get(i).charAt(0) - '0';
            int c = a.get(i).charAt(2) - '0';
            // if not in destroy list add to it
            if (!destroy_list.contains(button[b][c])) {
                destroy_list.add(button[b][c]);
            }
        }
    }

    public boolean same(int i, int j, int x, int y) {
        if (button[i][j].getForeground() == button[x][y].getForeground()) {
            return true;
        }
        return false;
    }

    public boolean changeslist(int i, int j, int x, int y) {
        if (Math.abs(i - x) + Math.abs(j - y) > 1 || Math.abs(i - x) + Math.abs(j - y) == 0) {
            return false;
        }
        int count = 0;
        ArrayList<String> list = new ArrayList<String>();
        if (i == x) {
            int k = y - j;

            for (int l = 1; l < 10; l++) {
                try {
                    if (same(i, j, x, y + k * l)) {
                        System.out.println("left- right: " + k);
                        count++;
                        list.add(i + "," + (y + k * l));
                        if (count > 1) {
                            addToDestroyList(list);
                            if (count == 3) {
                                dest.add(button[x][y]);
                            }
                            if (count == 4) {
                                // remove last index in dest
                                dest3.add(button[x][y]);

                            }
                        }
                    } else {
                        list.clear();
                        break;
                    }
                } catch (Exception e) {
                    if (count > 1) {
                        break;
                    }
                }
            }
            count = 0;
            for (int l = 1; l < 10; l++) {
                try {
                    if (same(i, j, i + l, y)) {
                        System.out.println("down");
                        list.add("" + (i + l) + "," + y);
                        count++;
                        if (count > 1) {
                            addToDestroyList(list);
                            if (count == 3) {
                                dest2.add(button[x][y]);
                            }
                            if (count == 4) {
                                
                                dest3.add(button[x][y]);

                            }
                        }
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    if (count > 1) {
                        break;
                    } else {
                        break;
                    }
                }
            }

            for (int l = 1; l < 10; l++) {
                try {
                    if (same(i, j, i - l, y)) {
                        System.out.println("up");
                        list.add("" + (i - l) + "," + y);
                        count++;
                        if (count > 1) {
                            addToDestroyList(list);
                            if (count == 3) {
                                dest2.add(button[x][y]);
                            }
                            if (count == 4) {
                                
                                dest3.add(button[x][y]);

                            }
                        }
                    } else {
                        list.clear();
                        break;
                    }
                } catch (Exception e) {
                    if (count > 1) {
                        list.clear();
                        break;
                    } else {
                        list.clear();
                        break;
                    }
                }
            }
        }
        count = 0;
        if (j == y) {
            int k = x - i;

            for (int l = 1; l < 10; l++) {
                try {
                    if (same(i, j, x + k * l, y)) {
                        System.out.println("up down " + (x + k * l) + y);
                        list.add("" + (x + k * l) + "," + y);
                        count++;
                        if (count > 1) {
                            addToDestroyList(list);
                            if (count == 3) {
                                dest2.add(button[x][y]);
                            }
                            if (count == 4) {
                                
                                dest3.add(button[x][y]);

                            }
                        }
                    } else {
                        list.clear();
                        break;
                    }
                } catch (Exception e) {
                    if (count > 1) {
                        list.clear();
                        break;
                    } else {
                        list.clear();
                        break;
                    }
                }
            }
            count = 0;
            for (int l = 1; l < 10; l++) {
                try {
                    if (same(i, j, x, y + l)) {
                        System.out.println("right");
                        list.add("" + x + "," + (y + l));
                        count++;
                        if (count > 1) {
                            addToDestroyList(list);
                            if (count == 3) {
                                dest.add(button[x][y]);
                            }
                            if (count == 4) {
                                
                                dest3.add(button[x][y]);

                            }
                        }
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    if (count > 1) {
                        break;
                    } else {
                        break;
                    }
                }
            }
            for (int l = 1; l < 10; l++) {
                try {
                    if (same(i, j, x, y - l)) {
                        System.out.println("left");
                        list.add(x + "," + (y - l));
                        count++;
                        if (count > 1) {
                            if (count == 3) {
                                dest.add(button[x][y]);
                            }
                            if (count == 4) {
                                
                                dest3.add(button[x][y]);

                            }
                            addToDestroyList(list);
                        }
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    if (count > 1) {
                        addToDestroyList(list);
                        list.clear();
                        break;
                    } else {
                        list.clear();
                        break;
                    }
                }

            }
        }
        // print everything in the destroy list
        for (int l = 0; l < dest3.size(); l++) {
            System.out.println(dest3.get(l).getText());
        }
        return true;
    }

    public void change(int i, int j, int x, int y) {
        if (!(changeable(i, j, x, y) || changeable(x, y, i, j))) {
            deselect(i, j);
            return;
        } else {
            changeslist(i, j, x, y);
            changeslist(x, y, i, j);
            // if (destroy_list.size() == 4)
            // dest.add(button[i][j]);
            // if (destroy_list.size() >= 5)
            // dest2.add(button[i][j]);
            // change the lastClicked
            lastClicked.setName("null");
            // change the colors and the names and text of the button[i][j] and [x][y]
            JButton temp1 = new JButton();
            temp1.setText(button[i][j].getText());
            // exchange the fonts and foreground colors
            temp1.setForeground(button[i][j].getForeground());
            button[i][j].setText(button[x][y].getText());
            button[i][j].setForeground(button[x][y].getForeground());
            button[x][y].setText(temp1.getText());
            button[x][y].setForeground(temp1.getForeground());

        }
    }

    public static void main(String[] args) {
        new CandyCrush();

    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    public void simpleChange(int i, int j, int x, int y) {
        JButton temp1 = new JButton();
        temp1.setText(button[i][j].getText());
        temp1.setForeground(button[i][j].getForeground());
        button[i][j].setText(button[x][y].getText());
        button[i][j].setForeground(button[x][y].getForeground());
        button[x][y].setText(temp1.getText());
        button[x][y].setForeground(temp1.getForeground());
    }

    public void randomCandy(int i, int j, String name) {
        Random rand1 = new Random();
        // String[] Candies = { SC };
        Color[] Colors = { blue, red, green, yellow };
        // int x = rand.nextInt(Candies.length);
        int y = rand1.nextInt(Colors.length);
        button[i][j].setText(name);
        button[i][j].setForeground(Colors[y]);
    }

    public void fill() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (button[i][j].getForeground().equals(lastClicked.getForeground())) {
                    if (i == 0) {
                        randomCandy(i, j, SC);
                        button[i][j].setBackground(bg);
                    } else if (!button[i - 1][j].getForeground().equals(lastClicked.getForeground())) {
                        simpleChange(i, j, i - 1, j);
                        button[i][j].setBackground(bg);
                    }
                }
            }
        }
    }

    public boolean isEmpty() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (button[i][j].getForeground().equals(lastClicked.getForeground())) {
                    return true;
                }
            }
        }
        return false;
    }
}

