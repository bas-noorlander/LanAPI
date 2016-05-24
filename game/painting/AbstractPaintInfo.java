package scripts.lanapi.game.painting;

import org.tribot.api.Screen;
import org.tribot.api.Timing;
import scripts.lanapi.game.persistance.Vars;
import scripts.lanapi.game.script.AbstractScript;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * @author Laniax
 */
public abstract class AbstractPaintInfo {

    // Fonts
    protected Font titleFont, defaultFont, statusFont;

    // Values
    private final int generalPadding = 10;
    private final int lineDistance = 20;

    // Shapes
    private final int backgroundRadius = 10;
    private final Rectangle viewport = Screen.getViewport();

    // Colors
    protected final Color blackTransparent = new Color(0, 0, 0, 139);
    private final Color shadowColor = new Color(24, 24, 24);
    protected final Color primary;
    protected final Color secondary;

    // Script reference
    private final AbstractScript script;

    /**
     * Return the primary color of your script. This will determine cursor color etc.
     * @return
     */
    public abstract Color getPrimaryColor();

    /**
     * Return the secondary color of your script. This will determine values inside texts etc.
     * @return
     */
    public abstract Color getSecondaryColor();

    /**
     * Return if the script is a premium script or not, determines the FREE or PREMIUM tag in the paint.
     * @return
     */
    public abstract boolean isScriptPremium();

    /**
     * Returns how the title should look in the paint.
     * @return
     */
    public abstract PaintBuilder paintTitle();

    /**
     * Return all the custom lines you want to display in the paint.
     * Most likely the XP, profit, etc.
     * @param runTime
     * @return
     */
    public abstract PaintBuilder getText(long runTime);

    /**
     * Hook into the draw function before all the text/graphics are rendered.
     *
     * @param g
     */
    public void customDrawBefore(Graphics2D g) {}

    /**
     * Hook into the draw function after all the text is rendered.
     *
     * @param g
     */
    public void customDrawAfter(Graphics2D g) {}

    /**
     * A custom draw function that is only called when the script is run locally.
     * This is called after everything has rendered.
     *
     * @param g
     */
    public void debugDraw(Graphics2D g) {}


    public AbstractPaintInfo() {

        // Load fonts
        this.titleFont = PaintHelper.getFont("https://dl.dropboxusercontent.com/u/21676524/RS/SourceSansPro-Semibold.ttf", 35f);
        this.defaultFont = PaintHelper.getFont("https://dl.dropboxusercontent.com/u/21676524/RS/SourceSansPro-Regular.ttf", 13f);
        this.statusFont = this.titleFont != null ? this.titleFont.deriveFont(14f) : null;

        // Colors
        this.primary = this.getPrimaryColor();
        this.secondary = this.getSecondaryColor();

        this.script = Vars.get().get("script");
    }

    public void draw(Graphics2D g) {

        this.customDrawBefore(g);

        // Before we start painting, we have to know where everything is supposed to go!

        PaintBuilder title = paintTitle();

        Rectangle2D titleBounds = PaintHelper.getStringBounds(g, this.titleFont, title);

        long runtime = this.script.getRunningTime();
        PaintBuilder texts = getText(runtime);

        int textLines = texts.getAll().size();

        int viewportRight = viewport.x + viewport.width;

        int titleOffsetFromBottom = (textLines * lineDistance) + (generalPadding * 5);
        int titleOffsetFromRight = (int) (titleBounds.getWidth() + generalPadding);

        int titleX = viewportRight - titleOffsetFromRight;
        int titleY = (viewport.y + viewport.height) - titleOffsetFromBottom;
        int titleHeight = (int) titleBounds.getHeight();

        // We have all the data to draw our paint!

        // Title
        PaintHelper.drawShadowedText(title, this.shadowColor, new Point(titleX, titleY), this.titleFont, g);

        // Background
        RoundRectangle2D roundRect = new RoundRectangle2D.Double(titleX, titleY + generalPadding, titleBounds.getWidth(), (textLines * lineDistance) + (generalPadding * 3), this.backgroundRadius, this.backgroundRadius);
        g.setColor(this.blackTransparent);
        g.fillRoundRect((int) roundRect.getX(), (int) roundRect.getY(), (int) roundRect.getWidth(), (int) roundRect.getHeight(), (int) roundRect.getArcWidth(), (int) roundRect.getArcHeight());

        // free or premium tag
        String tag = this.isScriptPremium() ? "PREMIUM" : "FREE";
        int tagWidth = PaintHelper.getStringWidth(g, this.statusFont, tag);
        g.setColor(Color.white);
        g.setFont(this.statusFont);
        g.drawString(tag, viewportRight - generalPadding - tagWidth, titleY - titleHeight - 3);

        int textXStart = titleX + generalPadding;
        int textYStart = titleY + (generalPadding*3);

        // Runtime
        PaintBuilder pb =
                new PaintBuilder()
                        .add()
                            .setColor(Color.white)
                            .setText("Runtime ")
                            .setColor(this.secondary)
                            .setText(Timing.msToString(runtime))
                        .end();

        PaintHelper.drawPaintBuilder(g, this.defaultFont, new Point(textXStart, textYStart), pb);

        // And all the scripter-defined texts
        boolean alternate = true;

        int y = 0; // we need the last Y later.

        List<PaintString> all = texts.getAll();

        for (int i = 1; i < all.size() + 1; i++) {

            PaintString ps = all.get(i-1);

            int x = alternate ? textXStart + (int) (titleBounds.getWidth() / 2) : textXStart;
            y = textYStart + (this.lineDistance * (int) Math.floor(i / 2));

            PaintHelper.drawPaintString(g, new Point(x, y), this.defaultFont, ps);

            alternate = !alternate;
        }

        // And lastly, the status text
        g.setColor(this.secondary);
        g.setFont(this.statusFont);
        g.drawString(PaintHelper.statusText, textXStart, y + this.lineDistance);

        this.customDrawAfter(g);

        if (this.script.isLocal())
            this.debugDraw(g);
    }
}
