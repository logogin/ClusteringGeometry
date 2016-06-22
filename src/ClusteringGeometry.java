import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.KmlFactory;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.LinearRing;
import de.micromata.opengis.kml.v_2_2_0.Placemark;


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

    public static double[][] createTriangle(double[] edge0, double[] edge1, double[] edge2) {
        return new double[][] {edge0, edge1, edge2};
    }

    public double[] computeMidPoint(double[] p0, double[] p1) {
        return new double[] {0.5 * (p0[0] + p1[0]), 0.5 * (p0[1] + p1[1])};
    }

    /**
     * http://www.movable-type.co.uk/scripts/latlong.html
     *
     * Computes mid-point
     * @param p0 first point {lat, lon}
     * @param p1 second point {lat, lon}
     * @return {lat, lon} in decimal degrees
     */
    public static double[] computeSphericalMidPoint(double[] p0, double[] p1) {
        double lat1 = Math.toRadians(p0[0]);
        double lon1 = Math.toRadians(p0[1]);
        double lat2 = Math.toRadians(p1[0]);
        double dLon = Math.toRadians(p1[1] - p0[1]);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);

        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2)
                    , Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By*By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        return new double[] {Math.toDegrees(lat3), Math.toDegrees(lon3)};
    }

    public static double computeBearing(double[] p0, double[] p1) {
        double lat1 = Math.toRadians(p0[0]);
        double lat2 = Math.toRadians(p1[0]);
        double dLon = Math.toRadians(p1[1] - p0[1]);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1)*Math.sin(lat2) -
                Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
        double brng = Math.atan2(y, x);

        return (Math.toDegrees(brng) + 360) % 360;
    }

    public static double computeFinalBearing(double[] p0, double[] p1) {
        return (computeBearing(p1, p0) + 180) % 360;
    }

    /**
     * Computes point at bearing
     * @param point{lat, lon}
     * @param bearing in decimal degrees
     * @param distance in meters
     * @return {lat, lon} in decimla degrees
     */
    public static double[] computeRhumbDestinationPoint(double[] point, double bearing, double distance) {
        final double  R = 6371009;  // Earth's mean radius in km
        double d = distance/R;  // d = angular distance covered on earth's surface
        double lat1 = Math.toRadians(point[0]);
        double lon1 = Math.toRadians(point[1]);
        double brng = Math.toRadians(bearing);

        double lat2 = lat1 + d * Math.cos(brng);
        double dLat = lat2 - lat1;
        double dPhi = Math.log(Math.tan(lat2/2 + Math.PI/4) / Math.tan(lat1/2 + Math.PI/4));
        double q = (!Double.isNaN(dLat/dPhi)) ? dLat/dPhi : Math.cos(lat1);  // E-W line gives dPhi=0
        double dLon = d*Math.sin(brng)/q;
        // check for some daft bugger going past the pole
        if ( Math.abs(lat2) > Math.PI/2 ) {
            lat2 = lat2 > 0 ? Math.PI - lat2 : -(Math.PI - lat2);
        }
        double lon2 = (lon1 + dLon + 3*Math.PI)%(2*Math.PI) - Math.PI;

        return new double[] {Math.toDegrees(lat2), Math.toDegrees(lon2)};
    }

    public static double[][] createPolygon(double[] ... edges) {
        return edges;
    }

    /**
     * @param center{lat, lon}
     * @param radius in meters
     * @param bearing in decimla degrees
     * @param numOfSectors
     * @return list of triangles with edges {p0, p1, p2}
     */
    public static List<double[][]> createSphericalSectorTriangles(double[] center, double radius, double bearing, int numOfSectors) {
        List<double[][]> triangles = new ArrayList<double[][]>(numOfSectors);
        double sectorAngle = 180 / numOfSectors;
        double previousBearing = bearing - 90;
        double[] previousEdge = computeRhumbDestinationPoint(center, previousBearing, radius);
        for (int i = 0; i < numOfSectors; i++) {
            double nextBearing = previousBearing + sectorAngle;
            double[] nextEdge = computeRhumbDestinationPoint(center, nextBearing, radius);
            triangles.add(createPolygon(center, nextEdge, previousEdge));
            previousBearing = nextBearing;
            previousEdge = nextEdge;
        }
        return triangles;
    }

    /**
     * @param center{lat, lon}
     * @param radius in meters
     * @param lookAheadRadius in meters
     * @param bearing in decimal degrees
     * @param numOfSectors
     * @return list of polygons with edges;
     */
    public static List<double[][]> createSphericalLookAheadPolygons(double[] center, double radius, double lookAheadRadius, double bearing, int numOfSectors) {
        List<double[][]> triangles = new ArrayList<double[][]>(numOfSectors);
        double sectorAngle = 180 / numOfSectors;
        double previousBearing = bearing - 90;
        double[] previousEdge = computeRhumbDestinationPoint(center, previousBearing, radius);
        double[] previousLookAheadEdge = computeRhumbDestinationPoint(center, previousBearing, lookAheadRadius);
        for (int i = 0; i < numOfSectors; i++) {
            double nextBearing = previousBearing + sectorAngle;
            double[] nextEdge = computeRhumbDestinationPoint(center, nextBearing, radius);
            double[] nextLookAheadEdge = computeRhumbDestinationPoint(center, nextBearing, lookAheadRadius);
            triangles.add(createPolygon(nextEdge, nextLookAheadEdge, previousLookAheadEdge, previousEdge));
            previousBearing = nextBearing;
            previousEdge = nextEdge;
            previousLookAheadEdge = nextLookAheadEdge;
        }
        return triangles;
    }

    public LinearRing createKmlPolygon(Folder folder, double[][] edges) {
        LinearRing ring = folder.createAndAddPlacemark().createAndSetLinearRing();
        for ( double[] edge : edges ) {
            ring.addToCoordinates(edge[1], edge[0]);
        }
        ring.addToCoordinates(edges[0][1], edges[0][0]);
        return ring;
    }

    public LineString createKmlPath(Folder folder, double[] ... points) {
        LineString string = folder.createAndAddPlacemark().createAndSetLineString();
        for ( double[] point : points ) {
            string.addToCoordinates(point[1], point[0]);
        }
        return string;
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        ClusteringGeometry geometry = new ClusteringGeometry();
        double[] center = new double[] {47.680645, 9.109039};
        double radius = 100;
        double lookAheadRadius = 120;
        double initalBearing = 45;
        int numOfSegments = 5;
        Kml kml = KmlFactory.createKml();
        Folder root = kml.createAndSetFolder().withName("Geometries");

        List<double[][]> triangles = createSphericalSectorTriangles(center, radius, initalBearing, numOfSegments);
        Folder folder = root.createAndAddFolder().withName("Triangles #1");
        for ( double[][] triangle : triangles ) {
            geometry.createKmlPolygon(folder, triangle);
        }

        List<double[][]> polygons = createSphericalLookAheadPolygons(center, radius, lookAheadRadius, initalBearing, numOfSegments);
        folder = root.createAndAddFolder().withName("Look ahead Polygons #1");
        for ( double[][] polygon : polygons ) {
            geometry.createKmlPolygon(folder, polygon);
        }

        double[] midPoint = computeSphericalMidPoint(polygons.get(1)[1], polygons.get(1)[2]);
        double bearing = computeFinalBearing(center, midPoint);
        triangles = createSphericalSectorTriangles(midPoint, radius, bearing, numOfSegments);
        folder = root.createAndAddFolder().withName("Triangles #2");
        for ( double[][] triangle : triangles ) {
            geometry.createKmlPolygon(folder, triangle);
        }

        polygons = createSphericalLookAheadPolygons(midPoint, radius, lookAheadRadius, bearing, numOfSegments);
        folder = root.createAndAddFolder().withName("Look ahead Polygons #2");
        for ( double[][] polygon : polygons ) {
            geometry.createKmlPolygon(folder, polygon);
        }

        folder = root.createAndAddFolder().withName("Path");
        geometry.createKmlPath(folder, center, midPoint, computeSphericalMidPoint(polygons.get(3)[1], polygons.get(3)[2]));

        kml.marshal(new File("geometry.kml"));
        System.exit(0);
    }

}
