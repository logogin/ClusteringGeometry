import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleEdge;


/**
 *
 * @created Jul 7, 2010
 * @author Pavel Danchenko
 */
public class ZColorXYDotRenderer extends XYDotRenderer {

    private PaintScale paintScale;

    public ZColorXYDotRenderer(PaintScale paintScale) {
        super();
        this.paintScale = paintScale;
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color
     *              information etc).
     * @param domainAxis  the domain (horizontal) axis.
     * @param rangeAxis  the range (vertical) axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot
     *                        (<code>null</code> permitted).
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2,
                         XYItemRendererState state,
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series,
                         int item,
                         CrosshairState crosshairState,
                         int pass) {
        XYZDataset xyzDataset = (XYZDataset)dataset;

        // do nothing if item is not visible
        if (!getItemVisible(series, item)) {
            return;
        }

        // get the data point...
        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        double adjx = (getDotWidth() - 1) / 2.0;
        double adjy = (getDotHeight() - 1) / 2.0;
        if (!Double.isNaN(y)) {
            RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
            double transX = domainAxis.valueToJava2D(x, dataArea,
                    xAxisLocation) - adjx;
            double transY = rangeAxis.valueToJava2D(y, dataArea, yAxisLocation)
                    - adjy;

            //g2.setPaint(paintScale.getPaint(xyzDataset.getZValue(series, item)));
            g2.setPaint(Color.getHSBColor((float)xyzDataset.getZValue(series, item)/514.155571919942f, 0.85f, 1.0f));
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                g2.fillRect((int) transY, (int) transX, getDotHeight(),
                        getDotWidth());
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                g2.fillRect((int) transX, (int) transY, getDotWidth(),
                        getDotHeight());
            }

            int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
            int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
            updateCrosshairValues(crosshairState, x, y, domainAxisIndex,
                    rangeAxisIndex, transX, transY, orientation);
        }

    }
}
