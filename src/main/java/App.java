import java.net.UnknownHostException;
import java.util.*;

public class App {
    private static List<Recordable> databases;
    private final static int PORT = 6666;

    public static void main(String args[]) {
        App app = new App();
        app.initDatabases();
        Server server = new Server();
        server.start(PORT, databases);
        //TODO Сервер нужен только web решения. Консольное приложение пишет сразу в базу.
    }

    private void initDatabases() {
        databases = new ArrayList<Recordable>();
        databases.add(new PostgreConnection("DT", "DT", "DoubleTrouble"));
        try {
            databases.add(new MongoConnection("DT", "DoubleTrouble"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("NoSQL MongoDB Connector IS NOT WORKING!");
        }


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