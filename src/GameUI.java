
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameUI extends JFrame {

    // -- CONSTS

    private static final String PIXELTA_FONT_FILEPATH = "res/fonts/Pixelta.ttf";
    private final String MAIN_MENU_BG_IMG = "res/images/bg3.jpg";
    private final String ICON_FILEPATH = "res/images/icon.png";
    public static final float DEFAULT_DERIVED_FONT_SIZE = 20f;
    private static final String INFO_DIALOGUE_TEXT =
            "had this idea for a while, but didnt have the *festive* inspiration for it. Had a few days so i made this." +
                    "\n\ngame is about my evening runs b4 sleep.. uh yeah that and imagination ig. inspired by ROAM120.exe" +
                    "\n\nthe photos are real and NOT edited (aside from some minor censoring in the colour #222222) - theyre taken with some unique settings, ill put the app I used in the github readme under resources" +
                    "\n\nokay.. and... what else? right!\ndear player, there are 2 easter eggs \n(its pretty obv if you find one)\nenjoy : )\n\n[CLICK TO CLOSE]";
    private static final String TIPS_DIALOGUE_TEXT =
            "take your time to explore the areas, im too lazy to make you able to go back and all that. you may need to restart the game to go through the different branches." +
                    "\n\nV -> GO TO" +
                    "\n* -> INSPECT" +
                    "\n < > ^ -> CHANGE PERSPECTIVE/ANGLE" + "\n\n[CLICK TO CLOSE]";

    // -- FIELDS

    public static final Font pixeltaFont = loadPixelFont();
    private boolean fullscreen = false;
    private Rectangle previousWindowBounds;
    private final Game gameState;
    private JPanel activeDialoguePanel;
    private Timer dialogueTimer;

    // -- CONSTRUCTOR

    public GameUI(Game gameState) {

        this.gameState = gameState;

        // just default to this size cuz like every game does that
        setSize(1280, 720);
        setLocationRelativeTo(null); // centers it on the screen

        // extra nice stuff
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("mikes3kilometers");
        setIconImage(new ImageIcon(ICON_FILEPATH).getImage());

        // draw the main menu
        setupMainMenu();

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
    private void setupMainMenu() {

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
        subtitle.setForeground(Color.WHITE);
        subtitle.putClientProperty("fontRatio", 0.04f);
        menuPanel.add(subtitle);

        JLabel title = new JLabel("3 KILOMETERS");
        title.setForeground(Color.WHITE);
        title.putClientProperty("fontRatio", 0.12f);
        menuPanel.add(title);

        // especially here, im not gonna comment each like yknow?
        // it makes sense tho so ull be fine
        JButton startButton = new JButton("START");
        startButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        startButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));
        startButton.setMinimumSize(new Dimension(200, 100));
        startButton.setBackground(Color.WHITE);
        startButton.setForeground(Color.BLACK);
        startButton.setFocusable(false);
        startButton.setBorderPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.putClientProperty("fontRatio", 0.05f);
        startButton.addActionListener(e -> gameState.start());
// debuggin for the pointer thingy
//        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
//            @Override
//            public void mouseEntered(java.awt.event.MouseEvent e) {
//                System.out.println("entered start: " + startButton.getBounds());
//            }
//        });

        // so much work for just a button!!!!
        menuPanel.add(startButton);
        rightPanel.add(menuPanel);

        // main menu clickables (for the "game" its all read from the json)
        Clickable infoBtn = new Clickable("dialog", "info", 0.275f, 0.27f, 0.08f, 0.03f);
        infoBtn.setDialogue(INFO_DIALOGUE_TEXT); // just its a longer dialogue and putting it all here is whew! a lot!
        Clickable tipsBtn = new Clickable("dialog", "tips", 0.275f, 0.37f, 0.08f, 0.03f);
        tipsBtn.setDialogue(TIPS_DIALOGUE_TEXT); // just its a longer dialogue and putting it all here is whew! a lot!

        List<Clickable> clickables = new ArrayList<>();
        clickables.add(infoBtn);
        clickables.add(tipsBtn);

        Clickable[] clickableArray = clickables.toArray(new Clickable[0]);
        setGameScreen(background, clickableArray);
    }



    // main menu and anything else without an angle label: passes null, so no label is drawn
    public void setGameScreen(BackgroundPanel background_layer, Clickable[] clickables) {
        setGameScreen(background_layer, clickables, null);
    }

    // game screens: angleText is drawn top left in white. pass null to skip it
    public void setGameScreen(BackgroundPanel background_layer, Clickable[] clickables, String angleText) {

        JLayeredPane panes = new JLayeredPane();
        panes.setLayout(null);

        panes.add(background_layer, JLayeredPane.DEFAULT_LAYER);

        JPanel clickable_layer = new JPanel(null);
        clickable_layer.setOpaque(false);

        // corner buttons on every screen, then whatever clickables were passed in
        List<Clickable> onScreenClickables = new ArrayList<>();
        onScreenClickables.add(new Clickable("fullscreen", "[ ]", 0.94f, 0.02f, 0.02f, 0.02f));
        onScreenClickables.add(new Clickable("exit", "X", 0.97f, 0.02f, 0.02f, 0.02f));
        onScreenClickables.addAll(Arrays.asList(clickables));

        for (Clickable clickable : onScreenClickables) {
            JButton button = clickable.createButton(this::handleClickable);
            clickable_layer.add(button);
        }
        panes.add(clickable_layer, JLayeredPane.PALETTE_LAYER);

        // angle label, added after the buttons layer so it sits on top of the background
        JLabel angleLabel = null;
        if (angleText != null) {
            angleLabel = new JLabel(angleText);
            angleLabel.setForeground(Color.WHITE);
            angleLabel.putClientProperty("fontRatio", 0.03f); // rescaleFonts handles its size
            panes.add(angleLabel, JLayeredPane.PALETTE_LAYER);
        }
        final JLabel label = angleLabel; // lambda below needs a final reference

        Runnable screenResizeRunnable = () -> {

            int width = panes.getWidth();
            int height = panes.getHeight();

            background_layer.setBounds(0, 0, width, height);
            clickable_layer.setBounds(0, 0, width, height);

            double diagonal = Math.hypot(width, height);

            for (Component component : clickable_layer.getComponents()) {
                if (component instanceof JButton) {
                    JButton button = (JButton) component;
                    Clickable clickable = (Clickable) button.getClientProperty("clickable");
                    button.setBounds((int) (width * clickable.getX()), (int) (height * clickable.getY()), (int) (diagonal * clickable.getW()), (int) (diagonal * clickable.getH()));
                }
            }

            // top left, 1% in from the corner. only JButtons get positioned above, so the label needs its own line
            if (label != null) {
                label.setBounds((int) (width * 0.01f), (int) (height * 0.01f), width / 2, (int) (height * 0.05f));
            }

            rescaleFonts(panes);
        };

        panes.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                screenResizeRunnable.run();
            }
        });

        setContentPane(panes);

        SwingUtilities.invokeLater(() -> {
            screenResizeRunnable.run();
            panes.revalidate();
            panes.repaint();
        });
    }

    // this is the infamous clickable
    // just takes the clickable that called the handle, and performs the necessary- whatever i want. i am supreme overlord here
    private void handleClickable(Clickable clickable) {
        switch (clickable.getClickableType()) {

            // special main menu only
            case "fullscreen":
                toggleFullscreen();
                break;
            case "exit":
                System.out.println("Appreciate your time!!!!!" +
                        "\n - @mike-pwns on Github");
                System.exit(0);

                // others
            case "turn":
                gameState.goToAngle(clickable.getTargetAngle());
                break;
            case "go":
                System.out.println("Go to " + clickable.getTargetLocation());
                break;
            case "dialog":
                showDialogue(clickable);
//                System.out.println("dialogue -> " + clickable.getDialogue());
                break;
            default:
                System.out.println("ClIckaABle Iz unRECOgnIzed!!!!!!! Vau!");
                break; // yea we dont need break but whatevs
        }
    }

    // scale font. not gonna try to explain this. this is claudes contribution of the proj
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
                        pixeltaFont.deriveFont(
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

    // super simple fullscreen thing
    private void toggleFullscreen() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice(); // pull the screen itself (with its info and all)

        // setting fullscreen
        if (!fullscreen) {

            // set the prev bounds (cuz were nice programmers and want going out of fullscreen to result in the same bounds)
            previousWindowBounds = getBounds();

            dispose(); // this gets rid of the screen itself, not all the data
            setUndecorated(true); // this just removes the top bar with all the -[]x thing
            setVisible(true); // idk why we needed this, but thank you claude for the debug tip :/
            device.setFullScreenWindow(this); // then it redraws it in fullscreen

        }

        // going out of fullscreen
        else {

            // release screen/monitor from fullscreen
            device.setFullScreenWindow(null);

            // same jist, get rid of the actual rendered window
            dispose();

            // add the -[]x "decoration" (id call it a heading?)
            setUndecorated(false);

            // apply the bounds saved before
            setBounds(previousWindowBounds);

            // just set it visible (undoes dispose pretty much)
            setVisible(true);


        }

        // change flag cuz duh doy
        fullscreen = !fullscreen;

        // same idea as before: just keep all the ops calm until we actually render ts
        SwingUtilities.invokeLater(() -> {
            getContentPane().revalidate();
            getContentPane().repaint();
            rescaleFonts(getContentPane());
        });
    }

    // takes a clickable, draws its dialogue right on top of it.
    // stays up until the player clicks it.
    private void showDialogue(Clickable clickable) {

        // pull the panes (this is like our workstation)
        JLayeredPane panes = (JLayeredPane) getContentPane();

        // stage 1: remove the old dialogue if there is one (we're making a new one anyways)
        if (activeDialoguePanel != null) {
            panes.remove(activeDialoguePanel);
            activeDialoguePanel = null;
        }

        // nothing to show? just redraw and leave
        if (clickable.getDialogue() == null || clickable.getDialogue().isBlank()) {
            panes.repaint();
            return;
        }

        // stage 2: build the new dialogue component

        // pull screen dimensions
        int screenWidth = panes.getWidth();
        int screenHeight = panes.getHeight();
        double diagonal = Math.hypot(screenWidth, screenHeight);

        // same font as the buttons: fontRatio of window height
        float ratio = clickable.getFontRatio();
        Font dialogueFont = pixeltaFont.deriveFont(Math.max(1f, ratio * screenHeight));

        // make the panel (again, dexters blood thingy, panel = 1 glass thing)
        JPanel dialoguePanel = new JPanel(new BorderLayout(0, 0));
        dialoguePanel.setBackground(Color.WHITE);
        dialoguePanel.setBorder(null);

        // the label needs its text turned into html (escape the special characters, newlines become <br>)
        String escaped = clickable.getDialogue()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>");

        JLabel dialogueLabel = new JLabel(
                "<html><div style='width:" + (screenWidth / 4)
                        + "px; text-align: center;'>"
                        + escaped
                        + "</div></html>"
        );

        dialogueLabel.setForeground(Color.BLACK);
        dialogueLabel.setFont(dialogueFont);
        dialogueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialogueLabel.setVerticalAlignment(SwingConstants.CENTER);

        // this is the bit that was missing: rescaleFonts() only touches components with this property,
        // so without it the text kept whatever size it had when it first appeared
        dialogueLabel.putClientProperty("fontRatio", ratio);

        dialoguePanel.add(dialogueLabel, BorderLayout.CENTER);

        // stage 3: sizing and position

        Dimension size = dialoguePanel.getPreferredSize();

        // same scaling as the buttons
        int x = (int) (screenWidth * clickable.getX());
        int y = (int) (screenHeight * clickable.getY());
        int w = (int) (diagonal * clickable.getW());
        int h = (int) (diagonal * clickable.getH());

        // compile the bounds info based on the clickables info that we got above
        int panelWidth = Math.min(Math.max(w, size.width), screenWidth);
        int panelHeight = Math.max(h, size.height);
        int panelX = Math.clamp(x + w / 2 - panelWidth / 2, 0, screenWidth - panelWidth);
        int panelY = Math.clamp(y, 0, screenHeight - panelHeight);

        dialoguePanel.setBounds(panelX, panelY, panelWidth, panelHeight);
        dialoguePanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // stage 4: dismissing the dialogue, by clicking on it or timer
        dialoguePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (dialogueTimer != null) {
                    dialogueTimer.stop();
                    dialogueTimer = null;
                }
                panes.remove(dialoguePanel);
                activeDialoguePanel = null;
                panes.repaint();
            }
        });

        // stays up for the info text (it's the long one), otherwise it fades based on length
        int length = clickable.getDialogue().length(); // just num chars
        int duration; // in ms

        if (clickable.getDialogue().equals(INFO_DIALOGUE_TEXT) || clickable.getDialogue().equals(TIPS_DIALOGUE_TEXT)) {
            duration = Integer.MAX_VALUE; // yeah so.. just click it
        } else if (length <= 20) {
            duration = 1000;
        } else if (length <= 50) {
            duration = 1500;
        } else {
            duration = 3000;
        }

        dialogueTimer = new javax.swing.Timer(duration, e -> {
            panes.remove(dialoguePanel);
            activeDialoguePanel = null;
            dialogueTimer = null;
            panes.repaint();
        });
        dialogueTimer.setRepeats(false);

        // stage 5: put it on screen, then start the auto dismisser timer
        activeDialoguePanel = dialoguePanel;
        panes.add(dialoguePanel, JLayeredPane.MODAL_LAYER);
        panes.revalidate();
        panes.repaint();
        dialogueTimer.start();
    }


    // font loader func - just reads the ttf file and sets the custom font to the var
    private static Font loadPixelFont() {
        try {
            // read the .ttf
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(PIXELTA_FONT_FILEPATH));

            // register the font with the graphix system, just meeans its chill to be used like any normal font.
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);

            // return it to be saved in the static var for everyone to use
            return font;
        } catch (Exception e) {
            return new Font("Arial", Font.BOLD, 12); // in case sum breaks we return this
        }
    }

    // -- BACKGROUND PANEL

    static class BackgroundPanel extends JPanel {
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