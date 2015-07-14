import java.util.List;

public interface Recordable {

    public boolean setRecord(String record);

    public List<String> searchRecords(String searchWord);

    public String getRecord(int id) throws DBException;

    public boolean isConnectionSuccessful();
}