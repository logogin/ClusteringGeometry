import java.awt.Color;
import java.awt.Paint;
import java.awt.color.ColorSpace;
import java.util.List;

import org.jfree.chart.renderer.PaintScale;


/**
 *
 * @created Jul 8, 2010
 * @author Pavel Danchenko
 */
public class GradientPaintScale implements PaintScale {

    private List<Integer[]> palette;
    private double lowerBound;
    private double upperBound;

    public GradientPaintScale(List<Integer[]> palette, double lowerBound, double upperBound) {
        this.palette = palette;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public double getLowerBound() {
        return 0;
    }

    @Override
    public Paint getPaint(double value) {
        int index = (int)Math.ceil((value - lowerBound)/(upperBound - lowerBound)*(palette.size() - 1));
        Integer[] color = palette.get(index);
        return new Color(color[0], color[1], color[2]);
        //return Color.getHSBColor((float)value/514.155571919942f, 0.85f, 1.0f);
    }

    @Override
    public double getUpperBound() {
        return 0;
    }

}
