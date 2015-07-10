import java.util.List;

/**
 * Created by Bulat Mukhutdinov on 07.07.2015.
 */
public interface Recordable {

    public boolean setRecord(String record);

    public List<String> searchRecords(String searchWord);

    public String getRecord(int id) throws DBException;

    public boolean isConnectionSuccessful();
}
