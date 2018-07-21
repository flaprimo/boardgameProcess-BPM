package boardgameProcess;

import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Database {
    private final static Logger LOGGER = Logger.getLogger(Database.class.getName());

    private static final String DATABASE_DRIVER = "org.h2.Driver";
    private static final String DATABASE_CONNECTION = "jdbc:h2:mem:boardgameProcess.db";
    private static final String DATABASE_USER = "demo";
    private static final String DATABASE_PASSWORD = "demo";

    private static JdbcConnectionPool connectionPool;

    public final static Database INSTANCE = new Database();

    private Database() {
        try {
            Class.forName(DATABASE_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println(e.toString());
        }
        try {
            connectionPool = JdbcConnectionPool.create(
                    DATABASE_CONNECTION, DATABASE_USER, DATABASE_PASSWORD);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        createTable();
        populateDatabase();

        LOGGER.info("\n\n\n"+Database.class.getName() +" - database created SUCCESSFUL\n\n\n");
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
            LOGGER.info("\n\n\n"+Database.class.getName() +" - update SUCCESSFUL\n" + sql + "\n\n\n");

            connection.close();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    public List<Map<String, Object>> query(String sql) {
        List<Map<String, Object>> resultsMap = null;

        try {
            Connection connection = connectionPool.getConnection();
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(sql);
            resultsMap = resultSetToList(results);
            LOGGER.info("\n\n\n"+Database.class.getName() +" - query SUCCESSFUL\n" + sql + "\n" + results + "\n\n\n");

            connection.close();
        } catch (Exception ex) {
            System.out.println(ex.toString());
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