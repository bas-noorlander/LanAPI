package scripts.lanapi.core.gui.client.frames;

import com.allatori.annotations.DoNotRename;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.tribot.api.General;
import org.tribot.api2007.types.RSTile;
import scripts.lanapi.core.gui.AbstractGUIController;
import scripts.lanapi.core.io.json.JsonObject;
import scripts.lanapi.game.persistance.Vars;
import scripts.lanapi.game.script.LANScript;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Laniax
 */
@DoNotRename
public class BugReportController extends AbstractGUIController {

    private Stage stage;
    @FXML
    @DoNotRename
    private TextArea notes;

    @FXML
    @DoNotRename
    private Button send;

    public RSTile player_pos;

    public BufferedImage screenshot;

    public JsonObject data;

    @Override
    public boolean getEnableNotifications() {
        return false;
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        send.setOnAction((e)-> {

            LANScript script = Vars.get().get("script");
            String urlString = script.signatureServerUrl() + "bugreport/new";

            URL url = null;
            try {
                url = new URL(urlString);

                Map<String,Object> params = new LinkedHashMap<>();
                params.put("playerpos", player_pos.toString());
                params.put("username", URLEncoder.encode(General.getTRiBotUsername(), "UTF-8"));
                params.put("screenshot", imgToBase64String(screenshot, "png"));
                params.put("notes", notes.getText());
                params.put("data", data.toString());

                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String,Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postDataBytes);

                new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Platform.runLater(() -> stage.close());
            General.println("Succesfully submitted a bug report! I'll respond via the tribot forums!");

        });
    }

    public static String imgToBase64String(final RenderedImage img, final String formatName) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, formatName, Base64.getEncoder().wrap(os));
            return os.toString(StandardCharsets.ISO_8859_1.name());
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
