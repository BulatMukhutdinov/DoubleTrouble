import java.net.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

public class Server {
    public void start(int port, List<Recordable> databases) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(port); // создаем сокет сервера и привязываем его к вышеуказанному порту (может быть любое число от 1025 до 65535)
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
}