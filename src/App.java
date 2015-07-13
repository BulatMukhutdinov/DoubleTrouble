/**
 * Created by Bulat Mukhutdinov on 09.07.2015.
 */
import com.sun.xml.internal.ws.client.ClientTransportException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

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
