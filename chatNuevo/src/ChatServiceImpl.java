import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ChatServiceImpl extends UnicastRemoteObject implements ChatService {
    private List<ChatClient> clients;

    public ChatServiceImpl() throws RemoteException {
        clients = new ArrayList<>();
    }

    @Override
    public void registerClient(ChatClient client) throws RemoteException {
        clients.add(client);
        // Enviar la lista de clientes conectados a todos los clientes
        for (ChatClient c : clients) {
            if (c != client) {
                c.updateClientList(clients);
            }
        }
    }

    @Override
    public void broadcastMessage(String message) throws RemoteException {
        for (ChatClient client : clients) {
            client.receiveMessage(message);
        }
    }

    @Override
    public List<ChatClient> getConnectedClients() throws RemoteException {
        return clients;
    }
}