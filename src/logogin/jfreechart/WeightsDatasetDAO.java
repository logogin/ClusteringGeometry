import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

/**
 *
 * @created Jul 8, 2010
 * @author Pavel Danchenko
 */
public class WeightsDatasetDAO {

    private DataSource dataSource;
    private String tableName;

    public abstract class DatabaseAction<T> {

        public abstract T action(ResultSet rs) throws SQLException;

        public abstract String getSql();

        public void prepareParameters(PreparedStatement stmt) throws SQLException {
        }
    }

    public WeightsDatasetDAO() throws IOException {
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream("service.properties"));
        dataSource = new BasicDataSource();
        BasicDataSource basicDataSource = (BasicDataSource)dataSource;
        basicDataSource.setDriverClassName(props.getProperty("dataset.datasource.driverName"));
        basicDataSource.setUrl(props.getProperty("dataset.datasource.url"));
        basicDataSource.setUsername(props.getProperty("dataset.datasource.username"));
        basicDataSource.setPassword(props.getProperty("dataset.datasource.password"));

        tableName = props.getProperty("dataset.tablename");
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private <T> T executeDatabaseAction(DatabaseAction<T> action) {
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(action.getSql());
            action.prepareParameters(stmt);
            ResultSet rs = stmt.executeQuery();
            T result = action.action(rs);
            stmt.close();
            return result;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        finally {
            try {
                if ( null != conn ) {
                    conn.close();
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

//    public double[][] getDataset(final String locationId) {
//
//        final int size = executeDatabaseAction(new WeightsDatasetDAO.DatabaseAction<Integer>() {
//            @Override
//            public String getSql() {
//                return "select count(row_id) from " + tableName + " where location_id = ?";
//            }
//
//            @Override
//            public void prepareParameters(PreparedStatement stmt) throws SQLException {
//                stmt.setString(1, locationId);
//            }
//
//            @Override
//            public Integer action(ResultSet rs) throws SQLException {
//                rs.next();
//                return rs.getInt(1);
//            }
//        });
//
//        return executeDatabaseAction(new WeightsDatasetDAO.DatabaseAction<double[][]>() {
//            @Override
//            public String getSql() {
//                return "select easting, northing, weight from "
//                + tableName + " where location_id = ? order by weight asc";
//            }
//
//            @Override
//            public void prepareParameters(PreparedStatement stmt) throws SQLException {
//                stmt.setString(1, locationId);
//            }
//
//            @Override
//            public double[][] action(ResultSet rs) throws SQLException {
//                double data[][] = new double[3][size];
//                int index = 0;
//                while( rs.next() ) {
//                    data[0][index] = rs.getDouble("easting");
//                    data[1][index] = rs.getDouble("northing");
//                    data[2][index] = rs.getDouble("weight");
//                    index++;
//                }
//                return data;
//            }
//        });
//    }

    public double[][] getDataset(final String locationId, final Double minThreshold, final Double maxThreshold) {

        final int size = executeDatabaseAction(new WeightsDatasetDAO.DatabaseAction<Integer>() {
            @Override
            public String getSql() {
                String filter = "";
                if ( null != minThreshold ) {
                    filter = " and ( weight > ? ) ";
                }
                if ( null != maxThreshold ) {
                    filter += " and ( weight < ? ) ";
                }
                return "select count(row_id) from " + tableName + " where location_id = ? " + filter;
            }

            @Override
            public void prepareParameters(PreparedStatement stmt) throws SQLException {
                stmt.setString(1, locationId);
                int paramIndex = 1;
                if ( null != minThreshold ) {
                    paramIndex++;
                    stmt.setDouble(paramIndex, minThreshold);
                }
                if ( null != maxThreshold ) {
                    paramIndex++;
                    stmt.setDouble(paramIndex, maxThreshold);
                }
            }

            @Override
            public Integer action(ResultSet rs) throws SQLException {
                rs.next();
                return rs.getInt(1);
            }
        });

        return executeDatabaseAction(new WeightsDatasetDAO.DatabaseAction<double[][]>() {
            @Override
            public String getSql() {
                String filter = "";
                if ( null != minThreshold ) {
                    filter = " and ( weight > ? ) ";
                }
                if ( null != maxThreshold ) {
                    filter += " and ( weight < ? ) ";
                }
                return "select easting, northing, weight from "
                    + tableName + " where location_id = ? "
                    + filter + " order by weight asc";
            }

            @Override
            public void prepareParameters(PreparedStatement stmt) throws SQLException {
                stmt.setString(1, locationId);
                int paramIndex = 1;
                if ( null != minThreshold ) {
                    paramIndex++;
                    stmt.setDouble(paramIndex, minThreshold);
                }
                if ( null != maxThreshold ) {
                    paramIndex++;
                    stmt.setDouble(paramIndex, maxThreshold);
                }
            }

            @Override
            public double[][] action(ResultSet rs) throws SQLException {
                double data[][] = new double[3][size];
                int index = 0;
                while( rs.next() ) {
                    data[0][index] = rs.getDouble("easting");
                    data[1][index] = rs.getDouble("northing");
                    data[2][index] = rs.getDouble("weight");
                    index++;
                }
                return data;
            }
        });
    }

    public DatasetMetadata getMetadata(final String locationId, final Double minThreshold, final Double maxThreshold) {
        return executeDatabaseAction(new WeightsDatasetDAO.DatabaseAction<DatasetMetadata>() {
            @Override
            public String getSql() {
                String filter = "";
                if ( null != minThreshold ) {
                    filter = " and ( weight > ? ) ";
                }
                if ( null != maxThreshold ) {
                    filter += " and ( weight < ? ) ";
                }
                return "select * from (select max(easting) as east" +
                        ", min(easting) as west" +
                        ", max(northing) as north" +
                        ", min(northing) as south" +
                        ", min(weight) as min_weight" +
                        ", max(weight) as max_weight from "
                        + tableName + " where location_id = ?) m1, " +
                                "(select min(weight) as min_filtered_weight, max(weight) as max_filtered_weight from "
                        + tableName + " where location_id = ? " + filter + ") m2";
            }

            @Override
            public void prepareParameters(PreparedStatement stmt) throws SQLException {
                stmt.setString(1, locationId);
                stmt.setString(2, locationId);
                int paramIndex = 2;
                if ( null != minThreshold ) {
                    paramIndex++;
                    stmt.setDouble(paramIndex, minThreshold);
                }
                if ( null != maxThreshold ) {
                    paramIndex++;
                    stmt.setDouble(paramIndex, maxThreshold);
                }
            }

            @Override
            public DatasetMetadata action(ResultSet rs) throws SQLException {
                DatasetMetadata metadata = new DatasetMetadata();
                rs.next();
                metadata.setNorth(rs.getDouble("north"));
                metadata.setSouth(rs.getDouble("south"));
                metadata.setEast(rs.getDouble("east"));
                metadata.setWest(rs.getDouble("west"));
                metadata.setMinWeight(rs.getDouble("min_weight"));
                metadata.setMaxWeight(rs.getDouble("max_weight"));
                metadata.setMinFilteredWeight(rs.getDouble("min_filtered_weight"));
                metadata.setMaxFilteredWeight(rs.getDouble("max_filtered_weight"));
                return metadata;
            }
        });
    }

//    public DatasetMetadata getMetadata(final String locationId) {
//        return executeDatabaseAction(new WeightsDatasetDAO.DatabaseAction<DatasetMetadata>() {
//            @Override
//            public String getSql() {
//                return "select max(easting) as east" +
//                        ", min(easting) as west" +
//                        ", max(northing) as north" +
//                        ", min(northing) as south" +
//                        ", min(weight) as min_weight" +
//                        ", max(weight) as max_weight from "
//                        + tableName + " where location_id = ?";
//            }
//
//            @Override
//            public void prepareParameters(PreparedStatement stmt) throws SQLException {
//                stmt.setString(1, locationId);
//            }
//
//            @Override
//            public DatasetMetadata action(ResultSet rs) throws SQLException {
//                DatasetMetadata metadata = new DatasetMetadata();
//                rs.next();
//                metadata.setNorth(rs.getDouble("north"));
//                metadata.setSouth(rs.getDouble("south"));
//                metadata.setEast(rs.getDouble("east"));
//                metadata.setWest(rs.getDouble("west"));
//                metadata.setMinWeight(rs.getDouble("min_weight"));
//                metadata.setMaxWeight(rs.getDouble("max_weight"));
//                return metadata;
//            }
//        });
//    }

    public DatasetMetadata getLatLonBoundaries(final String locationId) {
        return executeDatabaseAction(new WeightsDatasetDAO.DatabaseAction<DatasetMetadata>() {
            @Override
            public String getSql() {
                return "select max(longitude) as east" +
                        ", min(longitude) as west" +
                        ", max(latitude) as north" +
                        ", min(latitude) as south from " + tableName + " where location_id = ?";
            }

            @Override
            public void prepareParameters(PreparedStatement stmt) throws SQLException {
                stmt.setString(1, locationId);
            }

            @Override
            public DatasetMetadata action(ResultSet rs) throws SQLException {
                DatasetMetadata metadata = new DatasetMetadata();
                rs.next();
                metadata.setNorth(rs.getDouble("north"));
                metadata.setSouth(rs.getDouble("south"));
                metadata.setEast(rs.getDouble("east"));
                metadata.setWest(rs.getDouble("west"));
                return metadata;
            }
        });
    }
}
