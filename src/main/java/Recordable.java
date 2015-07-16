import java.util.List;
import java.util.Map;

public interface Recordable {

    public boolean setRecord(String record);

    public Map<String, String> searchRecords(String searchWord);

    public String getRecord(int id);

    public Map<String, String> getRecords();

    public boolean deleteRecord(int id);

    public boolean isConnectionSuccessful();
}