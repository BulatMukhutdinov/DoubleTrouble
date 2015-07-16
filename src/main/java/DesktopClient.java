import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class DesktopClient {
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JLabel statusLabel;
    private JPanel controlPanel;
    int serverPort;
    String address;

    public DesktopClient(int serverPort, String address) {
        this.serverPort=serverPort;
        prepareGUI();
    }

    private void prepareGUI() {
        mainFrame = new JFrame("Double Trouble");
        mainFrame.setSize(300, 200);
        mainFrame.setLayout(new GridLayout(3, 1));
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        headerLabel = new JLabel("", JLabel.CENTER);
        statusLabel = new JLabel("", JLabel.CENTER);

        statusLabel.setSize(350, 100);

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        mainFrame.add(headerLabel);
        mainFrame.add(controlPanel);
        mainFrame.add(statusLabel);
        mainFrame.setVisible(true);
    }

    private void showTextField(final DataOutputStream out, final DataInputStream in) {
        headerLabel.setText("Enter your text here");

        final JTextField userText = new JTextField(15);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String data = "User entered: " + userText.getText();
                statusLabel.setText(data);
                System.out.println("Sending this line to the server...");
                try {
                    out.writeUTF(userText.getText());
                    out.flush();
                    boolean isTransactionSucceed = in.readBoolean();
                    if (isTransactionSucceed) {
                        statusLabel.setText("Success!");
                    } else {
                        statusLabel.setText("Failed");
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
        controlPanel.add(userText);
        controlPanel.add(sendButton);
        mainFrame.setVisible(true);
    }

    public static void main(String args[]) {
        DesktopClient desktopClient = new DesktopClient(6666,"127.0.0.1");

        try {
            InetAddress ipAddress = InetAddress.getByName(desktopClient.address);
            System.out.println("Any of you heard of a socket with IP address " + desktopClient.address + " and port " + desktopClient.serverPort + "?");
            Socket socket = new Socket(ipAddress, desktopClient.serverPort);
            System.out.println("Yes! I just got hold of the program.");


            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();


            DataInputStream in = new DataInputStream(sin);
            DataOutputStream out = new DataOutputStream(sout);

            desktopClient.showTextField(out, in);
            System.out.println("Type in something and press enter. Will send it to the server and tell ya what it thinks.");
            System.out.println();
        } catch (SocketException x) {
            System.out.println("Server doesn't response anymore");
            System.out.println();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}