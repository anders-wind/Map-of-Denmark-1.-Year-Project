/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * If you are looking for more details on the methods of this class, please
 * refer to the Interface that this class implements<p>
 *
 * SQLExceptions are handled by each and every method in this class.<p>
 *
 * NullPointerExceptions are not handled, unless explicitly stated.<p>
 *
 * @author Aleksandar Jonovic
 */
public class DatabaseHandler implements DatabaseInterface {

    //DB & SQL fields
    private static final String user = "jonovic_dk";
    private static final String pass = "Jegeradministratorher123";
    private static final String jdbcurl = "jdbc:sqlserver://db.jonovic.dk;databaseName=jonovic_dk_db;";

    ArrayList<PreparedStatement> pstatements = new ArrayList();
    ArrayList<ResultSet> resultsets = new ArrayList();
    ArrayList<Connection> cons = new ArrayList();
    ArrayList<SQLWarning> warnings = new ArrayList();
    ComboPooledDataSource cpds = new ComboPooledDataSource();

    //Fields for Streets, Edges and Nodes.
    ArrayList<Street> streets = new ArrayList<>();
    ArrayList<Edge> edges = new ArrayList<>();
    ArrayList<Node> nodes = new ArrayList<>();

    /**
     * Constructor for this object. For more detail about the API methods of
     * this class, please refer to:
     * {@link MapOfDenmark.database.DatabaseInterface DatabaseInterface}
     */
    protected DatabaseHandler() {
        try {
            //contructor method to initiate (combo)DataSource for pooled connections on spawning an object.
            //initiateDataSource();
            initiateDataSource();
        } catch (SQLException ex) {
            throw new RuntimeException("Something went wrong while initializing the DataSource", ex);
        }

    }

    private void initiateDataSource() throws SQLException {
        // initiates the connection to the database, and sets the basic parameters for the connection pooling.

        cpds.setUser(user);
        cpds.setPassword(pass);
        cpds.setJdbcUrl(jdbcurl);
        cpds.setMinPoolSize(1);
        cpds.setAcquireIncrement(3);
        cpds.setMaxPoolSize(100);
        cpds.setIdleConnectionTestPeriod(10);
        cpds.setTestConnectionOnCheckin(true);
        cpds.setMaxIdleTimeExcessConnections(5);

    }

    private Connection getConnection() throws SQLException {

        return cpds.getConnection();

    }

    /**
     * This method is borrowed from:<br>
     *
     * http://docs.oracle.com/javase/tutorial/jdbc/basics/sqlexception.html<p>
     *
     * Direct download link to original source code:<br>
     * http://docs.oracle.com/javase/tutorial/jdbc/basics/examples/zipfiles/JDBCTutorial.zip<p>
     *
     * It provides a way of handling Exceptions, while ignoring common errors
     * which are not critical for application function.
     *
     *
     * @param sqlState SQL-state value
     * @return Boolean value TRUE or FALSE
     */
    public static boolean ignoreSQLException(String sqlState) {
        if (sqlState == null) {
            //System.out.println("The SQL state is not defined!");
            return false;
        }
        // X0Y32: Jar file already exists in schema
        if (sqlState.equalsIgnoreCase("X0Y32")) {
            return true;
        }
        // 42Y55: Table already exists in schema
        if (sqlState.equalsIgnoreCase("42Y55")) {
            return true;
        }
        return false;
    }

