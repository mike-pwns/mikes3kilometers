import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

public class GameUI extends JFrame {

    String currentImage;
    boolean optionLeft, optionRight; // depends on angle
    final String FONT_PIXELTA = "res/fonts/Pixelta.ttf";
    final Color OFF_WHITE = new Color(245, 245, 245);
    final String MAIN_MENU_BG_IMG = "res/images/bg3.jpg"; // TODO clean avifs and such
    final String ICON_FILEPATH = "res/images/icon.png";

    private Font pixelFont; // loaded once

    public GameUI(Runnable startGameMethod) {
        super();

        pixelFont = loadPixelFont();

        this.setSize(new Dimension(1280, 720));
        this.setTitle("mikes3kilometers");
        this.setIconImage(new ImageIcon(ICON_FILEPATH).getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setupMainMenu(startGameMethod);

        // rescale text whenever the window changes size
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                rescaleFonts(getContentPane());
            }
        });
        rescaleFonts(getContentPane()); // apply once at startup
    }

    private void setupMainMenu(Runnable startGameMethod) {
        this.getContentPane().removeAll();

        BackgroundPanel backgroundPanel = new BackgroundPanel(MAIN_MENU_BG_IMG);
        backgroundPanel.setLayout(new GridLayout(1, 2));

        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        backgroundPanel.add(leftPanel);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);

        JLabel subtitle = new JLabel("mischievous mike presents..");
        subtitle.setForeground(OFF_WHITE);
        subtitle.putClientProperty("fontRatio", 0.06f); // ratio of window height
        menuPanel.add(subtitle);

        JLabel title = new JLabel("3 KILOMETERS");
        title.setForeground(OFF_WHITE);
        title.putClientProperty("fontRatio", 0.12f);
        menuPanel.add(title);

        JButton startBtn = new JButton("START");
        startBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        startBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));
        startBtn.setMinimumSize(new Dimension(200, 100));
        startBtn.setBackground(OFF_WHITE);
        startBtn.setFocusable(false);
        startBtn.setBorderPainted(false);
        startBtn.setForeground(Color.BLACK);
        startBtn.addActionListener(e -> startGameMethod.run());
        startBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startBtn.putClientProperty("fontRatio", 0.05f);
        menuPanel.add(startBtn);

        rightPanel.add(menuPanel);
        backgroundPanel.add(rightPanel);

        this.setContentPane(backgroundPanel);
        this.revalidate();
        this.repaint();
    }

    // Walks every component, including nested panels, and applies each one's ratio
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

    // Loads the font file once; falls back to Arial if it's missing
    private Font loadPixelFont() {
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(FONT_PIXELTA));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont);
            return customFont;
        } catch (Exception e) {
            System.out.println(e);
        }
        return new Font("arial", Font.BOLD, 12);
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

// just a thing that user can click
/*
* [!] to inspect
* [v] to go
* [<] [>] to change angles
*
* */
class Clickable {
    String clickableType;   // "turn_left", "turn_right", "dialog", "go"
    String symbol;
    float x, y, w, h;
    Integer targetAngle;    // Integer so "missing" differs from 0
    String targetLocation;
    String dialogue;

    @Override
    public String toString() {
        return "{" + clickableType + " '" + symbol + "' x=" + x + " y=" + y
                + " w=" + w + " h=" + h
                + (targetAngle != null ? " targetAngle=" + targetAngle : "")
                + (targetLocation != null ? " targetLocation=" + targetLocation : "")
                + (dialogue != null ? " dialogue=\"" + dialogue + "\"" : "")
                + "}";
    }
}