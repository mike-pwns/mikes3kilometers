
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GameUI extends JFrame {

    // -- CONSTS

    private static final String FONT_PIXELTA = "res/fonts/Pixelta.ttf";
    private final Color OFF_WHITE = new Color(245, 245, 245);
    private final String MAIN_MENU_BG_IMG = "res/images/bg3.jpg";
    private final String ICON_FILEPATH = "res/images/icon.png";

    // -- FIELDS

    public static final Font pixelFont = loadPixelFont();

    private boolean fullscreen = false;
    private Rectangle previousWindowBounds;

    // -- CONSTRUCTOR

    public GameUI(Runnable startGameMethod) {

        // just default to this size cuz like every game does that
        setSize(1280, 720);
        setLocationRelativeTo(null); // centers it on the screen

        // extra nice stuff
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("mikes3kilometers");
        setIconImage(new ImageIcon(ICON_FILEPATH).getImage());

        // draw the main menu
        setupMainMenu(startGameMethod);

        // this is just a listener for whenever the window is resized we gotta resize the text too
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    rescaleFonts(getContentPane());
                    getContentPane().revalidate();
                    getContentPane().repaint();
                });
            }
        });

        setVisible(true);

        // later cuz we needa calculate AFTER the window is drawn (and swing kinda already knows whats goin on with the layout)
        SwingUtilities.invokeLater(() ->
                rescaleFonts(getContentPane())
        );
    }

    // -- METHODS

    // super simple.. main menu is a unique screen, so like we build it from scratch.
    // whereas the game screens
    private void setupMainMenu(Runnable startGameMethod) {

        // Background and menu layout
        BackgroundPanel background = new BackgroundPanel(
                MAIN_MENU_BG_IMG
        );

        // the below is written by hand, but like, i dont reckon i gotta explain it all.

        background.setLayout(new GridLayout(1, 2));

        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        background.add(leftPanel);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        background.add(rightPanel);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);

        JLabel subtitle = new JLabel("mischievous mike presents..");
        subtitle.setForeground(OFF_WHITE);
        subtitle.putClientProperty("fontRatio", 0.05f);
        menuPanel.add(subtitle);

        JLabel title = new JLabel("3 KILOMETERS");
        title.setForeground(OFF_WHITE);
        title.putClientProperty("fontRatio", 0.12f);
        menuPanel.add(title);

        // especially here, im not gonna comment each like yknow?
        // it makes sense tho so ull be fine
        JButton startButton = new JButton("START");
        startButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        startButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));
        startButton.setMinimumSize(new Dimension(200, 100));
        startButton.setBackground(OFF_WHITE);
        startButton.setForeground(Color.BLACK);
        startButton.setFocusable(false);
        startButton.setBorderPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.putClientProperty("fontRatio", 0.05f);
        startButton.addActionListener(e -> startGameMethod.run());

        // so much work for just a button!!!!
        menuPanel.add(startButton);
        rightPanel.add(menuPanel);

        // main menu clickables (for the "game" its all read from the json)
        Clickable fullscreenBtn = new Clickable(
                "fullscreen", "fullscreen",
                0.275f, 0.27f, 0.08f, 0.03f
        );
        Clickable tipsBtn = new Clickable(
                "other", "tips", 0.275f, 0.37f, 0.08f, 0.03f

        );

//        Clickable infoBtn = new Clickable(
//          "other", "bs info", 0.275f, 0.47f, 0.08f, 0.03f
//        );

        List<Clickable> clickables = new ArrayList<>();
        clickables.add(fullscreenBtn);
        clickables.add(tipsBtn);
