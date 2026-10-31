
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
    private final String MAIN_MENU_BG_IMG = "res/images/location_7/angle2.jpg";
    private final String ICON_FILEPATH = "res/images/icon.png";
    public static final float DEFAULT_DERIVED_FONT_SIZE = 20f;
    private static final String INFO_DIALOG_TEXT =
            "had this idea for a while, but didnt have the *festive* inspiration for it. had a few days so i made this." +
                    "\n\nmain goal of this proj is to showcase the horror esc vibe of a normally-familiar running route. think of this as an art piece if that helps you - a really weird and unique one i guess. its like, were not scared of the dark per se, but theres sum abt seeing a familiar place in the dark that can be scary - thats what i tried to capture (jeez i sound like a posh artist.. ahem.. wheres my wine?)" +
                    "\n\nthe photos are all taken by me. some are minimally edited for privacys sake. theyre taken with a flashlight + some unique (cool) settings, ill put the app I used in the github readme under resources" +
                    "\n\nokay.. and... what else? right\nthere may or may not be a monster too.. stay vigilant!\nenjoy : )\n\n[CLICK TO CLOSE]";
    private static final String TIPS_DIALOG_TEXT =
            "take your time to explore the areas, get immersed! " +
                    "\n\nthere arent really controls.. just click on the things \nno keybinds or nun of that (too much work XD)" +
                    "\n\ni would recommend playing with audio, cuz like thats atmosphere and all (to load audio on browser u gotta press unmute button top-right of the screen). also, the fullscreen [ ] btn works on browser too!" +
                    "\n\nuh i think thats about it. top right youll have (right to left) exit, fullscreen, mute/unmute audio, and return to main menu" + "\n\n[CLICK TO CLOSE]";
    private static final String CHEERPJ_DIALOG_TEXT =
            "just a notice that cheerpj (the thing i used to port the jframe bs to webassembly) was ported with ai in a few hours. i basically vibecoded / vibedebugged more in an afternoon than satan, baal, and moloch could in a year combined. but dw the core game i made with my own 2 hands" +
                 "\n\npoint is, game might be a bit janky at times cuz of the whole porting thing. the native version runs much smoother (and has centered text!!), but thats the classic *works on my machine* dilemma. maybe ill upload the original executable too, idk yet" +
                    "\n\n[CLICK TO CLOSE]";

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
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setBackground(Color.BLACK); // This is HUGE. thank me for this.

        // extra nice stuff
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("mikes3kilometers");
        setIconImage(loadImage(ICON_FILEPATH));

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

        Clickable cheerpjBtn = new Clickable("dialog", "cheerpj n ai", 0.275f, 0.47f, 0.08f, 0.03f);
        cheerpjBtn.setDialog(CHEERPJ_DIALOG_TEXT); // just its a longer dialogue and putting it all here is whew! a lot!


        List<Clickable> clickables = new ArrayList<>();
        clickables.add(infoBtn);
        clickables.add(tipsBtn);
        clickables.add(cheerpjBtn);

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
        if (gameState.getMusicPlayer().isMuted()) { muteString = "UNMUTE"; }
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

        gameState.soundEffectPlayer.playAudio("typewriter.wav");

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
            case "end_run":
                showGoodbyeScreen();
                gameState.specialMusicPlayer.setLoop(false);
                break;
            case "exit":
                System.out.println("Appreciate your time!!!!!" +
                        "\n - @mike-pwns on Github");

                gameState.musicPlayer.stopAudio();
                gameState.specialMusicPlayer.stopAudio();
                gameState.soundEffectPlayer.stopAudio();

                showGoodbyeScreen();
                break;

                // others
            case "turn":
                gameState.goToAngle(clickable.getTargetAngle());
                break;
            case "go":
                gameState.goToLocation(clickable.getTargetLocation());
//                System.out.println("Go to " + clickable.getTargetLocation());
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

    private static native void requestBrowserFullscreen();

    // super simple fullscreen thing
    private void toggleFullscreen() {
        requestBrowserFullscreen();
    }
    // takes a clickable, draws its dialogue right on top of it.
    // stays up until the player clicks it.
    // takes a clickable, draws its dialog right on top of it.
