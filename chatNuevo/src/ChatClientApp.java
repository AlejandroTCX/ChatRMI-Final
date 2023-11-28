import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class ChatClientApp extends UnicastRemoteObject implements ChatClient {
    private ChatService chatService;
    private List<ChatClient> connectedClients;
    private JTextArea chatTextArea;
    private JTextField messageTextField;
    private JList<String> clientList;

    protected ChatClientApp() throws RemoteException {
        super();
        createGUI();
        connectToChatService();
    }

    private void createGUI() {
        JFrame frame = new JFrame("Chat RMI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel chatPanel = new JPanel(new BorderLayout());

        chatTextArea = new JTextArea(10, 40);
        chatTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatTextArea);
        chatPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel messagePanel = new JPanel(new BorderLayout());
        messageTextField = new JTextField(30);
        JButton sendButton = new JButton("Enviar");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        messagePanel.add(messageTextField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

        JPanel clientPanel = new JPanel(new BorderLayout());
        DefaultListModel<String> clientListModel = new DefaultListModel<>();
        clientList = new JList<>(clientListModel);
        clientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane clientScrollPane = new JScrollPane(clientList);
        clientPanel.add(clientScrollPane, BorderLayout.CENTER);

        JButton updateButton = new JButton("Actualizar");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateClientList();
            }
        });
        clientPanel.add(updateButton, BorderLayout.SOUTH);

        JButton publicButton = new JButton("Enviar público");
        publicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendPublicMessage();
            }
        });
        messagePanel.add(publicButton, BorderLayout.WEST);

        frame.getContentPane().add(chatPanel, BorderLayout.CENTER);
        frame.getContentPane().add(messagePanel, BorderLayout.SOUTH);
        frame.getContentPane().add(clientPanel, BorderLayout.EAST);

        frame.pack();
        frame.setVisible(true);
    }

    private void connectToChatService() {
        try {
            String serverIP = "192.168.1.128";
            Registry registry = LocateRegistry.getRegistry(serverIP, 6001);
            chatService = (ChatService) registry.lookup("ChatService");
            chatService.registerClient(this);
            connectedClients = chatService.getConnectedClients();
            updateClientList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateClientList() {
        DefaultListModel<String> clientListModel = new DefaultListModel<>();
        for (ChatClient client : connectedClients) {
            if (client != this) {
                try {
                    String clientIP = client.getClientIP();
                    clientListModel.addElement(clientIP);
                } catch (RemoteException | java.net.UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
        clientList.setModel(clientListModel);
    }

    private void sendMessage() {
        try {
            String message = messageTextField.getText();
            ChatClient selectedClient = getSelectedClient();
            if (selectedClient != null) {
                selectedClient.receiveMessage("Mensaje privado de " + getClientIP() + ": " + message);
            } else {
                chatService.broadcastMessage("Mensaje público de " + getClientIP() + ": " + message);
            }
            messageTextField.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPublicMessage() {
        try {
            String message = messageTextField.getText();
            chatService.broadcastMessage("Mensaje público de " + getClientIP() + ": " + message);
            messageTextField.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getClientIP() throws UnknownHostException, java.net.UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    private ChatClient getSelectedClient() {
        int selectedIndex = clientList.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < connectedClients.size()) {
            return connectedClients.get(selectedIndex);
        }
        return null;
    }

    @Override
    public void receiveMessage(String message) throws RemoteException {
        chatTextArea.append("Cliente: " + message + "\n");
    }

    @Override
    public void updateClientList(List<ChatClient> clients) throws RemoteException {

    }

    public static void main(String[] args) {
        try {
            ChatClientApp chatClientApp = new ChatClientApp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}