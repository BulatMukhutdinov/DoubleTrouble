/**
 * Created by Bulat Mukhutdinov on 07.07.2015.
 */

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final static int PORT = 6666; // случайный порт (может быть любое число от 1025 до 65535)
    private static List<Recordable> databases;

    public static void main(String[] args) {
        initDatabases();
        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(PORT); // создаем сокет сервера и привязываем его к вышеуказанному порту
        } catch (IOException e) {
            e.printStackTrace();

        }
        System.out.println("Waiting for a client...");
        while (true) {
            try {
                socket = serverSocket.accept();// заставляем сервер ждать подключений и выводим сообщение когда кто-то связался с сервером
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client
            new SocketThread(socket, databases).start();
        }

    }

    private static void initDatabases() {
        databases = new ArrayList<Recordable>();
        databases.add(new PostgreConnection("PostgresDB", "postgres", "Bulat6666"));
    }

    //TODO подумать над решением отката транзакций
    private static void rollback(String transactionID) {

    }
}