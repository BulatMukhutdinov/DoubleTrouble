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
    public List<String> searchRecords(String searchWord) {
     /*   if (testConnection()) {
          //  throw new DBException();
        }
        Statement statement = null;
        try {
            statement = connection.createStatement();
            String sql = "select record from records where id=" + id;
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            return resultSet.getString("record");
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        return new ArrayList<String>();
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
    public List<String> getRecords() {
        Statement statement;
        try {
            statement = connection.createStatement();
            String sql = "select record from records";
            ResultSet resultSet = statement.executeQuery(sql);
            List<String> records = new ArrayList<String>();
            while (resultSet.next()) {
                records.add(resultSet.getString("record"));
            }
            return records;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isConnectionSuccessful() {
        return isConnectionSuccessful;
    }
}