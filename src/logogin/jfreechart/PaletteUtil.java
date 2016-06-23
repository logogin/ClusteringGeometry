import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 *
 * @created Jul 12, 2010
 * @author Pavel Danchenko
 */
public class PaletteUtil {

    public static List<Integer[]> readPalette(File file) throws IOException {
        List<Integer[]> palette = new ArrayList<Integer[]>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            String line;
            while ( (line = reader.readLine()) != null ) {
                String[] rgb = line.split(",");
                palette.add(new Integer[] {Integer.valueOf(rgb[0]), Integer.valueOf(rgb[1]), Integer.valueOf(rgb[2])});
            }
        } finally {
            reader.close();
        }
        return palette;
    }

    public static void generatePaletteIcon(List<Integer[]> palette, OutputStream out) throws IOException {
        BufferedImage image = new BufferedImage(512, 40,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(512, 40);

        float dist[] = new float[palette.size()];
        Color colors[] = new Color[palette.size()];
        float step = 1.0f / palette.size();
        for (int i = 0; i < palette.size(); i++) {
            dist[i] = i*step;
            Integer[] values = palette.get(i);
            colors[i] = new Color(values[0], values[1], values[2]);
        }
        LinearGradientPaint paint = new LinearGradientPaint(start, end, dist, colors);

        g2.setPaint(paint);
        g2.fillRect(0, 0, 512, 40);
        ImageIO.write(image, "png", out);
    }
}
