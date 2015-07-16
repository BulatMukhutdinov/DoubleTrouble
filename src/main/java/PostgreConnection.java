import java.sql.*;
import java.util.*;

public class PostgreConnection implements Recordable {
    public final String HOST = "jdbc:redshift://dt.ctueqkiqocnw.us-west-2.rds.amazonaws.com";
    public final String PORT = "5432";

    private final String URL;
    private final String USER;
    private final String PASS;
    private Connection connection;
    private boolean isConnectionSuccessful;

    public PostgreConnection(String url, String user, String pass) {
        this.URL = url;
        this.USER = user;
        this.PASS = pass;
        initConnection();
    }


    private void initConnection() {
        try {
            connection = DriverManager.getConnection(
                    HOST + ":" + PORT + "/" + URL, USER,
                    PASS);
            isConnectionSuccessful = true;
        } catch (SQLException e) {
            System.out.println("Connection Failed! Due to next reasons: " + e.getMessage());
            e.printStackTrace();
            isConnectionSuccessful = false;
        }
    }

    @Override
    public boolean setRecord(String record) {
        if (record == null || record.length() == 0) {
            System.out.println("Incorrect record!");
            return false;
        }
        Statement statement = null;
        try {
            statement = connection.createStatement();
            String sql = "insert into records(record) values ('" + record + "')";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Map<String, String> searchRecords(String searchWord) {

        Statement statement = null;
        try {
            statement = connection.createStatement();
            String sql = "select * from records where record like \'%" + searchWord + "%\'";
            ResultSet resultSet = statement.executeQuery(sql);
            Map<String, String> records = new HashMap<String, String>();
            while (resultSet.next()) {
                records.put(resultSet.getString("id"), resultSet.getString("record"));
            }
            return records;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getRecord(int id) {
        Statement statement;
        try {
            statement = connection.createStatement();
            String sql = "select record from records where id=" + id;
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            return resultSet.getString("record");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, String> getRecords() {
        Statement statement;
        try {
            statement = connection.createStatement();
            String sql = "select * from records";
            ResultSet resultSet = statement.executeQuery(sql);
            Map<String, String> records = new HashMap<String, String>();
            while (resultSet.next()) {
                records.put(resultSet.getString("id"), resultSet.getString("record"));
            }
            return records;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteRecord(int id) {
        Statement statement;
        try {
            statement = connection.createStatement();
            String sql = "delete from records where id = " + id;
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isConnectionSuccessful() {
        return isConnectionSuccessful;
    }
}