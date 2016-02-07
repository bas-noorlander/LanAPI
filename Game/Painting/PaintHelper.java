package scripts.LanAPI.Game.Painting;

import org.tribot.api2007.Projection;
import org.tribot.api2007.types.RSTile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Helper class that handles the script's paint and custom cursor logic.
 *
 * @author Laniax
 */
public class PaintHelper {

    public static RSTile destinationTile = null;
    public static String statusText = "Starting";

    public static int profit = 0;

    // Mouse painting related
    private static final int trailSize = 100;
    private static final double alpha = (255.0 / trailSize);
    private static final Point[] points = new Point[trailSize];
    private static int index = 0;

    private static final Color mouseBackgroundColor = new Color(0, 0, 0, 80);
    private static final Color mouseOutlineColor = new Color(0, 0, 0, 150);
    private static final Color mouseAccentColor = Color.CYAN;
    private static final BasicStroke stroke = new BasicStroke(2);

    public static boolean mouseDown = false;
    private static boolean oldMouseState = false;
    private static int pulseCounter = 0;

    /**
     * Draws text multiple times as if it is outlined/shadowed.
     *
     * @param paintString
     * @param g
     */
    public static void drawShadowedText(PaintString paintString, Graphics2D g) {

        int x = paintString.getPosition().x;
        int y = paintString.getPosition().y;

        g.setColor(paintString.getShadowColor());

        TextLayout textLayout = new TextLayout(paintString.getText(), paintString.getFont(), g.getFontRenderContext());

        textLayout.draw(g, x + 1, y);
        textLayout.draw(g, x, y + 1);
        textLayout.draw(g, x - 1, y);
        textLayout.draw(g, x, y - 1);

        g.setColor(paintString.getColor());
        textLayout.draw(g, x, y);
    }

    /**
     * Draws text multiple times as if it is outlined/shadowed.
     *
     * @param text
     * @param font
     * @param x
     * @param y
     * @param g
     */
    public static void drawShadowedText(String text, Font font, int x, int y, Graphics2D g) {

        g.setFont(font);

        TextLayout textLayout = new TextLayout(text, font, g.getFontRenderContext());
        g.setColor(Color.DARK_GRAY);
        textLayout.draw(g, x + 1, y);
        textLayout.draw(g, x, y + 1);
        textLayout.draw(g, x - 1, y);
        textLayout.draw(g, x, y - 1);

        g.setColor(Color.WHITE);
        textLayout.draw(g, x, y);
    }

    public static void drawPaintString(PaintString paintString, Graphics2D g) {
        Font font = paintString.getFont();
        if (font != null)
            g.setFont(font);

        if (paintString.getCentered() != null)
            PaintHelper.setCenteredText(paintString, g);

        if (paintString.getDrawShadow()) {
            PaintHelper.drawShadowedText(paintString, g);
        } else {
            g.setColor(paintString.getColor());
            g.drawString(paintString.getText(), paintString.getPosition().x, paintString.getPosition().y);
        }

        BufferedImage icon = paintString.getIcon();
        if (icon != null) {
            g.drawImage(icon, null, paintString.getPosition().x - icon.getWidth(), paintString.getPosition().y - icon.getHeight());
        }
    }

    /**
     * Downloads an image from the internet.
     *
     * @param url
     * @return an Image object containing the image.
     */
    public static Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            return null;
        }
    }

 /**
     * Downloads an image from the internet.
     *
     * @param url
     * @return an Image object containing the image.
     */
    public static BufferedImage getBufferedImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Downloads a font from the internet.
     *
     * @param url
     * @return an Font object containing the font.
     */
    public static Font getFont(String url, float size) {
        try {
            BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());
            return Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(size);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Draws the walking destination on the screen.
     *
     * @param g1
     */
    public static void drawDestinationTile(Graphics g1) {

        if (destinationTile == null || !destinationTile.isOnScreen())
            return;

        Graphics2D g = (Graphics2D) g1;

        Polygon poly = Projection.getTileBoundsPoly(destinationTile, 0);

        if (poly != null) {

            g.setColor(mouseBackgroundColor);
            g.fillPolygon(poly);

            g.setColor(mouseOutlineColor);
            g.drawPolygon(poly);
        }
    }

    /**
     * Draws a custom cursor with click animation.
     *
     * @param g1
     * @param mousePos
     * @param dragPos
     */
    public static void drawMouse(Graphics g1, Point mousePos, Point dragPos) {
        Graphics2D g = (Graphics2D) g1;

        g.setColor(mouseBackgroundColor);
        g.fillOval((int) mousePos.getX() - 10, (int) mousePos.getY() - 10, 20, 20);

        if (!oldMouseState && mouseDown) {
            pulseCounter = 5;
            mouseDown = false;
        }

        if (pulseCounter > 0) {
            int rgba = mouseAccentColor.getRGB() | 50 * pulseCounter-- & 0xff;
            g.setColor(new Color(rgba, true));
            g.fillOval((int) mousePos.getX() - 10, (int) mousePos.getY() - 10, 20, 20);
        }

        g.setStroke(stroke);
        g.setColor(mouseOutlineColor);
        g.drawOval((int) mousePos.getX() - 10, (int) mousePos.getY() - 10, 20, 20);
        g.fillOval((int) mousePos.getX() - 1, (int) mousePos.getY() - 1, 2, 2);

        oldMouseState = mouseDown;
    }


    public static void drawMouseTrail(Graphics g, ArrayList<Point> unused) {

        double alpha = 0;
        for (int i = index; i != (index == 0 ? trailSize - 1 : index - 1); i = (i + 1) % trailSize) {
            if (points[i] != null && points[(i + 1) % trailSize] != null) {

                int rgba = mouseAccentColor.getRGB() | (int) alpha & 0xff;
                g.setColor(new Color(rgba, true));

                g.drawLine(points[i].x, points[i].y, points[(i + 1) % trailSize].x, points[(i + 1) % trailSize].y);

                alpha += PaintHelper.alpha;
            }
        }
    }

    public static void moveMouseTrail(Point point) {
        points[index++] = point;
        index %= trailSize;
    }

    public static void setCenteredText(PaintString string, Graphics2D g) {

        Rectangle centerRect = string.getCentered();

        int width = centerRect.width;
        int height = centerRect.height;

        FontMetrics fm = g.getFontMetrics();

        int x = (width - fm.stringWidth(string.getText())) / 2;
        int y = (fm.getAscent() + (height - (fm.getAscent() + fm.getDescent())) / 2);

        int xRes = centerRect.x + x;
        int yRes = centerRect.y + y;

        string.setPosition(new Point(xRes, yRes));
    }

    public static void drawCenteredText(String str, int xPos, int yPos, Graphics2D g) {
        FontMetrics fm = g.getFontMetrics();
        int x = (xPos - fm.stringWidth(str)) / 2;
        int y = (fm.getAscent() + (yPos - (fm.getAscent() + fm.getDescent())) / 2);
        g.drawString(str, x, y);
    }

    public static String formatNumber(int number) {

        if (number > 1000000) {
            return number / 1000000 + "m";
        }

        if (number > 1000) {
            return number / 1000 + "k";
        }

        return String.valueOf(number);
    }
}