//        clickables.add(infoBtn);

        setGameScreen(background, clickables);
    }


    // this method just sets the actual screen.
    // combines a BackgroundPanel (an image for a background) with
    // a list of Clickables (stuff user can click on the painted backdrop) TODO change to JPanel?
    public void setGameScreen(BackgroundPanel background, List<Clickable> clickables) {

        // were essentially building a new pane (this is like a container of panes/layers of the screen: background + clickable pane)
        // note if u never saw this before, i didnt either, this container obj is just like dexters glass blood thing. JLayeredPane is the container, each glass pane is like JPanel. simple yeah?
        JLayeredPane layers = new JLayeredPane();
        layers.setLayout(null);

        // def layer is like the bottom layer, so we just set the background panel to that
        layers.add(background, JLayeredPane.DEFAULT_LAYER);

        // make a new TRANSPARENT jpanel for all the clickables to be drawn on it
        JPanel clickable_layer = new JPanel(null);
        clickable_layer.setOpaque(false);

        // go thru each clickable we were given and add a button version of that to the
        for (Clickable clickable : clickables) {
            JButton button = clickable.createButton(this::handleClickable); // fancy c++ syntax just means we pass the handler method from THIS instance into whatever may call it in that method (the scope of THIS obj)
            clickable_layer.add(button);
        }

        // add clickable layer to the top
        layers.add(clickable_layer, JLayeredPane.PALETTE_LAYER);

        // Resize both layers and reposition clickables.
        Runnable layoutScreen = () -> {
            int width = layers.getWidth();
            int height = layers.getHeight();

            background.setBounds(0, 0, width, height);
            clickable_layer.setBounds(0, 0, width, height);

            double diagonal = Math.hypot(width, height);

            for (Component component : clickable_layer.getComponents()) {
                JButton button = (JButton) component;

                Clickable clickable = (Clickable)
                        button.getClientProperty("clickable");

                button.setBounds(
                        (int) (width * clickable.x),
                        (int) (height * clickable.y),
                        (int) (diagonal * clickable.w),
                        (int) (diagonal * clickable.h)
                );
            }

            rescaleFonts(layers);
        };

        layers.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutScreen.run();
            }
        });

        setContentPane(layers);

        SwingUtilities.invokeLater(() -> {
            layoutScreen.run();
            layers.revalidate();
            layers.repaint();
        });
    }



    // -- CLICKABLE ACTIONS

    private void handleClickable(Clickable clickable) {
        switch (clickable.clickableType) {
            case "fullscreen" -> toggleFullscreen();

            case "turn_left" -> System.out.println("Turn left");

            case "turn_right" -> System.out.println("Turn right");

            case "go" -> System.out.println(
                    "Go to " + clickable.targetLocation
            );

            case "dialog" -> System.out.println(clickable.dialogue);

            default -> System.out.println(
                    "Unknown clickable: "
                            + clickable.clickableType
            );
        }
    }

    // -- FONT SCALING

    private void rescaleFonts(Container container) {
        int height = getContentPane().getHeight();

        if (height <= 0) {
            return;
        }

        for (Component component : container.getComponents()) {
            if (component instanceof JComponent jc
                    && jc.getClientProperty("fontRatio")
                    instanceof Float ratio) {

                component.setFont(
                        pixelFont.deriveFont(
                                Math.max(1f, ratio * height)
                        )
                );
            }

            if (component instanceof Container child) {
                rescaleFonts(child);
            }
        }

        container.revalidate();
        container.repaint();
    }

    // -- FULLSCREEN

    private void toggleFullscreen() {
        GraphicsDevice device = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();

        if (!fullscreen) {
            previousWindowBounds = getBounds();

            dispose();
            setUndecorated(true);
            device.setFullScreenWindow(this);

            fullscreen = true;
        } else {
            device.setFullScreenWindow(null);

            dispose();
            setUndecorated(false);

            if (previousWindowBounds != null) {
                setBounds(previousWindowBounds);
            }

            fullscreen = false;
            setVisible(true);
        }

        SwingUtilities.invokeLater(() -> {
            getContentPane().revalidate();
            getContentPane().repaint();
            rescaleFonts(getContentPane());
        });
    }

    // -- FONT LOADING

    private static Font loadPixelFont() {
        try {
            Font font = Font.createFont(
                    Font.TRUETYPE_FONT,
                    new File(FONT_PIXELTA)
            );

            GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .registerFont(font);

            return font;
        } catch (Exception e) {
            System.out.println(e);
            return new Font("Arial", Font.BOLD, 12);
        }
    }

    // -- BACKGROUND PANEL

    private static class BackgroundPanel extends JPanel {
        private final Image background;

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
}
