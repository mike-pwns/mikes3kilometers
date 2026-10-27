import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.function.Consumer;

public class Clickable {

    // -- FIELDS

    String clickableType;
    String symbol;

    // Position relative to screen dimensions
    float x, y, w, h;

    // Font size relative to screen height
    float fontRatio = 0.03f;

    // Type-specific fields
    Integer targetAngle;
    String targetLocation;
    String dialogue;

    // -- CONSTRUCTOR

    public Clickable(
            String clickableType,
            String symbol,
            float x, float y,
            float w, float h) {

        this.clickableType = clickableType;
        this.symbol = symbol;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    // gotta pass
    public JButton createButton(Consumer<Clickable> handler) {

        JButton button = new JButton(symbol);

        button.putClientProperty("clickable", this);
        button.putClientProperty("fontRatio", fontRatio);

        button.setFont(GameUI.pixelFont.deriveFont(20f));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setBorder(new LineBorder(Color.BLACK, 2));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> handler.accept(this));
        return button;
    }



    @Override
    public String toString() {
        return "{" + clickableType + " '" + symbol
                + "' x=" + x + " y=" + y
                + " w=" + w + " h=" + h
                + (targetAngle != null
                ? " targetAngle=" + targetAngle : "")
                + (targetLocation != null
                ? " targetLocation=" + targetLocation : "")
                + (dialogue != null
                ? " dialogue=\"" + dialogue + "\"" : "")
                + "}";
    }
}
