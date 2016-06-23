import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.general.DefaultHeatMapDataset;
import org.jfree.data.general.HeatMapDataset;
import org.jfree.data.general.HeatMapUtilities;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYZDataset;

import de.micromata.opengis.kml.v_2_2_0.LatLonBox;

/**
 *
 * @created Jul 6, 2010
 * @author Pavel Danchenko
 */
public class WeightsChart {
    /**
     * @param args
     */
    public static void main(String[] args)  throws Exception {
        WeightsMapService service = new WeightsMapService();
        WeightsMapRequest request = new WeightsMapRequest("berlin_176", 5.0, false, Color.BLUE, 1024, 1024);
        OutputStream imageStream = new BufferedOutputStream(new FileOutputStream(new File("weights_solid.png")));
        service.getWeightsImageStream(request, imageStream);
        //service.getWeightsImageStream(/*"krakow"*//*"london_189"*/"berlin_176", 50.0, null, 5.0, false, PaletteUtil.readPalette(new File("gradient.txt")), imageStream);
        imageStream.flush();
        imageStream.close();

        List<Integer[]> palette = PaletteUtil.readPalette(new File("gradient.txt"));
        OutputStream paletteOut = new FileOutputStream(new File("palette_icon.png"));
        PaletteUtil.generatePaletteIcon(palette, paletteOut);
        paletteOut.flush();
        paletteOut.close();

        imageStream = new BufferedOutputStream(new FileOutputStream(new File("weights_palette.png")));
        request = new WeightsMapRequest("berlin_176", 5.0, false, palette, 1024, 1024);
        service.getWeightsImageStream(request, imageStream);
        imageStream.flush();
        imageStream.close();

        imageStream = new BufferedOutputStream(new FileOutputStream(new File("weights_palette_threshold.png")));
        request = new WeightsMapRequest("berlin_176", 5.0, false, palette
                , 50.0, null, false, 1024, 1024);
        service.getWeightsImageStream(request, imageStream);
        imageStream.flush();
        imageStream.close();

        imageStream = new BufferedOutputStream(new FileOutputStream(new File("weights_palette_scaled.png")));
        request = new WeightsMapRequest("berlin_176", 5.0, false, palette
                , 50.0, null, true, 1024, 1024);
        service.getWeightsImageStream(request, imageStream);
        imageStream.flush();
        imageStream.close();

        OutputStream kmlStream = new BufferedOutputStream(new FileOutputStream(new File("weights.kml")));
        service.getWeightsKmlStream(/*"krakow"*//*"london_189"*/"berlin_176", "weights.kml", "weights.png", kmlStream);
        kmlStream.flush();
        kmlStream.close();
        System.exit(0);
    }
}
