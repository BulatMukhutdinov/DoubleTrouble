import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bulat Mukhutdinov on 09.07.2015.
 */
public class App {
    private static List<Recordable> databases;

    public static void main(String args[]) {
        App app = new App();
        app.initDatabases();
        Server server = new Server();
        server.start(6666, databases);
     /*   try {
            System.out.println(databases.get(0).getRecord(12));
        } catch (DBException e) {
            e.printStackTrace();
        }*/

        //TODO Сервер нужен только web решения. Консольное приложение пишет сразу в базу.
    }

    private void initDatabases() {
        databases = new ArrayList<Recordable>();
        databases.add(new PostgreConnection("PostgresDB", "postgres", "Bulat6666"));
        //TODO сюда добавить реализацию NoSQL базы


        //Проверка работоспособности баз
        List<Recordable> accessibleDatabases = new ArrayList<Recordable>(databases);
        for (Recordable recordable : databases) {
            if (!recordable.isConnectionSuccessful()) {
                System.out.println("The DB " + recordable.getClass().getName() + " is not accessible");
                accessibleDatabases.remove(recordable);
            }
        }
        databases = new ArrayList<Recordable>(accessibleDatabases);
    }
}
