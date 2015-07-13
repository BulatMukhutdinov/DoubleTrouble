/**
 * Created by Bulat Mukhutdinov on 07.07.2015.
 */

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
        if (!testConnection()) {
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
    public String getRecord(int id) throws DBException {
        if (!testConnection()) {
            throw new DBException();
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
        }
        return "";
    }

    private boolean testConnection() {
        try {
            Class.forName("com.amazon.redshift.jdbc4.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your PostgreSQL JDBC Driver? Include in your library path!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean isConnectionSuccessful() {
        return isConnectionSuccessful;
    }
}