// the box is only as wide as the text needs, up to a max width. stays up until clicked, or fades by length



    private void showDialog(Clickable clickable) {

        if (clickable.getSymbol().equals("!")) {
            gameState.mikesSpecialDialogMethod();
        }

        JLayeredPane panes = (JLayeredPane) getContentPane();

        // stage 1: remove the old dialog if there is one
        if (activeDialogPanel != null) {
            if (dialogTimer != null) {
                dialogTimer.stop();
                dialogTimer = null;
            }

            panes.remove(activeDialogPanel);
            activeDialogPanel = null;
            panes.repaint();
            return;
        }

        if (clickable.getDialog() == null || clickable.getDialog().isBlank()) {
            panes.repaint();
            return;
        }

        // stage 2: build the component

        int screenWidth = panes.getWidth();
        int screenHeight = panes.getHeight();

        float ratio = clickable.getFontRatio();
        Font dialogFont = pixeltaFont.deriveFont(Math.max(0.5f, ratio * screenHeight));

        // measure the text with the dialog font, so the box can be sized to the words
        FontMetrics fm = getFontMetrics(dialogFont);
        int maxTextWidth = screenWidth / 3;

        // widest line in the text (each \n is its own line)
        int widestLine = 0;
        for (String line : clickable.getDialog().split("\n")) {
            widestLine = Math.max(widestLine, fm.stringWidth(line));
        }

        // short text gets a narrow box. long text is capped and wraps inside it
        int textWidth = Math.min(Math.max(widestLine, 1), maxTextWidth);

        JPanel dialogPanel = new JPanel(new BorderLayout(0, 0));
        dialogPanel.setBackground(Color.WHITE);
        dialogPanel.setBorder(null);

        // use JTextArea instead of JLabel's HTML renderer (CheerpJ font compatibility)
        JTextArea dialogText = new JTextArea(clickable.getDialog());

        dialogText.setFont(dialogFont);
        dialogText.setForeground(Color.BLACK);
        dialogText.setBackground(Color.WHITE);

        dialogText.setEditable(false);
        dialogText.setFocusable(false);
        dialogText.setLineWrap(true);
        dialogText.setWrapStyleWord(true);

        dialogText.setOpaque(true);
        dialogText.setBorder(null);

        // center the text horizontally
        dialogText.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        dialogText.setAlignmentX(Component.CENTER_ALIGNMENT);

        // calculate the text height using the available width
        dialogText.setSize(textWidth, Short.MAX_VALUE);

        Dimension textSize = dialogText.getPreferredSize();
        dialogText.setPreferredSize(new Dimension(textWidth, textSize.height));

        dialogPanel.add(dialogText, BorderLayout.CENTER);

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

        int panelX = Math.max(0, Math.min(
                x + w / 2 - panelWidth / 2,
                screenWidth - panelWidth
        ));

        int panelY = Math.max(0, Math.min(
                y,
                screenHeight - panelHeight
        ));

        dialogPanel.setBounds(panelX, panelY, panelWidth, panelHeight);
        dialogPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // stage 4: dismiss by clicking, or by the timer
        java.awt.event.MouseAdapter dismissListener =
                new java.awt.event.MouseAdapter() {
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
                };

        dialogPanel.addMouseListener(dismissListener);
        dialogText.addMouseListener(dismissListener);

        // stays up for the long texts, otherwise fades based on length
        int length = clickable.getDialog().length();
        int duration;

        if (clickable.getDialog().equals(INFO_DIALOG_TEXT) || clickable.getDialog().equals(TIPS_DIALOG_TEXT) || clickable.getDialog().equals(CHEERPJ_DIALOG_TEXT)) {
            duration = Integer.MAX_VALUE;
        } else if (length <= 20) {
            duration = 1500;
        } else if (length <= 50) {
            duration = 2500;
        } else {
            duration = 10000;
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
        try (java.io.InputStream stream =
                     GameUI.class.getResourceAsStream(
                             "/" + PIXELTA_FONT_FILEPATH)) {

            if (stream == null) {
                throw new java.io.IOException(
                        "Pixelta font not found in JAR"
                );
            }

            Font font = Font.createFont(
                    Font.TRUETYPE_FONT,
                    stream
            );

            GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .registerFont(font);

            return font;

        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Arial", Font.BOLD, 12);
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
                        if (gameState.getMusicPlayer().isMuted()) { muteString = "UNMUTE"; }
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
            background = loadImage(imagePath);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (background != null) {
                g.drawImage(
                        background,
                        0, 0,
                        getWidth(), getHeight(),
                        this
                );
            }
        }
    }
    public void showGoodbyeScreen() {

        JPanel goodbyePanel = new JPanel();
        goodbyePanel.setBackground(Color.BLACK);
        goodbyePanel.setLayout(
                new BoxLayout(goodbyePanel, BoxLayout.Y_AXIS)
        );

        // Text
        JLabel goodbyeLabel = new JLabel("THANKS FOR PLAYING!");
        goodbyeLabel.setForeground(Color.WHITE);
        goodbyeLabel.setFont(pixeltaFont.deriveFont(40f));
        goodbyeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Load image
        Image goodbyeImage = loadImage("res/images/goodbye.jpg");

        // Scale image to fit a 3:4 rectangle
        int imageWidth = 450;
        int imageHeight = 600;

        Image scaledImage = goodbyeImage.getScaledInstance(
                imageWidth,
                imageHeight,
                Image.SCALE_SMOOTH
        );

        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Layout
        goodbyePanel.add(Box.createVerticalGlue());
        goodbyePanel.add(goodbyeLabel);
        goodbyePanel.add(Box.createVerticalStrut(25));
        goodbyePanel.add(imageLabel);
        goodbyePanel.add(Box.createVerticalGlue());

        setContentPane(goodbyePanel);

        goodbyePanel.revalidate();
        goodbyePanel.repaint();
    }
    private static Image loadImage(String path) {
        try {
            String resourcePath = "/" + path.replace("\\", "/");

            java.net.URL resource =
                    GameUI.class.getResource(resourcePath);

            if (resource == null) {
                System.err.println("Image not found: " + resourcePath);
                return null;
            }

            return new ImageIcon(resource).getImage();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}