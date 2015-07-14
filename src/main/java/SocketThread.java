import java.io.*;
import java.net.*;
import java.util.List;

public class SocketThread extends Thread {
    protected Socket socket;
    private List<Recordable> databases;

    public SocketThread(Socket clientSocket, List<Recordable> databases) {
        this.socket = clientSocket;
        this.databases = databases;
    }

    public void run() {
        try {
            System.out.println("Got a client " + getName());

            // Берем входной и выходной потоки сокета, теперь можем получать и отсылать данные клиенту.
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
            DataInputStream in = new DataInputStream(inputStream);
            DataOutputStream out = new DataOutputStream(outputStream);

            String line = null;
            while (true) {
                line = in.readUTF(); // ожидаем пока клиент пришлет строку текста.
                System.out.println("Message from client: \"" + line + "\"");
                boolean isTransactionSucceed = true;
                for (Recordable recordable : databases) {
                    if (!recordable.setRecord(line)) {
                        isTransactionSucceed = false;
                        //Не удалось завершить транзакцию. Здесь получение ID транзакции и откат всех транзакций во всех бд начиная с данной
                    }
                }
                out.writeBoolean(isTransactionSucceed);// отсылаем клиенту ответ.
                out.flush(); // заставляем поток закончить передачу данных.
                System.out.println("Waiting for the next record...");
            }
        } catch (SocketException x) {
            System.out.println("Client " + getName() + " closed");
            System.out.println();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    //TODO подумать над решением отката транзакций
    private static void rollback(String transactionID) {

    }
}