
/**
 *
 * @created Jul 9, 2010
 * @author Pavel Danchenko
 */
public class DatasetMetadata {
    private double north;
    private double south;
    private double east;
    private double west;
    private double minWeight;
    private double maxWeight;
    private double minFilteredWeight;
    private double maxFilteredWeight;

    public double getNorth() {
        return north;
    }
    public void setNorth(double north) {
        this.north = north;
    }

    public double getSouth() {
        return south;
    }
    public void setSouth(double south) {
        this.south = south;
    }

    public double getEast() {
        return east;
    }
    public void setEast(double east) {
        this.east = east;
    }

    public double getWest() {
        return west;
    }
    public void setWest(double west) {
        this.west = west;
    }

    public double getMinWeight() {
        return minWeight;
    }
    public void setMinWeight(double minWeight) {
        this.minWeight = minWeight;
    }

    public double getMaxWeight() {
        return maxWeight;
    }
    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public double getMinFilteredWeight() {
        return minFilteredWeight;
    }
    public void setMinFilteredWeight(double minFilteredWeight) {
        this.minFilteredWeight = minFilteredWeight;
    }

    public double getMaxFilteredWeight() {
        return maxFilteredWeight;
    }
    public void setMaxFilteredWeight(double maxFilteredWeight) {
        this.maxFilteredWeight = maxFilteredWeight;
    }
}
