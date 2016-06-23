import java.awt.Paint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.ui.RectangleInsets;

import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.KmlFactory;


/**
 *
 * @created Apr 5, 2011
 * @author Pavel Danchenko
 */
public class ClusteringGeometry {

    public List<double[][]> createSectorTriangles(double[] center, double radius, double[] vector, int numOfSectors) {
        List<double[][]> triangles = new ArrayList<double[][]>(numOfSectors);
        double sectorAngle = Math.PI / numOfSectors;
        double[] previousVector = rotateVector(vector, -Math.PI / 4);
        double[] previousEdge = computePointAtDirection(center, previousVector, radius);
        for (int i = 1; i < numOfSectors; i++) {
            double[] nextVector = rotateVector(previousVector, sectorAngle);
            double[] nextEdge = computePointAtDirection(center, nextVector, radius);
            triangles.add(createTriangle(center, nextEdge, previousEdge));
            previousVector = nextVector;
            previousEdge = nextEdge;
        }
        return triangles;
    }

    /**
     *    |cosa -sina|
     * R =|          |
     *    |sina  cosa|
     */
    public double[] rotateVector(double[] vector, double angle) {
        double sina = Math.sin(angle);
        double cosa = Math.cos(angle);
        return new double[] {vector[0] * cosa - vector[1] * sina, vector[0] * sina + vector[1] * cosa};
    }

    /**
     * r = sqrt(vx^2 + vy^2)
     * d/r=x/vx=y/vy
     * x = px + d*vx/r
     * y = py + d*vy/r
     */
    public double[] computePointAtDirection(double[] point, double[] vector, double distance) {
        double r = Math.sqrt(vector[0]*vector[0] + vector[1]*vector[1]);
        double ratio = distance / r;
        return new double[] {point[0] + vector[0] * ratio, point[1] + vector[1] * ratio};
    }

    public double[][] createTriangle(double[] edge0, double[] edge1, double[] edge2) {
        return new double[][] {edge0, edge1, edge2};
    }

    public double[] computeMidPoint(double[] p0, double[] p1) {
        return new double[] {0.5 * (p0[0] + p1[0]), 0.5 * (p0[1] + p1[1])};
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Kml kml = KmlFactory.createKml();
        kml.createAndSetFolder()
        .withName("Geometries").createAndAddPlacemark()
        .withName("Point 1").createAndSetPoint().addToCoordinates(9.109039, 47.680645);
        kml.marshal(new File("geometry.kml"));

        DefaultXYDataset xyDataset = new DefaultXYDataset();
        xyDataset.addSeries("photos", new double[][] {{9.109039}, {47.680645}});

        JFreeChart chart =
            ChartFactory.createScatterPlot(
                    "", "easting", "northing", xyDataset, PlotOrientation.VERTICAL, false, false, false);

        chart.setBackgroundPaint(null);
        chart.setTitle((String)null);
        //chart.getTitle().setVisible(false);
        chart.setBorderVisible(false);

//        XYShapeRenderer renderer = new XYShapeRenderer();
//        double pointRadius = chartRequest.getPointRadius();
//        if ( pointRadius > 1.0 ) {
//            renderer.setBaseShape(new Ellipse2D.Double(-pointRadius/2, -pointRadius/2, pointRadius, pointRadius));
//        } else {
//            renderer.setBaseShape(new Rectangle2D.Double(0, 0, 1, 1));
//        }

//        if ( null != chartRequest.getPalette() ) {
//
//            if ( chartRequest.isScalePaletteDomain() ) {
//                renderer.setPaintScale(new GradientPaintScale(chartRequest.getPalette(), metadata.getMinFilteredWeight(), metadata.getMaxFilteredWeight()));
//            } else {
//                renderer.setPaintScale(new GradientPaintScale(chartRequest.getPalette(), metadata.getMinWeight(), metadata.getMaxWeight()));
//            }
//        } else {
//            final Paint paint = chartRequest.getPointPaint();
//            renderer.setPaintScale(new PaintScale() {
//                @Override
//                public double getUpperBound() {
//                    return 0;
//                }
//                @Override
//                public double getLowerBound() {
//                    return 0;
//                }
//
//                @Override
//                public Paint getPaint(double value) {
//                    return paint;
//                }
//
//            });
//        }
//        chart.getXYPlot().setRenderer(renderer);

        chart.getXYPlot().setBackgroundPaint(null);
        if ( false ) {
            chart.getXYPlot().getDomainAxis().setVisible(false);
            chart.getXYPlot().setDomainGridlinesVisible(false);
            chart.getXYPlot().getRangeAxis().setVisible(false);
            chart.getXYPlot().setRangeGridlinesVisible(false);
        }

        //chart.setPadding(RectangleInsets.ZERO_INSETS);
        chart.getXYPlot().setInsets(RectangleInsets.ZERO_INSETS);
        //chart.getXYPlot().setAxisOffset(RectangleInsets.ZERO_INSETS);
        chart.getXYPlot().setOutlineVisible(false);

        //chart.getXYPlot().getDomainAxis().setAutoRange(false);
        //chart.getXYPlot().getDomainAxis().setRange(metadata.getWest(), metadata.getEast());
        //chart.getXYPlot().getDomainAxis().setRange(422977, 424856);
        //chart.getXYPlot().getRangeAxis().setAutoRange(false);
        //chart.getXYPlot().getRangeAxis().setRange(metadata.getSouth(), metadata.getNorth());
        //chart.getXYPlot().getRangeAxis().setRange(5544028, 5547106);

        ChartRenderingInfo info = new ChartRenderingInfo();
        ChartUtilities.writeChartAsPNG(new FileOutputStream("geometry.png"), chart, 1024, 1024, info);

        System.exit(0);
    }

}
