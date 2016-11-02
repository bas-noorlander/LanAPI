package scripts.lanapi.game.painting;

import org.tribot.api.General;
import org.tribot.api.Screen;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import scripts.lanapi.game.persistance.Vars;
import scripts.lanapi.game.script.LANScript;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
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
    private boolean is_hidden = false;
    private long hideshow_timestamp = 0L;

    private final int transition_duration = 1000;

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

    private HashMap<Integer, Shape> shape_ids = new HashMap<>();

    /**
     * Return the primary color of your script. This will determine cursor color etc.
     *
     * @return
     */
    public abstract Color getPrimaryColor();

    /**
     * Return the secondary color of your script. This will determine values inside texts etc.
     *
     * @return
     */
    public abstract Color getSecondaryColor();

    /**
     * Return if the script is a premium script or not, determines the FREE or PREMIUM tag in the paint.
     *
     * @return
     */
    public abstract boolean isScriptPremium();

    /**
     * Returns if the bugreport button should be shown.
     *
     * @return
     */
    public abstract boolean showReportBugButton();

    /**
     * Returns how the title should look in the paint.
     *
     * @return
     */
    public abstract PaintBuilder paintTitle();

    /**
     * Return all the custom lines you want to display in the paint.
     * Most likely the XP, profit, etc.
     *
     * @param runTime
     * @return
     */
    public abstract PaintBuilder getText(long runTime);

    /**
     * Hook into the draw function before all the text/graphics are rendered.
     *
     * @param g
     */
    public void customDrawBefore(Graphics2D g) {
    }

    /**
     * Hook into the draw function after all the text is rendered.
     *
     * @param g
     */
    public void customDrawAfter(Graphics2D g) {
    }

    /**
     * A custom draw function that is only called when the script is run locally.
     * This is called after everything else has been painted.
     *
     * @param g
     */
    public void debugDraw(Graphics2D g) {
    }

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

    /**
     * Called when a clickable shape was clicked by the user (never the bot).
     *
     * @param id - the same id as used with #addClickableShape. You can use this to identify which shape was clicked.
     */
    public void onShapeClicked(int id) {

        if (id == -1) {
            // paint hide/show
            this.hideshow_timestamp = System.currentTimeMillis();

            this.is_hidden = !this.is_hidden;
        }

    }

    public void addClickableShape(int id, Shape shape) {
        this.shape_ids.put(id, shape);
    }

    public Set<Map.Entry<Integer, Shape>> getAllClickableShapes() {
        return this.shape_ids.entrySet();
    }

    public void draw(Graphics2D g) {

        boolean is_transitioning = false;

        if (hideshow_timestamp == 0L) {
            // Script started. Animate the paint in.
            this.hideshow_timestamp = System.currentTimeMillis();
        }

        int viewport_right = viewport.x + viewport.width;

        if (System.currentTimeMillis() - this.hideshow_timestamp < this.transition_duration) {
            is_transitioning = true;
        }

        if (is_transitioning || !is_hidden) {
            this.customDrawBefore(g);
        }

        // Before we start painting, we have to know where everything is supposed to go!

        PaintBuilder title = paintTitle();

        Rectangle2D title_bounds = PaintHelper.getStringBounds(g, this.title_font, title);

        long runtime = this.script.getRunningTime();
        PaintBuilder texts = getText(runtime);

        int text_lines = (int) Math.max(Math.ceil((texts.getAll().size() / 2) + 1), 1);

        int title_offset_from_bottom = (text_lines * line_distance) + (general_padding * 5);
        int title_offset_from_right = (int) (title_bounds.getWidth() + general_padding);

        int title_y = (viewport.y + viewport.height) - title_offset_from_bottom;

//        int title_x = viewport_right - title_offset_from_right;

        if (is_transitioning) {
            int transition_percentage = (int) (System.currentTimeMillis() - this.hideshow_timestamp);

            if (is_hidden) {
                // animating from open to closed.
                viewport_right = viewport_right + transition_percentage; //((transition_percentage / title_y) / transition_duration);
            } else {
                  viewport_right = (viewport_right + title_offset_from_right) - transition_percentage;

                if (viewport_right < (viewport.x + viewport.width))
                    viewport_right = (viewport.x + viewport.width);
            }
        }

        int title_x = viewport_right - title_offset_from_right;
        int title_height = (int) title_bounds.getHeight();


        String bug_report = "Found a bug? Report it here!";

        int bug_report_width = PaintHelper.getStringWidth(g, this.default_font, bug_report);

        // We have all the data to draw our paint!

        g.setClip(viewport);

        if (showReportBugButton() && (is_transitioning || !is_hidden)) {

            g.setColor(this.primary);
            int box_x = (viewport.x + viewport.width) - (general_padding * 3) - bug_report_width;

            int y = 0;
            int transition_percentage = (int) (System.currentTimeMillis() - this.hideshow_timestamp);

            int open_y = viewport.y - this.background_radius;
            if (is_transitioning) {
                if (is_hidden) {
                    // hiding.. decrease Y
                    y = open_y - (transition_percentage /10);

                    if (y < -300)
                        y = -300;
                } else {
                    // showing.. increase Y
                    y = open_y + (transition_percentage /10);

                    if (y > 0)
                        y = 0;
                }
            }

            RoundRectangle2D found_a_bug_rect = new RoundRectangle2D.Double(
                    box_x,
                    y,
                    (general_padding * 2) + bug_report_width,
                    30,
                    this.background_radius,
                    this.background_radius
            );

            g.fillRoundRect((int) found_a_bug_rect.getX(), (int) found_a_bug_rect.getY(), (int) found_a_bug_rect.getWidth(), (int) found_a_bug_rect.getHeight(), (int) found_a_bug_rect.getArcWidth(), (int) found_a_bug_rect.getArcHeight());
            this.addClickableShape(1, found_a_bug_rect);
            g.setColor(Color.white);
            g.setFont(this.default_font);
            g.drawString(bug_report, box_x + general_padding, y + 20);
        }

        // Title
        if (is_transitioning || !is_hidden) {
            PaintHelper.drawShadowedText(title, this.shadow_color, new Point(title_x, title_y), this.title_font, g);

            // Background
            RoundRectangle2D round_rect = new RoundRectangle2D.Double(title_x, title_y + general_padding, title_bounds.getWidth(), (text_lines * line_distance) + (general_padding * 3), this.background_radius, this.background_radius);
            g.setColor(this.black_transparent);
            g.fillRoundRect((int) round_rect.getX(), (int) round_rect.getY(), (int) round_rect.getWidth(), (int) round_rect.getHeight(), (int) round_rect.getArcWidth(), (int) round_rect.getArcHeight());
        }
        // free or premium tag
        String tag = this.isScriptPremium() ? "PREMIUM" : "FREE";
        int tag_width = PaintHelper.getStringWidth(g, this.status_font, tag);
        int tag_height = PaintHelper.getStringHeight(g, this.status_font, tag);
        int tag_x = viewport_right - tag_width - (general_padding * 3);
        g.setColor(this.primary);
        Rectangle price_tag_rect = new Rectangle(tag_x, title_y - title_height - tag_height - general_padding, tag_width + (general_padding * 2), 20);

        if (is_transitioning || !is_hidden) {
            g.fillRect(price_tag_rect.x, price_tag_rect.y, price_tag_rect.width, price_tag_rect.height);
            g.setColor(Color.white);
            g.setFont(this.status_font);

            g.drawString(tag, tag_x + general_padding, title_y - title_height - general_padding + 5);
        }

        // paint toggle
        Rectangle toggle = new Rectangle(viewport.x + viewport.width + 1, price_tag_rect.y, 20, ((viewport.y + viewport.height) - general_padding) - price_tag_rect.y);

        Polygon clip = new Polygon(
                new int[]{527, 527, 523, 525, 520, 520, 400, 400, 527},
                new int[]{300, 210, 210, 204, 192, 140, 140, 340, 340}, 9);
        g.setClip(clip);

        this.addClickableShape(-1, toggle);

        g.setColor(this.primary);
        g.fillRect(toggle.x, toggle.y, toggle.width, toggle.height);
        g.setColor(this.black_transparent);

        int height = toggle.y - 4;

        while (height < toggle.getMaxY()) {
            g.drawLine(viewport.x + viewport.width, height, viewport.x + viewport.width + toggle.width, height + 20);
            height += 4;
        }

        g.setColor(Color.white);

        if (is_hidden) {
            g.fillArc(toggle.x - 10, toggle.y + (toggle.height / 2) - 10, 20, 20, 315, 90);
        } else {
            g.fillArc(toggle.x, toggle.y + (toggle.height / 2) - 10, 20, 20, 135, 90);
        }

        g.setClip(viewport);

        if (is_transitioning || !is_hidden) {

            int text_x_start = title_x + general_padding;
            int text_y_start = title_y + (general_padding * 3);

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

                PaintString ps = all.get(i - 1);

                int x = alternate ? (text_x_start + (int) (title_bounds.getWidth() / 2) - this.general_padding) : text_x_start;
                y = text_y_start + (this.line_distance * (int) Math.floor(i / 2));

                PaintHelper.drawPaintString(g, new Point(x, y), this.default_font, ps);

                alternate = !alternate;
            }

            // And lastly, the status text
            g.setColor(this.secondary);
            g.setFont(this.status_font);
            g.drawString(PaintHelper.status_text, text_x_start, y + this.line_distance);
        }

        g.setClip(null);

        this.customDrawAfter(g);

        if (this.script.isLocal())
            this.debugDraw(g);
    }
}