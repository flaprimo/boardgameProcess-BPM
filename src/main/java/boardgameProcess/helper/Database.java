package boardgameProcess.helper;

import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class Database {
    private final static Logger LOGGER = Logger.getLogger(Database.class.getName());
    private final static Database INSTANCE = new Database();

    private static JdbcConnectionPool connectionPool;

    private Database() {
        try {
            Properties properties = System.getProperties();
            properties.load(getClass().getClassLoader().getResourceAsStream("database.properties"));

            Class.forName(properties.getProperty("database.driver"));

            connectionPool = JdbcConnectionPool.create(
                    properties.getProperty("database.connection"),
                    properties.getProperty("database.user"),
                    properties.getProperty("database.password"));
        } catch (Exception e) {
            LOGGER.severe("\n\n\n" + Database.class.getName() + " - database connection pool creation FAILURE\n");
            e.printStackTrace();
        }

        createTable();
        populateDatabase();

        LOGGER.info("\n\n\n" + Database.class.getName() + " - database created SUCCESSFUL\n\n\n");
    }

    public static Database getInstance() {
        return Database.INSTANCE;
    }

    private void createTable() {
        String sql = "CREATE TABLE Products" +
                "(id int auto_increment primary key," +
                "name varchar(100)," +
                "category varchar(100)," +
                "price double," +
                "quantity integer)";

        update(sql);
    }

    private void populateDatabase() {
        String sql = "INSERT INTO Products VALUES" +
                "(default, 'Catan', 'Strategy', 40, 3)," +
                "(default, 'Carcassonne', 'Strategy', 30, 2)," +
                "(default, 'Risk', 'Strategy', 25, 1)," +
                "(default, 'Taboo', 'Party', 20, 2);";

        update(sql);
    }

    public void update(String sql) {
        try {
            Connection connection = connectionPool.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            LOGGER.info("\n\n\n" + Database.class.getName() + " - database update SUCCESSFUL" +
                    "\nsql: " + sql + "\n\n\n");

            connection.close();
        } catch (Exception e) {
            LOGGER.severe("\n\n\n" + Database.class.getName() + " - database update FAILURE\n");
            e.printStackTrace();
        }
    }

    public List<Map<String, Object>> query(String sql) {
        List<Map<String, Object>> resultsMap = null;

        try {
            Connection connection = connectionPool.getConnection();
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(sql);
            resultsMap = resultSetToList(results);
            LOGGER.info("\n\n\n" + Database.class.getName() + " - database query SUCCESSFUL" +
                    "\nsql: " + sql +
                    "\nresults: " + results + "\n\n\n");

            connection.close();
        } catch (Exception e) {
            LOGGER.severe("\n\n\n" + Database.class.getName() + " - database query FAILURE\n");
            e.printStackTrace();
        }

        return resultsMap;
    }

    private List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        List<Map<String, Object>> rows = new ArrayList<>();

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>(columns);

            for (int i = 1; i <= columns; ++i)
                row.put(md.getColumnName(i), rs.getObject(i));

            rows.add(row);
        }

        return rows;
    }
}