
/**
 *
 * @created Jul 9, 2010
 * @author Pavel Danchenko
 */
public class DatabaseDataset {
    private double[][] dataset;
    private double north;
    private double south;
    private double east;
    private double west;
    private double maxWeight;

    public double[][] getDataset() {
        return dataset;
    }
    public void setDataset(double[][] dataset) {
        this.dataset = dataset;
    }

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

    public double getMaxWeight() {
        return maxWeight;
    }
    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }
}
