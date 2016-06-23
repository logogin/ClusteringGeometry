import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ShapeUtilities;

import sun.security.action.GetLongAction;

import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.GroundOverlay;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.KmlFactory;
import de.micromata.opengis.kml.v_2_2_0.LatLonBox;


/**
 *
 * @created Jul 8, 2010
 * @author Pavel Danchenko
 */
public class WeightsMapService {

    private WeightsDatasetDAO dao;

    public WeightsMapService() throws Exception {
        dao = new WeightsDatasetDAO();
    }

    public void getWeightsImageStream(WeightsMapRequest chartRequest,
            /*String locationId, Double minThreshold, Double maxThreshold, Double pointRadius, Boolean showAxes, List<Integer[]> palette, */OutputStream out) throws IOException {
        DefaultXYZDataset xyzDataset = new DefaultXYZDataset();
//        double[][] databaseDataset = dao.getDataset(chartRequest.getLocationId());
//        DatasetMetadata metadata = dao.getMetadata(chartRequest.getLocationId());

        double[][] databaseDataset = dao.getDataset(chartRequest.getLocationId()
                , chartRequest.getMinThreshold(), chartRequest.getMaxThreshold());
        DatasetMetadata metadata = dao.getMetadata(chartRequest.getLocationId()
                , chartRequest.getMinThreshold(), chartRequest.getMaxThreshold());
//        if ( null != minThreshold || null != maxThreshold ) {
//            databaseDataset = filterDataset(databaseDataset
//                    , null != minThreshold ? minThreshold : 0.0
//                    , null != maxThreshold ? maxThreshold : metadata.getMaxWeight());
//        }

//        double min = databaseDataset[2][0];
//        double max = databaseDataset[2][0];
//        for (int i=0; i<databaseDataset[2].length; i++) {
//            if (min > databaseDataset[2][i] ) {
//                min = databaseDataset[2][i];
//            }
//            if ( max < databaseDataset[2][i] ) {
//                max = databaseDataset[2][i];
//            }
//        }

        xyzDataset.addSeries("weights", databaseDataset);
        JFreeChart chart =
            ChartFactory.createScatterPlot(
                    "", "easting", "northing", xyzDataset, PlotOrientation.VERTICAL, false, false, false);
        chart.setBackgroundPaint(null);
        chart.setTitle((String)null);
        //chart.getTitle().setVisible(false);
        chart.setBorderVisible(false);

        XYShapeRenderer renderer = new XYShapeRenderer();
        double pointRadius = chartRequest.getPointRadius();
        if ( pointRadius > 1.0 ) {
            renderer.setBaseShape(new Ellipse2D.Double(-pointRadius/2, -pointRadius/2, pointRadius, pointRadius));
        } else {
            renderer.setBaseShape(new Rectangle2D.Double(0, 0, 1, 1));
        }

        if ( null != chartRequest.getPalette() ) {

            if ( chartRequest.isScalePaletteDomain() ) {
                renderer.setPaintScale(new GradientPaintScale(chartRequest.getPalette(), metadata.getMinFilteredWeight(), metadata.getMaxFilteredWeight()));
            } else {
                renderer.setPaintScale(new GradientPaintScale(chartRequest.getPalette(), metadata.getMinWeight(), metadata.getMaxWeight()));
            }
        } else {
            final Paint paint = chartRequest.getPointPaint();
            renderer.setPaintScale(new PaintScale() {
                @Override
                public double getUpperBound() {
                    return 0;
                }
                @Override
                public double getLowerBound() {
                    return 0;
                }

                @Override
                public Paint getPaint(double value) {
                    return paint;
                }

            });
        }
        chart.getXYPlot().setRenderer(renderer);

        chart.getXYPlot().setBackgroundPaint(null);
        if ( !chartRequest.isShowAxes() ) {
            chart.getXYPlot().getDomainAxis().setVisible(false);
            chart.getXYPlot().setDomainGridlinesVisible(false);
            chart.getXYPlot().getRangeAxis().setVisible(false);
            chart.getXYPlot().setRangeGridlinesVisible(false);
        }

        //chart.setPadding(RectangleInsets.ZERO_INSETS);
        chart.getXYPlot().setInsets(RectangleInsets.ZERO_INSETS);
        //chart.getXYPlot().setAxisOffset(RectangleInsets.ZERO_INSETS);
        chart.getXYPlot().setOutlineVisible(false);

        chart.getXYPlot().getDomainAxis().setAutoRange(false);
        chart.getXYPlot().getDomainAxis().setRange(metadata.getWest(), metadata.getEast());
        //chart.getXYPlot().getDomainAxis().setRange(422977, 424856);
        chart.getXYPlot().getRangeAxis().setAutoRange(false);
        chart.getXYPlot().getRangeAxis().setRange(metadata.getSouth(), metadata.getNorth());
        //chart.getXYPlot().getRangeAxis().setRange(5544028, 5547106);


        ChartRenderingInfo info = new ChartRenderingInfo();
        ChartUtilities.writeChartAsPNG(out, chart, chartRequest.getImageWidth(), chartRequest.getImageHeight(), info);

//        ValueAxis xAxis = chart.getXYPlot().getDomainAxis();
//        ValueAxis yAxis = chart.getXYPlot().getRangeAxis();
//        Rectangle2D dataArea = info.getPlotInfo().getDataArea();
//        RectangleEdge domainEdge = chart.getXYPlot().getDomainAxisEdge();
//        RectangleEdge rangeEdge = chart.getXYPlot().getRangeAxisEdge();
//
//        Boundaries latLonBoundaries = dao.getLatLonBoundaries(locationId);
//        LatLonBox box = KmlFactory.createLatLonBox()
//        .withNorth(latLonBoundaries.getNorth())
//        .withSouth(latLonBoundaries.getNorth())
//        .withEast(xAxis.java2DToValue(0, dataArea, domainEdge))
//        .withWest(xAxis.java2DToValue(1024, dataArea, domainEdge));
    }

