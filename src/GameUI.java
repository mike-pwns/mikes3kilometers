import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

// class is like the window.. yeah bleh ig
public class GameUI extends JFrame {

    // -- CONSTS

    final String FONT_PIXELTA = "res/fonts/Pixelta.ttf";
    final Color OFF_WHITE = new Color(245, 245, 245);
    final String MAIN_MENU_BG_IMG = "res/images/bg3.jpg"; // TODO clean avifs and such
    final String ICON_FILEPATH = "res/images/icon.png";

    // -- FIELDS

    private final Font pixelFont; // loaded once, kinda kalm


    // -- CONSTRUCTOR


    public GameUI(Runnable startGameMethod) {
        super(); // this is an inherited class so gots to do that

        // load da font (shoutout da fonts btw, classy selection of fonts)
        pixelFont = loadPixelFont();

        // update window props like default size, where it appears, some other things
        this.setSize(new Dimension(1280, 720));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // cosmetic changes
        this.setTitle("mikes3kilometers");
        this.setIconImage(new ImageIcon(ICON_FILEPATH).getImage());

        // just draw the main menu cuz.. we just initialized the game and thats what we wanna see first!
        setupMainMenu(startGameMethod);

        // rescale text whenever the window changes size (ty claude i didnt wanna deal with this headache)
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                rescaleFonts(getContentPane());
            }
        });

        // apply the rescale font thing at startup to be safe or sum
        rescaleFonts(getContentPane());
    }


    // -- METHODS

    // this sets up the main menu!
    private void setupMainMenu(Runnable startGameMethod) {

        // clears everything (in case i make a "return to menu" thing sometime later
        this.getContentPane().removeAll();

        // set the background and make split it into 2 cols
        BackgroundPanel backgroundPanel = new BackgroundPanel(MAIN_MENU_BG_IMG);
        backgroundPanel.setLayout(new GridLayout(1, 2));

        // filler panel, screw you leftpanel (i said it)
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        backgroundPanel.add(leftPanel);

        // hello rightpanel, welcome to the club
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);

        // just gonna dump a few bits of text and buttons via the graceful gridbaglayout
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);

        // add subtitle
        JLabel subtitle = new JLabel("mischievous mike presents..");
        subtitle.setForeground(OFF_WHITE);
        subtitle.putClientProperty("fontRatio", 0.05f); // ratio of window height
        menuPanel.add(subtitle);

        // add title
        JLabel title = new JLabel("3 KILOMETERS");
        title.setForeground(OFF_WHITE);
        title.putClientProperty("fontRatio", 0.12f);
        menuPanel.add(title);

        // make the start button (we dont rly need a template cuz its a one time thing)
        JButton startBtn = new JButton("START");
        startBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        startBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));
        startBtn.setMinimumSize(new Dimension(200, 100));
        startBtn.setBackground(OFF_WHITE);
        startBtn.setFocusable(false); // qol change
        startBtn.setBorderPainted(false); // qol change
        startBtn.setForeground(Color.BLACK); // sets text colour
        startBtn.addActionListener(e -> startGameMethod.run());
        startBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // just makes the "pointer" cursor, just a lil touch yknow
        startBtn.putClientProperty("fontRatio", 0.05f);
        menuPanel.add(startBtn);

        rightPanel.add(menuPanel);
        backgroundPanel.add(rightPanel);

        // set this thing we build to whatever is shown in da frame
        this.setContentPane(backgroundPanel);
        this.revalidate();
        this.repaint();
    }

    // (THANK YOU CLAUDE) Walks every component, including nested panels, and applies each one's ratio
    private void rescaleFonts(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JComponent jc && jc.getClientProperty("fontRatio") instanceof Float ratio) {
                c.setFont(pixelFont.deriveFont(ratio * getHeight()));
            }
            if (c instanceof Container child) {
                rescaleFonts(child);
            }
        }
        revalidate();
        repaint();
    }

    public void renderAngle(Location location, int angle_index) {
        this.getContentPane().removeAll();

//        location = new Location();
//        BackgroundPanel anglePanel = new BackgroundPanel(location.angles[angle_index]);

//        this.setContentPane(anglePanel);
        this.revalidate();
        this.repaint();
    }

    // just loads the font, nothin fancy
    private Font loadPixelFont() {
        try {
            // custom fonts are annoying to deal with programmatically frankly
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(FONT_PIXELTA)); // read the ttf file in the res/fonts
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont); // register
            return customFont;
        } catch (Exception e) {
            System.out.println(e);
        }
        return new Font("arial", Font.BOLD, 12); //
    }

    public void setBackgroundImage(String URI) {
        // sets bg img based on passed uri
    }

    public void drawMonster(int[] coords, int[] dimensions) {
        // just draw black square :/
    }
}

// just a fancy panel w the whole background thing
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
