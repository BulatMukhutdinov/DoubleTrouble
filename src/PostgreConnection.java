import java.sql.*;

public class PostgreConnection implements Recordable {
    private final String URL = "PostgresDB";
    private final String USER = "postgres";
    private final String PASS = "Bulat6666";

    @Override
        public boolean setRecord(String record) {
        if (record == null || record.length() == 0) {
            System.out.println("Incorrect record!");
            return false;
        }
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your PostgreSQL JDBC Driver? Include in your library path!");
            e.printStackTrace();
            return false;

        }
        Connection connection;
        try {

            connection = DriverManager.getConnection(
                    "jdbc:postgresql://127.0.0.1:5432/" + URL, USER,
                    PASS);
            Statement statement = connection.createStatement();
            String sql = "insert into records(record) values ('" + record + "')";
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.out.println("Connection Failed! Due to next reasons: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