    public void getWeightsKmlStream(String locationId, String kmlName, String iconHref, OutputStream out) throws Exception {
        final Kml kml = KmlFactory.createKml();
        final GroundOverlay groundoverlay = kml.createAndSetGroundOverlay()
        .withName(kmlName)
        .withColor("7dffffff");
        //.withDrawOrder(1);

        groundoverlay.createAndSetIcon()
        .withHref(iconHref)
        //.withRefreshMode(RefreshMode.ON_INTERVAL)
        //.withRefreshInterval(86400d)
        .withViewBoundScale(0.75d);

        DatasetMetadata latLonboundaries = dao.getLatLonBoundaries(locationId);
        //groundoverlay.setLatLonBox(box);
        groundoverlay.createAndSetLatLonBox()
//        .withNorth(50.071243)
//        .withSouth(50.043746)
//        .withEast(19.950345)
//        .withWest(19.923706);
        .withNorth(latLonboundaries.getNorth())
        .withSouth(latLonboundaries.getSouth())
        .withEast(latLonboundaries.getEast())
        .withWest(latLonboundaries.getWest());

        kml.marshal(out);
    }

//    private double[][] filterDataset(double[][] dataset, double minThreshold, double maxThreshold) {
//        int filteredSize = 0;
//        for (int i=0; i<dataset[2].length; i++ ) {
//            if ( minThreshold < dataset[2][i] && dataset[2][i] < maxThreshold ) {
//                filteredSize++;
//            }
//        }
//        if ( 0 == filteredSize ) {
//            return new double[3][];
//        }
//        double filteredDataset[][] = new double[3][filteredSize];
//        int index = 0;
//        for (int i=0; i<dataset[2].length; i++ ) {
//            if ( minThreshold < dataset[2][i] && dataset[2][i] < maxThreshold ) {
//                filteredDataset[0][index] = dataset[0][i];
//                filteredDataset[1][index] = dataset[1][i];
//                filteredDataset[2][index] = dataset[2][i];
//                index++;
//            }
//        }
//        return filteredDataset;
//    }
}
