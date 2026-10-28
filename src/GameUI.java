
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


    // -- FIELDS

    public static final Font pixeltaFont = loadPixelFont();
    private boolean fullscreen = false;
    private Rectangle previousWindowBounds;
    private final Game gameState;
    private JPanel activeDialoguePanel;

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

        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                System.out.println("entered start: " + startButton.getBounds());
            }
        });

        // so much work for just a button!!!!
        menuPanel.add(startButton);
        rightPanel.add(menuPanel);

        // main menu clickables (for the "game" its all read from the json)
        Clickable fullscreenBtn = new Clickable("fullscreen", "fullscreen", 0.275f, 0.27f, 0.08f, 0.03f);
        Clickable infoBtn = new Clickable("dialog", "info", 0.275f, 0.37f, 0.08f, 0.03f);
        infoBtn.setDialogue(INFO_DIALOGUE_TEXT); // just its a longer dialogue and putting it all here is whew! a lot!

        Clickable exitBtn = new Clickable("exit", "exit", 0.278f, 0.47f, 0.08f, 0.03f);

        List<Clickable> clickables = new ArrayList<>();
        clickables.add(fullscreenBtn);
        clickables.add(infoBtn);
        clickables.add(exitBtn);

        Clickable[] clickableArray = clickables.toArray(new Clickable[0]);
        setGameScreen(background, clickableArray);
    }


    // this method just sets the actual screen.
// combines a BackgroundPanel (an image for a background) with
// a list of Clickables (stuff user can click on the painted backdrop) TODO change to JPanel?
    public void setGameScreen(BackgroundPanel background_layer, Clickable[] clickables) {


        // were essentially building a new pane (this is like a container of panes/layers of the screen: background + clickable pane)
        // note if u never saw this before, i didnt either, this container obj is just like dexters glass blood thing. JLayeredPane is the container, each glass pane is like JPanel. simple yeah?
        JLayeredPane panes = new JLayeredPane();
        panes.setLayout(null);

        // def layer is like the bottom layer, so we just set the background panel to that
        panes.add(background_layer, JLayeredPane.DEFAULT_LAYER);

        // make a new TRANSPARENT jpanel for all the clickables to be drawn on it
        JPanel clickable_layer = new JPanel(null);
        clickable_layer.setOpaque(false);

        // clickables part 1: adding all the clickables to the list

        List<Clickable> onScreenClickables = new ArrayList<>();

        // top right corner buttons, on every screen: fullscreen ("[]") and exit ("X").
        // these are present on every screen, so we can just add em once here
        onScreenClickables.add(new Clickable("fullscreen", "[ ]", 0.94f, 0.02f, 0.02f, 0.02f));
        onScreenClickables.add(new Clickable("exit", "X", 0.97f, 0.02f, 0.02f, 0.02f));

        // add everything in the passed clickables array
        onScreenClickables.addAll(Arrays.asList(clickables));

        // clickables part 2: go thru each clickable and add a button version of that to the layer

        for (Clickable clickable : onScreenClickables) {
            JButton button = clickable.createButton(this::handleClickable); // fancy c++ syntax just means we pass the handler method from THIS instance into whatever may call it in that method (the scope of THIS obj)
            clickable_layer.add(button);
        }

        // add clickable layer to the top
        panes.add(clickable_layer, JLayeredPane.PALETTE_LAYER);

        // just the runnable for whenever we wanna resize crap
        Runnable screenResizeRunnable = () -> {

            // pull the window width and height
            int width = panes.getWidth();
            int height = panes.getHeight();

            // set each layer to the full bounds
            background_layer.setBounds(0, 0, width, height);
            clickable_layer.setBounds(0, 0, width, height);

            // def diagonal (niche function btw, learned abt this just now)
            double diagonal = Math.hypot(width, height);

            // Go through each component (cuz it could be button or text, we just need the super of both of those)
            for (Component component : clickable_layer.getComponents()) {

                if (component instanceof JButton) {
                    // resize the button to match the scaled components of the clickable its assigned
                    JButton button = (JButton) component;
                    Clickable clickable = (Clickable) button.getClientProperty("clickable");
                    button.setBounds((int) (width * clickable.getX()), (int) (height * clickable.getY()), (int) (diagonal * clickable.getW()), (int) (diagonal * clickable.getH()));
                }
            }

            // rescale fonts... as per usual..
            rescaleFonts(panes);
        };

        // just run the runnable whenever the window is resized
        panes.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                screenResizeRunnable.run();
            }
        });

        // set the window (this) pane to what we just built
        setContentPane(panes);

        // once all is rendered, make sure the scaling is applied (+ painting and validating, cuz gotta do that)
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
            case "turn_left":
                System.out.println("Turn left");
                break;
            case "turn_right":
                System.out.println("Turn right");
                break;
            case "go":
                System.out.println("Go to " + clickable.getTargetLocation());
                break;
            case "dialog":
                showDialogue(clickable);
                System.out.println("dialogue -> " + clickable.getDialogue());
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

        // stage 4: dismissing the dialogue, by clicking on it
        dialoguePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                panes.remove(dialoguePanel);
                activeDialoguePanel = null;
                panes.repaint();
            }
        });

        // stage 5: put it on screen. no timer, so it stays until clicked
        activeDialoguePanel = dialoguePanel;
        panes.add(dialoguePanel, JLayeredPane.MODAL_LAYER);
        panes.revalidate();
        panes.repaint();
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