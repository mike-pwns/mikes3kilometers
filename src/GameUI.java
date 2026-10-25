import javax.swing.*; // yes.. never thought id ever use this again..
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.io.File;

public class GameUI extends JFrame {

    String currentImage;
    boolean optionLeft, optionRight; // depends on angle
    final String FONT_PIXELTA = "res/fonts/Pixela.ttf";
    final Color OFF_WHITE = new Color(245, 245, 245);

    public void changeImage() {
        // reference

        // clear all buttons
        // render left/right button based on currentscene
    }

    public GameUI(Runnable startGameMethod) {
        super();

        // then update individual stuff here


        // set fullscreen
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
//        this.setUndecorated(true);


        this.setTitle("mikes3kilometers");

        this.setIconImage(new ImageIcon("res/images/icon.png").getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setupMainMenu(startGameMethod);

//        this.setVisible(true); this is set true in the start
    }



    private void setupMainMenu(Runnable startGameMethod) {
        // clears the jframe
        this.getContentPane().removeAll();

        BackgroundPanel backgroundPanel = new BackgroundPanel("res/images/testbg.jpeg");
        backgroundPanel.setLayout(new GridLayout(1, 2));

        // straight filler
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        backgroundPanel.add(leftPanel);

        // important stuff here!
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);

        JLabel subtitle = new JLabel("mischievous mike presents..");
        setCustomFont(subtitle, FONT_PIXELTA, 50);
        menuPanel.add(subtitle);

        JLabel title = new JLabel("3 KILOMETERS");
        setCustomFont(title, FONT_PIXELTA, 140);
        menuPanel.add(title);

        JButton startBtn = new JButton("START");

        // set btn size
        startBtn.setMaximumSize(new Dimension(
                Integer.MAX_VALUE, 200
//                startBtn.getPreferredSize().height
        ));
        startBtn.setPreferredSize(new Dimension(
                Integer.MAX_VALUE, 100
        ));
        startBtn.setMinimumSize(new Dimension(
                200, 100
        ));

        // set btn colours
        startBtn.setBackground(OFF_WHITE);
        startBtn.setFocusable(false);
        startBtn.setBorderPainted(false);
        startBtn.setForeground(Color.BLACK);
        startBtn.addActionListener(e -> startGameMethod.run());
        menuPanel.add(startBtn);


        rightPanel.add(menuPanel);
        backgroundPanel.add(rightPanel);

        // draws the jframe
        this.setContentPane(backgroundPanel);
        this.revalidate();
        this.repaint();
    }



    private void setCustomFont(JLabel jLabel, String fontPath, float fontSize) {

        try {
            File fontFile = new File("res/fonts/Pixelta.ttf");
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);

            // 2. Register the font with the GraphicsEnvironment
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);

            // 3. Derive the font with a specific size (e.g., 24pt)
            Font sizedFont = customFont.deriveFont(fontSize);

            // 4. Apply the font to the JLabel
            jLabel.setFont(sizedFont);
            jLabel.setForeground(OFF_WHITE);
        } catch(Exception e) {
            System.out.println("bs");
        }

    }

    public void setBackgroundImage(String URI) {
        // sets bg img based on passed uri
    }


    public void drawMonster(int[] coords, int[] dimensions) {
        // just draw black square :/
    }

}


class BackgroundPanel extends JPanel {
    private Image background;

    public BackgroundPanel(String imagePath) {
        background = new ImageIcon(imagePath).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(
                background,
                0, 0,
                getWidth(), getHeight(),
                this
        );
    }
}
