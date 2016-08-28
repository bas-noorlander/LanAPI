package scripts.lanapi.game.painting;

import org.tribot.api.General;
import org.tribot.api.Screen;
import org.tribot.api.Timing;
import org.tribot.api2007.Game;
import scripts.lanapi.core.patterns.IStrategy;
import scripts.lanapi.game.persistance.Vars;
import scripts.lanapi.game.script.LANScript;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Laniax
 */
public abstract class AbstractPaintInfo {

    // Fonts
    protected Font title_font, default_font, status_font;

    // Values
    protected final int general_padding = 10;
    protected final int line_distance = 20;

    // Shapes
    protected final int background_radius = 10;
    private final Rectangle viewport = Screen.getViewport();

    // Colors
    protected final Color black_transparent = new Color(0, 0, 0, 139);
    private final Color shadow_color = new Color(24, 24, 24);
    public final Color primary;
    public final Color secondary;

    // Script reference
    private final LANScript script;

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
     * This is called after everything else has been painted.
     *
     * @param g
     */
    public void debugDraw(Graphics2D g) {}


    public AbstractPaintInfo() {

        // Load fonts
        this.title_font = PaintHelper.getFont("https://dl.dropboxusercontent.com/u/21676524/RS/SourceSansPro-Semibold.ttf", 35f);
        this.default_font = PaintHelper.getFont("https://dl.dropboxusercontent.com/u/21676524/RS/SourceSansPro-Regular.ttf", 13f);
        this.status_font = this.title_font != null ? this.title_font.deriveFont(14f) : null;

        // Colors
        this.primary = this.getPrimaryColor();
        this.secondary = this.getSecondaryColor();

        this.script = Vars.get().get("script");
    }

    public void draw(Graphics2D g) {

        this.customDrawBefore(g);

        // Before we start painting, we have to know where everything is supposed to go!

        PaintBuilder title = paintTitle();

        Rectangle2D title_bounds = PaintHelper.getStringBounds(g, this.title_font, title);

        long runtime = this.script.getRunningTime();
        PaintBuilder texts = getText(runtime);

        int text_lines = (int) Math.max(Math.ceil((texts.getAll().size() / 2) + 1), 1);

        int viewport_right = viewport.x + viewport.width;

        int title_offset_from_bottom = (text_lines * line_distance) + (general_padding * 5);
        int title_offset_from_right = (int) (title_bounds.getWidth() + general_padding);

        int title_x = viewport_right - title_offset_from_right;
        int title_y = (viewport.y + viewport.height) - title_offset_from_bottom;
        int title_height = (int) title_bounds.getHeight();

        // We have all the data to draw our paint!

        // Title
        PaintHelper.drawShadowedText(title, this.shadow_color, new Point(title_x, title_y), this.title_font, g);

        // Background
        RoundRectangle2D round_rect = new RoundRectangle2D.Double(title_x, title_y + general_padding, title_bounds.getWidth(), (text_lines * line_distance) + (general_padding * 3), this.background_radius, this.background_radius);
        g.setColor(this.black_transparent);
        g.fillRoundRect((int) round_rect.getX(), (int) round_rect.getY(), (int) round_rect.getWidth(), (int) round_rect.getHeight(), (int) round_rect.getArcWidth(), (int) round_rect.getArcHeight());

        // free or premium tag
        String tag = this.isScriptPremium() ? "PREMIUM" : "FREE";
        int tag_width = PaintHelper.getStringWidth(g, this.status_font, tag);
        g.setColor(Color.white);
        g.setFont(this.status_font);
        g.drawString(tag, viewport_right - general_padding - tag_width, title_y - title_height - 3);

        int text_x_start = title_x + general_padding;
        int text_y_start = title_y + (general_padding*3);

        // Runtime
        PaintBuilder pb =
                new PaintBuilder()
                        .add()
                        .setColor(Color.white)
                        .setText("Runtime ")
                        .setColor(this.secondary)
                        .setText(Timing.msToString(runtime))
                        .end();

        PaintHelper.drawPaintBuilder(g, this.default_font, new Point(text_x_start, text_y_start), pb);

        // And all the scripter-defined texts
        boolean alternate = true;

        int y = 0; // we need the last Y later.

        List<PaintString> all = texts.getAll();

        for (int i = 1; i < all.size() + 1; i++) {

            PaintString ps = all.get(i-1);

            int x = alternate ? text_x_start + (int) (title_bounds.getWidth() / 2) : text_x_start;
            y = text_y_start + (this.line_distance * (int) Math.floor(i / 2));

            PaintHelper.drawPaintString(g, new Point(x, y), this.default_font, ps);

            alternate = !alternate;
        }

        // And lastly, the status text
        g.setColor(this.secondary);
        g.setFont(this.status_font);
        g.drawString(PaintHelper.status_text, text_x_start, y + this.line_distance);

        this.customDrawAfter(g);

        if (this.script.isLocal())
            this.debugDraw(g);
    }
}
