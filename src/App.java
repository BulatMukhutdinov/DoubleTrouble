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

        //TODO Сервер нужен только web решения. Консольное приложение пишет сразу в базу.
    }

    private void initDatabases() {
        databases = new ArrayList<Recordable>();
        databases.add(new PostgreConnection("PostgresDB", "postgres", "Bulat6666"));
        //TODO сюда добавить реализацию NoSQL базы
    }
}
