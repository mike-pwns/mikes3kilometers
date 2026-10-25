import javax.swing.*; // yes.. never thought id ever use this again..
import java.awt.*;

public class GameUI extends JFrame {

    String currentImage;
    boolean optionLeft, optionRight; // depends on angle

    public void changeImage() {
        // reference

        // clear all buttons
        // render left/right button based on currentscene
    }

    public GameUI() {
        super();

        // then update individual stuff here

        this.add(new JLabel("Welcome to scene1"));

        // set fullscreen
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
//        this.setUndecorated(true);


        this.setTitle("mikes3kilometers");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


//        this.setVisible(true); this is set true in the start
    }

}
