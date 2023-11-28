import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChatService extends Remote {
    void registerClient(ChatClient client) throws RemoteException;
    void broadcastMessage(String message) throws RemoteException;
    List<ChatClient> getConnectedClients() throws RemoteException;
}