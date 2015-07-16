import java.util.*;

public class App {
    private static List<Recordable> databases;
    private final static int PORT = 6666;

    public static void main(String args[]) {
        App app = new App();
        app.initDatabases();
        //test JSP
        Server server = new Server();
        server.start(PORT, databases);
        //TODO Сервер нужен только web решения. Консольное приложение пишет сразу в базу.
    }

    private void initDatabases() {
        databases = new ArrayList<Recordable>();
        databases.add(new PostgreConnection("DT", "DT", "DoubleTrouble"));
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