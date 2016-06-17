package scripts.lanapi.game.painting;

import org.tribot.api.Screen;
import org.tribot.api2007.Projection;
import org.tribot.api2007.types.RSTile;
import scripts.lanapi.core.collections.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * Helper class that handles the script's paint and custom cursor logic.
 *
 * @author Laniax
 */
public class PaintHelper {

    public static RSTile destinationTile = null;
    public static String statusText = "Initializing";
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
     * Draws text multiple times as if it is shadowed.
     *
     * @param g
     */
    public static void drawShadowedText(PaintBuilder paintBuilder, Color shadowColor, Point pos, Font font, Graphics2D g) {

        Point shadowPos = new Point((int)pos.getX() +2 , (int)pos.getY() +2);
        drawPaintBuilder(g, font, shadowPos, paintBuilder, shadowColor);
        drawPaintBuilder(g, font, pos, paintBuilder);
    }

    public static void drawPaintBuilder(Graphics2D g, Font font, Point pos, PaintBuilder pb) {
        drawPaintBuilder(g, font, pos, pb, null);
    }

    public static void drawPaintBuilder(Graphics2D g, Font font, Point pos, PaintBuilder pb, Color overrideColor) {
        for (PaintString ps : pb.getAll()) {
            drawPaintString(g, pos, font, ps, overrideColor);
        }
    }

    public static void drawPaintString(Graphics2D g, Point pos, Font font, PaintString ps) {
        drawPaintString(g, pos, font, ps, null);
    }

    public static void drawPaintString(Graphics2D g, Point pos, Font font, PaintString ps, Color overrideColor) {
        int x = pos.x;
        g.setFont(font);
        for (Pair<String, Color> set : ps.getAll()) {
            g.setColor(overrideColor == null ? set.getValue() : overrideColor);
            g.drawString(set.getKey(), x, pos.y);
            x += PaintHelper.getStringWidth(g, font, set.getKey());
        }
    }

    public static Rectangle2D getStringBounds(Graphics2D g, Font font, PaintBuilder pb) {

        String res = "";

        for (PaintString str : pb.getAll()) {
            for (Map.Entry<String, Color> substrings : str.getAll()) {
                res += substrings.getKey();
            }
        }

        return getStringBounds(g, font, res);
    }

    public static Rectangle2D getStringBounds(Graphics2D g, Font font, PaintString str) {

        String res = "";

        for (Map.Entry<String, Color> substrings : str.getAll()) {
            res += substrings.getKey();
        }

        return getStringBounds(g, font, res);
    }


    public static Rectangle2D getStringBounds(Graphics2D g, Font font, String str) {
        FontRenderContext frc = g.getFontRenderContext();
        GlyphVector gv = font.createGlyphVector(frc, str);
        Rectangle2D bounds = g.getFontMetrics(font).getStringBounds(str, g);

        // The vector returns the correct height, however, a the bounds will have a more correct width that is better with spaces.
        // So lets combine those.
        return new Rectangle((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) gv.getPixelBounds(frc, 0, 0).getHeight());
    }

    public static int getStringHeight(Graphics2D g, Font font, String str) {
        return (int) getStringBounds(g, font, str).getHeight();
    }

    public static int getStringWidth(Graphics2D g, Font font, String str) {
        return (int) getStringBounds(g, font, str).getWidth();
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

    public static void drawTile(Graphics g, Color color, RSTile tile, boolean minimap) {

        Shape oldClip = g.getClip();

        g.setClip(Screen.getViewport());

        Polygon poly = Projection.getTileBoundsPoly(tile, 0);

        if (poly != null) {
            g.setColor(mouseBackgroundColor);
            g.fillPolygon(poly);
            g.setColor(color);
            g.drawPolygon(poly);
        }

        Point p = Projection.tileToMinimap(tile);

        if (minimap && p != null) {
            g.setClip(null);
            g.setColor(color);
            g.fillOval(p.x - 2, p.y - 2, 4, 4);
        }

        g.setClip(oldClip);

    }

    /**
     * Draws a custom cursor with click animation.
     *
     * @param g1
     * @param mousePos
     * @param dragPos
     * @param mouseColor
     */
    public static void drawMouse(Graphics g1, Point mousePos, Point dragPos, Color mouseColor) {
        Graphics2D g = (Graphics2D) g1;

        g.setColor(mouseBackgroundColor);
        g.fillOval((int) mousePos.getX() - 10, (int) mousePos.getY() - 10, 20, 20);

        if (!oldMouseState && mouseDown) {
            pulseCounter = 5;
            mouseDown = false;
        }

        if (pulseCounter > 0) {
            int rgba = mouseColor.getRGB() | 50 * pulseCounter-- & 0xff;
            g.setColor(new Color(rgba, true));
            g.fillOval((int) mousePos.getX() - 10, (int) mousePos.getY() - 10, 20, 20);
        }

        g.setStroke(stroke);
        g.setColor(mouseOutlineColor);
        g.drawOval((int) mousePos.getX() - 10, (int) mousePos.getY() - 10, 20, 20);
        g.fillOval((int) mousePos.getX() - 1, (int) mousePos.getY() - 1, 2, 2);

        oldMouseState = mouseDown;
    }


    public static void drawMouseTrail(Graphics g, ArrayList<Point> unused, Color mouseColor) {

        Color col = mouseColor.brighter();

        double al = 0;
        for (int i = index; i != (index == 0 ? trailSize - 1 : index - 1); i = (i + 1) % trailSize) {
            if (points[i] != null && points[(i + 1) % trailSize] != null) {

                g.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), (int) al & 0xff));
                g.drawLine(points[i].x, points[i].y, points[(i + 1) % trailSize].x, points[(i + 1) % trailSize].y);

                al += alpha;
            }
        }
    }

    public static void moveMouseTrail(Point point) {
        points[index++] = point;
        index %= trailSize;
    }

    public static String formatNumber(int number) {
        return formatNumber(number, false);
    }

    public static String formatNumber(int number, boolean appendGP) {

        String result;

        boolean isNegative = number < 0;

        number = Math.abs(number);

        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);

        if (number > 1000000000) {
            result = decimalFormat.format((double)number / 1000000000) + "b";
            appendGP = false;
        } else if (number > 1000000) {
            result = decimalFormat.format((double)number / 1000000) + "m";
            appendGP = false;
        } else if (number > 1000) {
            result = decimalFormat.format((double)number / 1000) + "k";
            appendGP = false;
        } else {
            result = String.valueOf(number);
        }

        if (isNegative) {
            result = "-" + result;
        }

        return appendGP ? result + "gp" : result;
    }
}
