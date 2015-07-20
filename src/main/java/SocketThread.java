import java.io.*;
import java.net.*;
import java.util.*;

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


            String line = null;
            while (true) {
                line = in.readUTF(); // ожидаем пока клиент пришлет строку текста.
                System.out.println("Message from client: \"" + line + "\"");
                //TODO парсинг строки, вызов нужного метода
                sendAnswer(line, outputStream);

            }
        } catch (SocketException x) {
            System.out.println("Client " + getName() + " closed");
            System.out.println();
        } catch (EOFException x) {
            System.out.println("Waiting for the next record...");
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    //TODO подумать над решением отката транзакций
    private static void rollback(String transactionID) {

    }

    public void sendAnswer(String line, OutputStream outputStream) throws IOException {
        if (line.indexOf(' ') > 0) {
            List<String> params = new ArrayList<String>(Arrays.asList(line.split(" ")));
            if (params.get(0).toLowerCase().equals("setrecord")) {
                DataOutputStream out = new DataOutputStream(outputStream);
                boolean isTransactionSucceed = true;
                String record = "";
                for (int i = 1; i < params.size(); i++) {
                    record += params.get(i) + " ";
                }
                for (Recordable recordable : databases) {
                    if (!recordable.setRecord(record)) {
                        isTransactionSucceed = false;
                        //Не удалось завершить транзакцию. Здесь получение ID транзакции и откат всех транзакций во всех бд начиная с данной
                    }
                }
                out.writeBoolean(isTransactionSucceed);
                out.flush();
            } else if (params.get(0).toLowerCase().equals("getrecord")) {
                DataOutputStream out = new DataOutputStream(outputStream);
                int id;
                try {
                    id = Integer.getInteger(params.get(1));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    return;
                }
                out.writeUTF(databases.get(0).getRecord(id));
                out.flush();
            } else if (params.get(0).toLowerCase().equals("deleterecord")) {
                DataOutputStream out = new DataOutputStream(outputStream);
                boolean isTransactionSucceed = true;
                int id = Integer.getInteger(params.get(1));
                for (Recordable recordable : databases) {
                    if (!recordable.deleteRecord(id)) {
                        isTransactionSucceed = false;
                        //Не удалось завершить транзакцию. Здесь получение ID транзакции и откат всех транзакций во всех бд начиная с данной
                    }
                }
                out.writeBoolean(isTransactionSucceed);
                out.flush();
            } else if (params.get(0).toLowerCase().equals("searchrecord")) {
                ObjectOutputStream out = new ObjectOutputStream(outputStream);
                String searchWord = "";
                for (int i = 1; i < params.size(); i++) {
                    searchWord += params.get(i);
                }
                Map<String, String> listOfRecords = new HashMap<String, String>(databases.get(0).searchRecords(searchWord));
                out.writeObject(listOfRecords);
                out.flush();
            } else {
                ObjectOutputStream out = new ObjectOutputStream(outputStream);
                out.writeObject(null);
                out.flush();
            }
        } else if (line.length() > 0 && line.toLowerCase().equals("getrecords")) {
            ObjectOutputStream out = new ObjectOutputStream(outputStream);
            Map<String, String> listOfRecords = new HashMap<String, String>(databases.get(0).getRecords());
            out.writeObject(listOfRecords);
            out.flush();
        } else {
            ObjectOutputStream out = new ObjectOutputStream(outputStream);
            out.writeObject(null);
            out.flush();
        }
    }
}