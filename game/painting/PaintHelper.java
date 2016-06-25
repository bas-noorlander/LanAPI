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

    public static RSTile destination_tile = null;
    public static String status_text = "Initializing";
    public static int profit = 0;

    // Mouse painting related
    private static final int trail_size = 100;
    private static final double alpha = (255.0 / trail_size);
    private static final Point[] points = new Point[trail_size];
    private static int index = 0;

    private static final Color mouse_background_color = new Color(0, 0, 0, 80);
    private static final Color mouse_outline_color = new Color(0, 0, 0, 150);
    private static final Color mouse_accent_color = Color.CYAN;
    private static final BasicStroke stroke = new BasicStroke(2);

    public static boolean mouse_down = false;
    private static boolean old_mouse_state = false;
    private static int pulse_counter = 0;

    /**
     * Draws text multiple times as if it is shadowed.
     *
     * @param g
     */
    public static void drawShadowedText(PaintBuilder paint_builder, Color shadow_color, Point pos, Font font, Graphics2D g) {

        Point shadow_pos = new Point((int)pos.getX() +2 , (int)pos.getY() +2);
        drawPaintBuilder(g, font, shadow_pos, paint_builder, shadow_color);
        drawPaintBuilder(g, font, pos, paint_builder);
    }

    public static void drawPaintBuilder(Graphics2D g, Font font, Point pos, PaintBuilder pb) {
        drawPaintBuilder(g, font, pos, pb, null);
    }

    public static void drawPaintBuilder(Graphics2D g, Font font, Point pos, PaintBuilder pb, Color override_color) {
        for (PaintString ps : pb.getAll()) {
            drawPaintString(g, pos, font, ps, override_color);
        }
    }

    public static void drawPaintString(Graphics2D g, Point pos, Font font, PaintString ps) {
        drawPaintString(g, pos, font, ps, null);
    }

    public static void drawPaintString(Graphics2D g, Point pos, Font font, PaintString ps, Color override_color) {
        int x = pos.x;
        g.setFont(font);
        for (Pair<String, Color> set : ps.getAll()) {
            g.setColor(override_color == null ? set.getValue() : override_color);
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

        // The vector returns the correct height, however, the metric bounds will have a more correct width that is better with spaces.
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
            BufferedInputStream input_stream = new BufferedInputStream(new URL(url).openStream());
            return Font.createFont(Font.TRUETYPE_FONT, input_stream).deriveFont(size);
        } catch (Exception e) {
            return null;
        }
    }

    public static void drawTile(Graphics g, Color color, RSTile tile, boolean minimap) {

        Shape old_clip = g.getClip();

        g.setClip(Screen.getViewport());

        Polygon poly = Projection.getTileBoundsPoly(tile, 0);

        if (poly != null) {
            g.setColor(mouse_background_color);
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

        g.setClip(old_clip);

    }

    /**
     * Draws a custom cursor with click animation.
     *
     * @param g1
     * @param mouse_pos
     * @param drag_pos
     * @param mouse_color
     */
    public static void drawMouse(Graphics g1, Point mouse_pos, Point drag_pos, Color mouse_color) {
        Graphics2D g = (Graphics2D) g1;

        g.setColor(mouse_background_color);
        g.fillOval((int) mouse_pos.getX() - 10, (int) mouse_pos.getY() - 10, 20, 20);

        if (!old_mouse_state && mouse_down) {
            pulse_counter = 5;
            mouse_down = false;
        }

        if (pulse_counter > 0) {
            int rgba = mouse_color.getRGB() | 50 * pulse_counter-- & 0xff;
            g.setColor(new Color(rgba, true));
            g.fillOval((int) mouse_pos.getX() - 10, (int) mouse_pos.getY() - 10, 20, 20);
        }

        g.setStroke(stroke);
        g.setColor(mouse_outline_color);
        g.drawOval((int) mouse_pos.getX() - 10, (int) mouse_pos.getY() - 10, 20, 20);
        g.fillOval((int) mouse_pos.getX() - 1, (int) mouse_pos.getY() - 1, 2, 2);

        old_mouse_state = mouse_down;
    }


    public static void drawMouseTrail(Graphics g, ArrayList<Point> unused, Color mouse_color) {

        Color col = mouse_color.brighter();

        double al = 0;
        for (int i = index; i != (index == 0 ? trail_size - 1 : index - 1); i = (i + 1) % trail_size) {
            if (points[i] != null && points[(i + 1) % trail_size] != null) {

                g.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), (int) al & 0xff));
                g.drawLine(points[i].x, points[i].y, points[(i + 1) % trail_size].x, points[(i + 1) % trail_size].y);

                al += alpha;
            }
        }
    }

    public static void moveMouseTrail(Point point) {
        points[index++] = point;
        index %= trail_size;
    }

    public static String formatNumber(int number) {
        return formatNumber(number, false);
    }

    public static String formatNumber(int number, boolean append_gp) {

        String result;

        boolean is_negative = number < 0;

        number = Math.abs(number);

        DecimalFormat decimal_format = new DecimalFormat("#.#");
        decimal_format.setRoundingMode(RoundingMode.CEILING);

        if (number > 1000000000) {
            result = decimal_format.format((double)number / 1000000000) + "b";
            append_gp = false;
        } else if (number > 1000000) {
            result = decimal_format.format((double)number / 1000000) + "m";
            append_gp = false;
        } else if (number > 1000) {
            result = decimal_format.format((double)number / 1000) + "k";
            append_gp = false;
        } else {
            result = String.valueOf(number);
        }

        if (is_negative) {
            result = "-" + result;
        }

        return append_gp ? result + "gp" : result;
    }
}
