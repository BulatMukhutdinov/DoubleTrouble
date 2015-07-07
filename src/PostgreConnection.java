/**
 * Created by Bulat Mukhutdinov on 07.07.2015.
 */

import java.sql.*;

public class PostgreConnection implements Recordable {
    private final String URL;
    private final String USER;
    private final String PASS;

    public PostgreConnection(String url, String user, String pass) {
        this.URL = url;
        this.USER = user;
        this.PASS = pass;
    }

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

    @Override
    public String getRecord(String searchWord) {
        return null;
    }

    @Override
    public String getRecord(int id) {
        return null;
    }
}
