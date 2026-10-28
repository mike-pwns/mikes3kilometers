
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
    private final String MAIN_MENU_BG_IMG = "res/images/main_menu_bg.jpg";
    private final String ICON_FILEPATH = "res/images/icon.png";
    public static final float DEFAULT_DERIVED_FONT_SIZE = 20f;
    private static final String INFO_DIALOG_TEXT =
            "had this idea for a while, but didnt have the *festive* inspiration for it. " +
                    "\nHad a few days so i made this." +
                    "\n\ngame is about my evening runs b4 sleep.. uh yeah that and imagination ig. inspired by ROAM120.exe" +
                    "\n\nthe photos are real and NOT edited (aside from some minor censoring in the colour #222222) - theyre taken with some unique settings, ill put the app I used in the github readme under resources" +
                    "\n\nokay.. and... what else? right!\ndear player, there are 2 easter eggs \n(its pretty obv if you find one)\nenjoy : )\n\n[CLICK TO CLOSE]";
    private static final String TIPS_DIALOG_TEXT =
            "take your time to explore the areas, get immersed! " +
                    "\n\nthere arent really controls.. just click on the things \nno keybinds or nun of that" +
                    "\n\nuh i think thats about it. top right you'll have (right to left) " +
                    "\nexit, fullscreen, mute/unmute audio, and return to main menu" + "\n\n[CLICK TO CLOSE]";

    // -- FIELDS

    public static final Font pixeltaFont = loadPixelFont();
    private boolean fullscreen = false;
    private Rectangle previousWindowBounds;
    private final Game gameState;
    private JPanel activeDialogPanel;
    private Timer dialogTimer;

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
        setMinimumSize(new Dimension(480, 360));

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
    public void setupMainMenu() {

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
        infoBtn.setDialog(INFO_DIALOG_TEXT); // just its a longer dialogue and putting it all here is whew! a lot!
        Clickable tipsBtn = new Clickable("dialog", "tips", 0.275f, 0.37f, 0.08f, 0.03f);
        tipsBtn.setDialog(TIPS_DIALOG_TEXT); // just its a longer dialogue and putting it all here is whew! a lot!

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

        // corner buttons on every screen, right to left: exit, fullscreen, menu, mute
        List<Clickable> onScreenClickables = new ArrayList<>();
        onScreenClickables.add(new Clickable("exit", "X", 0.97f, 0.02f, 0.02f, 0.02f));
        onScreenClickables.add(new Clickable("fullscreen", "[ ]", 0.94f, 0.02f, 0.02f, 0.02f));

        String muteString;
        if (gameState.isMuted()) { muteString = "UNMUTE"; }
        else { muteString = "MUTE"; }
        onScreenClickables.add(new Clickable("mute", muteString, 0.862f, 0.02f, 0.06f, 0.02f));
        onScreenClickables.addAll(Arrays.asList(clickables));



        // angle label, added after the buttons layer so it sits on top of the background
        JLabel angleLabel = null;
        if (angleText != null) {
            onScreenClickables.add(new Clickable("menu", "MENU", 0.784f, 0.02f, 0.06f, 0.02f));
            angleLabel = new JLabel(angleText);
            angleLabel.setForeground(Color.WHITE);
            angleLabel.putClientProperty("fontRatio", 0.03f); // rescaleFonts handles its size
            panes.add(angleLabel, JLayeredPane.PALETTE_LAYER);

        }
        for (Clickable clickable : onScreenClickables) {
            JButton button = clickable.createButton(this::handleClickable);
            clickable_layer.add(button);
        }
        panes.add(clickable_layer, JLayeredPane.PALETTE_LAYER);

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

            // special
            case "menu":
                gameState.showMainMenu();
                break;
            case "mute":
                gameState.toggleMute();
                break;
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
                gameState.goToLocation(clickable.getTargetLocation());
                System.out.println("Go to " + clickable.getTargetLocation());
                break;
            case "dialog":
                showDialog(clickable);
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
    // takes a clickable, draws its dialog right on top of it.
// the box is only as wide as the text needs, up to a max width. stays up until clicked, or fades by length
    private void showDialog(Clickable clickable) {

        JLayeredPane panes = (JLayeredPane) getContentPane();

        // stage 1: remove the old dialog if there is one
        if (activeDialogPanel != null) {
            panes.remove(activeDialogPanel);
            activeDialogPanel = null;
        }

        if (clickable.getDialog() == null || clickable.getDialog().isBlank()) {
            panes.repaint();
            return;
        }

        // stage 2: build the component

        int screenWidth = panes.getWidth();
        int screenHeight = panes.getHeight();

        float ratio = clickable.getFontRatio();
        Font dialogFont = pixeltaFont.deriveFont(Math.max(1f, ratio * screenHeight));

        // measure the text with the dialog font, so the box can be sized to the words
        FontMetrics fm = getFontMetrics(dialogFont);
        int maxTextWidth = screenWidth / 3;

        // widest line in the text (each \n is its own line)
        int widestLine = 0;
        for (String line : clickable.getDialog().split("\n")) {
            widestLine = Math.max(widestLine, fm.stringWidth(line));
        }

        // short text gets a narrow box. long text is capped and wraps inside it
// short text gets a narrow box. anything longer wraps at the max width
        int textWidth = Math.min(Math.max(widestLine, 0), maxTextWidth) + 0;
        JPanel dialogPanel = new JPanel(new BorderLayout(0, 0));
        dialogPanel.setBackground(Color.WHITE);
        dialogPanel.setBorder(null);

        // escape the special characters, newlines become <br>
        String escaped = clickable.getDialog()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>");

        JLabel dialogLabel = new JLabel("<html><div style='width:" + textWidth
                + "px; text-align: center;'>" + escaped + "</div></html>");

        dialogLabel.setForeground(Color.BLACK);
        dialogLabel.setFont(dialogFont);
        dialogLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialogLabel.setVerticalAlignment(SwingConstants.CENTER);

        // rescaleFonts() picks this up on resize
        dialogLabel.putClientProperty("fontRatio", ratio);

        dialogPanel.add(dialogLabel, BorderLayout.CENTER);

        // stage 3: size and position

        Dimension size = dialogPanel.getPreferredSize();

        int x = (int) (screenWidth * clickable.getX());
        int y = (int) (screenHeight * clickable.getY());
        int w = (int) (Math.hypot(screenWidth, screenHeight) * clickable.getW());

        // only as wide as the text needs (capped by the screen). centered over the clickable
        // the box is never shorter than the clickable it came from
        int clickableHeight = (int) (Math.hypot(screenWidth, screenHeight) * clickable.getH());
        int panelWidth = Math.min(size.width, screenWidth);
        int panelHeight = Math.max(size.height, clickableHeight);
        int panelX = Math.clamp(x + w / 2 - panelWidth / 2, 0, screenWidth - panelWidth);
        int panelY = Math.clamp(y, 0, screenHeight - panelHeight);
        dialogPanel.setBounds(panelX, panelY, panelWidth, panelHeight);
        dialogPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // stage 4: dismiss by clicking, or by the timer
        dialogPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (dialogTimer != null) {
                    dialogTimer.stop();
                    dialogTimer = null;
                }
                panes.remove(dialogPanel);
                activeDialogPanel = null;
                panes.repaint();
            }
        });

        // stays up for the long texts, otherwise fades based on length
        int length = clickable.getDialog().length();
        int duration;

        if (clickable.getDialog().equals(INFO_DIALOG_TEXT) || clickable.getDialog().equals(TIPS_DIALOG_TEXT)) {
            duration = Integer.MAX_VALUE;
        } else if (length <= 20) {
            duration = 1500;
        } else if (length <= 50) {
            duration = 2500;
        } else {
            duration = 4000;
        }

        dialogTimer = new javax.swing.Timer(duration, e -> {
            panes.remove(dialogPanel);
            activeDialogPanel = null;
            dialogTimer = null;
            panes.repaint();
        });
        dialogTimer.setRepeats(false);

        // stage 5: show it, then start the timer
        activeDialogPanel = dialogPanel;
        panes.add(dialogPanel, JLayeredPane.MODAL_LAYER);
        panes.revalidate();
        panes.repaint();
        dialogTimer.start();
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

    // changes the text of the mute button without rebuilding the screen
    public void refreshMuteButton(boolean muted) {
        for (Component c : getContentPane().getComponents()) {
            if (c instanceof JComponent layer) {
                for (Component child : layer.getComponents()) {
                    if (child instanceof JButton button
                            && button.getClientProperty("clickable") instanceof Clickable clickable
                            && clickable.getClickableType().equals("mute")) {
                        String muteString;
                        if (gameState.isMuted()) { muteString = "UNMUTE"; }
                        else { muteString = "MUTE"; }
                        button.setText(muteString);
                        return;
                    }
                }
            }
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