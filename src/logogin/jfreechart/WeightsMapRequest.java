import java.awt.Paint;
import java.util.List;


/**
 *
 * @created 21 лип. 2010
 * @author Pavel Danchenko
 */
public class WeightsMapRequest {

    private String locationId;
    private Double minThreshold;
    private Double maxThreshold;
    private double pointRadius;
    private boolean showAxes;
    private List<Integer[]> palette;
    private boolean scalePaletteDomain;
    private Paint pointPaint;
    private int imageWidth;
    private int imageHeight;

    public WeightsMapRequest(String locationId, double pointRadius, boolean showAxes
            , Paint pointPaint, int imageWidth, int imageHeight) {
        this.locationId = locationId;
        this.minThreshold = null;
        this.maxThreshold = null;
        this.palette = null;
        this.scalePaletteDomain = false;
        this.pointRadius = pointRadius;
        this.showAxes = showAxes;
        this.pointPaint = pointPaint;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public WeightsMapRequest(String locationId, double pointRadius, boolean showAxes
            , List<Integer[]> palette, int imageWidth, int imageHeight) {
        this.locationId = locationId;
        this.minThreshold = null;
        this.maxThreshold = null;
        this.palette = palette;
        this.scalePaletteDomain = false;
        this.pointRadius = pointRadius;
        this.showAxes = showAxes;
        this.pointPaint = null;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public WeightsMapRequest(String locationId, double pointRadius, boolean showAxes
            , List<Integer[]> palette, Double minThreshold, Double maxThreshold, boolean scalePaletteDomain, int imageWidth, int imageHeight) {
        this.locationId = locationId;
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;
        this.palette = palette;
        this.scalePaletteDomain = scalePaletteDomain;
        this.pointRadius = pointRadius;
        this.showAxes = showAxes;
        this.pointPaint = null;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public String getLocationId() {
        return locationId;
    }

    public Double getMinThreshold() {
        return minThreshold;
    }

    public Double getMaxThreshold() {
        return maxThreshold;
    }

    public double getPointRadius() {
        return pointRadius;
    }

    public boolean isShowAxes() {
        return showAxes;
    }

    public List<Integer[]> getPalette() {
        return palette;
    }

    public boolean isScalePaletteDomain() {
        return scalePaletteDomain;
    }

    public Paint getPointPaint() {
        return pointPaint;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }
}
