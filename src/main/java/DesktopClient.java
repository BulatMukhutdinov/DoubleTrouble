import com.sun.deploy.panel.JreTableModel;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class DesktopClient {
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JPanel controlPanel;
    private JPanel tablePanel;
    private JTable Table;
    private String[] column = {"ID", "Message"};
    private DefaultTableModel model;
    private final static int PORT = 6666;
    private final static String address = "52.28.195.240";
    public DesktopClient() {
        prepareGUI();
    }

    private void prepareGUI() {
        mainFrame = new JFrame("Double Trouble");
        mainFrame.setSize(900, 700);
        mainFrame.getContentPane().setBackground(Color.WHITE);
        mainFrame.setLayout(null);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        Dimension labelSize = new Dimension(80, 80);
        headerLabel = new JLabel("", JLabel.CENTER);
        headerLabel.setVerticalAlignment(JLabel.BOTTOM);
        headerLabel.setHorizontalAlignment(JLabel.LEFT);
        headerLabel.setPreferredSize(labelSize);
        statusLabel = new JLabel("", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.PLAIN, 26));
        headerLabel.setText("  Double Trouble NoSQL & SQL driven databases");
        headerLabel.setBounds(20, 20, 900, 100);
        statusLabel.setBackground(Color.WHITE);

        statusLabel.setFont(new Font("Arial", Font.ITALIC, 20));
        statusLabel.setForeground(Color.DARK_GRAY);

        statusLabel.setSize(350, 100);
        statusLabel.setBounds(250, 550, 350, 100);

        controlPanel = new JPanel();

        controlPanel.setLayout(new FlowLayout());
        controlPanel.setBackground(Color.WHITE);

        controlPanel.setBounds(20, 120, 800, 100);

        tablePanel = new JPanel();

        tablePanel.setLayout(new FlowLayout());
        tablePanel.setBackground(Color.WHITE);

        tablePanel.setBounds(100, 250, 600, 300);
        model = new DefaultTableModel(column, 0);
        Table = new JTable(model);
        Table.setModel(model);
        Table.setFont(new Font("Arial", Font.ITALIC, 18));
        Table.setBounds(100, 250, 500, 200);
        Table.setColumnSelectionAllowed(false);//выбор столбца
        Table.setRowSelectionAllowed(false);//откл строк
        mainFrame.add(headerLabel);
        mainFrame.add(controlPanel);
        mainFrame.add(tablePanel);
        mainFrame.add(statusLabel);
        mainFrame.setVisible(true);
    }

    private void showTextField(final DataOutputStream out, final InputStream in) {
        final JTextField userText = new JTextField(55);
        userText.setFont(new Font("Courier New", Font.PLAIN, 24));
        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.PLAIN, 16));
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Sending this line to the server...");
                try {
                    DataInputStream sin = new DataInputStream(in);
                    if (userText.getText().trim().length() == 0)
                        statusLabel.setText("Input message before sending");
                    else {
                        out.writeUTF("setrecord " + userText.getText());
                        out.flush();
                        boolean isTransactionSucceed = sin.readBoolean();
                        if (isTransactionSucceed) {
                            statusLabel.setText("Success");
                            model.fireTableDataChanged();
                        } else
                            statusLabel.setText("Fail");
                        System.out.println();
                    }
                } catch (SocketException x) {
                    System.out.println("Server doesn't response anymore");
                    System.out.println();
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        });
        JButton getButton = new JButton("Get");
        getButton.setFont(new Font("Arial", Font.PLAIN, 16));
        getButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Getting records from server...");
                try {
                    out.writeUTF("getrecords");
                    out.flush();
                    ObjectInputStream sin = new ObjectInputStream(in);
                    Map<String, String> map = new HashMap<String, String>((Map<? extends String, ? extends String>) sin.readObject());
                    if (map != null) {
                        System.out.println(map.size());
                        int i = 0;
                        String[][] data = new String[map.size() + 1][2];
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            data[i][0] = entry.getKey();
                            data[i][1] = entry.getValue();
                            i++;
                            System.out.println(entry.getKey() + " " + entry.getValue());
                        }
                        model.setColumnIdentifiers(column);
                        model.setDataVector(data, column);
                        model.fireTableDataChanged();
                    }
                    System.out.println();
                } catch (SocketException x) {
                    System.out.println("Server doesn't response anymore");
                    System.out.println();
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        });
        JButton getIDButton = new JButton("GetByID");
        getIDButton.setFont(new Font("Arial", Font.PLAIN, 16));
        getIDButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Getting record by ID from server...");
                try {
                    if (userText.getText().trim().length() == 0)
                        statusLabel.setText("Input id number before sending");
                    else {
                        try {
                            int id = Integer.parseInt(userText.getText());
                            out.writeUTF("getrecord " + id);
                            out.flush();
                            DataInputStream sin = new DataInputStream(in);
                            String result = sin.readUTF();
                            if (result.trim().length() != 0) {
                                statusLabel.setText(result);
                            } else
                                statusLabel.setText("No such entry");
                            System.out.println();
                        } catch (NumberFormatException el) {
                            statusLabel.setText("ID should be an Integer");
                        }
                    }
                } catch (SocketException x) {
                    System.out.println("Server doesn't response anymore");
                    System.out.println();
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        });
        JButton getWordButton = new JButton("GetByWord");
        getWordButton.setFont(new Font("Arial", Font.PLAIN, 16));
        getWordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Getting record by WORD from server...");
                try {
                    if (userText.getText().trim().length() == 0)
                        statusLabel.setText("Input WORD before sending");
                    else {
                        out.writeUTF("searchrecord " + userText.getText());
                        out.flush();
                        ObjectInputStream sin = new ObjectInputStream(in);
                        Map<String, String> map = new HashMap<String, String>((Map<? extends String, ? extends String>) sin.readObject());
                        if (map != null) {
                            String[] column = {"ID", "Message"};
                            System.out.println(map.size());
                            if (map.size() == 0)
                                statusLabel.setText("No such entry");
                            else {
                                int i = 0;
                                String[][] data = new String[map.size() + 1][2];
                                for (Map.Entry<String, String> entry : map.entrySet()) {
                                    data[i][0] = entry.getKey();
                                    data[i][1] = entry.getValue();
                                    i++;
                                    System.out.println(entry.getKey() + " " + entry.getValue());
                                }
                                model.setDataVector(data, column);
                                model.fireTableDataChanged();
                            }
                        }
                    }
                } catch (SocketException x) {
                    System.out.println("Server doesn't response anymore");
                    System.out.println();
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        });
        JButton deleteButton = new JButton("Delete");
        deleteButton.setFont(new Font("Arial", Font.PLAIN, 16));
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("deleting record by ID from server...");
                try {
                    if (userText.getText().trim().length() == 0)
                        statusLabel.setText("Input id number before deleting");
                    else {
                        out.writeUTF("deleterecord " + userText.getText());
                        out.flush();
                        DataInputStream sin = new DataInputStream(in);
                        boolean isTransactionSucceed = sin.readBoolean();
                        if (isTransactionSucceed) {
                            statusLabel.setText("record " + userText.getText() + " deleted");
                        } else {
                            statusLabel.setText("record " + userText.getText() + "wasn't deleted");
                        }
                        model.fireTableDataChanged();
                    }
                } catch (SocketException x) {
                    System.out.println("Server doesn't response anymore");
                    System.out.println();
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        });
        controlPanel.add(userText);
        controlPanel.add(sendButton);
        controlPanel.add(getButton);
        controlPanel.add(getIDButton);
        controlPanel.add(getWordButton);
        controlPanel.add(deleteButton);
        tablePanel.add(Table);
        tablePanel.setPreferredSize(new Dimension(450, 200));
        mainFrame.setVisible(true);
    }

    public static void main(String args[]) {
        DesktopClient desktopClient = new DesktopClient();

        try {
            InetAddress ipAddress = InetAddress.getByName(address);
            System.out.println("Any of you heard of a socket with IP address " + address + " and port " + PORT + "?");
            Socket socket = new Socket(ipAddress, PORT);
            System.out.println("Yes! I just got hold of the program.");

            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();

            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);

            desktopClient.showTextField(out, in);
            System.out.println("Type in something and press enter. Will send it to the server and tell ya what it thinks.");
            System.out.println();
        } catch (SocketException x) {
            desktopClient.statusLabel.setText("Server doesn't response anymore");
            System.out.println();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}