    /**
     * This method is borrowed from:<br>
     *
     * http://docs.oracle.com/javase/tutorial/jdbc/basics/sqlexception.html<p>
     *
     * Direct download link to original source code:<br>
     * http://docs.oracle.com/javase/tutorial/jdbc/basics/examples/zipfiles/JDBCTutorial.zip<p>
     *
     * It provides a way of handling Exceptions, while ignoring common errors
     * which are not critical for application function.
     *
     *
     * @param ex
     */
    public static void printSQLException(SQLException ex) {

        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                if (ignoreSQLException(
                        ((SQLException) e).
                        getSQLState()) == false) {

                    e.printStackTrace(System.err);
                    System.err.println("SQLState: "
                            + ((SQLException) e).getSQLState());

                    System.err.println("Error Code: "
                            + ((SQLException) e).getErrorCode());

                    System.err.println("Message: " + e.getMessage());

                    Throwable t = ex.getCause();
                    while (t != null) {
                        //System.out.println("Cause: " + t);
                        t = t.getCause();
                    }
                }
            }
        }
    }

    private void executeUpdate(PreparedStatement pstatement) {
        //SET method, for putting data into the database.
        try {
            //Execute the prepared statement.
            pstatement.executeUpdate();

        } catch (SQLException ex) {
            printSQLException(ex);
        }
    }

    private ResultSet executeQuery(PreparedStatement pstatement) throws SQLException {
        //GET method for getting data from the database.
        ResultSet results = pstatement.executeQuery();
        return results;
    }

    /**
     *
     */
    private void getNumCon() {
        //Maintenance debug method, for getting all number of alive connections and connection pools - use to debug current state of the pool.
        try {
            System.out.println("Number of Connections: " + cpds.getNumConnections());
            System.out.println("Number of Idle Connections: " + cpds.getNumIdleConnections());
            System.out.println("Number of Busy Connections: " + cpds.getNumBusyConnections());
            System.out.println("Number of Connection Pools: " + cpds.getNumUserPools());
            System.out.println("");
        } catch (SQLException ex) {
            printSQLException(ex);
        }
    }

    private void closeConnection(ArrayList<Connection> cons, ArrayList<PreparedStatement> pstatements, ArrayList<ResultSet> resultsets) {
        //This is where all the "tidy'd" up Connections, preparedStatements and ResultSets go to die (and get closed).
        try {
            //Not all methods produce ResulSets, so check these first, and weed out.
            if (resultsets != null) {
                for (ResultSet results : resultsets) {
                    results.close();

                }
                this.resultsets.removeAll(resultsets);
            }
            //All methods create Connections and preparedStatements, so collect these, close them and destroy them.
            if (cons != null && pstatements != null) {

                for (PreparedStatement pstatement : pstatements) {
                    pstatement.close();

                }

                for (Connection con : cons) {
                    con.close();
                }

                this.pstatements.removeAll(pstatements);
                this.cons.removeAll(cons);

            }

        } catch (SQLException ex) {
            printSQLException(ex);
        }
    }

    public ArrayList<Street> getStreets() {

        try {

            String sql = "SELECT FNODE#, TNODE#, LENGTH, VEJKODE FROM jonovic_dk_db.dbo.edges;";
            Long time = System.currentTimeMillis();
            Connection con = cpds.getConnection();

            PreparedStatement pstatement = con.prepareStatement(sql);

            ResultSet rs = executeQuery(pstatement);
            time -= System.currentTimeMillis();
            int streetid;

            while (rs.next()) {
                int fromnode = rs.getInt(1);
                int tonode = rs.getInt(2);
                int length = rs.getInt(3);
                streetid = rs.getInt(4);

                int currentStreetId = streetIdExists(streetid);
                if (currentStreetId != 0) {
                    Street s = streets.get(currentStreetId);
                    s.addEdge(new Edge(getNode(fromnode), getNode(tonode), length));
                } else {
                    // ArrayList<Edge>
                    //streets.add(new Street(streetid,new ArrayList<Edge>()));
                }
            }

            //Tidy up the conneciton
            cons.add(con);
            pstatements.add(pstatement);
            resultsets.add(rs);

            System.out.println("Time spent fetching elements: " + -time * 0.001 + " seconds...");

        } catch (SQLException ex) {
            printSQLException(ex);
        } finally {
            closeConnection(cons, pstatements, resultsets);
        }

        return streets;
    }

    private int streetIdExists(int id) {

        for (Street street : streets) {
            if (street.getID() == id) {
                return streets.indexOf(street);
            }
        }

        return 0;
    }

    private Node getNode(int id) {
        Node node = null;
        try {
            String sql = "SELECT \"X-COORD\", \"Y-COORD\" FROM [jonovic_dk_db].[dbo].[nodes] WHERE \"KDV-ID\" = ?;";
            Connection con = cpds.getConnection();

            PreparedStatement pstatement = con.prepareStatement(sql);
            pstatement.setInt(1, id);
            ResultSet rs = executeQuery(pstatement);

            while (rs.next()) {
                node = new Node(rs.getDouble(1), rs.getDouble(2), id);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return node;
    }

    @Override
    public String getString() {
        String node = "";
        try {
            Long time = System.currentTimeMillis();
            String sql = "SELECT * FROM [jonovic_dk_db].[dbo].[road2id];";
            Connection con = cpds.getConnection();

            PreparedStatement pstatement = con.prepareStatement(sql);
            ResultSet rs = executeQuery(pstatement);
            time -= System.currentTimeMillis();

            System.out.println("Time spent fetching elements: " + -time * 0.001 + " seconds...");
            int i = 0;
            while (rs.next()) {
                i++;
                
                System.out.println(getNodeIDs(rs.getString(1),rs.getInt(2))[0]);
                System.out.println(getNodeIDs(rs.getString(1),rs.getInt(2))[1]);

            }
            System.out.println(i + " elements fetched.");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return node;
    }

    @Override
    public double[] getNodeIDs(String vejnavn, int vejkode) {
        double[] nodeIDs = new double[2];
        try {
            Connection con = cpds.getConnection();
            
            String sql = "SELECT TNODE#, FNODE# FROM [jonovic_dk_db].[dbo].[edges] WHERE VEJNAVN = ? AND VEJKODE = ?;";
            PreparedStatement pstatement = con.prepareStatement(sql);
            pstatement.setString(1, vejnavn);
            pstatement.setInt(2, vejkode);
            ResultSet rs = executeQuery(pstatement);
            while(rs.next()){
                nodeIDs[0] = rs.getFloat(1);
                nodeIDs[1] = rs.getFloat(2);
            }

        } catch (SQLException ex){
            printSQLException(ex);
        }
        return nodeIDs;

    }

    @Override
    public String getEdges() {
        String s = "";

        return s;
    }

}
