package pewpew.smash.database;

import java.sql.*;

public final class Database {

    private static final Database instance = new Database();
    private DatabaseConfig config;

    public static synchronized Database getInstance() {
        return instance;
    }

    public void executeQuery(String query, ResultSetProcessor processor, Object... params) {
        execute(query, processor, params);
    }

    public int executeQuery(String query, Object... params) {
        return execute(query, params);
    }

    private void execute(String query, ResultSetProcessor processor, Object... params) {
        try (Connection connection = DriverManager.getConnection(
                config.getHOST(),
                config.getUSER(),
                config.getPASSWORD());
                PreparedStatement stmt = connection.prepareStatement(query)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                processor.process(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int execute(String query, Object... params) {
        try (Connection connection = DriverManager.getConnection(
                config.getHOST(),
                config.getUSER(),
                config.getPASSWORD());
                PreparedStatement stmt = connection.prepareStatement(query)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void dispose() {
        if (config != null) {
            config.consume();
        }
    }

    private Database() {
        config = DatabaseConfig.getInstance();
    }
}