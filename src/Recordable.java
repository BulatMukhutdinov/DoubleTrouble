/**
 * Created by Bulat Mukhutdinov on 07.07.2015.
 */
public interface Recordable {

    public boolean setRecord(String record);

    public String getRecord(String searchWord);

    public String getRecord(int id);
}
