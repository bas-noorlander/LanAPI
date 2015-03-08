package scripts.LanAPI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.tribot.api2007.Projection;
import org.tribot.api2007.types.RSTile;


/**
 * Helper class that handles the script's paint and custom cursor logic.
 * 
 * @author Laniax
 *
 */
public class Paint {

	private static final Color mouseColor = new Color(0,0,0,80);
	private static final Color mouseColorOutline = new Color(0,0,0,150);
	private static final BasicStroke stroke = new BasicStroke(2);

	private final static RenderingHints antialiasing = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	public static boolean mouseDown = false;
	private static boolean oldMouseState = false;
	private static int pulseCounter = 0;
	public static RSTile destinationTile = null;

	public static String statusText = "Starting";

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

		TextLayout textLayout = new TextLayout(text, font, g.getFontRenderContext());
		g.setColor(Color.DARK_GRAY);
		textLayout.draw(g, x+1, y);
		textLayout.draw(g, x, y+1);
		textLayout.draw(g, x-1, y);
		textLayout.draw(g, x, y-1);

		g.setColor(Color.WHITE);
		textLayout.draw(g, x, y);
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
	 * Downloads a font from the internet.
	 * 
	 * @param url
	 * @return an Font object containing the font.
	 */
	public static Font getFont(String url, float size) {
		try {
			BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());
			return Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(size);
		}
		catch(Exception e) {
			return null;
		}
	}

	/**
	 * Draws the walking destination on the screen.
	 * @param g1
	 */
	public static void drawDestinationTile(Graphics g1) {

		if (destinationTile == null || !destinationTile.isOnScreen())
			return;

		Graphics2D g = (Graphics2D)g1;

		g.setRenderingHints(antialiasing);

		Polygon poly = Projection.getTileBoundsPoly(destinationTile, 0);

		if (poly != null) {

			g.setColor(mouseColor);
			g.fillPolygon(poly);

			g.setColor(mouseColorOutline);
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
		Graphics2D g = (Graphics2D)g1;

		g.setRenderingHints(antialiasing);
		g.setColor(mouseColor);
		g.fillOval((int)mousePos.getX() - 10, (int)mousePos.getY() - 10, 20, 20);

		if (!oldMouseState && mouseDown) {
			pulseCounter = 5;
			mouseDown = false;
		}

		if (pulseCounter > 0) { 
			g.setColor(new Color(0,246,255,50*pulseCounter--));
			g.fillOval((int)mousePos.getX() - 10, (int)mousePos.getY() - 10, 20, 20);
		}

		g.setStroke(stroke);
		g.setColor(mouseColorOutline);
		g.drawOval((int)mousePos.getX() - 10, (int)mousePos.getY() - 10, 20, 20);
		g.fillOval((int)mousePos.getX() - 1, (int)mousePos.getY() - 1, 2, 2);

		oldMouseState = mouseDown;	
	}
}
